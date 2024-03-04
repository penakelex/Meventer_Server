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
    val minimalRating: Float,
    val price: Int,
    val organizers: List<Int>
)

@Serializable
data class EventCreate(
    val name: String,
    val description: String,
    val startTime: Instant,
    val minimalAge: Short?,
    val maximalAge: Short?,
    val minimalRating: Float?,
    val price: Int?,
    val organizers: List<Int>
)

@Serializable
data class EventSelection(
    val tags: List<String>?,
    val age: Short?,
    val rating: Float?,
    val minimalPrice: Int?,
    val maximalPrice: Int?,
    val sortBy: String
) {
    enum class SortingStates(val state: String) {
        NEAREST_ONES_FIRST("Nearest ones first"),
        FURTHER_ONES_FIRST("Further ones first")
    }
}