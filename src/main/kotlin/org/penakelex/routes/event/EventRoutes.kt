package org.penakelex.routes.event

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.eventRoutes(controller: EventsController) = route("/event") {
    post("/global") { controller.getGlobalEvents(call) }
    get("/{eventID}") { controller.getEvent(call) }
    authenticate {
        post("/create") { controller.createEvent(call) }
        post("/user") { controller.getUserEvents(call) }
        //TODO: addParticipant -> toParticipantOfEvent (or smth)
        post("/addParticipant") { controller.addParticipantToEvent(call) }
        //TODO: Same as with addParticipant
        post("/addOrganizer") { controller.addOrganizerToEvent(call) }
        //TODO: Same as with addParticipant
        post("/addInFavourites") { controller.inFeaturedEventChange(call) }
        post("/update") { controller.updateEvent(call) }
        post("/delete") { controller.deleteEvent(call) }
    }
}