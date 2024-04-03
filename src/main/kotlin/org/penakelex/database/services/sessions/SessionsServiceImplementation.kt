package org.penakelex.database.services.sessions

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Sessions
import org.penakelex.response.Result

class SessionsServiceImplementation : TableService(), SessionsService {
    override suspend fun createSession(
        userID: Int,
        endOfValidity: Long
    ): Pair<Result, Int> = databaseQuery {
        return@databaseQuery Result.OK to Sessions.insertAndGetId {
            it[user_id] = userID
            it[end_of_validity] = endOfValidity
        }.value
    }

    override suspend fun checkSession(
        userID: Int,
        sessionID: Int,
        endOfValidity: Long
    ): Result = databaseQuery {
        val (userIDFromDatabase, endOfValidityFromDatabase) = Sessions.select {
            Sessions.id.eq(sessionID)
        }.singleOrNull()?.let {
            it[Sessions.user_id] to it[Sessions.end_of_validity]
        } ?: return@databaseQuery Result.SESSION_WITH_SUCH_ID_NOT_FOUND
        if (userID != userIDFromDatabase || endOfValidity != endOfValidityFromDatabase) {
            return@databaseQuery Result.SESSION_INVALID
        }
        return@databaseQuery Result.OK
    }

    override suspend fun deleteSession(userID: Int, sessionID: Int): Result = databaseQuery {
        val deletedSessionsCount = Sessions.deleteWhere {
            user_id.eq(userID) and id.eq(sessionID)
        }
        if (deletedSessionsCount != 1) return@databaseQuery Result.SESSION_WITH_SUCH_ID_NOT_FOUND
        return@databaseQuery Result.OK
    }
}