package org.penakelex.routes.user

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.userRoutes(controller: UsersController) {
    post("/user/register") { controller.registerUser(call) }
    post("/user/login") { controller.loginUser(call) }
    post("/user/sendEmailCode") { controller.sendEmailCode(call) }
    post("/user/verifyEmailCode") { controller.verifyEmailCode(call) }
    post("/user/getData") { controller.getUserData(call) }
}