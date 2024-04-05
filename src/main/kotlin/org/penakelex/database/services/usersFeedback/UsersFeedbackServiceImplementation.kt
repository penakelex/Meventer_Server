package org.penakelex.database.services.usersFeedback

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.penakelex.database.models.UserFeedback
import org.penakelex.database.models.UserFeedbackCreate
import org.penakelex.database.models.UserFeedbackUpdate
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Users
import org.penakelex.database.tables.UsersFeedback
import org.penakelex.response.Result

class UsersFeedbackServiceImplementation : TableService(), UsersFeedbackService {
    override suspend fun insertFeedback(fromUserID: Int, feedback: UserFeedbackCreate): Result = databaseQuery {
        if (fromUserID == feedback.toUserID) return@databaseQuery Result.YOU_CAN_NOT_FEEDBACK_YOURSELF
        val usersCount = Users.select {
            Users.id.eq(fromUserID) or Users.id.eq(feedback.toUserID)
        }.count().toInt()
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
                    id = it[UsersFeedback.id].value,
                    fromUserID = it[UsersFeedback.from_user_id],
                    rating = it[UsersFeedback.rating],
                    comment = it[UsersFeedback.comment]
                )
            }
        return@databaseQuery Result.OK to feedbacks
    }

    override suspend fun updateFeedback(userID: Int, feedback: UserFeedbackUpdate): Result = databaseQuery {
        val updatedFeedbacksCount = UsersFeedback.update(
            where = { UsersFeedback.id.eq(feedback.id) and UsersFeedback.from_user_id.eq(userID) }
        ) {
            it[rating] = feedback.rating
            it[comment] = feedback.comment
        }
        if (updatedFeedbacksCount != 1) {
            return@databaseQuery Result.FEEDBACK_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteFeedback(userID: Int, feedbackID: Long): Result = databaseQuery {
        val deletedFeedbacksCount = UsersFeedback.deleteWhere {
            UsersFeedback.id.eq(feedbackID) and from_user_id.eq(userID)
        }
        if (deletedFeedbacksCount != 1) {
            return@databaseQuery Result.FEEDBACK_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT
        }
        return@databaseQuery Result.OK
    }
}