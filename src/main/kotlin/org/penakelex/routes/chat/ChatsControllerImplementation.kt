package org.penakelex.routes.chat

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import org.penakelex.database.models.*
import org.penakelex.database.services.Service
import org.penakelex.response.Result
import org.penakelex.response.toResponse
import org.penakelex.response.toResultResponse
import org.penakelex.routes.extensions.getIntJWTPrincipalClaim
import org.penakelex.session.USER_ID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.set

class ChatsControllerImplementation(
    private val service: Service
) : ChatsController {
    private val clients = ConcurrentHashMap<Int, Client>()
    override suspend fun chatSocket(call: ApplicationCall, webSocketSession: WebSocketSession) {
        val userID = call.getIntJWTPrincipalClaim(USER_ID)
        try {
            val onJoinResult = onJoin(
                userID = userID,
                webSocketSession = webSocketSession
            )
            if (onJoinResult != Result.OK) {
                call.respond(onJoinResult.toResultResponse())
            }
            webSocketSession.incoming.consumeEach { frame ->
                if (frame is Frame.Text) selectMessage(
                    userID = userID,
                    message = frame.readText()
                )
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        } finally {
            tryDisconnect(userID = userID)
        }
    }

    private fun onJoin(
        userID: Int,
        webSocketSession: WebSocketSession
    ): Result {
        if (clients.containsKey(userID)) {
            return Result.USER_WITH_SUCH_ID_IS_ALREADY_CHAT_CLIENT
        }
        clients[userID] = Client(
            userID = userID,
            webSocketSession = webSocketSession
        )
        return Result.OK
    }

    private suspend fun tryDisconnect(userID: Int) {
        if (clients.containsKey(userID)) {
            clients[userID]!!.webSocketSession.close()
            clients.remove(userID)
        }
    }

    private suspend fun selectMessage(
        userID: Int,
        message: String
    ) {
        try {
            sendMessage(
                senderID = userID,
                sentMessage = Json.decodeFromString(message)
            )
            return
        } catch (_: Exception) {
        }
        try {
            updateMessage(
                updaterID = userID,
                updatedMessage = Json.decodeFromString(message)
            )
            return
        } catch (_: Exception) {
        }
        try {
            deleteMessage(
                deleterID = userID,
                deletedMessage = Json.decodeFromString(message)
            )
        } catch (_: Exception) {
        }
    }

    private suspend fun sendMessage(
        senderID: Int,
        sentMessage: MessageSend
    ) {
        val (gettingChatParticipantsResult, chatParticipants) = service.chatsService.getChatParticipants(
            chatID = sentMessage.chatID
        )
        if (gettingChatParticipantsResult != Result.OK) return
        val (messageInsertResult, messageID) = service.messagesService.insertNewMessage(
            senderID = senderID, message = sentMessage
        )
        if (messageInsertResult != Result.OK) return
        sendToClients(
            chatParticipants = chatParticipants!!.toSet(),
            message = Json.encodeToString(
                serializer = Message.serializer(),
                value = Message(
                    id = messageID!!,
                    chatID = sentMessage.chatID,
                    senderID = senderID,
                    body = sentMessage.body,
                    timestamp = sentMessage.timestamp,
                    attachment = sentMessage.attachment
                )
            )
        )
    }

    private suspend fun updateMessage(
        updaterID: Int,
        updatedMessage: MessageUpdate
    ) {
        val (gettingChatParticipantsResult, chatParticipants) = service.chatsService.getChatParticipants(
            chatID = updatedMessage.chatID
        )
        if (gettingChatParticipantsResult != Result.OK) return
        val messageUpdateResult = service.messagesService.updateMessage(
            message = updatedMessage,
            updaterID = updaterID
        )
        if (messageUpdateResult != Result.OK) return
        sendToClients(
            chatParticipants = chatParticipants!!.toSet(),
            message = Json.encodeToString(
                MessageUpdated.serializer(),
                MessageUpdated(
                    id = updatedMessage.id,
                    body = updatedMessage.body
                )
            )
        )
    }

    private suspend fun deleteMessage(
        deleterID: Int,
        deletedMessage: MessageDelete
    ) {
        val (gettingChatParticipantsResult, chatParticipants) = service.chatsService.getChatParticipants(
            chatID = deletedMessage.chatID
        )
        if (gettingChatParticipantsResult != Result.OK) return
        val messageDeleteResult = service.messagesService.deleteMessage(
            messageID = deletedMessage.id,
            deleterID = deleterID
        )
        if (messageDeleteResult != Result.OK) return
        sendToClients(
            chatParticipants = chatParticipants!!.toSet(),
            message = deletedMessage.id.toString()
        )
    }

    private suspend fun sendToClients(
        chatParticipants: Set<Int>,
        message: String
    ) {
        val chatParticipantsClients = clients.values.filter { client ->
            client.userID in chatParticipants
        }
        for (client in chatParticipantsClients) {
            client.webSocketSession.send(message)
        }
    }

    override suspend fun createClosedChat(call: ApplicationCall) = call.respond(
        service.chatsService.createChat(
            chat = call.receive<ChatCreate>(),
            originatorID = call.getIntJWTPrincipalClaim(USER_ID),
            open = false
        ).first.toResultResponse()
    )

    override suspend fun createDialog(call: ApplicationCall) = call.respond(
        service.chatsService.createDialog(
            firstUserID = call.getIntJWTPrincipalClaim(USER_ID),
            secondUserID = call.receive<Int>()
        ).toResponse()
    )

    override suspend fun getChatParticipants(call: ApplicationCall) = call.respond(
        service.chatsService.getChatParticipants(
            chatID = call.receive<Long>()
        ).toResponse()
    )

    override suspend fun getAllChats(call: ApplicationCall) = call.respond(
        service.chatsService.getAllChats(
            userID = call.getIntJWTPrincipalClaim(USER_ID)
        ).toResponse()
    )

    override suspend fun getAllMessages(call: ApplicationCall) {
        val chatID = call.receive<Long>()
        val userID = call.getIntJWTPrincipalClaim(USER_ID)
        val (gettingChatParticipantsResult, participants) = service.chatsService.getChatParticipants(
            chatID = chatID
        )
        if (gettingChatParticipantsResult != Result.OK) {
            return call.respond(gettingChatParticipantsResult.toResponse())
        }
        if (userID !in participants!!) {
            return call.respond(Result.YOU_ARE_NOT_PARTICIPANT_OF_THIS_CHAT.toResponse())
        }
        call.respond(
            service.messagesService.getAllMessages(
                chatID = chatID
            ).toResponse()
        )
    }

    override suspend fun changeUserAsParticipant(call: ApplicationCall) {
        val (chatID, changingID) = call.receive<ChatParticipantUpdate>()
        val userID = call.getIntJWTPrincipalClaim(USER_ID)
        val changingResult = if (changingID == null) service.chatsService.changeUserAsParticipant(
            chatID = chatID,
            userID = userID
        ) else service.chatsService.changeUserAsParticipant(
            chatID = chatID,
            userID = changingID,
            changerID = userID
        )
        call.respond(changingResult.toResultResponse())
    }

    override suspend fun changeParticipantAsAdministrator(call: ApplicationCall) = call.respond(
        service.chatsService.changeParticipantAsAdministrator(
            updaterID = call.getIntJWTPrincipalClaim(USER_ID),
            chatAdministrator = call.receive<ChatAdministratorUpdate>()
        ).toResultResponse()
    )

    override suspend fun updateChatName(call: ApplicationCall) = call.respond(
        service.chatsService.updateChatName(
            chat = call.receive<ChatNameUpdate>(),
            userID = call.getIntJWTPrincipalClaim(USER_ID)
        ).toResultResponse()
    )

    override suspend fun deleteChat(call: ApplicationCall) = call.respond(
        service.chatsService.deleteChat(
            chatID = call.receive<Long>(),
            userID = call.getIntJWTPrincipalClaim(USER_ID)
        ).toResultResponse()
    )
}