package org.penakelex.routes.file

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.penakelex.fileSystem.FileManagerImplementation
import org.penakelex.response.Result
import org.penakelex.response.toResultResponse

class FilesControllerImplementation(
    private val fileManager: FileManagerImplementation
) : FilesController {
    override suspend fun getFile(call: ApplicationCall) {
        val file = fileManager.downloadFile(
            fileName = call.parameters["fileName"]
                ?: return call.respond(Result.EMPTY_FILENAME.toResultResponse())
        ) ?: return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}