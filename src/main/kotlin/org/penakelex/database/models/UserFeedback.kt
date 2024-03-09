package org.penakelex.database.models

import kotlinx.serialization.Serializable

@Serializable
data class UserFeedbackCreate(
    val toUserID: Int,
    val rating: Float,
    val comment: String
)

@Serializable
data class UserFeedback(
    val fromUserID: Int,
    val rating: Float,
    val comment: String
)