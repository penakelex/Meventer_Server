package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

/**
 * UsersFeedback table object
 * */
object UsersFeedback : LongIdTable("users_feedback") {
    val to_user_id = integer("to_user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val from_user_id = integer("from_user_id").references(Users.id)
    val rating = float("rating")
    val comment = text("comment")
}