package org.penakelex.routes.file

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.reflect.*
import org.penakelex.fileSystem.FileManager
import org.penakelex.response.Result
import org.penakelex.response.toHttpStatusCode

class FilesControllerImplementation(
    private val fileManager: FileManager
) : FilesController {
    override suspend fun insertFile(call: ApplicationCall) {
        val fileName = fileManager.uploadFile(
            fileBytes = call.receive<ByteArray>()
        )
        val result = if (fileName != null) Result.OK
        else Result.CAN_NOT_CREATE_FILE_FROM_GIVEN_BYTES
        call.respond(
            result.toHttpStatusCode(),
            fileName,
            typeInfo<String>()
        )
    }

    override suspend fun getFile(call: ApplicationCall) {
        val file = fileManager.downloadFile(
            fileName = call.parameters["fileName"]
                ?: return call.response.status(Result.EMPTY_FILENAME.toHttpStatusCode())
        ) ?: return call.response.status(HttpStatusCode.NotFound)
        call.response.status(HttpStatusCode.OK)
        call.respondFile(file)
    }
}