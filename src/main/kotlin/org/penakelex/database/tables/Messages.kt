package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Messages : LongIdTable("messages") {
    val chat_id = long("chat_id").references(Chats.id)
    val sender_id = integer("sender_id").references(Users.id)
    val body = text("body")
    val timestamp = timestamp("timestamp")
}