package org.penakelex.routes.event

import io.ktor.server.application.*

interface EventsController {
    suspend fun createEvent(call: ApplicationCall)
    suspend fun updateEvent(call: ApplicationCall)
    suspend fun deleteEvent(call: ApplicationCall)
    suspend fun getEvent(call: ApplicationCall)
    suspend fun changeUserAsParticipant(call: ApplicationCall)
    suspend fun changeUserAsOrganizer(call: ApplicationCall)
    suspend fun changeEventInFavourites(call: ApplicationCall)
    suspend fun getUserEvents(call: ApplicationCall)
    suspend fun getGlobalEvents(call: ApplicationCall)
}