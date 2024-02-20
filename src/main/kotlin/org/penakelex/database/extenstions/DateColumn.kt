package org.penakelex.database.extenstions

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import java.util.Date

fun Table.date(name: String): Column<Date> =
    registerColumn(name, DateColumnType())

class DateColumnType : ColumnType() {
    override fun sqlType() = "DATE"
}