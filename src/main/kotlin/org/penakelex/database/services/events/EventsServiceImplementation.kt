package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.extenstions.binaryContains
import org.penakelex.database.extenstions.eqAny
import org.penakelex.database.extenstions.getAgeOfPersonToday
import org.penakelex.database.models.*
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Events
import org.penakelex.database.tables.Users
import org.penakelex.response.Result
import java.time.Instant

class EventsServiceImplementation : TableService(), EventsService {
    override suspend fun insertEvent(
        event: EventCreate,
        originatorID: Int,
        images: List<String>,
        chatID: Long
    ): Result = databaseQuery {
        Events.insert {
            it[originator] = originatorID
            it[name] = event.name
            it[Events.images] = images.toTypedArray()
            it[chat_id] = chatID
            it[description] = event.description
            it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            it[maximal_age] = event.maximalAge
            if (event.price != null) it[price] = event.price
            if (event.tags != null) it[tags] = event.tags.toTypedArray()
        }
        return@databaseQuery Result.OK
    }

    override suspend fun updateEvent(
        event: EventUpdate,
        organizerID: Int,
        newImages: List<String>
    ): Result = databaseQuery {
        val isNewImagesIsNotEmpty = newImages.isNotEmpty()
        var images = if (event.deletedImages != null || isNewImagesIsNotEmpty) Events.select {
            Events.id.eq(event.eventID) and (
                    Events.originator.eq(organizerID) or Events.organizers.eqAny(organizerID)
                    )
        }.singleOrNull()?.let {
            it[Events.images].toList()
        } else null
        if (event.deletedImages != null) images = images?.filter { image -> image !in event.deletedImages }
        if (isNewImagesIsNotEmpty) images = images?.plus(newImages) ?: newImages
        val updatedEventsCount = Events.update(
            where = {
                Events.id.eq(event.eventID) and (
                        Events.originator.eq(organizerID) or Events.organizers.eqAny(organizerID)
                        )
            }
        ) {
            if (event.name != null) it[name] = event.name
            if (event.description != null) it[description] = event.description
            if (event.startTime != null) it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            if (event.maximalAge != null) it[maximal_age] = event.maximalAge
            if (event.price != null) it[price] = event.price
            if (event.tags != null) it[tags] = event.tags.toTypedArray()
            if (images != null) it[Events.images] = images.toTypedArray()
        }
        if (updatedEventsCount != 1) {
            return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteEvent(eventID: Int, originatorID: Int): Result = databaseQuery {
        val deleteCount = Events.deleteWhere {
            Events.id.eq(eventID) and originator.eq(originatorID)
        }
        if (deleteCount == 0) return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
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
            Events.originator.eq(id) or Events.participants.eqAny(id) or Events.in_favourites.eqAny(id) or
                    Events.organizers.eqAny(id).let {
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
            Events.in_favourites.eqAny(id).let {
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
            Events.participants.eqAny(id).let {
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
            Events.organizers.eqAny(id).let {
                if (actual) it and Events.start_time.greaterEq(Instant.now())
                else if (aforetime) it and Events.start_time.less(Instant.now())
                else it
            }
        }.orderBy(Events.start_time).map { resultRowToEvent(resultRow = it) }
        return@databaseQuery if (events.isEmpty()) Result.ORGANIZER_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        else Result.OK to events
    }

    override suspend fun getOriginatorEvents(
        id: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val events = Events.select {
            Events.originator.eq(id).let {
                if (actual) it and Events.start_time.greaterEq(Instant.now())
                else if (aforetime) it and Events.start_time.less(Instant.now())
                else it
            }
        }.orderBy(Events.start_time).map { resultRowToEvent(resultRow = it) }
        return@databaseQuery if (events.isEmpty()) Result.ORGANIZER_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        else Result.OK to events
    }

    override suspend fun changeUserAsParticipant(userID: Int, eventID: Int): Pair<Result, Long?> = databaseQuery {
        val (participants, organizers, eventRequirements, chatID) = Events.select {
            Events.id.eq(eventID)
        }.singleOrNull()?.let {
            if (it[Events.originator] == userID) {
                return@databaseQuery Result.AS_ORIGINATOR_YOU_CAN_NOT_BE_SOMEONE_ELSE to null
            }
            return@let EventSelectParticipant(
                participants = it[Events.participants],
                organizers = it[Events.organizers],
                eventRequirements = EventRequirements(
                    minimalAge = it[Events.minimal_age],
                    maximalAge = it[Events.maximal_age]
                ),
                chatID = it[Events.chat_id]
            )
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND to null
        val userAge = Users.select { Users.id.eq(userID) }.singleOrNull()?.let {
            it[Users.date_of_birth].getAgeOfPersonToday()
        } ?: return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        val permittedAges = eventRequirements.minimalAge..(eventRequirements.maximalAge ?: Short.MAX_VALUE)
        if (userAge !in permittedAges) {
            return@databaseQuery Result.YOUR_AGE_DOES_NOT_MATCH_THE_REQUIRED_BY_THE_EVENT_ORGANIZERS to null
        }
        Events.update(
            where = { Events.id.eq(eventID) }
        ) {
            it[Events.participants] = participants.run {
                if (participants.binaryContains(userID)) filter { id -> id != userID }.toTypedArray()
                else plus(userID).sortedArray()
            }
            if (organizers.binaryContains(userID)) {
                it[Events.organizers] = organizers.filter { id -> id != userID }.toTypedArray()
            }
        }
        return@databaseQuery Result.OK to chatID
    }

    override suspend fun changeUserAsOrganizer(changerID: Int, organizer: EventOrganizer): Result = databaseQuery {
        val (organizers, participants, originatorID) = Events.select {
            Events.id.eq(organizer.eventID)
        }.singleOrNull()?.let {
            Triple(
                it[Events.organizers],
                it[Events.participants],
                it[Events.originator]
            )
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        if (changerID != originatorID) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_EVENT
        }
        if (organizer.changingID == originatorID) {
            return@databaseQuery Result.AS_ORIGINATOR_YOU_CAN_NOT_BE_SOMEONE_ELSE
        }
        if (organizer.changingID in organizers) {
            return@databaseQuery Result.YOU_ARE_ALREADY_ORGANIZER_OF_THIS_EVENT
        }
        val addingUser = Users.select { Users.id.eq(organizer.changingID) }.singleOrNull()
        if (addingUser == null) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        }
        Events.update(
            where = { Events.id.eq(organizer.eventID) and Events.originator.eq(changerID) }
        ) {
            it[Events.organizers] = organizers.run {
                if (binaryContains(organizer.changingID)) filter { id -> id != organizer.changingID }.toTypedArray()
                else plus(organizer.changingID).sortedArray()
            }
            if (participants.binaryContains(organizer.changingID)) {
                it[Events.participants] = participants.filter { id -> id != organizer.changingID }.toTypedArray()
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun changeEventInFavourites(userID: Int, eventID: Int): Result = databaseQuery {
        val inFavourites = Events.select { Events.id.eq(eventID) }.singleOrNull()?.let {
            it[Events.in_favourites]
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        Events.update(
            where = { Events.id.eq(eventID) }
        ) {
            it[in_favourites] = inFavourites.run {
                if (binaryContains(userID)) filter { id -> id != userID }.toTypedArray()
                else plus(userID).sortedArray()
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun getGlobalEvents(selection: EventSelection): Pair<Result, List<Event>> = databaseQuery {
        return@databaseQuery Result.OK to Events.select {
            selection.getSelectExpression()
        }.orderBy(
            Events.start_time to when (selection.sortBy) {
                EventSelection.SortingStates.FURTHER_ONES_FIRST.state -> SortOrder.DESC
                else -> SortOrder.ASC
            }
        ).map { resultRowToEvent(resultRow = it) }
    }

    private fun resultRowToEvent(resultRow: ResultRow) = Event(
        id = resultRow[Events.id].value,
        name = resultRow[Events.name],
        images = resultRow[Events.images].toList(),
        description = resultRow[Events.description],
        startTime = resultRow[Events.start_time],
        minimalAge = resultRow[Events.minimal_age],
        maximalAge = resultRow[Events.maximal_age],
        price = resultRow[Events.price],
        originator = resultRow[Events.originator],
        organizers = resultRow[Events.organizers].toList(),
        participants = resultRow[Events.participants].toList(),
        inFavourites = resultRow[Events.in_favourites].toList()
    )
}