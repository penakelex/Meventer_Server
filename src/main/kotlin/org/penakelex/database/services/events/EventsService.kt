package org.penakelex.database.services.events

import org.penakelex.database.models.*
import org.penakelex.response.Result

interface EventsService {
    suspend fun insertEvent(event: EventCreate, originatorID: Int, images: List<String>, chatID: Long): Result
    suspend fun updateEvent(event: EventUpdate, organizerID: Int, newImages: List<String>): Result
    suspend fun deleteEvent(eventID: Int, originatorID: Int): Result
    suspend fun getEvent(eventID: Int): Pair<Result, Event?>
    suspend fun getUserEvents(userID: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getInFavouritesEvents(userID: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getParticipantEvents(userID: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getOrganizerEvents(userID: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun getOriginatorEvents(userID: Int, actual: Boolean, aforetime: Boolean): Pair<Result, List<Event>?>
    suspend fun changeUserAsParticipant(changingID: Int, eventID: Int, changerID: Int): Pair<Result, Long?>
    suspend fun changeUserAsOrganizer(changerID: Int, organizer: EventOrganizer): Result
    suspend fun changeEventInFavourites(userID: Int, eventID: Int): Result
    suspend fun getGlobalEvents(selection: EventSelection): Pair<Result, List<Event>?>
}