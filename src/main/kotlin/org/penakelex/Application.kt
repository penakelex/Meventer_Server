package org.penakelex

import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.penakelex.plugins.*
import java.io.File
import java.security.KeyStore

fun main() {
    embeddedServer(
        factory = Netty,
        environment = applicationEngineEnvironment {
            connectSsl()
            module(Application::module)
        }
    ).start(wait = true)
}

fun Application.module() {
    configureDI()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
}

private fun ApplicationEngineEnvironmentBuilder.connectSsl() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val keyStorePassword = config.property(
        "ktor.security.ssl.keyStorePassword"
    ).getString().toCharArray()
    sslConnector(
        keyStore = KeyStore.getInstance(
            File(
                config.property("ktor.security.ssl.keyStore")
                    .getString()
            ),
            keyStorePassword
        ),
        keyAlias = config.property("ktor.security.ssl.keyAlias")
            .getString(),
        keyStorePassword = { keyStorePassword },
        privateKeyPassword = {
            config.property("ktor.security.ssl.privateKeyPassword")
                .getString().toCharArray()
        }
    ) {
        port = config.port
        host = config.host
    }
}