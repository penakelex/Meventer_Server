package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Dialogs : Table("dialogs") {
    val id = long("id")
    val first = integer("first")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
    val second = integer("second")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}