package org.penakelex.database.services.chats

import org.penakelex.database.models.Chat
import org.penakelex.database.models.ChatAdministratorUpdate
import org.penakelex.database.models.ChatCreate
import org.penakelex.database.models.ChatNameUpdate
import org.penakelex.response.Result

interface ChatsService {
    suspend fun createChat(originatorID: Int, chat: ChatCreate, open: Boolean): Pair<Result, Long?>
    suspend fun createDialog(firstUserID: Int, secondUserID: Int): Pair<Result, Long?>
    suspend fun getChatParticipants(chatID: Long): Pair<Result, List<Int>?>
    suspend fun getAllChats(userID: Int): Pair<Result, List<Chat>?>
    suspend fun changeUserAsParticipant(chatID: Long, userID: Int, changerID: Int? = null): Result
    suspend fun changeParticipantAsAdministrator(updaterID: Int, chatAdministrator: ChatAdministratorUpdate): Result
    suspend fun updateChatName(chat: ChatNameUpdate, userID: Int): Result
    suspend fun deleteChat(chatID: Long, userID: Int): Result
}