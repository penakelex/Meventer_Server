package org.penakelex.plugins

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import org.penakelex.database.services.Service
import org.penakelex.response.Result
import org.penakelex.response.toResultResponse
import org.penakelex.session.JWTValues
import org.penakelex.session.getAlgorithm
import org.penakelex.session.PASSWORD
import org.penakelex.session.USER_ID

fun Application.configureSecurity() {
    val service by inject<Service>()
    val values by inject<JWTValues>()
    authentication {
        jwt {
            realm = values.realm
            verifier(
                JWT
                    .require(getAlgorithm(values.secret))
                    .withAudience(values.audience)
                    .withIssuer(values.issuer)
                    .build()
            )
            validate { jwtCredential ->
                val isTokenValid = service.usersService.isTokenValid(
                    jwtCredential.payload.getClaim(USER_ID).asInt() ?: return@validate null,
                    jwtCredential.payload.getClaim(PASSWORD).asString() ?: return@validate null
                ) == Result.OK
                val isTokenNotExpired = jwtCredential.payload.expiresAt.time > System.currentTimeMillis()
                if (isTokenNotExpired && isTokenValid) JWTPrincipal(jwtCredential.payload)
                else null
            }
            challenge { _, _ -> call.respond(Result.TOKEN_IS_NOT_VALID_OR_EXPIRED.toResultResponse()) }
        }
    }
}
