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

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.jooq.Clause.CREATE_TABLE;
import static org.jooq.Clause.CREATE_TABLE_AS;
import static org.jooq.Clause.CREATE_TABLE_COLUMNS;
import static org.jooq.Clause.CREATE_TABLE_CONSTRAINTS;
import static org.jooq.Clause.CREATE_TABLE_NAME;
// ...
// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
import static org.jooq.SQLDialect.DERBY;
// ...
import static org.jooq.SQLDialect.FIREBIRD;
import static org.jooq.SQLDialect.H2;
// ...
import static org.jooq.SQLDialect.HSQLDB;
import static org.jooq.SQLDialect.IGNITE;
// ...
// ...
import static org.jooq.SQLDialect.MARIADB;
// ...
import static org.jooq.SQLDialect.MYSQL;
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
// ...
// ...
import static org.jooq.SQLDialect.SQLITE;
// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.YUGABYTE;
import static org.jooq.impl.DSL.asterisk;
import static org.jooq.impl.DSL.commentOnTable;
import static org.jooq.impl.DSL.createIndex;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.insertInto;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.select;
import static org.jooq.impl.DSL.sql;
import static org.jooq.impl.DSL.table;
import static org.jooq.impl.Keywords.K_AS;
import static org.jooq.impl.Keywords.K_COMMENT;
import static org.jooq.impl.Keywords.K_CREATE;
import static org.jooq.impl.Keywords.K_GLOBAL_TEMPORARY;
import static org.jooq.impl.Keywords.K_IF_NOT_EXISTS;
import static org.jooq.impl.Keywords.K_INDEX;
import static org.jooq.impl.Keywords.K_IS;
import static org.jooq.impl.Keywords.K_ON_COMMIT_DELETE_ROWS;
import static org.jooq.impl.Keywords.K_ON_COMMIT_DROP;
import static org.jooq.impl.Keywords.K_ON_COMMIT_PRESERVE_ROWS;
import static org.jooq.impl.Keywords.K_TABLE;
import static org.jooq.impl.Keywords.K_TEMPORARY;
import static org.jooq.impl.Keywords.K_UNIQUE;
import static org.jooq.impl.Keywords.K_WITH_DATA;
import static org.jooq.impl.Keywords.K_WITH_NO_DATA;
import static org.jooq.impl.SQLDataType.INTEGER;
import static org.jooq.impl.Tools.EMPTY_FIELD;
import static org.jooq.impl.Tools.anyMatch;
import static org.jooq.impl.Tools.begin;
import static org.jooq.impl.Tools.enums;
import static org.jooq.impl.Tools.executeImmediate;
import static org.jooq.impl.Tools.findAny;
import static org.jooq.impl.Tools.map;
import static org.jooq.impl.Tools.storedEnumType;
import static org.jooq.impl.Tools.tryCatch;
import static org.jooq.impl.Tools.BooleanDataKey.DATA_SELECT_NO_DATA;
import static org.jooq.impl.Tools.DataKey.DATA_SELECT_INTO_TABLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.jooq.Comment;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Constraint;
import org.jooq.Context;
import org.jooq.CreateTableColumnStep;
import org.jooq.CreateTableConstraintStep;
import org.jooq.CreateTableWithDataStep;
import org.jooq.DataType;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Nullability;
// ...
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.Table;
import org.jooq.TableOptions.OnCommit;
import org.jooq.impl.QOM.UNotYetImplemented;


/**
 * @author Lukas Eder
 */
final class CreateTableImpl
extends
    AbstractDDLQuery
implements
    CreateTableWithDataStep,
    CreateTableColumnStep,
    UNotYetImplemented

