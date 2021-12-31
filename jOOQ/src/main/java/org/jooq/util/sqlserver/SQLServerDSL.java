package org.jooq.util.sqlserver;

import org.jooq.Field;
import org.jooq.QueryPart;
import org.jooq.SQLDialect;
import org.jooq.Support;
import org.jooq.impl.DSL;

























































public class SQLServerDSL
        extends DSL
{
    @Support({SQLDialect.SQLSERVER})
    public static Field<String> soundex(String string) {
        return soundex((Field<String>)val(string, String.class));
    }






    @Support({SQLDialect.SQLSERVER})
    public static Field<String> soundex(Field<String> field) {
        return field("{soundex}({0})", String.class, new QueryPart[] { (QueryPart)nullSafe(field) });
    }






    @Support({SQLDialect.SQLSERVER})
    public static Field<Integer> difference(String value1, String value2) {
        return difference((Field<String>)val(value1, String.class), (Field<String>)val(value2, String.class));
    }






    @Support({SQLDialect.SQLSERVER})
    public static Field<Integer> difference(Field<String> value1, String value2) {
        return difference(nullSafe(value1), (Field<String>)val(value2, String.class));
    }






    @Support({SQLDialect.SQLSERVER})
    public static Field<Integer> difference(String value1, Field<String> value2) {
        return difference((Field<String>)val(value1, String.class), nullSafe(value2));
    }






    @Support({SQLDialect.SQLSERVER})
    public static Field<Integer> difference(Field<String> value1, Field<String> value2) {
        return field("{difference}({0}, {1})", Integer.class, new QueryPart[] { (QueryPart)nullSafe(value1), (QueryPart)nullSafe(value2) });
    }
}