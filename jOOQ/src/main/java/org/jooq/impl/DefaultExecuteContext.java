/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static org.jooq.conf.SettingsTools.renderLocale;
import static org.jooq.impl.Tools.EMPTY_INT;
import static org.jooq.impl.Tools.EMPTY_QUERY;
import static org.jooq.impl.Tools.EMPTY_STRING;

import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jooq.Configuration;
import org.jooq.ConnectionProvider;
import org.jooq.Constants;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Delete;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteType;
import org.jooq.Insert;
import org.jooq.Merge;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.ResultQuery;
import org.jooq.Routine;
import org.jooq.SQLDialect;
import org.jooq.Scope;
import org.jooq.Update;
import org.jooq.conf.Settings;
import org.jooq.tools.JooqLogger;
import org.jooq.tools.jdbc.JDBCUtils;

import org.jetbrains.annotations.NotNull;

// ...

/**
 * A default implementation for the {@link ExecuteContext}.
 *
 * @author Lukas Eder
 */
class DefaultExecuteContext implements ExecuteContext {

    private static final JooqLogger                       log       = JooqLogger.getLogger(DefaultExecuteContext.class);

    // Persistent attributes (repeatable)
    private final Configuration                           originalConfiguration;
    private final Configuration                           derivedConfiguration;
    private final Map<Object, Object>                     data;
    private final Query                                   query;
    private final Routine<?>                              routine;
    private String                                        sql;

    private final boolean                                 batch;
    private final Query[]                                 batchQueries;
    private final String[]                                batchSQL;
    private final int[]                                   batchRows;

    ConnectionProvider                                    connectionProvider;
    private Connection                                    connection;
    private SettingsEnabledConnection                     wrappedConnection;
    private PreparedStatement                             statement;
    private int                                           statementExecutionCount;
    private ResultSet                                     resultSet;
    private Record                                        record;
    private Result<?>                                     result;
    int                                                   recordLevel;
    int                                                   resultLevel;
    private int                                           rows      = -1;
    private RuntimeException                              exception;
    private SQLException                                  sqlException;
    private SQLWarning                                    sqlWarning;
    private String[]                                      serverOutput;

    // ------------------------------------------------------------------------
    // XXX: Static utility methods for handling blob / clob lifecycle
    // ------------------------------------------------------------------------

    private static final ThreadLocal<List<AutoCloseable>> RESOURCES = new ThreadLocal<>();

    /**
     * Clean up blobs, clobs and the local configuration.
     * <p>
     * <h5>BLOBS and CLOBS</h5>
     * <p>
     * [#1326] This is necessary in those dialects that have long-lived
     * temporary lob objects, which can cause memory leaks in certain contexts,
     * where the lobs' underlying session / connection is long-lived as well.
     * Specifically, Oracle and ojdbc have some trouble when streaming temporary
     * lobs to UDTs:
     * <ol>
     * <li>The lob cannot have a call-scoped life time with UDTs</li>
     * <li>Freeing the lob after binding will cause an ORA-22275</li>
     * <li>Not freeing the lob after execution will cause an
     * {@link OutOfMemoryError}</li>
     * </ol>
     * <p>
     * <h5>Local configuration</h5>
     * <p>
     * [#1544] There exist some corner-cases regarding the {@link SQLOutput}
     * API, used for UDT serialisation / deserialisation, which have no elegant
     * solutions of obtaining a {@link Configuration} and thus a JDBC
     * {@link Connection} object short of:
     * <ul>
     * <li>Making assumptions about the JDBC driver and using proprietary API,
     * e.g. that of ojdbc</li>
     * <li>Dealing with this problem globally by using such a local
     * configuration</li>
     * </ul>
     *
     * @see <a
     *      href="http://stackoverflow.com/q/11439543/521799">http://stackoverflow.com/q/11439543/521799</a>
     */
    static final void clean() {
        List<AutoCloseable> resources = RESOURCES.get();

        if (resources != null) {
            for (AutoCloseable resource : resources)
                JDBCUtils.safeClose(resource);

            RESOURCES.remove();
        }

        LOCAL_CONNECTION.remove();
    }

    /**
     * Register a blob for later cleanup with {@link #clean()}
     */
    static final void register(Blob blob) {
        register((AutoCloseable) blob::free);
    }

    /**
     * Register a clob for later cleanup with {@link #clean()}
     */
    static final void register(Clob clob) {
        register((AutoCloseable) clob::free);
    }

