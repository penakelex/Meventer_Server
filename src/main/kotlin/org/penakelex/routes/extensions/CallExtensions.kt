package org.penakelex.routes.extensions

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun ApplicationCall.getIntJWTPrincipalClaim(
    claimName: String
): Int = principal<JWTPrincipal>()!!.payload.getClaim(claimName).asInt()