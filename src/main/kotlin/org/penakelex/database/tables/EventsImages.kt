package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventsImages : Table("events_images") {
    val event_id = integer("event_id")
        .references(Events.id, onDelete = ReferenceOption.CASCADE)
    val image = text("image")
}