    /**
     * Register an xml for later cleanup with {@link #clean()}
     */
    static final void register(SQLXML xml) {
        register((AutoCloseable) xml::free);
    }

    /**
     * Register an array for later cleanup with {@link #clean()}
     */
    static final void register(Array array) {
        register((AutoCloseable) array::free);
    }

    /**
     * Register a closeable for later cleanup with {@link #clean()}
     */
    static final void register(AutoCloseable closeable) {
        List<AutoCloseable> list = RESOURCES.get();

        if (list == null) {
            list = new ArrayList<>();
            RESOURCES.set(list);
        }

        list.add(closeable);
    }

    // ------------------------------------------------------------------------
    // XXX: Static utility methods for handling Configuration lifecycle
    // ------------------------------------------------------------------------

    private static final ThreadLocal<ExecuteContext> LOCAL_EXECUTE_CONTEXT = new ThreadLocal<>();

    /**
     * Get the registered {@link ExecuteContext}.
     * <p>
     * It can be safely assumed that such a configuration is available once the
     * {@link ExecuteContext} has been established, until the statement is
     * closed.
     */
    static final ExecuteContext localExecuteContext() {
        return LOCAL_EXECUTE_CONTEXT.get();
    }

    /**
     * Run a runnable with a new {@link #localExecuteContext()}.
     */
    static final <E extends Exception> void localExecuteContext(ExecuteContext ctx, ThrowingRunnable<E> runnable) throws E {
        localExecuteContext(ctx, () -> { runnable.run(); return null; });
    }

    /**
     * Run a supplier with a new {@link #localExecuteContext()}.
     */
    static final <T, E extends Exception> T localExecuteContext(ExecuteContext ctx, ThrowingSupplier<T, E> supplier) throws E {
        ExecuteContext old = localExecuteContext();

        try {
            LOCAL_EXECUTE_CONTEXT.set(ctx);
            return supplier.get();
        }
        finally {
            LOCAL_EXECUTE_CONTEXT.set(old);
        }
    }

    // ------------------------------------------------------------------------
    // XXX: Static utility methods for handling Configuration lifecycle
    // ------------------------------------------------------------------------

    private static final ThreadLocal<Connection> LOCAL_CONNECTION = new ThreadLocal<>();

    /**
     * Get the registered connection.
     * <p>
     * It can be safely assumed that such a connection is available once the
     * {@link ExecuteContext} has been established, until the statement is
     * closed.
     */
    static final Connection localConnection() {
        return LOCAL_CONNECTION.get();
    }

    /**
     * Get the registered connection's "target connection" through
     * {@link Configuration#unwrapperProvider()} if applicable.
     * <p>
     * It can be safely assumed that such a connection is available once the
     * {@link ExecuteContext} has been established, until the statement is
     * closed.
     */
    static final Connection localTargetConnection(Scope scope) {
        Connection result = localConnection();
















        log.info("Could not unwrap native Connection type. Consider implementing an org.jooq.UnwrapperProvider");
        return result;
    }

    // ------------------------------------------------------------------------
    // XXX: Constructors
    // ------------------------------------------------------------------------

    DefaultExecuteContext(Configuration configuration) {
        this(configuration, null, null, null);
    }

    DefaultExecuteContext(Configuration configuration, Query[] batchQueries) {
        this(configuration, null, batchQueries, null);
    }

    DefaultExecuteContext(Configuration configuration, Query query) {
        this(configuration, query, null, null);
    }

    DefaultExecuteContext(Configuration configuration, Routine<?> routine) {
        this(configuration, null, null, routine);
    }

    private DefaultExecuteContext(Configuration configuration, Query query, Query[] batchQueries, Routine<?> routine) {

        // [#4277] The ExecuteContext's Configuration will always return the same Connection,
        //         e.g. when running statements from sub-ExecuteContexts
        // [#7569] The original configuration is attached to Record and Result instances
        this.connectionProvider = configuration.connectionProvider();
        this.originalConfiguration = configuration;
        this.derivedConfiguration = configuration.derive(new ExecuteContextConnectionProvider());
        this.data = new DataMap();
        this.query = query;
        this.routine = routine;

        if (routine != null) {
            this.batch = false;
            this.batchQueries = null;
            this.batchRows = null;
            this.batchSQL = null;
        }
        else if (batchQueries != null) {
            this.batch = true;
            this.batchQueries = batchQueries;
            this.batchRows = new int[batchQueries.length];
            this.batchSQL = new String[batchQueries.length];

            Arrays.fill(this.batchRows, -1);
        }
        else if (query == null) {
            this.batch = false;
            this.batchQueries = null;
            this.batchRows = null;
            this.batchSQL = null;
        }
        else {
            this.batch = false;
            this.batchQueries = null;
            this.batchRows = null;
            this.batchSQL = null;
        }

        clean();
    }

