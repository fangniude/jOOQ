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

import static org.jooq.impl.DSL.row;

import java.util.Arrays;
import java.util.Collection;

import org.jooq.BetweenAndStep11;
import org.jooq.Comparator;
import org.jooq.Condition;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function11;
import org.jooq.QuantifiedSelect;
import org.jooq.Record;
import org.jooq.Record11;
import org.jooq.Records;
import org.jooq.Result;
import org.jooq.Row;
import org.jooq.Row11;
import org.jooq.Select;
import org.jooq.SelectField;
import org.jooq.Statement;

import org.jetbrains.annotations.NotNull;

/**
 * @author Lukas Eder
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
final class RowImpl11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> extends AbstractRow<Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> implements Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> {

    RowImpl11(SelectField<T1> field1, SelectField<T2> field2, SelectField<T3> field3, SelectField<T4> field4, SelectField<T5> field5, SelectField<T6> field6, SelectField<T7> field7, SelectField<T8> field8, SelectField<T9> field9, SelectField<T10> field10, SelectField<T11> field11) {
        super(field1, field2, field3, field4, field5, field6, field7, field8, field9, field10, field11);
    }

    RowImpl11(FieldsImpl<?> fields) {
        super((FieldsImpl) fields);
    }

    // ------------------------------------------------------------------------
    // Mapping convenience methods
    // ------------------------------------------------------------------------

    @Override
    public final <U> SelectField<U> mapping(Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function) {
        return rf().convertFrom(Records.mapping(function));
    }

    @Override
    public final <U> SelectField<U> mapping(Class<U> uType, Function11<? super T1, ? super T2, ? super T3, ? super T4, ? super T5, ? super T6, ? super T7, ? super T8, ? super T9, ? super T10, ? super T11, ? extends U> function) {
        return rf().convertFrom(uType, Records.mapping(function));
    }

    // ------------------------------------------------------------------------
    // XXX: Row accessor API
    // ------------------------------------------------------------------------

    @Override
    public final Field<T1> field1() {
        return (@NotNull Field<T1>) fields.field(0);
    }

    @Override
    public final Field<T2> field2() {
        return (@NotNull Field<T2>) fields.field(1);
    }

    @Override
    public final Field<T3> field3() {
        return (@NotNull Field<T3>) fields.field(2);
    }

    @Override
    public final Field<T4> field4() {
        return (@NotNull Field<T4>) fields.field(3);
    }

    @Override
    public final Field<T5> field5() {
        return (@NotNull Field<T5>) fields.field(4);
    }

    @Override
    public final Field<T6> field6() {
        return (@NotNull Field<T6>) fields.field(5);
    }

    @Override
    public final Field<T7> field7() {
        return (@NotNull Field<T7>) fields.field(6);
    }

    @Override
    public final Field<T8> field8() {
        return (@NotNull Field<T8>) fields.field(7);
    }

    @Override
    public final Field<T9> field9() {
        return (@NotNull Field<T9>) fields.field(8);
    }

    @Override
    public final Field<T10> field10() {
        return (@NotNull Field<T10>) fields.field(9);
    }

    @Override
    public final Field<T11> field11() {
        return (@NotNull Field<T11>) fields.field(10);
    }

    // ------------------------------------------------------------------------
    // Generic comparison predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition compare(Comparator comparator, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowCondition(this, row, comparator);
    }

    @Override
    public final Condition compare(Comparator comparator, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return new RowCondition(this, record.valuesRow(), comparator);
    }

    @Override
    public final Condition compare(Comparator comparator, T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(comparator, row(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10))));
    }

    @Override
    public final Condition compare(Comparator comparator, Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(comparator, row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final Condition compare(Comparator comparator, Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    @Override
    public final Condition compare(Comparator comparator, QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowSubqueryCondition(this, select, comparator);
    }

    // ------------------------------------------------------------------------
    // Equal / Not equal comparison predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition equal(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.EQUALS, row);
    }

    @Override
    public final Condition equal(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.EQUALS, record);
    }

    @Override
    public final Condition equal(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition equal(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition eq(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return equal(row);
    }

    @Override
    public final Condition eq(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return equal(record);
    }

    @Override
    public final Condition eq(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return equal(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition eq(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return equal(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition notEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.NOT_EQUALS, row);
    }

    @Override
    public final Condition notEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.NOT_EQUALS, record);
    }

    @Override
    public final Condition notEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.NOT_EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition notEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.NOT_EQUALS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition ne(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return notEqual(row);
    }

    @Override
    public final Condition ne(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return notEqual(record);
    }

    @Override
    public final Condition ne(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return notEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition ne(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return notEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    // ------------------------------------------------------------------------
    // Ordering comparison predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition lessThan(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.LESS, row);
    }

    @Override
    public final Condition lessThan(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.LESS, record);
    }

    @Override
    public final Condition lessThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.LESS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition lessThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.LESS, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition lt(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return lessThan(row);
    }

    @Override
    public final Condition lt(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return lessThan(record);
    }

    @Override
    public final Condition lt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return lessThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition lt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return lessThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition lessOrEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.LESS_OR_EQUAL, row);
    }

    @Override
    public final Condition lessOrEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.LESS_OR_EQUAL, record);
    }

    @Override
    public final Condition lessOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.LESS_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition lessOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.LESS_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition le(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return lessOrEqual(row);
    }

    @Override
    public final Condition le(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return lessOrEqual(record);
    }

    @Override
    public final Condition le(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return lessOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition le(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return lessOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition greaterThan(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.GREATER, row);
    }

    @Override
    public final Condition greaterThan(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.GREATER, record);
    }

    @Override
    public final Condition greaterThan(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.GREATER, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition greaterThan(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.GREATER, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition gt(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return greaterThan(row);
    }

    @Override
    public final Condition gt(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return greaterThan(record);
    }

    @Override
    public final Condition gt(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return greaterThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition gt(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return greaterThan(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition greaterOrEqual(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return compare(Comparator.GREATER_OR_EQUAL, row);
    }

    @Override
    public final Condition greaterOrEqual(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return compare(Comparator.GREATER_OR_EQUAL, record);
    }

    @Override
    public final Condition greaterOrEqual(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return compare(Comparator.GREATER_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition greaterOrEqual(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return compare(Comparator.GREATER_OR_EQUAL, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition ge(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return greaterOrEqual(row);
    }

    @Override
    public final Condition ge(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return greaterOrEqual(record);
    }

    @Override
    public final Condition ge(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return greaterOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    @Override
    public final Condition ge(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return greaterOrEqual(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11);
    }

    // ------------------------------------------------------------------------
    // [NOT] BETWEEN predicates
    // ------------------------------------------------------------------------

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return between(row(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10))));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return between(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition<>(this, row, false, false);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> between(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return between(record.valuesRow());
    }

    @Override
    public final Condition between(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override
    public final Condition between(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return between(minValue).and(maxValue);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return betweenSymmetric(row(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10))));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return betweenSymmetric(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition<>(this, row, false, true);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> betweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return betweenSymmetric(record.valuesRow());
    }

    @Override
    public final Condition betweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override
    public final Condition betweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return betweenSymmetric(minValue).and(maxValue);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return notBetween(row(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10))));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return notBetween(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition<>(this, row, true, false);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetween(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return notBetween(record.valuesRow());
    }

    @Override
    public final Condition notBetween(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override
    public final Condition notBetween(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetween(minValue).and(maxValue);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return notBetweenSymmetric(row(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10))));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return notBetweenSymmetric(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowBetweenCondition<>(this, row, true, true);
    }

    @Override
    public final BetweenAndStep11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> notBetweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return notBetweenSymmetric(record.valuesRow());
    }

    @Override
    public final Condition notBetweenSymmetric(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    @Override
    public final Condition notBetweenSymmetric(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> minValue, Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> maxValue) {
        return notBetweenSymmetric(minValue).and(maxValue);
    }

    // ------------------------------------------------------------------------
    // [NOT] DISTINCT predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition isNotDistinctFrom(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowIsDistinctFrom(this, row, true);
    }

    @Override
    public final Condition isNotDistinctFrom(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return isNotDistinctFrom(record.valuesRow());
    }

    @Override
    public final Condition isNotDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return isNotDistinctFrom(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10)));
    }

    @Override
    public final Condition isNotDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return isNotDistinctFrom(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final Condition isNotDistinctFrom(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowIsDistinctFrom(this, select, true);
    }

    @Override
    public final Condition isDistinctFrom(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> row) {
        return new RowIsDistinctFrom(this, row, false);
    }

    @Override
    public final Condition isDistinctFrom(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> record) {
        return isDistinctFrom(record.valuesRow());
    }

    @Override
    public final Condition isDistinctFrom(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8, T9 t9, T10 t10, T11 t11) {
        return isDistinctFrom(Tools.field(t1, (DataType) dataType(0)), Tools.field(t2, (DataType) dataType(1)), Tools.field(t3, (DataType) dataType(2)), Tools.field(t4, (DataType) dataType(3)), Tools.field(t5, (DataType) dataType(4)), Tools.field(t6, (DataType) dataType(5)), Tools.field(t7, (DataType) dataType(6)), Tools.field(t8, (DataType) dataType(7)), Tools.field(t9, (DataType) dataType(8)), Tools.field(t10, (DataType) dataType(9)), Tools.field(t11, (DataType) dataType(10)));
    }

    @Override
    public final Condition isDistinctFrom(Field<T1> t1, Field<T2> t2, Field<T3> t3, Field<T4> t4, Field<T5> t5, Field<T6> t6, Field<T7> t7, Field<T8> t8, Field<T9> t9, Field<T10> t10, Field<T11> t11) {
        return isDistinctFrom(row(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11));
    }

    @Override
    public final Condition isDistinctFrom(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return new RowIsDistinctFrom(this, select, false);
    }

    // ------------------------------------------------------------------------
    // [NOT] IN predicates
    // ------------------------------------------------------------------------

    @Override
    public final Condition in(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... rows) {
        return in(Arrays.asList(rows));
    }

    @Override
    public final Condition in(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();

        for (Record record : records)
            rows.add(record.valuesRow());

        return new RowInCondition(this, rows, false);
    }

    @Override
    public final Condition notIn(Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... rows) {
        return notIn(Arrays.asList(rows));
    }

    @Override
    public final Condition notIn(Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>... records) {
        QueryPartList<Row> rows = new QueryPartList<>();

        for (Record record : records)
            rows.add(record.valuesRow());

        return new RowInCondition(this, rows, true);
    }

    @Override
    public final Condition in(Collection<? extends Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> rows) {
        return new RowInCondition(this, new QueryPartList<Row>(rows), false);
    }

    @Override
    public final Condition in(Result<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> result) {
        return new RowInCondition(this, new QueryPartList<Row>(Tools.rows(result)), false);
    }

    @Override
    public final Condition notIn(Collection<? extends Row11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> rows) {
        return new RowInCondition(this, new QueryPartList<Row>(rows), true);
    }

    @Override
    public final Condition notIn(Result<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> result) {
        return new RowInCondition(this, new QueryPartList<Row>(Tools.rows(result)), true);
    }

    // ------------------------------------------------------------------------
    // Predicates involving subqueries
    // ------------------------------------------------------------------------

    @Override
    public final Condition equal(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override
    public final Condition equal(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.EQUALS, select);
    }

    @Override
    public final Condition eq(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return equal(select);
    }

    @Override
    public final Condition eq(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return equal(select);
    }

    @Override
    public final Condition notEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override
    public final Condition notEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.NOT_EQUALS, select);
    }

    @Override
    public final Condition ne(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return notEqual(select);
    }

    @Override
    public final Condition ne(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return notEqual(select);
    }

    @Override
    public final Condition greaterThan(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override
    public final Condition greaterThan(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER, select);
    }

    @Override
    public final Condition gt(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterThan(select);
    }

    @Override
    public final Condition gt(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterThan(select);
    }

    @Override
    public final Condition greaterOrEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override
    public final Condition greaterOrEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.GREATER_OR_EQUAL, select);
    }

    @Override
    public final Condition ge(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterOrEqual(select);
    }

    @Override
    public final Condition ge(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return greaterOrEqual(select);
    }

    @Override
    public final Condition lessThan(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override
    public final Condition lessThan(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS, select);
    }

    @Override
    public final Condition lt(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessThan(select);
    }

    @Override
    public final Condition lt(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessThan(select);
    }

    @Override
    public final Condition lessOrEqual(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override
    public final Condition lessOrEqual(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.LESS_OR_EQUAL, select);
    }

    @Override
    public final Condition le(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessOrEqual(select);
    }

    @Override
    public final Condition le(QuantifiedSelect<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return lessOrEqual(select);
    }

    @Override
    public final Condition in(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.IN, select);
    }

    @Override
    public final Condition notIn(Select<? extends Record11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>> select) {
        return compare(Comparator.NOT_IN, select);
    }

































}
