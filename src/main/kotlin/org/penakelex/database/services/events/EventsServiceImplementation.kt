package org.penakelex.database.services.events

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.penakelex.database.extenstions.getAgeOfPersonToday
import org.penakelex.database.models.*
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.*
import org.penakelex.response.Result
import java.time.Instant

class EventsServiceImplementation : TableService(), EventsService {
    override suspend fun insertEvent(
        event: EventCreate,
        originatorID: Int,
        images: List<String>,
        chatID: Long
    ): Result = databaseQuery {
        val eventID = Events.insertAndGetId {
            it[originator] = originatorID
            it[name] = event.name
            it[chat_id] = chatID
            it[description] = event.description
            it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            //it[place] = event.place
            //it[coordinates] = arrayOf(event.coordinates.first, event.coordinates.second)
            it[maximal_age] = event.maximalAge
            if (event.price != null) it[price] = event.price
        }.value
        if (!event.tags.isNullOrEmpty()) EventsTags.batchInsert(event.tags) { tag ->
            this[EventsTags.tag] = tag
            this[EventsTags.event_id] = eventID
        }
        if (images.isNotEmpty()) EventsImages.batchInsert(images) { image ->
            this[EventsImages.event_id] = eventID
            this[EventsImages.image] = image
        }
        return@databaseQuery Result.OK
    }

    override suspend fun updateEvent(
        event: EventUpdate,
        organizerID: Int,
        newImages: List<String>
    ): Result = databaseQuery {
        val organizersIDs = EventsOrganizers.slice(EventsOrganizers.organizer_id).select {
            EventsOrganizers.event_id.eq(event.eventID)
        }.map { it[EventsOrganizers.organizer_id] }
        if (organizerID !in organizersIDs) {
            val originatorID = Events.slice(Events.originator).select {
                Events.id.eq(event.eventID)
            }.singleOrNull()?.let {
                it[Events.originator]
            } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
            if (originatorID != organizerID) {
                return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_EVENT
            }
        }
        Events.update(
            where = { Events.id.eq(event.eventID) }
        ) {
            if (event.name != null) it[name] = event.name
            if (event.description != null) it[description] = event.description
            if (event.startTime != null) it[start_time] = event.startTime
            if (event.minimalAge != null) it[minimal_age] = event.minimalAge
            if (event.maximalAge != null) it[maximal_age] = event.maximalAge
            if (event.price != null) it[price] = event.price
            //if (event.place != null) it[place] = event.place
            /*if (event.coordinates != null) it[coordinates] = arrayOf(
                event.coordinates.first, event.coordinates.second
            )*/
        }
        if (!event.tags.isNullOrEmpty()) updateEventTags(
            tags = event.tags,
            eventID = event.eventID
        )
        if (!event.deletedImages.isNullOrEmpty()) EventsImages.deleteWhere {
            image.inList(event.deletedImages) and event_id.eq(event.eventID)
        }
        if (newImages.isNotEmpty()) EventsImages.batchInsert(newImages) { image ->
            this[EventsImages.event_id] = event.eventID
            this[EventsImages.image] = image
        }
        return@databaseQuery Result.OK
    }

    private fun updateEventTags(
        tags: List<String>,
        eventID: Int
    ) {
        val lastTags = EventsTags.slice(EventsTags.tag).select {
            EventsTags.event_id.eq(eventID)
        }.map { it[EventsTags.tag] }
        val deletedTags = lastTags.minus(tags.toSet())
        val addedTags = tags.minus(lastTags.toSet())
        if (deletedTags.isNotEmpty()) EventsTags.deleteWhere {
            tag.inList(deletedTags) and event_id.eq(eventID)
        }
        if (addedTags.isNotEmpty()) EventsTags.batchInsert(addedTags) { tag ->
            this[EventsTags.event_id] = eventID
            this[EventsTags.tag] = tag
        }
    }

    override suspend fun deleteEvent(eventID: Int, originatorID: Int): Result = databaseQuery {
        val deleteCount = Events.deleteWhere {
            Events.id.eq(eventID) and originator.eq(originatorID)
        }
        if (deleteCount != 1) return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        return@databaseQuery Result.OK
    }

