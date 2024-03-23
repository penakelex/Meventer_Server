package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.IntegerColumnType
import org.penakelex.database.extenstions.array

object Chats : LongIdTable("chats") {
    val name = text("name").nullable()
    val participants = array<Int>("participants", IntegerColumnType())
    val originator = integer("originator").references(Users.id)
        .nullable()
    val administrators = array<Int>("administrators", IntegerColumnType())
        .nullable()
    val open = bool("open").default(false)
}