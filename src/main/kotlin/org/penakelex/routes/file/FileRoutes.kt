package org.penakelex.routes.file

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Route.fileRoutes(controller: FilesController) = route("/file") {
    authenticate {
        post("/upload") { controller.insertFile(call) }
        get("/{fileName}") { controller.getFile(call) }
    }
}