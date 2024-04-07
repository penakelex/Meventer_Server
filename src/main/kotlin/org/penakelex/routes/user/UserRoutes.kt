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
        post("/data") { controller.getUserData(call) }
        post("/byNickname") { controller.getUsersByNickname(call) }
        get("/verifyToken") { controller.verifyToken(call) }
        route("/update") {
            post("/data") { controller.updateUserData(call) }
            post("/email") { controller.updateUserEmail(call) }
            post("/password") { controller.updateUserPassword(call) }
        }
        route("/feedback") {
            post("/create") { controller.createFeedback(call) }
            post("/get") { controller.getFeedbackToUser(call) }
            post("/update") { controller.updateFeedback(call) }
            post("/delete") { controller.deleteFeedback(call) }
        }
        post("/logout") { controller.logOut(call) }
    }
}