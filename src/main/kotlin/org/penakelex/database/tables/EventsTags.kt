package org.penakelex.database.tables

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EventsTags : Table("events_tags") {
    val tag = text("tag")
    val event_id = integer("event_id")
        .references(Events.id, onDelete = ReferenceOption.CASCADE)
}