package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersFeedback : LongIdTable("users_feedback") {
    val to_user_id = integer("to_user_id").references(Users.id)
    val from_user_id = integer("from_user_id").references(Users.id)
    val rating = float("rating")
    val comment = text("comment")
}