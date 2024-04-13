package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.timestamp

/**
 * Events table object
 * */
object Events : IntIdTable("events") {
    val name = text("name")
    val description = text("description")
    val start_time = timestamp("start_time")
    val chat_id = long("chat_id")
        .references(Chats.id, onDelete = ReferenceOption.CASCADE)
//        .uniqueIndex()
    val minimal_age = ushort("minimal_age")
        .default(0.toUShort())
    val maximal_age = ushort("maximal_age")
        .nullable()
    val price = integer("price")
        .default(0)
    //val place = text("place")
    //val coordinates = array<String>("coordinates", TextColumnType())
    val originator = integer("originator")
        .references(Users.id, onDelete = ReferenceOption.CASCADE)
}