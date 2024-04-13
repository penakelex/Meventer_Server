package org.penakelex.routes.file

import io.ktor.server.application.*

interface FilesController {
    suspend fun insertFile(call: ApplicationCall)
    suspend fun getFile(call: ApplicationCall)
}