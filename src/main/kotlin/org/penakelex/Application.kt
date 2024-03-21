package org.penakelex

import com.typesafe.config.ConfigFactory
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.penakelex.plugins.*
import java.io.File

fun main() {
    val config = HoconApplicationConfig(ConfigFactory.load())
    val environment = applicationEngineEnvironment {
        val alias = config.property("ktor.security.ssl.keyAlias").getString()
        val keyStorePassword = config.property("ktor.security.ssl.keyStorePassword").getString()
        val privateKeyPassword = config.property("ktor.security.ssl.privateKeyPassword").getString()
        val sslPort = config.property("ktor.deployment.sslPort").getString().toInt()
        val keyStore = buildKeyStore {
            certificate(alias) {
                password = privateKeyPassword
                domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
            }
        }
        keyStore.saveToFile(
            File("keystore.jks"),
            keyStorePassword
        )
        module {
            module()
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = alias,
            keyStorePassword = { keyStorePassword.toCharArray() },
            privateKeyPassword = { privateKeyPassword.toCharArray() }
        ) {
            port = sslPort
        }
        connector {
            host = config.property("ktor.deployment.host").getString()
            port = sslPort
        }
        connector {
            host = config.property("ktor.deployment.host").getString()
            port = config.property("ktor.deployment.port").getString().toInt()
        }
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