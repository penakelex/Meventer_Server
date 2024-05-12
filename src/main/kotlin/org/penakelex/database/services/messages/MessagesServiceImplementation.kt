package org.penakelex.database.services.messages

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.models.Message
import org.penakelex.database.models.MessageSend
import org.penakelex.database.models.MessageUpdate
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.*
import org.penakelex.response.Result

class MessagesServiceImplementation : TableService(), MessagesService {
    override suspend fun insertNewMessage(
        senderID: Int,
        message: MessageSend,
        participants: List<Int>
    ): Pair<Result, Long?> = databaseQuery {
        val isUserParticipantOfTheChat = ChatsParticipants.select {
            ChatsParticipants.chat_id.eq(message.chatID) and ChatsParticipants.participant_id.eq(senderID)
        }.singleOrNull() != null
        val isUserParticipantOfTheDialog = Dialogs.select {
            Dialogs.first.eq(senderID) or Dialogs.second.eq(senderID)
        }.singleOrNull() != null
        if (!isUserParticipantOfTheChat && !isUserParticipantOfTheDialog) {
            return@databaseQuery Result.YOU_CAN_NOT_SEND_MESSAGES_IN_THIS_CHAT to null
        }
        val messageID = Messages.insertAndGetId {
            if (isUserParticipantOfTheChat) it[chat_id] = message.chatID
            else it[dialog_id] = message.chatID
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
        val messages = Messages.select {
            Messages.chat_id.eq(chatID) or Messages.dialog_id.eq(chatID)
        }.toList()
        val messagesSenders = Users
            .slice(Users.id, Users.name, Users.avatar).select {
                Users.id.inList(messages.map { it[Messages.sender_id] })
            }.associateBy(
                keySelector = { it[Users.id].value },
                valueTransform = { it[Users.name] to it[Users.avatar] }
            )
        val messagesAttachments = MessagesAttachments.select {
            MessagesAttachments.message_id.inList(messages.map { it[Messages.id].value })
        }.associateBy(
            keySelector = { it[MessagesAttachments.message_id] },
            valueTransform = { it[MessagesAttachments.attachment] }
        )
        return@databaseQuery Result.OK to messages.map {
            val messageID = it[Messages.id].value
            val senderID = it[Messages.sender_id]
            val (senderName, senderAvatar) = messagesSenders.getValue(senderID)
            Message(
                messageID = messageID,
                chatID = chatID,
                senderID = senderID,
                senderName = senderName,
                senderAvatar = senderAvatar,
                body = it[Messages.body],
                timestamp = it[Messages.timestamp],
                attachment = messagesAttachments[messageID]
            )
        }
    }

    override suspend fun updateMessage(message: MessageUpdate, updaterID: Int): Result = databaseQuery {
        val updatedMessagesCount = Messages.update(
            where = { Messages.id.eq(message.messageID) and Messages.sender_id.eq(updaterID) }
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
        val attachment = MessagesAttachments.slice(MessagesAttachments.attachment).select {
            MessagesAttachments.message_id.eq(messageID)
        }.singleOrNull()?.let {
            it[MessagesAttachments.attachment]
        }
        return@databaseQuery Result.OK to attachment
    }
}