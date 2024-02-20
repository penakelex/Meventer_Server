package org.penakelex.database.services

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.penakelex.database.tables.Users

class Service(
    database: Database
) {
    init {
        transaction(database) {
            SchemaUtils.createSchema()
            SchemaUtils.create(Users)
        }
    }
}