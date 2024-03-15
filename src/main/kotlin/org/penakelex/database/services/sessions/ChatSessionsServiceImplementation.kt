package org.penakelex.database.services.sessions

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.ChatSessions
import org.penakelex.response.Result

class ChatSessionsServiceImplementation : TableService(), ChatSessionsService {
    override suspend fun insertNewSession(userID: Int): Pair<Result, Int> = databaseQuery {
        return@databaseQuery Result.OK to ChatSessions.insertAndGetId {
            it[user_id] = userID
        }.value
    }

    override suspend fun getSession(userID: Int): Pair<Result, Int?> = databaseQuery {
        val sessionID = ChatSessions.select {
            ChatSessions.user_id.eq(userID)
        }.singleOrNull()?.let {
            it[ChatSessions.id].value
        } ?: return@databaseQuery Result.CHAT_SESSION_FOR_USER_WITH_SUCH_ID_NOT_FOUND to null
        return@databaseQuery Result.OK to sessionID
    }

    override suspend fun deleteSession(userID: Int, sessionID: Int): Result = databaseQuery {
        val deletedSessionsCount = ChatSessions.deleteWhere {
            user_id.eq(userID) and ChatSessions.id.eq(sessionID)
        }
        if (deletedSessionsCount != 1) {
            return@databaseQuery Result.CHAT_SESSION_FOR_USER_WITH_SUCH_ID_AND_SUCH_SESSION_ID_NOT_FOUND
        }
        return@databaseQuery Result.OK
    }
}