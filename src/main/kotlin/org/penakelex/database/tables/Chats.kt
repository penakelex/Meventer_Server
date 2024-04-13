package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Chats : Table("chats") {
    val id = long("id")
    val name = text("name")
    val originator = integer("originator")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
    val open = bool("open").default(false)
    override val primaryKey = PrimaryKey(id)
}