{
    private static final Set<SQLDialect> NO_SUPPORT_IF_NOT_EXISTS           = SQLDialect.supportedBy(DERBY, FIREBIRD);
    private static final Set<SQLDialect> NO_SUPPORT_WITH_DATA               = SQLDialect.supportedBy(H2, MARIADB, MYSQL, SQLITE);
    private static final Set<SQLDialect> NO_SUPPORT_CTAS_COLUMN_NAMES       = SQLDialect.supportedBy(H2);
    private static final Set<SQLDialect> EMULATE_INDEXES_IN_BLOCK           = SQLDialect.supportedBy(FIREBIRD, POSTGRES, YUGABYTE);
    private static final Set<SQLDialect> EMULATE_SOME_ENUM_TYPES_AS_CHECK   = SQLDialect.supportedBy(CUBRID, DERBY, FIREBIRD, HSQLDB, POSTGRES, SQLITE, YUGABYTE);
    private static final Set<SQLDialect> EMULATE_STORED_ENUM_TYPES_AS_CHECK = SQLDialect.supportedBy(CUBRID, DERBY, FIREBIRD, HSQLDB, SQLITE);
    private static final Set<SQLDialect> REQUIRES_WITH_DATA                 = SQLDialect.supportedBy(HSQLDB);
    private static final Set<SQLDialect> WRAP_SELECT_IN_PARENS              = SQLDialect.supportedBy(HSQLDB);
    private static final Set<SQLDialect> SUPPORT_TEMPORARY                  = SQLDialect.supportedBy(MARIADB, MYSQL, POSTGRES, YUGABYTE);
    private static final Set<SQLDialect> EMULATE_COMMENT_IN_BLOCK           = SQLDialect.supportedBy(FIREBIRD, POSTGRES, YUGABYTE);
    private static final Set<SQLDialect> REQUIRE_EXECUTE_IMMEDIATE          = SQLDialect.supportedBy(FIREBIRD);
    private static final Set<SQLDialect> NO_SUPPORT_NULLABLE_PRIMARY_KEY    = SQLDialect.supportedBy(MARIADB, MYSQL);
    private static final Set<SQLDialect> REQUIRE_NON_PK_COLUMNS             = SQLDialect.supportedBy(IGNITE);





    private final Table<?>               table;
    private Select<?>                    select;
    private Boolean                      withData;
    private final List<Field<?>>         columnFields;
    private final List<DataType<?>>      columnTypes;
    private final List<Constraint>       constraints;
    private final List<Index>            indexes;
    private final boolean                temporary;
    private final boolean                ifNotExists;
    private OnCommit                     onCommit;
    private Comment                      comment;
    private SQL                          storage;

    CreateTableImpl(Configuration configuration, Table<?> table, boolean temporary, boolean ifNotExists) {
        super(configuration);

        this.table = table;
        this.temporary = temporary;
        this.ifNotExists = ifNotExists;
        this.columnFields = new ArrayList<>();
        this.columnTypes = new ArrayList<>();
        this.constraints = new ArrayList<>();
        this.indexes = new ArrayList<>();
    }

    final Table<?>          $table()        { return table; }
    final boolean           $temporary()    { return temporary; }
    final OnCommit          $onCommit()     { return onCommit; }
    final Select<?>         $select()       { return select; }
    final List<Field<?>>    $columnFields() { return columnFields; }
    final List<DataType<?>> $columnTypes()  { return columnTypes; }
    final List<Constraint>  $constraints()  { return constraints; }
    final List<Index>       $indexes()      { return indexes; }
    final boolean           $ifNotExists()  { return ifNotExists; }
    final Comment           $comment()      { return comment; }

    // ------------------------------------------------------------------------
    // XXX: DSL API
    // ------------------------------------------------------------------------

    @Override
    public final CreateTableImpl as(Select<? extends Record> s) {
        this.select = s;
        return this;
    }

    @Override
    public final CreateTableImpl withData() {
        withData = true;
        return this;
    }

    @Override
    public final CreateTableImpl withNoData() {
        withData = false;
        return this;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public final CreateTableImpl column(Field<?> field) {
        return column((Field) field, field.getDataType());
    }

    @Override
    public final CreateTableImpl columns(Field<?>... fields) {
        return columns(Arrays.asList(fields));
    }

    @Override
    public final CreateTableImpl columns(Name... fields) {
        return columns(Tools.fieldsByName(fields));
    }

    @Override
    public final CreateTableImpl columns(String... fields) {
        return columns(Tools.fieldsByName(fields));
    }

    @Override
    public final CreateTableImpl columns(Collection<? extends Field<?>> fields) {
        for (Field<?> field : fields)
            column(field);

        return this;
    }

    @Override
    public final <T> CreateTableImpl column(Field<T> field, DataType<T> type) {
        columnFields.add(field);
        columnTypes.add(type);
        return this;
    }

    @Override
    public final CreateTableImpl column(Name field, DataType<?> type) {
        columnFields.add(field(field, type));
        columnTypes.add(type);
        return this;
    }

    @Override
    public final CreateTableImpl column(String field, DataType<?> type) {
        return column(name(field), type);
    }

    @Override
    public final CreateTableImpl constraint(Constraint c) {
        return constraints(Arrays.asList(c));
    }

    @Override
    public final CreateTableImpl constraints(Constraint... c) {
        return constraints(Arrays.asList(c));
    }

    @Override
    public final CreateTableImpl constraints(Collection<? extends Constraint> c) {
        constraints.addAll(c);
        return this;
    }

    @Override
    public final CreateTableConstraintStep primaryKey(String... fields) {
        return constraint(DSL.primaryKey(fields));
    }

    @Override
    public final CreateTableConstraintStep primaryKey(Name... fields) {
        return constraint(DSL.primaryKey(fields));
    }

    @Override
    public final CreateTableConstraintStep primaryKey(Field<?>... fields) {
        return constraint(DSL.primaryKey(fields));
    }

    @Override
    public final CreateTableConstraintStep primaryKey(Collection<? extends Field<?>> fields) {
        return constraint(DSL.primaryKey(fields));
    }

    @Override
    public final CreateTableConstraintStep unique(String... fields) {
        return constraint(DSL.unique(fields));
    }

    @Override
    public final CreateTableConstraintStep unique(Name... fields) {
        return constraint(DSL.unique(fields));
    }

    @Override
    public final CreateTableConstraintStep unique(Field<?>... fields) {
        return constraint(DSL.unique(fields));
    }

    @Override
    public final CreateTableConstraintStep unique(Collection<? extends Field<?>> fields) {
        return constraint(DSL.unique(fields));
    }

    @Override
    public final CreateTableConstraintStep check(Condition condition) {
        return constraint(DSL.check(condition));
    }

    @Override
    public final CreateTableImpl index(Index i) {
        return indexes(Arrays.asList(i));
    }

    @Override
    public final CreateTableImpl indexes(Index... i) {
        return indexes(Arrays.asList(i));
    }

    @Override
    public final CreateTableImpl indexes(Collection<? extends Index> i) {
        indexes.addAll(i);
        return this;
    }

    @Override
    public final CreateTableImpl onCommitDeleteRows() {
        onCommit = OnCommit.DELETE_ROWS;
        return this;
    }

    @Override
    public final CreateTableImpl onCommitPreserveRows() {
        onCommit = OnCommit.PRESERVE_ROWS;
        return this;
    }

    @Override
    public final CreateTableImpl onCommitDrop() {
        onCommit = OnCommit.DROP;
        return this;
    }

    @Override
    public final CreateTableImpl comment(String c) {
        return comment(DSL.comment(c));
    }

    @Override
    public final CreateTableImpl comment(Comment c) {
        comment = c;
        return this;
    }

    @Override
    public final CreateTableImpl storage(SQL sql) {
        storage = sql;
        return this;
    }

    @Override
    public final CreateTableImpl storage(String sql) {
        return storage(sql(sql));
    }

    @Override
    public final CreateTableImpl storage(String sql, Object... bindings) {
        return storage(sql(sql, bindings));
    }

    @Override
    public final CreateTableImpl storage(String sql, QueryPart... parts) {
        return storage(sql(sql, parts));
    }

    // ------------------------------------------------------------------------
    // XXX: QueryPart API
    // ------------------------------------------------------------------------

    private final boolean supportsIfNotExists(Context<?> ctx) {
        return !NO_SUPPORT_IF_NOT_EXISTS.contains(ctx.dialect());
    }

    @Override
    public final void accept(Context<?> ctx) {
        if (ifNotExists && !supportsIfNotExists(ctx))
            tryCatch(ctx, DDLStatementType.CREATE_TABLE, c -> accept0(c));
        else
            accept0(ctx);
    }

    private static final void executeImmediateIf(boolean wrap, Context<?> ctx, Consumer<? super Context<?>> runnable) {
        if (wrap) {
            executeImmediate(ctx, runnable);
        }
        else {
            runnable.accept(ctx);
            ctx.sql(';');
        }
    }

    private final void accept0(Context<?> ctx) {
        boolean bc = comment != null && EMULATE_COMMENT_IN_BLOCK.contains(ctx.dialect());
        boolean bi = !indexes.isEmpty() && EMULATE_INDEXES_IN_BLOCK.contains(ctx.dialect());

        if (bc || bi) {
            begin(ctx, c1 -> {
                executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(c1.dialect()), c1, c2 -> accept1(c2));

                if (bc) {
                    c1.formatSeparator();

                    executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(ctx.dialect()), c1,
                        c2 -> c2.visit(commentOnTable(table).is(comment))
                    );
                }

                if (bi) {
                    for (Index index : indexes) {
                        c1.formatSeparator();

                        executeImmediateIf(REQUIRE_EXECUTE_IMMEDIATE.contains(c1.dialect()), c1, c2 -> {
                            if ("".equals(index.getName()))
                                c2.visit(createIndex().on(index.getTable(), index.getFields()));
                            else
                                c2.visit(createIndex(index.getUnqualifiedName()).on(index.getTable(), index.getFields()));
                        });
                    }
                }
            });
        }
        else
            accept1(ctx);
    }

    private final void accept1(Context<?> ctx) {
        ctx.start(CREATE_TABLE);

        if (select != null) {








            acceptCreateTableAsSelect(ctx);
        }
        else {
            toSQLCreateTable(ctx);
            toSQLOnCommit(ctx);
        }

        if (comment != null && !EMULATE_COMMENT_IN_BLOCK.contains(ctx.dialect())) {
            ctx.formatSeparator()
               .visit(K_COMMENT).sql(' ');








            ctx.visit(comment);
        }

        // [#7772] This data() value should be available from ctx directly, not only from ctx.configuration()
        if (storage != null && ctx.configuration().data("org.jooq.ddl.ignore-storage-clauses") == null)
            ctx.formatSeparator()
               .visit(storage);

        ctx.end(CREATE_TABLE);
    }

    private void toSQLCreateTable(Context<?> ctx) {
        toSQLCreateTableName(ctx);

        if (!columnFields.isEmpty()
                && (select == null || !NO_SUPPORT_CTAS_COLUMN_NAMES.contains(ctx.dialect()))) {
            ctx.sqlIndentStart(" (")
               .start(CREATE_TABLE_COLUMNS);

            Field<?> identity = null;
            boolean qualify = ctx.qualify();
            ctx.qualify(false);

            for (int i = 0; i < columnFields.size(); i++) {
                DataType<?> type = columnType(ctx, i);
                if (identity == null && type.identity())
                    identity = columnFields.get(i);

                ctx.visit(columnFields.get(i));

                if (select == null) {
                    ctx.sql(' ');
                    Tools.toSQLDDLTypeDeclarationForAddition(ctx, type);
                }

                if (i < columnFields.size() - 1)
                    ctx.sql(',').formatSeparator();
            }

            // [#10551] Ignite requires at least one non-PK column.
            toSQLDummyColumns(ctx);

            ctx.qualify(qualify);
            ctx.end(CREATE_TABLE_COLUMNS)
               .start(CREATE_TABLE_CONSTRAINTS);

            if (!constraints.isEmpty())
                for (Constraint constraint : constraints)

                    // [#6841] SQLite has a weird requirement of the PRIMARY KEY keyword being on the column directly,
                    //         when there is an identity. Thus, we must not repeat the primary key specification here.
                    if (((ConstraintImpl) constraint).supported(ctx) && (ctx.family() != SQLITE || !matchingPrimaryKey(constraint, identity)))
                        ctx.sql(',')
                           .formatSeparator()
                           .visit(constraint);

            if (EMULATE_SOME_ENUM_TYPES_AS_CHECK.contains(ctx.dialect())) {
                for (int i = 0; i < columnFields.size(); i++) {
                    DataType<?> type = columnTypes.get(i);

                    if (EnumType.class.isAssignableFrom(type.getType())) {

                        @SuppressWarnings("unchecked")
                        DataType<EnumType> enumType = (DataType<EnumType>) type;

                        if (EMULATE_STORED_ENUM_TYPES_AS_CHECK.contains(ctx.dialect()) || !storedEnumType(enumType)) {
                            Field<?> field = columnFields.get(i);
                            List<Field<String>> literals = map(enums(enumType.getType()), e -> inline(e.getLiteral()));

                            ctx.sql(',')
                               .formatSeparator()
                               .visit(DSL.constraint(table.getName() + "_" + field.getName() + "_chk")
                                         .check(((Field) field).in(literals)));
                        }
                    }
                }
            }

            ctx.end(CREATE_TABLE_CONSTRAINTS);

            if (!indexes.isEmpty() && !EMULATE_INDEXES_IN_BLOCK.contains(ctx.dialect())) {
                ctx.qualify(false);

                for (Index index : indexes) {
                    ctx.sql(',').formatSeparator();

                    if (index.getUnique())
                        ctx.visit(K_UNIQUE).sql(' ');

                    ctx.visit(K_INDEX);

                    if (!"".equals(index.getName()))
                        ctx.sql(' ').visit(index.getUnqualifiedName());

                    ctx.sql(" (")
                       .visit(new SortFieldList(index.getFields()))
                       .sql(')');
                }

                ctx.qualify(qualify);
            }

            ctx.sqlIndentEnd(')');
        }
    }

    private final void toSQLDummyColumns(Context<?> ctx) {

        // [#10551] [#11268] TODO: Make this behaviour configurable
        if (REQUIRE_NON_PK_COLUMNS.contains(ctx.dialect())) {
            Field<?>[] primaryKeyColumns = primaryKeyColumns();

            if (primaryKeyColumns != null && primaryKeyColumns.length == columnFields.size()) {
                ctx.sql(',').formatSeparator();
                ctx.visit(field(name("dummy")));

                if (select == null) {
                    ctx.sql(' ');
                    Tools.toSQLDDLTypeDeclarationForAddition(ctx, INTEGER);
                }
            }
        }
    }

    private final DataType<?> columnType(Context<?> ctx, int i) {
        DataType<?> type = columnTypes.get(i);

        if (NO_SUPPORT_NULLABLE_PRIMARY_KEY.contains(ctx.dialect()) && type.nullability() == Nullability.DEFAULT && isPrimaryKey(i))
            type = type.nullable(false);

        return type;
    }

    private final Field<?>[] primaryKeyColumns() {
        return Tools.findAny(
            constraints,
            c -> c instanceof ConstraintImpl && ((ConstraintImpl) c).$primaryKey() != null,
            c -> ((ConstraintImpl) c).$primaryKey()
        );
    }

    private final boolean isPrimaryKey(int i) {
        return anyMatch(primaryKeyColumns(), columnFields.get(i)::equals);
    }

    private final boolean matchingPrimaryKey(Constraint constraint, Field<?> identity) {
        if (constraint instanceof ConstraintImpl)
            return ((ConstraintImpl) constraint).matchingPrimaryKey(identity);

        return false;
    }

    private final void acceptCreateTableAsSelect(Context<?> ctx) {
        toSQLCreateTable(ctx);
        toSQLOnCommit(ctx);
        ctx.formatSeparator()
           .visit(K_AS);

        if (WRAP_SELECT_IN_PARENS.contains(ctx.dialect()))
            ctx.sqlIndentStart(" (");
        else
            ctx.formatSeparator();

        if (FALSE.equals(withData) && NO_SUPPORT_WITH_DATA.contains(ctx.dialect()))
            ctx.data(DATA_SELECT_NO_DATA, true);

        ctx.start(CREATE_TABLE_AS);

        if (!columnFields.isEmpty() && NO_SUPPORT_CTAS_COLUMN_NAMES.contains(ctx.dialect()))
            ctx.visit(select(asterisk()).from(select.asTable(table(name("t")), columnFields.toArray(EMPTY_FIELD))));
        else
            ctx.visit(select);

        ctx.end(CREATE_TABLE_AS);

        if (FALSE.equals(withData) && NO_SUPPORT_WITH_DATA.contains(ctx.dialect()))
            ctx.data().remove(DATA_SELECT_NO_DATA);

        if (WRAP_SELECT_IN_PARENS.contains(ctx.dialect())) {
            ctx.sqlIndentEnd(')');
        }

        if (FALSE.equals(withData) && !NO_SUPPORT_WITH_DATA.contains(ctx.dialect()))
            ctx.formatSeparator()
               .visit(K_WITH_NO_DATA);
        else if (TRUE.equals(withData) && !NO_SUPPORT_WITH_DATA.contains(ctx.dialect()))
            ctx.formatSeparator()
               .visit(K_WITH_DATA);
        else if (REQUIRES_WITH_DATA.contains(ctx.dialect()))
            ctx.formatSeparator()
               .visit(K_WITH_DATA);
    }



































    private final void toSQLCreateTableName(Context<?> ctx) {
        ctx.start(CREATE_TABLE_NAME)
           .visit(K_CREATE)
           .sql(' ');

        if (temporary)
            if (SUPPORT_TEMPORARY.contains(ctx.dialect()))
                ctx.visit(K_TEMPORARY).sql(' ');
            else
                ctx.visit(K_GLOBAL_TEMPORARY).sql(' ');

        ctx.visit(K_TABLE)
           .sql(' ');

        if (ifNotExists && supportsIfNotExists(ctx))
            ctx.visit(K_IF_NOT_EXISTS)
               .sql(' ');

        ctx.visit(table)
           .end(CREATE_TABLE_NAME);
    }

    private final void toSQLOnCommit(Context<?> ctx) {
        if (temporary && onCommit != null) {
            switch (onCommit) {
                case DELETE_ROWS:   ctx.formatSeparator().visit(K_ON_COMMIT_DELETE_ROWS);   break;
                case PRESERVE_ROWS: ctx.formatSeparator().visit(K_ON_COMMIT_PRESERVE_ROWS); break;
                case DROP:          ctx.formatSeparator().visit(K_ON_COMMIT_DROP);          break;
            }
        }
    }




















}
