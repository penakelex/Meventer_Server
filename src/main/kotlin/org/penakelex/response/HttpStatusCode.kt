package org.penakelex.response

import io.ktor.http.*

fun Result.toHttpStatusCode() = HttpStatusCode(
    value = code,
    description = message
)