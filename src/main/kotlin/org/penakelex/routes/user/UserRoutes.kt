package org.penakelex.routes.user

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.userRoutes(controller: UsersController) = route("/user") {
    post("/register") { controller.registerUser(call) }
    post("/login") { controller.loginUser(call) }
    post("/sendEmailCode") { controller.sendEmailCode(call) }
    post("/verifyEmailCode") { controller.verifyEmailCode(call) }
    authenticate {
        //TODO: getData -> data
        post("/getData") { controller.getUserData(call) }
        route("/feedback") {
            post("/create") { controller.createFeedback(call) }
            post("/get") { controller.getFeedbackToUser(call) }
        }
    }
}