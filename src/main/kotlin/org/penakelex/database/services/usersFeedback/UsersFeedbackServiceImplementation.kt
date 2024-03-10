package org.penakelex.database.services.usersFeedback

import org.jetbrains.exposed.sql.*
import org.penakelex.database.models.UserFeedback
import org.penakelex.database.models.UserFeedbackCreate
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Users
import org.penakelex.database.tables.UsersFeedback
import org.penakelex.response.Result

class UsersFeedbackServiceImplementation : TableService(), UsersFeedbackService {
    override suspend fun insertFeedback(fromUserID: Int, feedback: UserFeedbackCreate): Result = databaseQuery {
        if (fromUserID == feedback.toUserID) return@databaseQuery Result.YOU_CAN_NOT_FEEDBACK_YOURSELF
        val usersCount = Users.select {
            Users.id.eq(fromUserID) or Users.id.eq(feedback.toUserID)
        }.toList().size
        //TODO: Перенести изменения отсюда
        if (usersCount != 2) return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        val feedbackWithSameUsersID = UsersFeedback.select {
            UsersFeedback.to_user_id.eq(feedback.toUserID) and UsersFeedback.from_user_id.eq(fromUserID)
        }.singleOrNull()
        if (feedbackWithSameUsersID != null) {
            return@databaseQuery Result.FEEDBACK_FROM_SAME_USER_AND_TO_SAME_USER
        }
        UsersFeedback.insert {
            it[to_user_id] = feedback.toUserID
            it[from_user_id] = fromUserID
            it[rating] = feedback.rating
            it[comment] = feedback.comment
        }
        return@databaseQuery Result.OK
    }

    override suspend fun getAllFeedbackToUser(id: Int): Pair<Result, List<UserFeedback>?> = databaseQuery {
        val feedbacks = UsersFeedback.select { UsersFeedback.to_user_id.eq(id) }
            .orderBy(UsersFeedback.id to SortOrder.ASC).map {
                UserFeedback(
                    fromUserID = it[UsersFeedback.from_user_id],
                    rating = it[UsersFeedback.rating],
                    comment = it[UsersFeedback.comment]
                )
            }
        if (feedbacks.isEmpty()) return@databaseQuery Result.FEEDBACKS_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        return@databaseQuery Result.OK to feedbacks
    }
}