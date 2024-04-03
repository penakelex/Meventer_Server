package org.penakelex.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.penakelex.routes.Controller
import org.penakelex.routes.chat.chatRoutes
import org.penakelex.routes.event.eventRoutes
import org.penakelex.routes.file.fileRoutes
import org.penakelex.routes.user.userRoutes

fun Application.configureRouting() {
    val controller by inject<Controller>()
    routing {
        greetingRoute()
        userRoutes(controller.usersController)
        eventRoutes(controller.eventsController)
        fileRoutes(controller.filesController)
        chatRoutes(controller.chatsController)
    }
}

fun Route.greetingRoute() {
    get {
        call.respond(HttpStatusCode.OK, "Hello There! - Meventer")
    }
}