    @Override
    public final Map<Object, Object> data() {
        return data;
    }

    @Override
    public final Object data(Object key) {
        return data.get(key);
    }

    @Override
    public final Object data(Object key, Object value) {
        return data.put(key, value);
    }

    @Override
    public final ExecuteType type() {

        // This can only be a routine
        if (routine != null) {
            return ExecuteType.ROUTINE;
        }

        // This can only be a BatchSingle or BatchMultiple execution
        else if (batch) {
            return ExecuteType.BATCH;
        }

        // Any other type of query
        else if (query != null) {
            if (query instanceof ResultQuery) {
                return ExecuteType.READ;
            }
            else if (query instanceof Insert
                  || query instanceof Update
                  || query instanceof Delete
                  || query instanceof Merge) {

                return ExecuteType.WRITE;
            }
            else if (query instanceof DDLQuery) {
                return ExecuteType.DDL;
            }

            // Analyse SQL in plain SQL queries:
            else {
                String s = query.getSQL().toLowerCase(renderLocale(configuration().settings()));

                // TODO: Use a simple lexer to parse SQL here. Potentially, the
                // SQL Console's SQL formatter could be used...?
                if (s.matches("^(with\\b.*?\\bselect|select|explain)\\b.*?"))
                    return ExecuteType.READ;

                // These are sample DML statements. There may be many more
                else if (s.matches("^(insert|update|delete|merge|replace|upsert|lock)\\b.*?"))
                    return ExecuteType.WRITE;

                // These are only sample DDL statements. There may be many more
                else if (s.matches("^(create|alter|drop|truncate|grant|revoke|analyze|comment|flashback|enable|disable)\\b.*?"))
                    return ExecuteType.DDL;

                // JDBC escape syntax for routines
                else if (s.matches("^\\s*\\{\\s*(\\?\\s*=\\s*)call.*?"))
                    return ExecuteType.ROUTINE;

                // Vendor-specific calling of routines / procedural blocks
                else if (s.matches("^(call|begin|declare)\\b.*?"))
                    return ExecuteType.ROUTINE;
            }
        }

        // Fetching JDBC result sets, e.g. with SQL.fetch(ResultSet)
        else if (resultSet != null) {
            return ExecuteType.READ;
        }

        // No query available
        return ExecuteType.OTHER;
    }

    @Override
    public final Query query() {
        return query;
    }

    @Override
    public final Query[] batchQueries() {
        return batch         ? batchQueries
             : query != null ? new Query[] { query }
             :                 EMPTY_QUERY;
    }

    @Override
    public final Routine<?> routine() {
        return routine;
    }

    @Override
    public final void sql(String s) {
        this.sql = s;

        // If this isn't a BatchMultiple query
        if (batchSQL != null && batchSQL.length == 1)
            batchSQL[0] = s;
    }

    @Override
    public final String sql() {
        return sql;
    }

    @Override
    public final String[] batchSQL() {
        return batch                            ? batchSQL
             : routine != null || query != null ? new String[] { sql }
             :                                    EMPTY_STRING;
    }

    @Override
    public final void statement(PreparedStatement s) {
        this.statement = s;
    }

    @Override
    public final PreparedStatement statement() {
        return statement;
    }

    @Override
    public final int statementExecutionCount() {
        return statementExecutionCount;
    }

    @Override
    public final void resultSet(ResultSet rs) {
        this.resultSet = rs;
    }

    @Override
    public final ResultSet resultSet() {
        return resultSet;
    }

    @Override
    public final Configuration configuration() {
        return derivedConfiguration;
    }

    // [#4277] [#7569] The original configuration that was used to create the
    //                 derived configuration in this ExecuteContext
    final Configuration originalConfiguration() {
        return originalConfiguration;
    }

    @Override
    public final DSLContext dsl() {
        return configuration().dsl();
    }

    @Override
    public final Settings settings() {
        return Tools.settings(configuration());
    }

    @Override
    public final SQLDialect dialect() {
        return Tools.configuration(configuration()).dialect();
    }

