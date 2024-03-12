package org.penakelex.database.extenstions

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.jdbc.JdbcConnectionImpl
import org.jetbrains.exposed.sql.transactions.TransactionManager

fun <T> Table.array(name: String, columnType: ColumnType): Column<Array<T>> =
    registerColumn(name, ArrayColumnType(columnType))

class ArrayColumnType(private val type: ColumnType) : ColumnType() {

    override fun sqlType(): String = "${type.sqlType()} ARRAY"

    override fun valueToDB(value: Any?): Any? =
        if (value is Array<*>) {
            (TransactionManager.current().connection as JdbcConnectionImpl)
                .connection.createArrayOf(type.sqlType().split("(")[0], value)
        } else super.valueToDB(value)


    override fun valueFromDB(value: Any): Any {
        if (value is java.sql.Array) return value.array
        if (value is Array<*>) return value
        error("")
    }

    override fun notNullValueToDB(value: Any): Any {
        if (value is Array<*>) {
            if (value.isEmpty()) return "'{}'"
            return ((TransactionManager.current().connection as JdbcConnectionImpl).connection).createArrayOf(
                type.sqlType().split("(")[0], value
            ) ?: error("Can't create non null array for $value")
        } else return super.notNullValueToDB(value)
    }
}
