package org.penakelex.database.tables

import org.jetbrains.exposed.sql.Table

/**
 * UsersEmailCodes table object
 * */
object UsersEmailCodes : Table("users_email_codes") {
    val email = text("email")
    val code = varchar("code", 6)
    val expiration_time = long("expiration_time")
}