    @Override
    public final SQLDialect family() {
        return dialect().family();
    }

    @Override
    public final void connectionProvider(ConnectionProvider provider) {
        this.connectionProvider = provider;
    }

    @Override
    public final Connection connection() {
        // All jOOQ internals are expected to get a connection through this
        // single method. It can thus be guaranteed, that every connection is
        // wrapped by a ConnectionProxy, transparently, in order to implement
        // Settings.getStatementType() correctly.
        if (wrappedConnection == null && connectionProvider != null)
            connection(connectionProvider, connectionProvider.acquire());

        return wrappedConnection;
    }

    /**
     * Initialise this {@link DefaultExecuteContext} with a pre-existing
     * {@link Connection}.
     * <p>
     * [#3191] This is needed, e.g. when using
     * {@link Query#keepStatement(boolean)}.
     */
    final void connection(ConnectionProvider provider, Connection c) {
        if (c != null) {
            LOCAL_CONNECTION.set(c);
            connection = c;
            wrappedConnection = wrapConnection(provider, c);
        }
    }

    private final SettingsEnabledConnection wrapConnection(ConnectionProvider provider, Connection c) {
        return new SettingsEnabledConnection(new ProviderEnabledConnection(provider, c), derivedConfiguration.settings(), this);
    }

    final void incrementStatementExecutionCount() {
        statementExecutionCount++;
    }

    final DefaultExecuteContext withStatementExecutionCount(int count) {
        statementExecutionCount = count;
        return this;
    }

    @Override
    public final void record(Record r) {
        this.record = r;
    }

    @Override
    public final Record record() {
        return record;
    }

    @Override
    public final int recordLevel() {
        return recordLevel;
    }

    @Override
    public final int rows() {
        return rows;
    }

    @Override
    public final void rows(int r) {
        this.rows = r;

        // If this isn't a BatchMultiple query
        if (batchRows != null && batchRows.length == 1)
            batchRows[0] = r;
    }

    @Override
    public final int[] batchRows() {
        return batch                            ? batchRows
             : routine != null || query != null ? new int[] { rows }
             :                                    EMPTY_INT;
    }

    @Override
    public final void result(Result<?> r) {
        this.result = r;
    }

    @Override
    public final Result<?> result() {
        return result;
    }

    @Override
    public final int resultLevel() {
        return resultLevel;
    }

    @Override
    public final RuntimeException exception() {
        return exception;
    }

    @Override
    public final void exception(RuntimeException e) {
        this.exception = Tools.translate(sql(), e);

        if (Boolean.TRUE.equals(settings().isDebugInfoOnStackTrace())) {

            // [#5570] Add jOOQ version and SQL Dialect info on the stack trace
            //         to help users write better bug reports.
            //         See http://stackoverflow.com/q/39712695/521799
            StackTraceElement[] oldStack = exception.getStackTrace();
            if (oldStack != null) {
                StackTraceElement[] newStack = new StackTraceElement[oldStack.length + 1];
                System.arraycopy(oldStack, 0, newStack, 1, oldStack.length);
                newStack[0] = new StackTraceElement(
                    "org.jooq_" + Constants.VERSION + "." + dialect(),
                    "debug", null, -1);
                exception.setStackTrace(newStack);
            }
        }
    }

    @Override
    public final SQLException sqlException() {
        return sqlException;
    }

    @Override
    public final void sqlException(SQLException e) {
        this.sqlException = e;
        exception(Tools.translate(sql(), e));
    }

    @Override
    public final SQLWarning sqlWarning() {
        return sqlWarning;
    }

    @Override
    public final void sqlWarning(SQLWarning e) {
        this.sqlWarning = e;
    }

    @Override
    public final String[] serverOutput() {
        return serverOutput == null ? EMPTY_STRING : serverOutput;
    }

    @Override
    public final void serverOutput(String[] output) {
        this.serverOutput = output;
    }

    private final class ExecuteContextConnectionProvider implements ConnectionProvider {

        @NotNull
        @Override
        public final Connection acquire() {

            // [#4277] Connections are acquired lazily in parent ExecuteContext. A child ExecuteContext
            //         may well need a Connection earlier than the parent, in case of which acquisition is
            //         forced in the parent as well.
            if (connection == null)
                DefaultExecuteContext.this.connection();

            return wrapConnection(this, connection);
        }

        @Override
        public final void release(Connection c) {}
    }
}
