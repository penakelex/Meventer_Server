package org.penakelex.database.services.usersFeedback

import org.penakelex.database.models.UserFeedback
import org.penakelex.database.models.UserFeedbackCreate
import org.penakelex.response.Result

interface UsersFeedbackService {
    suspend fun insertFeedback(fromUserID: Int, feedback: UserFeedbackCreate): Result
    suspend fun getAllFeedbackToUser(id: Int): Pair<Result, List<UserFeedback>?>
}