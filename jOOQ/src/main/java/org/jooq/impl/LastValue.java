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

import static org.jooq.impl.Names.N_LAST_VALUE;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function1;
import org.jooq.QueryPart;
import org.jooq.Traverser;

/**
 * @author Lukas Eder
 */
final class LastValue<T> extends AbstractWindowFunction<T> implements QOM.LastValue<T> {

    final Field<T> field;

    LastValue(Field<T> field) {
        super(N_LAST_VALUE, field.getDataType().null_());

        this.field = field;
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {







            default:
                ctx.visit(N_LAST_VALUE).sql('(').visit(field);





                ctx.sql(')');
                acceptNullTreatment(ctx);
                break;
        }

        acceptOverClause(ctx);
    }

    // -------------------------------------------------------------------------
    // XXX: Query Object Model
    // -------------------------------------------------------------------------

    @Override
    public final Field<T> $field() {
        return field;
    }

    @Override
    public final <R> R $traverse(Traverser<?, R> traverser) {
        return QOM.traverse(traverser, this, field, $windowSpecification() != null ? $windowSpecification() : $windowDefinition());
    }

    @Override
    public final QueryPart $replace(
        Predicate<? super QueryPart> recurse,
        Function1<? super QueryPart, ? extends QueryPart> replacement
    ) {
        return QOM.replace(
            this,
            field, $windowSpecification(), $windowDefinition(),
            (f, s, d) -> new FirstValue<>(f).$windowSpecification(s).$windowDefinition(d).$nullTreatment(nullTreatment),
            recurse,
            replacement
        );
    }
}
