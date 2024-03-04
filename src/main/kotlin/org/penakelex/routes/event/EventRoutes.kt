package org.penakelex.routes.event

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.eventRoutes(controller: EventsController) {
    post("/event/create") { controller.createEvent(call) }
    post("/event/user") { controller.getUserEvents(call) }
    post("/event/global") { controller.getGlobalEvents(call) }
}