package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventsInFavourites : Table("events_in_favourites") {
    val event_id = integer("event_id")
        .references(Events.id, onDelete = ReferenceOption.CASCADE)
    val user_favourite_id = integer("user_favourite_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}