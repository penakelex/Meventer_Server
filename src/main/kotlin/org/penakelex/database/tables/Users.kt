package org.penakelex.database.tables

import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Table
import org.penakelex.database.extenstions.array
import org.penakelex.database.extenstions.date

object Users : Table() {
    val id = integer("id").autoIncrement()
    val email = text("email")
    val password = text("password")
    val nickname = text("nickname")
    val avatar = text("avatar")
    val date_of_birth = date("date_of_birth")
    val rating = float("rating").default(0f)
    val events = array<Int>("events", IntegerColumnType()).default(arrayOf())
    override val primaryKey = PrimaryKey(id)
}