package org.penakelex.routes.chat

import io.ktor.websocket.*

data class Client(
    val userID: Int,
    val webSocketSession: WebSocketSession
)
