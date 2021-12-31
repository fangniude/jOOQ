package org.jooq.util.sqlserver;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.UUID;
import org.jooq.DataType;
import org.jooq.SQLDialect;
import org.jooq.impl.DefaultDataType;
import org.jooq.impl.SQLDataType;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.jooq.types.UShort;



























































public class SQLServerDataType
{
    public static final DataType<UByte> TINYINT = (DataType<UByte>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TINYINTUNSIGNED, "tinyint");
    public static final DataType<Short> SMALLINT = (DataType<Short>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.SMALLINT, "smallint");
    public static final DataType<Integer> INT = (DataType<Integer>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.INTEGER, "int");
    public static final DataType<Integer> INT_IDENTITY = (DataType<Integer>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.INTEGER, "int identity");
    public static final DataType<Long> BIGINT = (DataType<Long>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BIGINT, "bigint");
    public static final DataType<Double> FLOAT = (DataType<Double>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.FLOAT, "float");
    public static final DataType<Float> REAL = (DataType<Float>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.REAL, "real");
    public static final DataType<BigDecimal> NUMERIC = (DataType<BigDecimal>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.NUMERIC, "numeric");
    public static final DataType<BigDecimal> DECIMAL = (DataType<BigDecimal>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.DECIMAL, "decimal");
    public static final DataType<Boolean> BIT = (DataType<Boolean>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BIT, "bit");
    public static final DataType<Date> DATE = (DataType<Date>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.DATE, "date");
    public static final DataType<Timestamp> DATETIME = (DataType<Timestamp>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TIMESTAMP, "datetime");
    public static final DataType<Time> TIME = (DataType<Time>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TIME, "time");
    public static final DataType<String> VARCHAR = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.VARCHAR, "varchar", "varchar(max)");
    public static final DataType<String> CHAR = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.CHAR, "char");
    public static final DataType<String> TEXT = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.CLOB, "text");
    public static final DataType<String> NVARCHAR = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.NVARCHAR, "nvarchar", "nvarchar(max)");
    public static final DataType<String> NCHAR = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.NCHAR, "nchar");
    public static final DataType<String> NTEXT = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.NCLOB, "ntext");
    public static final DataType<byte[]> VARBINARY = (DataType<byte[]>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.VARBINARY, "varbinary", "varbinary(max)");
    public static final DataType<byte[]> BINARY = (DataType<byte[]>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BINARY, "binary");





    protected static final DataType<byte[]> __BLOB = (DataType<byte[]>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BLOB, "binary");
    protected static final DataType<Boolean> __BOOLEAN = (DataType<Boolean>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BOOLEAN, "bit");
    protected static final DataType<Double> __DOUBLE = (DataType<Double>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.DOUBLE, "float");
    protected static final DataType<byte[]> __LONGVARBINARY = (DataType<byte[]>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.LONGVARBINARY, "varbinary", "varbinary(max)");
    protected static final DataType<String> __LONGVARCHAR = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.LONGVARCHAR, "varchar", "varchar(max)");
    protected static final DataType<String> __NCLOB = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.NCLOB, "text");
    protected static final DataType<String> __LONGNVARCHAR = (DataType<String>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.LONGNVARCHAR, "varchar", "varchar(max)");
    protected static final DataType<Byte> __BYTE = (DataType<Byte>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TINYINT, "signed tinyint", "tinyint");





    protected static final DataType<BigInteger> __BIGINTEGER = (DataType<BigInteger>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.DECIMAL_INTEGER, "numeric");
    protected static final DataType<UShort> __SMALLINTUNSIGNED = (DataType<UShort>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.SMALLINTUNSIGNED, "int");
    protected static final DataType<UInteger> __INTEGERUNSIGNED = (DataType<UInteger>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.INTEGERUNSIGNED, "bigint");
    protected static final DataType<ULong> __BIGINTUNSIGNED = (DataType<ULong>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BIGINTUNSIGNED, "numeric");





    public static final DataType<Timestamp> SMALLDATETIME = (DataType<Timestamp>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TIMESTAMP, "smalldatetime");
    public static final DataType<Timestamp> DATETIME2 = (DataType<Timestamp>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TIMESTAMP, "datetime2");
    public static final DataType<Timestamp> DATETIMEOFFSET = (DataType<Timestamp>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.TIMESTAMP, "datetimeoffset");
    public static final DataType<BigDecimal> MONEY = (DataType<BigDecimal>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.DECIMAL, "money");
    public static final DataType<BigDecimal> SMALLMONEY = (DataType<BigDecimal>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.DECIMAL, "smallmoney");
    public static final DataType<byte[]> IMAGE = (DataType<byte[]>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BINARY, "image");
    public static final DataType<UUID> UNIQUEIDENTIFIER = (DataType<UUID>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.UUID, "uniqueidentifier");
    public static final DataType<Long> ROWVERSION = (DataType<Long>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.BIGINT, "rowversion");
    public static final DataType<byte[]> TIMESTAMP = (DataType<byte[]>)new DefaultDataType(SQLDialect.SQLSERVER, SQLDataType.VARBINARY, "timestamp");
}