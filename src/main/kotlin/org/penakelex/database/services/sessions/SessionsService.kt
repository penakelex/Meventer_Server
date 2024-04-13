package org.penakelex.database.services.sessions

import org.penakelex.response.Result

interface SessionsService {
    suspend fun createSession(userID: Int, endOfValidity: Long): Pair<Result, Int>
    suspend fun checkSession(userID: Int, sessionID: Int, endOfValidity: Long): Result
    suspend fun deleteSession(userID: Int, sessionID: Int): Result
}
