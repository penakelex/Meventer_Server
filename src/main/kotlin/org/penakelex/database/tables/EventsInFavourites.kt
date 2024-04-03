package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventsInFavourites : Table("in_favourites_events") {
    val event_id = integer("event_id")
        .references(Events.id, onDelete = ReferenceOption.CASCADE)
    val user_favourite_id = integer("user_favourite_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}