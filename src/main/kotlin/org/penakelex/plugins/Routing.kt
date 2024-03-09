package org.penakelex.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.penakelex.routes.Controller
import org.penakelex.routes.event.eventRoutes
import org.penakelex.routes.file.fileRoutes
import org.penakelex.routes.user.userRoutes

fun Application.configureRouting() {
    val controller by inject<Controller>()
    routing {
        userRoutes(controller.usersController)
        eventRoutes(controller.eventsController)
        fileRoutes(controller.filesController)
    }
}
