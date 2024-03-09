package org.penakelex.routes.file

import io.ktor.server.application.*
import io.ktor.server.response.*
import org.penakelex.fileSystem.FileManager
import org.penakelex.response.Result
import org.penakelex.response.toResultResponse

class FilesControllerImplementation(
    private val fileManager: FileManager
) : FilesController {
    override suspend fun getFile(call: ApplicationCall) {
        val file = fileManager.downloadFile(
            fileName = call.parameters["fileName"]
                ?: return call.respond(Result.EMPTY_FILENAME.toResultResponse())
        )
        call.respondFile(file)
    }
}