    override suspend fun getEvent(eventID: Int): Pair<Result, Event?> = databaseQuery {
        val event = Events.select {
            Events.id.eq(eventID)
        }.singleOrNull() ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND to null
        return@databaseQuery Result.OK to getEventByResultRow(event)
    }

    override suspend fun getUserEvents(
        userID: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val isUserExists = Users.select { Users.id.eq(userID) }.singleOrNull() != null
        if (!isUserExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        val participantEvents = EventsParticipants.slice(EventsParticipants.event_id).select {
            EventsParticipants.participant_id.eq(userID)
        }.map { it[EventsParticipants.event_id] }
        val organizerEvents = EventsOrganizers.slice(EventsOrganizers.event_id).select {
            EventsOrganizers.organizer_id.eq(userID)
        }.map { it[EventsOrganizers.event_id] }
        val inFavouritesEvents = EventsInFavourites.slice(EventsInFavourites.event_id).select {
            EventsInFavourites.user_favourite_id.eq(userID)
        }.map { it[EventsInFavourites.event_id] }
        val eventsIDs = participantEvents.union(organizerEvents.toSet()).union(inFavouritesEvents.toSet()).toList()
        val events = getEvents(
            eventsIDs = eventsIDs,
            expression = Events.id.inList(eventsIDs) or Events.originator.eq(userID),
            actual = actual,
            aforetime = aforetime
        )
        return@databaseQuery Result.OK to events
    }

    override suspend fun getInFavouritesEvents(
        userID: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val isUserExists = Users.select { Users.id.eq(userID) }.singleOrNull() != null
        if (!isUserExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        val events = getEvents(
            eventsIDs = EventsInFavourites.slice(EventsInFavourites.event_id).select {
                EventsInFavourites.user_favourite_id.eq(userID)
            }.map { it[EventsInFavourites.event_id] },
            actual = actual,
            aforetime = aforetime
        )
        return@databaseQuery Result.OK to events
    }

    override suspend fun getParticipantEvents(
        userID: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val isUserExists = Users.select { Users.id.eq(userID) }.singleOrNull() != null
        if (!isUserExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        val events = getEvents(
            eventsIDs = EventsParticipants.slice(EventsParticipants.event_id).select {
                EventsParticipants.participant_id.eq(userID)
            }.map { it[EventsParticipants.event_id] },
            actual = actual,
            aforetime = aforetime
        )
        return@databaseQuery Result.OK to events
    }

    override suspend fun getOrganizerEvents(
        userID: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val isUserExists = Users.select { Users.id.eq(userID) }.singleOrNull() != null
        if (!isUserExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        val events = getEvents(
            eventsIDs = EventsOrganizers.slice(EventsOrganizers.event_id).select {
                EventsOrganizers.organizer_id.eq(userID)
            }.map { it[EventsOrganizers.event_id] },
            actual = actual,
            aforetime = aforetime
        )
        return@databaseQuery Result.OK to events
    }

    override suspend fun getOriginatorEvents(
        userID: Int,
        actual: Boolean,
        aforetime: Boolean
    ): Pair<Result, List<Event>?> = databaseQuery {
        val isUserExists = Users.select { Users.id.eq(userID) }.singleOrNull() != null
        if (!isUserExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        val events = getEvents(
            events = Events.select {
                getTimeExpression(
                    expression = Events.originator.eq(userID),
                    actual = actual,
                    aforetime = aforetime
                )
            }.orderBy(Events.start_time to SortOrder.ASC).toList()
        )
        return@databaseQuery Result.OK to events
    }

    override suspend fun changeUserAsParticipant(
        changingID: Int,
        eventID: Int,
        changerID: Int
    ): Pair<Result, Long?> = databaseQuery {
        val isChangingUserOrganizer = EventsOrganizers.select {
            EventsOrganizers.organizer_id.eq(changingID) and EventsOrganizers.event_id.eq(eventID)
        }.singleOrNull() != null
        val (minimalAge, maximalAge, chatID) = Events.slice(
            Events.originator, Events.minimal_age, Events.maximal_age, Events.chat_id
        ).select {
            Events.id.eq(eventID)
        }.singleOrNull()?.let {
            val originatorID = it[Events.originator]
            if (originatorID == changingID) {
                return@databaseQuery Result.ORIGINATOR_CAN_NOT_BE_SOMEONE_ELSE to null
            }
            val isChangerNotOriginator = originatorID != changerID
            val isUserChangesHimself = changerID != changingID
            if (isChangerNotOriginator && isUserChangesHimself && !isChangingUserOrganizer) {
                val isChangerIsOrganizer = EventsOrganizers.select {
                    EventsOrganizers.organizer_id.eq(changerID) and EventsOrganizers.event_id.eq(eventID)
                }.singleOrNull() != null
                if (!isChangerIsOrganizer) {
                    return@databaseQuery Result.YOU_CAN_NOT_CHANGE_OTHER_USERS_ON_THIS_EVENT to null
                }
            }
            return@let Triple(
                it[Events.minimal_age],
                it[Events.maximal_age],
                it[Events.chat_id]
            )
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND to null
        val userAge = Users.slice(Users.date_of_birth).select { Users.id.eq(changingID) }.singleOrNull()?.let {
            it[Users.date_of_birth].getAgeOfPersonToday()
        } ?: return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        val permittedAges = minimalAge..(maximalAge ?: UShort.MAX_VALUE)
        if (userAge !in permittedAges) {
            return@databaseQuery Result.YOUR_AGE_DOES_NOT_MATCH_THE_REQUIRED_BY_THE_EVENT_ORGANIZERS to null
        }
        val isUserAlreadyParticipant = EventsParticipants.select {
            EventsParticipants.event_id.eq(eventID) and EventsParticipants.participant_id.eq(changingID)
        }.singleOrNull() != null
        if (isUserAlreadyParticipant) {
            EventsParticipants.deleteWhere { event_id.eq(eventID) and participant_id.eq(changingID) }
        } else {
            EventsParticipants.insert {
                it[event_id] = eventID
                it[participant_id] = changingID
            }
            if (isChangingUserOrganizer) EventsOrganizers.deleteWhere {
                organizer_id.eq(changingID) and event_id.eq(eventID)
            }
        }
        return@databaseQuery Result.OK to chatID
    }

    override suspend fun changeUserAsOrganizer(changerID: Int, organizer: EventOrganizer): Result = databaseQuery {
        val originatorID = Events.select {
            Events.id.eq(organizer.eventID)
        }.singleOrNull()?.let {
            it[Events.originator]
        } ?: return@databaseQuery Result.EVENT_WITH_SUCH_ID_NOT_FOUND
        if (changerID != originatorID) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_EVENT
        }
        if (organizer.changingID == originatorID) {
            return@databaseQuery Result.ORIGINATOR_CAN_NOT_BE_SOMEONE_ELSE
        }
        val addingUser = Users.select { Users.id.eq(organizer.changingID) }.singleOrNull()
        if (addingUser == null) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        }
        val isUserAlreadyOrganizer = EventsOrganizers.select {
            EventsOrganizers.organizer_id.eq(organizer.changingID) and EventsOrganizers.event_id.eq(organizer.eventID)
        }.singleOrNull() != null
        if (isUserAlreadyOrganizer) {
            EventsOrganizers.deleteWhere { organizer_id.eq(organizer.changingID) and event_id.eq(organizer.eventID) }
            EventsParticipants.insert {
                it[participant_id] = organizer.changingID
                it[event_id] = organizer.eventID
            }
        } else {
            EventsOrganizers.insert {
                it[organizer_id] = organizer.changingID
                it[event_id] = organizer.eventID
            }
            EventsParticipants.deleteWhere {
                participant_id.eq(organizer.changingID) and event_id.eq(organizer.eventID)
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun changeEventInFavourites(userID: Int, eventID: Int): Result = databaseQuery {
        val isEventInFavourites = EventsInFavourites.select {
            EventsInFavourites.event_id.eq(eventID) and EventsInFavourites.user_favourite_id.eq(userID)
        }.singleOrNull() != null
        if (isEventInFavourites) {
            EventsInFavourites.deleteWhere { event_id.eq(eventID) and user_favourite_id.eq(userID) }
        } else {
            EventsInFavourites.insert {
                it[event_id] = eventID
                it[user_favourite_id] = userID
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun getGlobalEvents(selection: EventSelection): Pair<Result, List<Event>> = databaseQuery {
        val selectedEvents = Events.slice(Events.id).select {
            selection.getSelectExpression()
        }.map { it[Events.id].value }
        val selectedEventsByTags = if (selection.tags.isNullOrEmpty()) null
        else EventsTags.slice(EventsTags.event_id).select {
            EventsTags.tag.inList(selection.tags)
        }.map { it[EventsTags.event_id] }
        val events = Events.select {
            Events.id.inList(
                selectedEvents.let {
                    if (selectedEventsByTags == null) it
                    else it.union(selectedEventsByTags.toSet()).toList()
                }
            )
        }.orderBy(
            Events.start_time to when (selection.sortBy) {
                EventSelection.SortingStates.FURTHER_ONES_FIRST.state -> SortOrder.DESC
                else -> SortOrder.ASC
            }
        )
        return@databaseQuery Result.OK to getEvents(events.toList())
    }

    private fun getEvents(
        eventsIDs: List<Int>,
        expression: Op<Boolean> = Events.id.inList(eventsIDs),
        actual: Boolean = false,
        aforetime: Boolean = false
    ): List<Event> {
        if (eventsIDs.isEmpty()) return listOf()
        val events = Events.select {
            getTimeExpression(
                expression = expression,
                actual = actual,
                aforetime = aforetime
            )
        }.orderBy(Events.start_time to SortOrder.ASC)
        val images = getImages(eventsIDs)
        val tags = getTags(eventsIDs)
        val participants = getParticipants(eventsIDs)
        val organizers = getOrganizers(eventsIDs)
        val inFavourites = getInFavourites(eventsIDs)
        return events.map {
            getEventFromValues(
                eventID = it[Events.id].value,
                eventResultRow = it,
                images = images,
                tags = tags,
                participants = participants,
                inFavourites = inFavourites,
                organizers = organizers
            )
        }
    }

    private fun getEvents(events: List<ResultRow>): List<Event> {
        if (events.isEmpty()) return listOf()
        val eventsIDs = events.map { it[Events.id].value }
        val images = getImages(eventsIDs)
        val tags = getTags(eventsIDs)
        val participants = getParticipants(eventsIDs)
        val organizers = getOrganizers(eventsIDs)
        val inFavourites = getInFavourites(eventsIDs)
        return events.map {
            getEventFromValues(
                eventID = it[Events.id].value,
                eventResultRow = it,
                images = images,
                tags = tags,
                participants = participants,
                inFavourites = inFavourites,
                organizers = organizers
            )
        }
    }

    private fun getEventByResultRow(eventResultRow: ResultRow): Event {
        val eventID = eventResultRow[Events.id].value
        val images = EventsImages.slice(EventsImages.image).select {
            EventsImages.event_id.eq(eventID)
        }.map { it[EventsImages.image] }
        val tags = EventsTags.slice(EventsTags.tag).select {
            EventsTags.event_id.eq(eventID)
        }.map { it[EventsTags.tag] }
        val participants = EventsParticipants.slice(EventsParticipants.participant_id).select {
            EventsParticipants.event_id.eq(eventID)
        }.map { it[EventsParticipants.participant_id] }
        val organizers = EventsOrganizers.slice(EventsOrganizers.organizer_id).select {
            EventsOrganizers.event_id.eq(eventID)
        }.map { it[EventsOrganizers.organizer_id] }
        val inFavourites = EventsInFavourites.slice(EventsInFavourites.user_favourite_id).select {
            EventsInFavourites.event_id.eq(eventID)
        }.map { it[EventsInFavourites.user_favourite_id] }
        return Event(
            eventID = eventID,
            name = eventResultRow[Events.name],
            images = images,
            description = eventResultRow[Events.description],
            startTime = eventResultRow[Events.start_time],
            minimalAge = eventResultRow[Events.minimal_age],
            maximalAge = eventResultRow[Events.maximal_age],
            price = eventResultRow[Events.price],
            originator = eventResultRow[Events.originator],
            organizers = organizers,
            participants = participants,
            inFavourites = inFavourites,
            tags = tags
        )
    }

    private fun getEventFromValues(
        eventID: Int,
        eventResultRow: ResultRow,
        images: Map<Int, List<String>>,
        tags: Map<Int, List<String>>,
        organizers: Map<Int, List<Int>>,
        participants: Map<Int, List<Int>>,
        inFavourites: Map<Int, List<Int>>
    ): Event = Event(
        eventID = eventID,
        name = eventResultRow[Events.name],
        images = images.getOrDefault(eventID, listOf()),
        description = eventResultRow[Events.description],
        startTime = eventResultRow[Events.start_time],
        minimalAge = eventResultRow[Events.minimal_age],
        maximalAge = eventResultRow[Events.maximal_age],
        price = eventResultRow[Events.price],
        originator = eventResultRow[Events.originator],
        organizers = organizers.getOrDefault(eventID, listOf()),
        participants = participants.getOrDefault(eventID, listOf()),
        inFavourites = inFavourites.getOrDefault(eventID, listOf()),
        tags = tags.getOrDefault(eventID, listOf())
    )

    private fun getImages(eventsIDs: List<Int>): Map<Int, List<String>> {
        val images = EventsImages.select { EventsImages.event_id.inList(eventsIDs) }.map {
            it[EventsImages.event_id] to it[EventsImages.image]
        }
        return buildMap {
            for ((eventID, image) in images) set(
                key = eventID,
                value = getOrDefault(eventID, listOf()).plus(image)
            )
        }
    }

    private fun getTags(eventsIDs: List<Int>): Map<Int, List<String>> {
        val tags = EventsTags.select { EventsTags.event_id.inList(eventsIDs) }.map {
            it[EventsTags.event_id] to it[EventsTags.tag]
        }
        return buildMap {
            for ((eventID, tag) in tags) set(
                key = eventID,
                value = getOrDefault(eventID, listOf()).plus(tag)
            )
        }
    }

    private fun getOrganizers(eventsIDs: List<Int>): Map<Int, List<Int>> {
        val organizers = EventsOrganizers.select { EventsOrganizers.event_id.inList(eventsIDs) }.map {
            it[EventsOrganizers.event_id] to it[EventsOrganizers.organizer_id]
        }
        return buildMap {
            for ((eventID, organizerID) in organizers) set(
                key = eventID,
                value = getOrDefault(eventID, listOf()).plus(organizerID)
            )
        }
    }

    private fun getParticipants(eventsIDs: List<Int>): Map<Int, List<Int>> {
        val participants = EventsParticipants.select { EventsParticipants.event_id.inList(eventsIDs) }.map {
            it[EventsParticipants.event_id] to it[EventsParticipants.participant_id]
        }
        return buildMap {
            for ((eventID, participantID) in participants) set(
                key = eventID,
                value = getOrDefault(eventID, listOf()).plus(participantID)
            )
        }
    }

    private fun getInFavourites(eventsIDs: List<Int>): Map<Int, List<Int>> {
        val inFavourites = EventsInFavourites.select { EventsInFavourites.event_id.inList(eventsIDs) }.map {
            it[EventsInFavourites.event_id] to it[EventsInFavourites.user_favourite_id]
        }
        return buildMap {
            for ((eventID, userID) in inFavourites) set(
                key = eventID,
                value = getOrDefault(eventID, listOf()).plus(userID)
            )
        }
    }

    private fun getTimeExpression(
        expression: Op<Boolean>,
        actual: Boolean,
        aforetime: Boolean
    ) = if (actual) expression and Events.start_time.greaterEq(Instant.now())
    else if (aforetime) expression and Events.start_time.less(Instant.now())
    else expression
}