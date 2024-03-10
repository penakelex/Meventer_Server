package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.ResultRow
import org.penakelex.database.models.*
import org.penakelex.response.Result

interface EventsService {
    suspend fun insertEvent(event: EventCreate, originatorID: Int, images: List<String>): Result
    suspend fun updateEvent(event: EventUpdate, organizerID: Int): Result
    suspend fun deleteEvent(eventID: Int, originatorID: Int): Result
    suspend fun getEvent(eventID: Int): Pair<Result, Event?>
    suspend fun getUserEvents(id: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getFeaturedEvents(id: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getParticipantEvents(id: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getOrganizerEvents(id: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getOriginatorEvents(id: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun changeUserAsParticipant(userID: Int, eventID: Int): Result
    suspend fun changeUserAsOrganizer(adderID: Int, organizer: EventOrganizer): Result
    suspend fun changeEventInFavourites(userID: Int, eventID: Int): Result
    suspend fun getGlobalEvents(selection: EventSelection): Pair<Result, List<Event>?>
    fun resultRowToEvent(resultRow: ResultRow): Event
}