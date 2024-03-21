package org.penakelex

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.penakelex.plugins.*

fun main() {
    val environment = applicationEngineEnvironment {
        connector {
            host = "0.0.0.0"
            port = 8080
        }
        connector {
            host = "0.0.0.0"
            port = 8085
        }
        module(Application::module)
    }
    embeddedServer(Netty, environment).start(true)
}

fun Application.module() {
    configureDI()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}