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

import static org.jooq.impl.DSL.name;
import static org.jooq.impl.Keywords.K_TABLE;
import static org.jooq.impl.Keywords.K_UNNEST;
import static org.jooq.impl.Names.N_ARRAY_TABLE;
import static org.jooq.impl.Names.N_COLUMN_VALUE;
import static org.jooq.impl.Tools.map;

import java.util.ArrayList;
import java.util.List;

// ...
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Param;
// ...
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableOptions;
import org.jooq.UDTRecord;
import org.jooq.exception.DataTypeException;
import org.jooq.impl.QOM.UNotYetImplemented;
import org.jooq.impl.QOM.UTransient;
import org.jooq.util.h2.H2DataType;

/**
 * An unnested array
 *
 * @author Lukas Eder
 */
final class ArrayTable extends AbstractTable<Record> implements UNotYetImplemented {

    private final Field<?>           array;
    private final FieldsImpl<Record> field;
    private final Name               alias;
    private final Name[]             fieldAliases;

    ArrayTable(Field<?> array) {
        this(array, N_ARRAY_TABLE);
    }

    ArrayTable(Field<?> array, Name alias) {

        // [#7863] TODO: Possibly resolve field aliases from UDT type
        this(array, alias, new Name[] { N_COLUMN_VALUE });
    }

    @SuppressWarnings({ "unchecked" })
    ArrayTable(Field<?> array, Name alias, Name[] fieldAliases) {
        super(TableOptions.expression(), alias);

        Class<?> arrayType;

        if (array.getDataType().getType().isArray()) {
            arrayType = array.getDataType().getArrayComponentType();
        }
















        // Is this case possible?
        else
            arrayType = Object.class;

        this.array = array;
        this.alias = alias;
        this.fieldAliases = fieldAliases;
        this.field = init(arrayType, alias);
    }

    private static final FieldsImpl<Record> init(Class<?> arrayType, Name alias) {

        // [#1114] [#7863] VARRAY/TABLE of OBJECT have more than one field
        if (Record.class.isAssignableFrom(arrayType)) {
            try {
                return new FieldsImpl<>(map(
                    ((Record) arrayType.getDeclaredConstructor().newInstance()).fields(),
                    f -> DSL.field(name(alias.last(), f.getName()), f.getDataType())
                ));
            }
            catch (Exception e) {
                throw new DataTypeException("Bad UDT Type : " + arrayType, e);
            }
        }

        // Simple array types have a synthetic field called "COLUMN_VALUE"
        else
            return new FieldsImpl<>(DSL.field(name(alias.last(), "COLUMN_VALUE"), DSL.getDataType(arrayType)));
    }

    @Override
    public final Class<? extends Record> getRecordType() {
        return RecordImplN.class;
    }

    @Override
    public final Table<Record> as(Name as) {
        return new ArrayTable(array, as);
    }

    @Override
    public final Table<Record> as(Name as, Name... fields) {
        return new ArrayTable(array, as, fields);
    }

    @Override
    public final boolean declaresTables() {

        // Always true, because unnested tables are always aliased
        return true;
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(table(ctx.configuration()));
    }

    private final Table<Record> table(Configuration configuration) {
        switch (configuration.family()) {








            case H2:
                return new H2ArrayTable().as(alias);

            // [#756] These dialects need special care when aliasing unnested
            // arrays



            case HSQLDB:
            case POSTGRES:
            case YUGABYTE:
                return new PostgresHSQLDBTable().as(alias, fieldAliases);

            // Other dialects can simulate unnested arrays using UNION ALL
            default:
                if (array.getDataType().getType().isArray() && array instanceof Param)
                    return emulate();








                else
                    return DSL.table("{0}", array).as(alias);
        }
    }

    private class PostgresHSQLDBTable extends DialectArrayTable {

        @Override
        public final void accept(Context<?> ctx) {
            ctx.visit(K_UNNEST).sql('(').visit(array).sql(")");
        }
    }

    private class H2ArrayTable extends DialectArrayTable {

        @Override
        public final void accept(Context<?> ctx) {
            ctx.visit(K_TABLE)
               .sql('(')
               .visit(fieldAliases == null || fieldAliases.length == 0 ? N_COLUMN_VALUE : fieldAliases[0])
               .sql(' ');

            // If the array type is unknown (e.g. because it's returned from
            // a stored function), then a reasonable choice for arbitrary types is varchar
            if (array.getDataType().getType() == Object[].class)
                ctx.sql(H2DataType.VARCHAR.getTypeName());
            else
                ctx.sql(array.getDataType().getTypeName());

            ctx.sql(" = ").visit(array).sql(')');
        }
    }












    private abstract class DialectArrayTable extends AbstractTable<Record> implements UTransient {

        DialectArrayTable() {
            super(TableOptions.expression(), alias);
        }

        @Override
        public final Class<? extends Record> getRecordType() {
            return RecordImplN.class;
        }

        @Override
        final FieldsImpl<Record> fields0() {
            return ArrayTable.this.fields0();
        }
    }

    @SuppressWarnings("unchecked")
    private final Table<Record> emulate() {
        return new ArrayTableEmulation(((Param<Object[]>) array).getValue()).as(alias, fieldAliases);
    }

    @Override
    final FieldsImpl<Record> fields0() {
        return field;
    }
}
