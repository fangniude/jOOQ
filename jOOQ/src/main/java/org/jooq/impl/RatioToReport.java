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

import static org.jooq.SQLDialect.SQLITE;
import static org.jooq.impl.Names.N_RATIO_TO_REPORT;
import static org.jooq.impl.SQLDataType.DECIMAL;
import static org.jooq.impl.SQLDataType.DOUBLE;
import static org.jooq.impl.Tools.castIfNeeded;

import java.math.BigDecimal;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.Function1;
import org.jooq.QueryPart;
import org.jooq.Traverser;

import org.jetbrains.annotations.NotNull;

/**
 * @author Lukas Eder
 */
final class RatioToReport
extends
    AbstractAggregateFunction<BigDecimal>
implements
    QOM.RatioToReport
{

    private final Field<? extends Number> field;

    RatioToReport(Field<? extends Number> field) {
        super(N_RATIO_TO_REPORT, DECIMAL, field);

        this.field = field;
    }

    @Override
    public final void accept(Context<?> ctx) {
        switch (ctx.family()) {
















            case CUBRID:

            case FIREBIRD:







            case MARIADB:
            case MYSQL:
            case POSTGRES:
            case SQLITE:
            case YUGABYTE:
                DataType<?> cast;

                switch (ctx.family()) {
                    case SQLITE:   cast = DOUBLE;          break;
                    case FIREBIRD: cast = DECIMAL(38, 19); break;
                    default:       cast = DECIMAL;         break;
                }

                ctx.visit(castIfNeeded(field, cast))
                   .sql(" / ")
                   .visit(DSL.sum(field));
                acceptOverClause(ctx);
                break;

            default:
                ctx.visit(N_RATIO_TO_REPORT).sql('(').visit(field).sql(')');
                acceptOverClause(ctx);
                break;
        }
    }

    // -------------------------------------------------------------------------
    // XXX: Query Object Model
    // -------------------------------------------------------------------------

    @Override
    public final Field<? extends Number> $field() {
        return field;
    }

    @Override
    public final <R> R $traverse(Traverser<?, R> traverser) {
        return QOM.traverse(traverser, this, field);
    }

    @Override
    public final QueryPart $replace(
        Predicate<? super QueryPart> recurse,
        Function1<? super QueryPart, ? extends QueryPart> replacement
    ) {
        return QOM.replace(this, field, RatioToReport::new, recurse, replacement);
    }
}
