package org.penakelex.routes.chat

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*

fun Route.chatRoutes(controller: ChatsController) = route("/chat") {
    authenticate {
        webSocket("/socket") {
            controller.chatSocket(call = call, webSocketSession = this)
        }
        route("/create") {
            post("/closed") { controller.createClosedChat(call) }
            post("/dialog") { controller.createDialog(call) }
        }
        post("/participants") { controller.getChatParticipants(call) }
        route("/getAll") {
            post("/chats") { controller.getAllChats(call) }
            post("/messages") { controller.getAllMessages(call) }
        }
        route("/change") {
            post("/participant") { controller.changeUserAsParticipant(call) }
            post("/administrator") { controller.changeParticipantAsAdministrator(call) }
            post("/name") { controller.updateChatName(call) }
        }
        post("/delete") { controller.deleteChat(call) }
    }
}