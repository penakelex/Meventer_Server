package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventsOrganizers : Table("events_organizers") {
    val event_id = integer("event_id")
        .references(Events.id, onDelete = ReferenceOption.CASCADE)
    val organizer_id = integer("organizer_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}