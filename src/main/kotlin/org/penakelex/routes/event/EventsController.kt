package org.penakelex.routes.event

import io.ktor.server.application.*

interface EventsController {
    suspend fun createEvent(call: ApplicationCall)
    suspend fun getUserEvents(call: ApplicationCall)
    suspend fun getGlobalEvents(call: ApplicationCall)
}