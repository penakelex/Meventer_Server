package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.ResultRow
import org.penakelex.database.models.Event
import org.penakelex.database.models.EventCreate
import org.penakelex.database.models.EventSelection
import org.penakelex.response.Result

interface EventsService {
    suspend fun insertEvent(event: EventCreate, images: List<String>): Result
    suspend fun getUserEvents(
        id: Int,
        actual: Boolean = false,
        aforetime: Boolean = false
    ): Pair<Result, List<Event>?>

    suspend fun getFeaturedEvents(
        id: Int,
        actual: Boolean = false,
        aforetime: Boolean = false
    ): Pair<Result, List<Event>?>

    suspend fun getParticipantEvents(
        id: Int,
        actual: Boolean = false,
        aforetime: Boolean = false
    ): Pair<Result, List<Event>?>

    suspend fun getOrganizerEvents(
        id: Int,
        actual: Boolean = false,
        aforetime: Boolean = false
    ): Pair<Result, List<Event>?>

    suspend fun getGlobalEvents(selection: EventSelection): Pair<Result, List<Event>?>
    fun resultRowToEvent(resultRow: ResultRow): Event
}