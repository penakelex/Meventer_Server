package org.penakelex.routes.user

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.penakelex.routes.Controller

fun Route.userRoutes(controller: Controller) {
    post("/user/register") { controller.usersController.registerUser(call) }
    post("/user/login") { controller.usersController.loginUser(call) }
    post("/user/sendEmailCode") { controller.usersController.sendEmailCode(call) }
    post("/user/verifyEmailCode") { controller.usersController.verifyEmailCode(call) }
}