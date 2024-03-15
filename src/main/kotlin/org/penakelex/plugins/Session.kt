package org.penakelex.plugins

import io.ktor.server.application.*
import io.ktor.server.application.ApplicationCallPipeline.ApplicationPhase.Plugins
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.sessions.*
import org.koin.ktor.ext.inject
import org.penakelex.database.services.Service
import org.penakelex.response.Result
import org.penakelex.session.ChatSession
import org.penakelex.session.PASSWORD
import org.penakelex.session.USER_ID

fun Application.configureSessions() {
    val service by inject<Service>()
    install(Sessions) {
        cookie<ChatSession>("chat_session")
    }
    intercept(Plugins) {
        if (call.sessions.get<ChatSession>() != null) return@intercept
        val payload = call.principal<JWTPrincipal>()!!.payload
        val userID = payload.getClaim(USER_ID).asInt()
        val isTokenValidResult = service.usersService.isTokenValid(
            userID = userID,
            password = payload.getClaim(PASSWORD).asString()
        )
        if (isTokenValidResult != Result.OK) return@intercept
        val (_, sessionID) = service.chatSessionsService.insertNewSession(userID)
        call.sessions.set(
            ChatSession(
                userID = userID,
                sessionID = sessionID
            )
        )
    }
}