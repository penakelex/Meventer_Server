package org.penakelex

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.penakelex.plugins.*

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0"
    ) {
        configureDI()
        configureSockets()
        configureSerialization()
        configureSecurity()
        configureRouting()
    }.start(true)
}

fun Application.module() {
    configureDI()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}