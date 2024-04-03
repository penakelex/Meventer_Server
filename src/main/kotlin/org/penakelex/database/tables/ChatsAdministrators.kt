package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ChatsAdministrators : Table("chats_administrators") {
    val chat_id = long("chat_id")
        .references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val administrator_id = integer("administrator_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}