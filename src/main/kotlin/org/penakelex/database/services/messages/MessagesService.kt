package org.penakelex.database.services.messages

import org.penakelex.database.models.Message
import org.penakelex.database.models.MessageSend
import org.penakelex.database.models.MessageUpdate
import org.penakelex.response.Result

interface MessagesService {
    suspend fun insertNewMessage(senderID: Int, message: MessageSend): Pair<Result, Long?>
    suspend fun getAllMessages(chatID: Long): Pair<Result, List<Message>?>
    suspend fun updateMessage(message: MessageUpdate, updaterID: Int): Result
    suspend fun deleteMessage(messageID: Long, deleterID: Int): Result
}