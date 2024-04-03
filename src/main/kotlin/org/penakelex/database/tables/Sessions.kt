package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Sessions : IntIdTable("sessions") {
    val user_id = integer("user_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
    val end_of_validity = long("end_of_validity")
}