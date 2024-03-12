@file:UseSerializers(InstantSerializer::class)

package org.penakelex.database.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.penakelex.database.extenstions.InstantSerializer
import java.time.Instant

/**
 * Data transfer object for event
 * @property id event ID
 * @property name event name
 * @property images list of images names
 * @property description event description
 * @property startTime event start time
 * @property minimalAge minimal age required by organizers
 * @property maximalAge maximal age required by organizers
 * @property price price to pay on the event
 * @property originator originator ID of the event
 * @property organizers list of IDs of the event organizers
 * */
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
    val originator: Int,
    val organizers: List<Int>
)

/**
 * Data transfer object for creating event
 * @property name creating event name
 * @property description creating event description
 * @property startTime creating event start time
 * @property minimalAge creating event permitted minimal age
 * @property maximalAge creating event permitted maximal age
 * @property price creating event price
 * */
@Serializable
data class EventCreate(
    val name: String,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val price: Int?
)

/**
 * Data transfer object for selecting events
 * @property tags list of tags
 * @property age permitted age by event
 * @property minimalPrice minimal price
 * @property maximalPrice maximal price
 * @property sortBy selected events sorting key
 * */
@Serializable
data class EventSelection(
    val tags: List<String>?,
    val age: Short?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String?
) {
    /**
     * Values for sorting key for events selection
     * */
    enum class SortingStates(val state: String) {
        NEAREST_ONES_FIRST("Nearest ones first"),
        FURTHER_ONES_FIRST("Further ones first")
    }
}

/**
 * Data transfer object for changing user status as organizer
 * @property eventID event ID
 * @property changingID user ID to change his status
 * */
@Serializable
data class EventOrganizer(
    val eventID: Int,
    val changingID: Int
)

/**
 * Data transfer object for getting events for user
 * @property userID user ID to get his events
 * @property actual if true then request responds actual events
 * @property aforetime if true then request responds aforetime events
 * @property type type of getting events
 * */
@Serializable
data class EventsGet(
    val userID: Int?,
    val actual: Boolean?,
    val aforetime: Boolean?,
    val type: String?
)

/**
 * Data transfer object for updating event
 * @property eventID event ID
 * @property name event name to update
 * @property description event description to update
 * @property startTime event start time to change
 * @property minimalAge event permitted minimal age to change
 * @property maximalAge event permitted maximal age to change
 * @property price event price to change
 * */
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

/**
 * Data transfer object for event requirements
 * @property minimalAge minimal permitted age
 * @property maximalAge maximal permitted age
 * */
data class EventRequirements(
    val minimalAge: Short,
    val maximalAge: Short?
)