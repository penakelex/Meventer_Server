package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventsParticipants : Table("events_participants") {
    val event_id = integer("event_id")
        .references(Events.id, onDelete = ReferenceOption.CASCADE)
    val participant_id = integer("participant_id")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}