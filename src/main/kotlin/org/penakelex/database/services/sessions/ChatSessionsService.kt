package org.penakelex.database.services.sessions

import org.penakelex.response.Result

interface ChatSessionsService {
    suspend fun insertNewSession(userID: Int): Pair<Result, Int>
    suspend fun getSession(userID: Int): Pair<Result, Int?>
    suspend fun deleteSession(userID: Int, sessionID: Int): Result
}