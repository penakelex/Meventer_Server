package org.penakelex.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.penakelex.di.mainModule

fun Application.configureDI() {
    install(Koin) {
        modules(mainModule)
    }
}