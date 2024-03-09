package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.extenstions.any
import org.penakelex.database.models.*
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Events
import org.penakelex.database.tables.Users
import org.penakelex.response.Result
import java.time.Instant
import java.time.LocalDate

class EventsServiceImplementation : TableService(), EventsService {
    override suspend fun insertEvent(
        event: EventCreate,
        originatorID: Int,
        images: List<String>
    ): Result = databaseQuery {
        Events.insert {
            it[originator] = originatorID
            it[name] = event.name
            it[Events.images] = images.toTypedArray()
            it[description] = event.description
            it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            it[maximal_age] = event.maximalAge
            if (event.price != null) it[price] = event.price
        }.also {
            println("${it[Events.id]} - идентификатор события")
        }
        return@databaseQuery Result.OK
    }

    override suspend fun updateEvent(event: EventUpdate, organizerID: Int): Result = databaseQuery {
        Events.update(
            where = {
                Events.id.eq(event.eventID) and (
                        Events.originator.eq(organizerID) or Events.organizers.any(organizerID)
                )
            }
        ) {
            if (event.name != null) it[name] = event.name
            if (event.description != null) it[description] = event.description
            if (event.startTime != null) it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            if (event.maximalAge != null) it[maximal_age] = event.maximalAge
            if (event.price != null) it[price] = event.price
        }.also {
            println("Какое-то число, которое возвратило обновление таблицы: $it")
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteEvent(eventID: Int, originatorID: Int): Result = databaseQuery {
        Events.deleteWhere { Events.id.eq(eventID) and originator.eq(originatorID) }.also {
            println("Какое-то число, которое возвратило удаление из таблицы: $it")
        }
        return@databaseQuery Result.OK
    }

    override suspend fun getEvent(eventID: Int): Pair<Result, Event?> = databaseQuery {
        val event = Events.select {
            Events.id.eq(eventID)
        }.singleOrNull()?.let {
            resultRowToEvent(resultRow = it)
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND to null
        return@databaseQuery Result.OK to event
    }

    override suspend fun getUserEvents(
        id: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val events = Events.select {
            Events.participants.any(id).or(Events.in_favourites.any(id))
                .or(Events.organizers.any(id)).let {
                    if (actual) it and Events.start_time.greaterEq(Instant.now())
                    else if (aforetime) it and Events.start_time.less(Instant.now())
                    else it
                }
        }.orderBy(Events.start_time).map { resultRowToEvent(resultRow = it) }
        return@databaseQuery if (events.isEmpty()) Result.EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        else Result.OK to events
    }

    override suspend fun getFeaturedEvents(
        id: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val events = Events.select {
            Events.in_favourites.any(id).let {
                if (actual) it and Events.start_time.greaterEq(Instant.now())
                else if (aforetime) it and Events.start_time.less(Instant.now())
                else it
            }
        }.orderBy(Events.start_time).map { resultRowToEvent(resultRow = it) }
        return@databaseQuery if (events.isEmpty()) Result.FEATURED_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        else Result.OK to events
    }

    override suspend fun getParticipantEvents(
        id: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val events = Events.select {
            Events.participants.any(id).let {
                if (actual) it and Events.start_time.greaterEq(Instant.now())
                else if (aforetime) it and Events.start_time.less(Instant.now())
                else it
            }
        }.orderBy(Events.start_time).map { resultRowToEvent(resultRow = it) }
        return@databaseQuery if (events.isEmpty()) Result.PARTICIPANT_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        else Result.OK to events
    }

    override suspend fun getOrganizerEvents(
        id: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val events = Events.select {
            Events.organizers.any(id).let {
                if (actual) it and Events.start_time.greaterEq(Instant.now())
                else if (aforetime) it and Events.start_time.less(Instant.now())
                else it
            }
        }.orderBy(Events.start_time).map { resultRowToEvent(resultRow = it) }
        return@databaseQuery if (events.isEmpty()) Result.ORGANIZER_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        else Result.OK to events
    }

    override suspend fun addParticipantToEvent(userID: Int, eventID: Int): Result = databaseQuery {
        val (participants, organizers, eventRequirements) = Events.select {
            Events.id.eq(eventID)
        }.singleOrNull()?.let {
            Triple(
                it[Events.participants],
                it[Events.organizers],
                EventRequirements(
                    minimalAge = it[Events.minimal_age],
                    maximalAge = it[Events.maximal_age]
                )
            )
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        val userAge = Users.select { Users.id.eq(userID) }.singleOrNull()?.let {
            val dateOfBirth = it[Users.date_of_birth]
            val todayDate = LocalDate.now()
            if (todayDate.monthValue - dateOfBirth.monthValue > 0
                || todayDate.monthValue - dateOfBirth.monthValue == 0
                && todayDate.dayOfMonth - dateOfBirth.dayOfMonth >= 0
            ) todayDate.year - dateOfBirth.year
            else todayDate.year - dateOfBirth.year - 1
        } ?: return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        if (userAge !in eventRequirements.minimalAge..(eventRequirements.maximalAge ?: Short.MAX_VALUE)) {
            return@databaseQuery Result.YOUR_AGE_DOES_NOT_MATCH_THE_REQUIRED_BY_THE_EVENT_ORGANIZERS
        }
        Events.update({ Events.id.eq(eventID) }) {
            it[Events.participants] = participants.plus(userID).sortedArray()
            if (userID in organizers) {
                it[Events.organizers] = organizers.filter { id -> id != userID }.toTypedArray()
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun addOrganizerToEvent(adderID: Int, organizer: EventOrganizer): Result = databaseQuery {
        val (organizers, participants, originatorID) = Events.select {
            Events.id.eq(organizer.eventID)
        }.singleOrNull()?.let {
            Triple(
                it[Events.organizers],
                it[Events.participants],
                it[Events.originator]
            )
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        if (adderID != originatorID) return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_EVENT
        Users.select { Users.id.eq(organizer.addingID) }.singleOrNull()
            ?: return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        Events.update({ Events.id.eq(organizer.eventID) and Events.originator.eq(adderID) }) {
            it[Events.organizers] = organizers.plus(organizer.addingID).sortedArray()
            if (organizer.addingID in participants) {
                it[Events.participants] = participants.filter { id -> id != organizer.addingID }.toTypedArray()
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun addEventInFavourites(userID: Int, eventID: Int): Result = databaseQuery {
        val inFavourites = Events.select { Events.id.eq(eventID) }.singleOrNull()?.let {
            it[Events.in_favourites]
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        Events.update({ Events.id.eq(eventID) }) {
            it[in_favourites] = inFavourites.plus(userID).sortedArray()
        }
        return@databaseQuery Result.OK
    }

    override suspend fun getGlobalEvents(selection: EventSelection): Pair<Result, List<Event>> = databaseQuery {
        return@databaseQuery Result.OK to Events.select {
            selection.getSelectExpression()
        }.orderBy(
            Events.start_time to when (selection.sortBy) {
                EventSelection.SortingStates.NEAREST_ONES_FIRST.state -> SortOrder.ASC
                else -> SortOrder.DESC
            }
        ).map { resultRowToEvent(resultRow = it) }
    }

    override fun resultRowToEvent(resultRow: ResultRow) = Event(
        id = resultRow[Events.id].value,
        name = resultRow[Events.name],
        images = resultRow[Events.images].toList(),
        description = resultRow[Events.description],
        startTime = resultRow[Events.start_time],
        minimalAge = resultRow[Events.minimal_age],
        maximalAge = resultRow[Events.maximal_age],
        price = resultRow[Events.price],
        organizers = resultRow[Events.organizers].toList()
    )
}