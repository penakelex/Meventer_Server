package org.penakelex.database.services.chats

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.models.*
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.*
import org.penakelex.response.Result
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max

class ChatsServiceImplementation : TableService(), ChatsService {
    private lateinit var newID: AtomicLong
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
        if (!this::newID.isInitialized) setID().join()
        val id = newID.getAndIncrement()
        Chats.insert {
            it[Chats.id] = id
            it[name] = chat.name
            it[originator] = originatorID
            it[Chats.open] = open
        }
        if (chat.administrators.isNotEmpty()) {
            ChatsAdministrators.batchInsert(chat.administrators) { administratorID ->
                this[ChatsAdministrators.chat_id] = id
                this[ChatsAdministrators.administrator_id] = administratorID
            }
        }
        ChatsParticipants.batchInsert(
            data = chat.administrators.plus(originatorID)
        ) { participantID ->
            this[ChatsParticipants.chat_id] = id
            this[ChatsParticipants.participant_id] = participantID
        }
        return@databaseQuery Result.OK to id
    }

    override suspend fun createDialog(firstUserID: Int, secondUserID: Int): Pair<Result, Long?> = databaseQuery {
        val isDialogExists = Dialogs.select {
            (Dialogs.first.eq(firstUserID) and Dialogs.second.eq(secondUserID)) or
                    (Dialogs.first.eq(secondUserID) and Dialogs.second.eq(firstUserID))
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
        if (!this::newID.isInitialized) setID().join()
        val id = newID.getAndIncrement()
        Dialogs.insert {
            it[Dialogs.id] = id
            it[first] = firstUserID
            it[second] = secondUserID
        }
        return@databaseQuery Result.OK to id
    }

    override suspend fun getChatParticipants(chatID: Long): Pair<Result, List<Int>?> = databaseQuery {
        val isChatExists = Chats.select {
            Chats.id.eq(chatID)
        }.singleOrNull() != null
        val dialog = Dialogs.select {
            Dialogs.id.eq(chatID)
        }.singleOrNull()
        if (!isChatExists && dialog == null) return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND to null
        val participants = if (isChatExists) ChatsParticipants.slice(ChatsParticipants.participant_id).select {
            ChatsParticipants.chat_id.eq(chatID)
        }.map { it[ChatsParticipants.participant_id] }
        else listOf(dialog!![Dialogs.first], dialog[Dialogs.second])
        return@databaseQuery Result.OK to participants
    }

    override suspend fun getAllChats(userID: Int): Pair<Result, List<Chat>?> = databaseQuery {
        val chatsIDs = ChatsParticipants.slice(ChatsParticipants.chat_id).select {
            ChatsParticipants.participant_id.eq(userID)
        }.map { it[ChatsParticipants.chat_id] }
        val chats = Chats.select {
            Chats.id.inList(chatsIDs)
        }.toList()
        val dialogs = Dialogs.select {
            Dialogs.first.eq(userID) or Dialogs.second.eq(userID)
        }.toList()
        val dialogsUsers = Users.slice(Users.id, Users.name).select {
            Users.id.inList(
                buildSet {
                    for (dialog in dialogs) {
                        add(dialog[Dialogs.first])
                        add(dialog[Dialogs.second])
                    }
                }.minus(userID)
            )
        }.associate { it[Users.id].value to it[Users.name] }
        val chatsLastMessages = Messages.select {
            Messages.chat_id.inList(chatsIDs.plus(dialogs.map { it[Dialogs.id] }))
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
                    messageID = chatMessage[Messages.id].value,
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
        val (participants, administrators) = getChatsParticipantsAndAdministrators(
            chatsIDs = chatsIDs
        )
        return@databaseQuery Result.OK to chats.map {
            val id = it[Chats.id]
            Chat(
                chatID = id,
                name = it[Chats.name],
                originator = it[Chats.originator],
                participants = participants.getOrDefault(id, listOf()),
                administrators = administrators.getOrDefault(id, listOf()),
                lastMessages = lastMessages.getOrDefault(id, listOf())
            )
        }.plus(
            dialogs.map {
                val dialogID = it[Dialogs.id]
                val first = it[Dialogs.first]
                val second = it[Dialogs.second]
                val companionID = if (first != userID) first else second
                Chat(
                    chatID = dialogID,
                    name = dialogsUsers.getOrDefault(
                        companionID,
                        ""
                    ),
                    originator = null,
                    participants = listOf(first, second),
                    administrators = administrators.getOrDefault(dialogID, listOf()),
                    lastMessages = lastMessages.getOrDefault(dialogID, listOf())
                )
            }
        )
    }

    override suspend fun changeUserAsParticipant(chatID: Long, userID: Int, changerID: Int?): Result = databaseQuery {
        val isUsersNotExists = Users.select {
            Users.id.eq(userID)
        }.singleOrNull() == null
        if (isUsersNotExists) {
            return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        }
        val (originator, isOpen) = Chats.slice(Chats.originator, Chats.open).select {
            Chats.id.eq(chatID)
        }.singleOrNull()?.let {
            it[Chats.originator] to it[Chats.open]
        } ?: return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        val isChangingUserAdministrator = ChatsAdministrators.select {
            ChatsAdministrators.chat_id.eq(chatID) and ChatsAdministrators.administrator_id.eq(userID)
        }.singleOrNull() != null
        val isChangerAdministrator =
            if (changerID != null) ChatsAdministrators.select {
                ChatsAdministrators.chat_id.eq(chatID) and ChatsAdministrators.administrator_id.eq(changerID)
            }.singleOrNull() != null
            else isChangingUserAdministrator
        if (changerID != null && !isChangerAdministrator && originator != changerID) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_CHAT
        }
        val isDeletingUser = ChatsParticipants.select {
            ChatsParticipants.participant_id.eq(userID) and ChatsParticipants.chat_id.eq(chatID)
        }.singleOrNull() != null
        if (changerID == null && !isDeletingUser && !isOpen) {
            return@databaseQuery Result.YOU_CAN_NOT_JOIN_A_CLOSED_CHAT_YOURSELF
        }
        val isChangerNotOriginator = changerID != null && changerID != originator
        val isChangerCanNotDeleteUser = isChangingUserAdministrator && isChangerNotOriginator && isDeletingUser
        if (isChangerCanNotDeleteUser) {
            return@databaseQuery Result.YOU_CAN_NOT_DELETE_ADMINISTRATOR_FROM_CHAT_AS_ADMINISTRATOR
        }
        if (isDeletingUser) {
            ChatsParticipants.deleteWhere { participant_id.eq(userID) and chat_id.eq(chatID) }
            if (isChangingUserAdministrator) ChatsAdministrators.deleteWhere {
                administrator_id.eq(userID) and chat_id.eq(chatID)
            }
        } else {
            ChatsParticipants.insert {
                it[participant_id] = userID
                it[chat_id] = chatID
            }
        }
        return@databaseQuery Result.OK
    }

    override suspend fun changeParticipantAsAdministrator(
        updaterID: Int,
        chatAdministrator: ChatAdministratorUpdate
    ): Result = databaseQuery {
        val originator = Chats.select {
            Chats.id.eq(chatAdministrator.chatID)
        }.singleOrNull()?.let {
            it[Chats.originator]
        } ?: return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        if (updaterID != originator) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_CHAT
        }
        val isUpdatingUserNotParticipantOfChat = ChatsParticipants.select {
            ChatsParticipants.participant_id.eq(chatAdministrator.updatingID) and
                    ChatsParticipants.chat_id.eq(chatAdministrator.chatID)
        }.singleOrNull() == null
        if (isUpdatingUserNotParticipantOfChat) {
            return@databaseQuery Result.ADMINISTRATOR_MUST_BE_PARTICIPANT_OF_THE_CHAT
        }
        val isUpdatingUserAlreadyAdministrator = ChatsAdministrators.select {
            ChatsAdministrators.administrator_id.eq(chatAdministrator.updatingID) and
                    ChatsAdministrators.chat_id.eq(chatAdministrator.chatID)
        }.singleOrNull() != null
        if (isUpdatingUserAlreadyAdministrator) ChatsAdministrators.deleteWhere {
            administrator_id.eq(chatAdministrator.updatingID) and chat_id.eq(chatAdministrator.chatID)
        }
        else ChatsAdministrators.insert {
            it[administrator_id] = chatAdministrator.updatingID
            it[chat_id] = chatAdministrator.chatID
        }
        return@databaseQuery Result.OK
    }

    override suspend fun updateChatName(chat: ChatNameUpdate, userID: Int): Result = databaseQuery {
        val originator = Chats.slice(Chats.originator).select {
            Chats.id.eq(chat.chatID)
        }.singleOrNull()?.let {
            it[Chats.originator]
        } ?: return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        if (originator != userID) {
            return@databaseQuery Result.YOU_CAN_NOT_MANAGE_THIS_CHAT
        }
        Chats.update(
            where = { Chats.id.eq(chat.chatID) }
        ) {
            it[name] = chat.name
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteChat(chatID: Long, userID: Int): Result = databaseQuery {
        val deletedChatsCount = Chats.deleteWhere {
            id.eq(chatID) and originator.eq(userID)
        }
        if (deletedChatsCount != 1) {
            return@databaseQuery Result.CHAT_WITH_SUCH_ID_NOT_FOUND
        }
        return@databaseQuery Result.OK
    }

    private fun setID() = CoroutineScope(Dispatchers.IO).launch {
        databaseQuery {
            val chatsMaximalID = Chats.slice(Chats.id).selectAll()
                .orderBy(Chats.id to SortOrder.DESC)
                .limit(1)
                .singleOrNull()
            val dialogsMaximalID = Dialogs.slice(Dialogs.id).selectAll()
                .orderBy(Dialogs.id to SortOrder.DESC)
                .limit(1)
                .singleOrNull()
            val id = if (chatsMaximalID != null && dialogsMaximalID != null) max(
                dialogsMaximalID[Dialogs.id], chatsMaximalID[Chats.id]
            )
            else if (chatsMaximalID == null && dialogsMaximalID != null) dialogsMaximalID[Dialogs.id]
            else if (chatsMaximalID != null) chatsMaximalID[Chats.id]
            else 1L
            newID = AtomicLong(id + 1)
        }
    }

    private fun getChatsParticipantsAndAdministrators(
        chatsIDs: List<Long>
    ): Pair<Map<Long, List<Int>>, Map<Long, List<Int>>> {
        val participants = ChatsParticipants.select {
            ChatsParticipants.chat_id.inList(chatsIDs)
        }.map {
            it[ChatsParticipants.chat_id] to it[ChatsParticipants.participant_id]
        }
        val administrators = ChatsAdministrators.select {
            ChatsAdministrators.chat_id.inList(chatsIDs)
        }.map { it[ChatsAdministrators.chat_id] to it[ChatsAdministrators.administrator_id] }
        return buildMap<Long, List<Int>> {
            for ((chatID, participantID) in participants) set(
                key = chatID,
                value = getOrDefault(chatID, listOf()).plus(participantID)
            )
        } to buildMap {
            for ((chatID, administratorID) in administrators) set(
                key = chatID,
                value = getOrDefault(chatID, listOf()).plus(administratorID)
            )
        }
    }
}