package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.date

/**
 * Users table object
 * */
object Users : IntIdTable() {
    val email = text("email")
    val password = text("password")
    val nickname = text("nickname")
    val avatar = text("avatar")
    val date_of_birth = date("date_of_birth")
    val rating = float("rating").default(0f)
}