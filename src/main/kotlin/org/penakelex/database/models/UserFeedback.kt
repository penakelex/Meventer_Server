package org.penakelex.database.models

import kotlinx.serialization.Serializable

/**
 * Data transfer object for creating feedback to user
 * @property toUserID user ID to receive feedback
 * @property rating feedback rating
 * @property comment feedback comment
 * */
@Serializable
data class UserFeedbackCreate(
    val toUserID: Int,
    val rating: Float,
    val comment: String
)

/**
 * Data transfer object for user feedback
 * @property fromUserID user ID that left feedback
 * @property rating feedback rating
 * @property comment feedback comment
 * */
@Serializable
data class UserFeedback(
    val id: Long,
    val fromUserID: Int,
    val rating: Float,
    val comment: String
)

@Serializable
data class UserFeedbackUpdate(
    val id: Long,
    val rating: Float,
    val comment: String
)