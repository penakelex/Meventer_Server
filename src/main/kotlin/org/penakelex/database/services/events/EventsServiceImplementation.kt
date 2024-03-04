package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.*
import org.penakelex.database.extenstions.any
import org.penakelex.database.models.Event
import org.penakelex.database.models.EventCreate
import org.penakelex.database.models.EventSelection
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Events
import org.penakelex.response.Result
import java.time.Instant

class EventsServiceImplementation : TableService(), EventsService {
    override suspend fun insertEvent(event: EventCreate, images: List<String>): Result = databaseQuery {
        Events.insert {
            it[name] = event.name
            it[Events.images] = images.toTypedArray()
            it[description] = event.description
            it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            it[maximal_age] = event.maximalAge
            if (event.minimalRating != null) it[minimal_rating] = event.minimalRating
            if (event.price != null) it[price] = event.price
            it[organizers] = event.organizers.toTypedArray()
        }.also {
            println(it[Events.id])
        }
        return@databaseQuery Result.OK
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

    override fun resultRowToEvent(resultRow: ResultRow) = Event(
        id = resultRow[Events.id].value,
        name = resultRow[Events.name],
        images = resultRow[Events.images].toList(),
        description = resultRow[Events.description],
        startTime = resultRow[Events.start_time],
        minimalAge = resultRow[Events.minimal_age],
        maximalAge = resultRow[Events.maximal_age],
        minimalRating = resultRow[Events.minimal_rating],
        price = resultRow[Events.price],
        organizers = resultRow[Events.organizers].toList()
    )


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
}