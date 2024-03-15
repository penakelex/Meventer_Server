package org.penakelex.routes.chat

import io.ktor.server.application.*
import io.ktor.websocket.*

interface ChatsController {
    suspend fun chatSocket(call: ApplicationCall, webSocketSession: WebSocketSession)
    suspend fun createClosedChat(call: ApplicationCall)
    suspend fun createDialog(call: ApplicationCall)
    suspend fun getChatParticipants(call: ApplicationCall)
    suspend fun getAllChats(call: ApplicationCall)
    suspend fun changeUserAsParticipant(call: ApplicationCall)
    suspend fun changeParticipantAsAdministrator(call: ApplicationCall)
    suspend fun updateChatName(call: ApplicationCall)
    suspend fun deleteChat(call: ApplicationCall)
}