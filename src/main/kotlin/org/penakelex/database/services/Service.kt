package org.penakelex.database.services

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.penakelex.database.services.events.EventsService
import org.penakelex.database.services.users.UsersService
import org.penakelex.database.services.usersEmailCodes.UsersEmailCodesService
import org.penakelex.database.services.usersFeedback.UsersFeedbackService
import org.penakelex.database.tables.Events
import org.penakelex.database.tables.Users
import org.penakelex.database.tables.UsersEmailCodes
import org.penakelex.database.tables.UsersFeedback

/**
 * Container class for database table services
 * @property usersService Users table service
 * @property usersEmailCodesService UsersEmailCodes table service
 * @param database exemplar of the database to initialize its schema and tables
 * */
class Service(
    val usersService: UsersService,
    val usersEmailCodesService: UsersEmailCodesService,
    val eventsService: EventsService,
    val usersFeedbackService: UsersFeedbackService,
    database: Database
) {
    init {
        transaction(database) {
            SchemaUtils.createSchema()
            SchemaUtils.create(Users, UsersEmailCodes, Events, UsersFeedback)
        }
    }
}