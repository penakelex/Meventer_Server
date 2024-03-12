package org.penakelex.routes.user

import io.ktor.server.application.*

interface UsersController {
    suspend fun verifyToken(call: ApplicationCall)
    suspend fun sendEmailCode(call: ApplicationCall)
    suspend fun verifyEmailCode(call: ApplicationCall)
    suspend fun registerUser(call: ApplicationCall)
    suspend fun loginUser(call: ApplicationCall)
    suspend fun getUserData(call: ApplicationCall)
    suspend fun createFeedback(call: ApplicationCall)
    suspend fun getFeedbackToUser(call: ApplicationCall)
}