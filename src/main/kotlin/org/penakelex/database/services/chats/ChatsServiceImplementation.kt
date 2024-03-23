package org.penakelex.database.services.chats

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.extenstions.binaryContains
import org.penakelex.database.extenstions.eqAny
import org.penakelex.database.models.*
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Chats
import org.penakelex.database.tables.Messages
import org.penakelex.database.tables.MessagesAttachments
import org.penakelex.database.tables.Users
import org.penakelex.response.Result

class ChatsServiceImplementation : TableService(), ChatsService {
    override suspend fun createChat(
        originatorID: Int,
        chat: ChatCreate,
        open: Boolean
    ): Pair<Result, Long?> = databaseQuery {
        val existingAdministratorsIDsCount = Users.select {
            Users.id.inList(chat.administrators)
        }.count().toInt()
        if (existingAdministratorsIDsCount != chat.administrators.size) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        return@databaseQuery Result.OK to Chats.insertAndGetId {
            it[name] = chat.name
            it[originator] = originatorID
            it[administrators] = chat.administrators.sorted().toTypedArray()
            it[participants] = chat.administrators.plus(originatorID).sorted().toTypedArray()
            it[Chats.open] = open
        }.value
    }

    override suspend fun createDialog(firstUserID: Int, secondUserID: Int): Pair<Result, Long?> = databaseQuery {
        val isDialogExists = Chats.select {
            Chats.participants.eq(arrayOf(firstUserID, secondUserID))
        }.singleOrNull() != null
        if (isDialogExists) {
            return@databaseQuery Result.YOU_ALREADY_HAVE_CHAT_WITH_THIS_USER to null
        }
        val existingUsersIDsCount = Users.select {
            Users.id.eq(firstUserID) or Users.id.eq(secondUserID)
        }.count().toInt()
        if (existingUsersIDsCount != 2) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        return@databaseQuery Result.OK to Chats.insertAndGetId {
            it[participants] = arrayOf(firstUserID, secondUserID).sortedArray()
        }.value
    }

    override suspend fun getChatParticipants(chatID: Long): Pair<Result, List<Int>?> = databaseQuery {
        val chatParticipants = Chats.select {
            Chats.id.eq(chatID)
        }.singleOrNull()?.let {
            it[Chats.participants]
        } ?: return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND to null
        return@databaseQuery Result.OK to chatParticipants.asList()
    }

    override suspend fun getAllChats(userID: Int): Pair<Result, List<Chat>?> = databaseQuery {
        val isNotUserExists = Users.select(Users.id.eq(userID)).singleOrNull() == null
        if (isNotUserExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        }
        val chats = Chats.select {
            Chats.participants.eqAny(userID)
        }.toList()
        val chatsLastMessages = Messages.select {
            Messages.chat_id.inList(chats.map { it[Chats.id].value })
        }.toList()
        val messagesAttachments = MessagesAttachments.select {
            MessagesAttachments.message_id.inList(chatsLastMessages.map { it[Messages.id].value })
        }.associateBy(
            keySelector = { it[MessagesAttachments.message_id] },
            valueTransform = { it[MessagesAttachments.attachment] }
        )
        val lastMessages = buildMap<Long, List<Message>> {
            for (chatMessage in chatsLastMessages) {
                val chatID = chatMessage[Messages.chat_id]
                val message = Message(
                    id = chatMessage[Messages.id].value,
                    chatID = chatID,
                    senderID = chatMessage[Messages.sender_id],
                    body = chatMessage[Messages.body],
                    timestamp = chatMessage[Messages.timestamp],
                    attachment = messagesAttachments[chatMessage[Messages.id].value]
                )
                set(
                    key = chatID,
                    value = get(chatID)?.plus(message) ?: listOf(message)
                )
            }
        }
        return@databaseQuery Result.OK to chats.map {
            Chat(
                name = it[Chats.name],
                originator = it[Chats.originator],
                participants = it[Chats.participants].asList(),
                administrators = it[Chats.administrators]?.asList(),
                lastMessages = lastMessages[it[Chats.id].value]!!
            )
        }
    }

    override suspend fun changeUserAsParticipant(chatID: Long, userID: Int, changerID: Int?): Result = databaseQuery {
        val isUsersNotExists = Users.select {
            Users.id.eq(userID).let {
                if (changerID != null) it or Users.id.eq(changerID)
                else it
            }
        }.count().toInt() != (if (changerID != null) 2 else 1)
        if (isUsersNotExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        }
        val (participants, administrators, originator, isOpen) = Chats.select {
            Chats.id.eq(chatID)
        }.singleOrNull()?.let {
            ChatSelect(
                participants = it[Chats.participants],
                administrators = it[Chats.administrators]!!,
                originator = it[Chats.originator]!!,
                isOpen = it[Chats.open]
            )
        } ?: return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        if (!isOpen && changerID != null && !administrators.binaryContains(changerID) && originator != changerID) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_CHAT
        }
        val userIsAdministrator = administrators.binaryContains(userID)

        val isDeletingUser = participants.binaryContains(userID)
        if (userIsAdministrator && changerID != null && changerID != originator && isDeletingUser) {
            return@databaseQuery Result.YOU_CAN_NOT_DELETE_ADMINISTRATOR_FROM_CHAT_AS_ADMINISTRATOR
        }
        Chats.update(
            where = { Chats.id.eq(chatID) }
        ) {
            if (isDeletingUser) {
                it[Chats.participants] = participants.filter { id -> id != userID }.toTypedArray()
                if (userIsAdministrator) {
                    it[Chats.administrators] = administrators.filter { id -> id != userID }.toTypedArray()
                }
            } else {
                it[Chats.participants] = participants.plus(userID)
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun changeParticipantAsAdministrator(
        updaterID: Int,
        chatAdministrator: ChatAdministratorUpdate
    ): Result = databaseQuery {
        val (participants, administrators, originator) = Chats.select {
            Chats.id.eq(chatAdministrator.chatID)
        }.singleOrNull()?.let {
            Triple(
                first = it[Chats.participants],
                second = it[Chats.administrators]!!,
                third = it[Chats.originator]!!
            )
        } ?: return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        if (updaterID != originator) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_CHAT
        }
        if (!participants.binaryContains(chatAdministrator.updatingID)) {
            return@databaseQuery Result.ADMINISTRATOR_MUST_BE_PARTICIPANT_OF_THE_CHAT
        }
        Chats.update(
            where = { Chats.id.eq(chatAdministrator.chatID) }
        ) {
            it[Chats.administrators] = administrators.run {
                if (administrators.binaryContains(chatAdministrator.updatingID)) {
                    filter { id -> id != chatAdministrator.updatingID }.toTypedArray()
                } else plus(chatAdministrator.updatingID)
            }

        }
        return@databaseQuery Result.OK
    }

    override suspend fun updateChatName(chat: ChatNameUpdate, userID: Int): Result = databaseQuery {
        val updatedChatsCount = Chats.update(
            where = { Chats.id.eq(chat.id) and Chats.originator.eq(userID) }
        ) {
            it[name] = chat.name
        }
        if (updatedChatsCount != 1) {
            return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteChat(chatID: Long, userID: Int): Result = databaseQuery {
        val deletedChatsCount = Chats.deleteWhere {
            Chats.id.eq(chatID) and originator.eq(userID)
        }
        if (deletedChatsCount != 1) {
            return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        }
        return@databaseQuery Result.OK
    }
}