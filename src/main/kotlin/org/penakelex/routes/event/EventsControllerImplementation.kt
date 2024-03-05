package org.penakelex.routes.event

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import org.penakelex.database.models.EventCreate
import org.penakelex.database.models.EventSelection
import org.penakelex.database.services.Service
import org.penakelex.fileSystem.FileManager
import org.penakelex.response.Result
import org.penakelex.response.toResponse
import org.penakelex.response.toResultResponse
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
            service.eventsService.insertEvent(event = event, images = images).toResultResponse()
        )
    }

    override suspend fun getUserEvents(call: ApplicationCall) {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: call.principal<JWTPrincipal>()!!.payload.getClaim(USER_ID).asInt()
        val actual = call.parameters["actual"].toBoolean()
        val aforetime = call.parameters["aforetime"].toBoolean()
        val getResponse = when (call.parameters["type"] ?: "") {
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

            else -> Result.UNRESOLVED_EVENT_TYPE to null
        }
        call.respond(getResponse.toResponse())
    }

    override suspend fun getGlobalEvents(call: ApplicationCall) = call.respond(
        service.eventsService.getGlobalEvents(
            selection = call.receive<EventSelection>()
        ).toResponse()
    )
}