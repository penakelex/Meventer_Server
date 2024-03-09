package org.penakelex

import io.ktor.server.application.*
import org.penakelex.plugins.*

fun main(args: Array<String>) =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureDI()
    configureCleaning()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}