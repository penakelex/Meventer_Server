package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object ChatSessions : IntIdTable("sessions") {
    val user_id = integer("user_id").references(Users.id)
}