package org.penakelex.database.models

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val chatID: Long,
    val name: String?,
    val originator: Int?,
    val participants: List<Int>,
    val administrators: List<Int>?,
    val lastMessages: List<Message>
)

@Serializable
data class ChatAdministratorUpdate(
    val updatingID: Int,
    val chatID: Long
)

@Serializable
data class ChatCreate(
    val name: String,
    val administrators: List<Int>
)

@Serializable
data class ChatNameUpdate(
    val chatID: Long,
    val name: String
)

@Serializable
data class ChatParticipantUpdate(
    val chatID: Long,
    val changingID: Int?
)