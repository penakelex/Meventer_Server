package org.penakelex.routes.file

import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Route.fileRoutes(controller: FilesController) = route("/file") {
    get("/{fileName}") { controller.getFile(call) }
}