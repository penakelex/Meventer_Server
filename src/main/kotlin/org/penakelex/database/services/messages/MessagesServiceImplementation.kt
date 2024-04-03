package org.penakelex.database.services.messages

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.models.Message
import org.penakelex.database.models.MessageSend
import org.penakelex.database.models.MessageUpdate
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.ChatsParticipants
import org.penakelex.database.tables.Messages
import org.penakelex.database.tables.MessagesAttachments
import org.penakelex.response.Result

class MessagesServiceImplementation : TableService(), MessagesService {
    override suspend fun insertNewMessage(
        senderID: Int,
        message: MessageSend
    ): Pair<Result, Long?> = databaseQuery {
        val isUserNotAParticipantOfTheChat = ChatsParticipants.select {
            ChatsParticipants.chat_id.eq(message.chatID) and ChatsParticipants.participant_id.eq(senderID)
        }.singleOrNull() == null
        if (isUserNotAParticipantOfTheChat) {
            return@databaseQuery Result.YOU_CAN_NOT_SEND_MESSAGES_IN_THIS_CHAT to null
        }
        val messageID = Messages.insertAndGetId {
            it[chat_id] = message.chatID
            it[sender_id] = senderID
            it[body] = message.body
            it[timestamp] = message.timestamp
        }.value
        if (message.attachment != null) {
            MessagesAttachments.insert {
                it[message_id] = messageID
                it[attachment] = message.attachment
            }
        }
        return@databaseQuery Result.OK to messageID
    }

    override suspend fun getAllMessages(chatID: Long): Pair<Result, List<Message>?> = databaseQuery {
        val messagesQuery = Messages.select {
            Messages.chat_id.eq(chatID)
        }.toList()
        val messagesAttachments = MessagesAttachments.select {
            MessagesAttachments.message_id.inList(messagesQuery.map { it[Messages.id].value })
        }.associateBy(
            keySelector = { it[MessagesAttachments.message_id] },
            valueTransform = { it[MessagesAttachments.attachment] }
        )
        return@databaseQuery Result.OK to messagesQuery.map {
            val messageID = it[Messages.id].value
            Message(
                id = messageID,
                chatID = chatID,
                senderID = it[Messages.sender_id],
                body = it[Messages.body],
                timestamp = it[Messages.timestamp],
                attachment = messagesAttachments[messageID]
            )
        }
    }

    override suspend fun updateMessage(message: MessageUpdate, updaterID: Int): Result = databaseQuery {
        val updatedMessagesCount = Messages.update(
            where = { Messages.id.eq(message.id) and Messages.sender_id.eq(updaterID) }
        ) {
            it[body] = message.body
        }
        if (updatedMessagesCount != 1) {
            return@databaseQuery Result.MESSAGE_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteMessage(messageID: Long, deleterID: Int): Pair<Result, String?> = databaseQuery {
        val deletedMessagesCount = Messages.deleteWhere {
            Messages.id.eq(messageID) and sender_id.eq(deleterID)
        }
        if (deletedMessagesCount != 1) {
            return@databaseQuery Result.MESSAGE_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT to null
        }
        val attachment = MessagesAttachments.select {
            MessagesAttachments.message_id.eq(messageID)
        }.singleOrNull()?.let {
            it[MessagesAttachments.attachment]
        }
        return@databaseQuery Result.OK to attachment
    }
}