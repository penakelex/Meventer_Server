package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object ChatsParticipants : Table("chats_participants") {
    val chat_id = long("chat_id")
        .references(Chats.id, onDelete = ReferenceOption.CASCADE)
    val participant_id = integer("participant_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}