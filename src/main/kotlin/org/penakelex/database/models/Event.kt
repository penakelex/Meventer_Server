@file:UseSerializers(InstantSerializer::class)

package org.penakelex.database.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.penakelex.database.extenstions.InstantSerializer
import java.time.Instant

@Serializable
data class Event(
    val id: Int,
    val name: String,
    val images: List<String>,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short,
    val maximalAge: Short?,
    val price: Int,
    //TODO: Add originator, cause he isn`t in organizers
    val organizers: List<Int>
)

@Serializable
data class EventCreate(
    val name: String,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?
)

@Serializable
data class EventSelection(
    val tags: List<String>?,
    val age: Short?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String?
) {
    enum class SortingStates(val state: String) {
        NEAREST_ONES_FIRST("Nearest ones first"),
        FURTHER_ONES_FIRST("Further ones first")
    }
}

@Serializable
data class EventOrganizer(
    val eventID: Int,
    val addingID: Int
)

@Serializable
data class EventsGet(
    val userID: Int?,
    val actual: Boolean?,
    val aforetime: Boolean?,
    val type: String?
)

@Serializable
data class EventUpdate(
    val eventID: Int,
    val name: String?,
    val description: String?,
    val startTime: Instant?,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?
)

data class EventRequirements(
    val minimalAge: Short,
    val maximalAge: Short?
)