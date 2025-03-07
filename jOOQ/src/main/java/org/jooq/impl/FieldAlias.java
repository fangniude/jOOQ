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

import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jooq.Clause;
import org.jooq.Context;
import org.jooq.Field;
import org.jooq.Function1;
import org.jooq.Name;
import org.jooq.QueryPart;
import org.jooq.Traverser;

/**
 * @author Lukas Eder
 */
final class FieldAlias<T> extends AbstractField<T> implements QOM.FieldAlias<T> {

    private final Alias<Field<T>> alias;

    FieldAlias(Field<T> field, Name alias) {
        super(alias, field.getDataType());

        this.alias = new Alias<>(field, this, alias);
    }

    @Override
    public final void accept(Context<?> ctx) {
        ctx.visit(alias);
    }

    @Override // Avoid AbstractField implementation
    public final Clause[] clauses(Context<?> ctx) {
        return null;
    }

    @Override
    public final Field<T> as(Name as) {
        return alias.wrapped().as(as);
    }

    @Override
    public final boolean declaresFields() {
        return true;
    }

    @Override
    public Name getQualifiedName() {
        return getUnqualifiedName();
    }

    /**
     * Get the aliased field wrapped by this field.
     */
    Field<T> getAliasedField() {
        if (alias != null)
            return alias.wrapped();

        return null;
    }

    // -------------------------------------------------------------------------
    // XXX: Query Object Model
    // -------------------------------------------------------------------------

    @Override
    public final Field<T> $field() {
        return alias.wrapped();
    }

    @Override
    public final Name $alias() {
        return getQualifiedName();
    }

    @Override
    public final <R> R $traverse(Traverser<?, R> traverser) {
        return QOM.traverse(traverser, this, $field(), $alias());
    }

    @Override
    public final QueryPart $replace(
        Predicate<? super QueryPart> recurse,
        Function1<? super QueryPart, ? extends QueryPart> replacement
    ) {
        return QOM.replace(this, $field(), $alias(), FieldAlias::new, recurse, replacement);
    }
}
