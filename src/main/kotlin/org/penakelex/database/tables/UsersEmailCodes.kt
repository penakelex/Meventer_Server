package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

/**
 * UsersEmailCodes table object
 * */
object UsersEmailCodes : IntIdTable("users_email_codes") {
    val email = text("email")
    val code = varchar("code", 6)
    val expiration_time = long("expiration_time")
}