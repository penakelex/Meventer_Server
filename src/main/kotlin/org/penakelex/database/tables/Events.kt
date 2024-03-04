package org.penakelex.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.TextColumnType
import org.jetbrains.exposed.sql.javatime.timestamp
import org.penakelex.database.extenstions.array

object Events : IntIdTable() {
    val name = text("name")
    val images = array<String>("images", TextColumnType())
    val description = text("description")
    val start_time = timestamp("start_time")
    val minimal_age = short("minimal_age").default(0)
    val maximal_age = short("maximum_age").nullable()
    val minimal_rating = float("minimal_rating").default(0f)
    val price = integer("price").default(0)
    val participants = array<Int>("participants", IntegerColumnType())
        .default(arrayOf())
    val organizers = array<Int>("organizers", IntegerColumnType())
    val in_favourites = array<Int>("in_favourites", IntegerColumnType())
        .default(arrayOf())
}