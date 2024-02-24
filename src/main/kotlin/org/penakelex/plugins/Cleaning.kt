package org.penakelex.plugins

import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.ktor.ext.inject
import org.penakelex.database.services.Service

private const val TEN_MINUTES_IN_MILLIS = 600_000L

fun Application.configureCleaning() {
    val service by inject<Service>()
    launch(Dispatchers.Default) {
        while (true) {
            service.usersEmailCodesService.deleteExpiredCodes()
            delay(TEN_MINUTES_IN_MILLIS)
        }
    }
}