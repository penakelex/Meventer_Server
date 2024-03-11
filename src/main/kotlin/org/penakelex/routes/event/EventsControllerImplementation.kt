package org.penakelex.routes.event

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.penakelex.database.models.*
import org.penakelex.database.services.Service
import org.penakelex.fileSystem.FileManager
import org.penakelex.response.Result
import org.penakelex.response.toResponse
import org.penakelex.response.toResultResponse
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
        } ?: return call.respond(Result.EMPTY_FORM_ITEM_OF_MULTI_PART_DATA.toResultResponse())
        val images = fileManager.uploadFile(multiPartData.filterIsInstance<PartData.FileItem>())
        call.respond(
            service.eventsService.insertEvent(
                event = event,
                originatorID = call.getIntJWTPrincipalClaim(USER_ID),
                images = images
            ).toResultResponse()
        )
    }

    override suspend fun updateEvent(call: ApplicationCall) = call.respond(
        service.eventsService.updateEvent(
            event = call.receive<EventUpdate>(),
            organizerID = call.getIntJWTPrincipalClaim(USER_ID)
        ).toResultResponse()
    )

    override suspend fun deleteEvent(call: ApplicationCall) = call.respond(
        service.eventsService.deleteEvent(
            eventID = call.receive<Int>(),
            originatorID = call.getIntJWTPrincipalClaim(USER_ID)
        ).toResultResponse()
    )

    override suspend fun getEvent(call: ApplicationCall) {
        val eventID = call.parameters["eventID"]?.toIntOrNull()
            ?: return call.respond(Result.EMPTY_EVENT_ID.toResponse())
        call.respond(
            service.eventsService.getEvent(
                eventID = eventID
            ).toResponse()
        )
    }

    override suspend fun changeUserAsParticipant(call: ApplicationCall) = call.respond(
        service.eventsService.changeUserAsParticipant(
            userID = call.getIntJWTPrincipalClaim(USER_ID),
            eventID = call.receive<Int>()
        ).toResultResponse()
    )

    override suspend fun changeUserAsOrganizer(call: ApplicationCall) = call.respond(
        service.eventsService.changeUserAsOrganizer(
            changerID = call.getIntJWTPrincipalClaim(USER_ID),
            organizer = call.receive<EventOrganizer>()
        ).toResultResponse()
    )

    override suspend fun changeEventInFavourites(call: ApplicationCall) = call.respond(
        service.eventsService.changeEventInFavourites(
            userID = call.getIntJWTPrincipalClaim(USER_ID),
            eventID = call.receive<Int>()
        ).toResultResponse()
    )

    override suspend fun getUserEvents(call: ApplicationCall) {
        val eventsGet = call.receive<EventsGet>()
        val id = eventsGet.userID ?: call.getIntJWTPrincipalClaim(USER_ID)
        val actual = eventsGet.actual ?: false
        val aforetime = eventsGet.aforetime ?: false
        val gettingResponse = when (eventsGet.type ?: "") {
            EventsType.All.type -> {
                service.eventsService.getUserEvents(id, actual, aforetime)
            }

            EventsType.Featured.type -> {
                service.eventsService.getFeaturedEvents(id, actual, aforetime)
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
        call.respond(gettingResponse.toResponse())
    }

    override suspend fun getGlobalEvents(call: ApplicationCall) = call.respond(
        service.eventsService.getGlobalEvents(
            selection = call.receive<EventSelection>()
        ).toResponse()
    )
}