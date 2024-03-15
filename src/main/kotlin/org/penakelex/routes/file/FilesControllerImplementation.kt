package org.penakelex.routes.file

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.penakelex.fileSystem.FileManager
import org.penakelex.response.Result
import org.penakelex.response.toResponse
import org.penakelex.response.toResultResponse

class FilesControllerImplementation(
    private val fileManager: FileManager
) : FilesController {
    override suspend fun insertFile(call: ApplicationCall) {
        val fileName = fileManager.uploadFile(
            fileBytes = call.receive<ByteArray>()
        )
        if (fileName == null) return call.respond(
            Result.CAN_NOT_CREATE_FILE_FROM_GIVEN_BYTES.toResponse()
        )
        call.respond(
            Pair(Result.OK, fileName).toResponse()
        )
    }

    override suspend fun getFile(call: ApplicationCall) {
        val file = fileManager.downloadFile(
            fileName = call.parameters["fileName"]
                ?: return call.respond(Result.EMPTY_FILENAME.toResultResponse())
        ) ?: return call.respond(HttpStatusCode.NotFound)
        call.respondFile(file)
    }
}