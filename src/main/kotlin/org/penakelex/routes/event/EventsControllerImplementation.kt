package org.penakelex.routes.event

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json
import org.penakelex.database.models.*
import org.penakelex.database.services.Service
import org.penakelex.fileSystem.FileManager
import org.penakelex.response.Result
import org.penakelex.response.toHttpStatusCode
import org.penakelex.routes.extensions.getIntJWTPrincipalClaim
import org.penakelex.session.USER_ID

class EventsControllerImplementation(
    private val service: Service,
    private val fileManager: FileManager
) : EventsController {
    override suspend fun createEvent(call: ApplicationCall) {
        val multiPartData = call.receiveMultipart().readAllParts()
        val event: EventCreate = multiPartData.filterIsInstance<PartData.FormItem>().singleOrNull()?.let {
            Json.decodeFromString(it.value)
        } ?: return call.response.status(Result.EMPTY_FORM_ITEM_OF_MULTI_PART_DATA.toHttpStatusCode())
        val images = fileManager.uploadFiles(multiPartData.filterIsInstance<PartData.FileItem>())
        val originatorID = call.getIntJWTPrincipalClaim(USER_ID)
        val (creatingChatResult, chatID) = service.chatsService.createChat(
            originatorID = originatorID,
            chat = ChatCreate(
                name = event.name,
                administrators = listOf()
            ),
            open = true
        )
        if (creatingChatResult != Result.OK) {
            return call.response.status(creatingChatResult.toHttpStatusCode())
        }
        call.response.status(
            service.eventsService.insertEvent(
                event = event,
                originatorID = originatorID,
                images = images,
                chatID = chatID!!
            ).toHttpStatusCode()
        )
    }

    override suspend fun updateEvent(call: ApplicationCall) {
        val multiPartData = call.receiveMultipart().readAllParts()
        val event: EventUpdate = multiPartData.filterIsInstance<PartData.FormItem>().singleOrNull()?.let {
            Json.decodeFromString(it.value)
        } ?: return call.response.status(Result.EMPTY_FORM_ITEM_OF_MULTI_PART_DATA.toHttpStatusCode())
        val newImages = fileManager.uploadFiles(multiPartData.filterIsInstance<PartData.FileItem>())
        call.response.status(
            service.eventsService.updateEvent(
                event = event,
                organizerID = call.getIntJWTPrincipalClaim(USER_ID),
                newImages = newImages
            ).toHttpStatusCode()
        )
        fileManager.deleteFiles(event.deletedImages)
    }

    override suspend fun deleteEvent(call: ApplicationCall) = call.response.status(
        service.eventsService.deleteEvent(
            eventID = call.receive<Int>(),
            originatorID = call.getIntJWTPrincipalClaim(USER_ID)
        ).toHttpStatusCode()
    )

    override suspend fun getEvent(call: ApplicationCall) {
        val (result, event) = service.eventsService.getEvent(
            eventID = call.parameters["eventID"]?.toIntOrNull()
                ?: return call.response.status(Result.EMPTY_EVENT_ID.toHttpStatusCode())
        )
        call.respond(
            result.toHttpStatusCode(),
            event,
            typeInfo<Event?>()
        )
    }

    override suspend fun changeUserAsParticipant(call: ApplicationCall) {
        val event = call.receive<EventParticipant>()
        val changerID = call.getIntJWTPrincipalClaim(USER_ID)
        val (changingResult, chatID) = service.eventsService.changeUserAsParticipant(
            changingID = event.changingID ?: changerID,
            eventID = event.eventID,
            changerID = changerID
        )
        if (changingResult == Result.OK) service.chatsService.changeUserAsParticipant(
            chatID = chatID!!,
            userID = changerID
        )
        call.response.status(changingResult.toHttpStatusCode())
    }

    override suspend fun changeUserAsOrganizer(call: ApplicationCall) = call.response.status(
        service.eventsService.changeUserAsOrganizer(
            changerID = call.getIntJWTPrincipalClaim(USER_ID),
            organizer = call.receive<EventOrganizer>()
        ).toHttpStatusCode()
    )

    override suspend fun changeEventInFavourites(call: ApplicationCall) = call.response.status(
        service.eventsService.changeEventInFavourites(
            userID = call.getIntJWTPrincipalClaim(USER_ID),
            eventID = call.receive<Int>()
        ).toHttpStatusCode()
    )

    override suspend fun getUserEvents(call: ApplicationCall) {
        val eventsGet = call.receive<EventsGet>()
        val id = eventsGet.userID ?: call.getIntJWTPrincipalClaim(USER_ID)
        val actual = eventsGet.actual ?: false
        val aforetime = eventsGet.aforetime ?: false
        val (gettingResult, events) = when (eventsGet.type ?: "") {
            EventsType.All.type -> {
                service.eventsService.getUserEvents(id, actual, aforetime)
            }

            EventsType.InFavourites.type -> {
                service.eventsService.getInFavouritesEvents(id, actual, aforetime)
            }

            EventsType.Organizer.type -> {
                service.eventsService.getOrganizerEvents(id, actual, aforetime)
            }

            EventsType.Participant.type -> {
                service.eventsService.getParticipantEvents(id, actual, aforetime)
            }

            EventsType.Originator.type -> {
                service.eventsService.getOriginatorEvents(id, actual, aforetime)
            }

            else -> Result.UNRESOLVED_EVENT_TYPE to null
        }
        call.respond(
            gettingResult.toHttpStatusCode(),
            events,
            typeInfo<List<Event>?>()
        )
    }

    override suspend fun getGlobalEvents(call: ApplicationCall) {
        val (result, events) = service.eventsService.getGlobalEvents(
            selection = call.receive<EventSelection>()
        )
        call.respond(result.toHttpStatusCode(), events, typeInfo<List<Event>?>())
    }
}