package org.penakelex.plugins

import com.auth0.jwt.JWT
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject
import org.penakelex.database.services.Service
import org.penakelex.ecnryption.decipher
import org.penakelex.response.Result
import org.penakelex.response.toHttpStatusCode
import org.penakelex.session.JWTController
import org.penakelex.session.JWTValues
import org.penakelex.session.SESSION_ID
import org.penakelex.session.USER_ID

fun Application.configureSecurity() {
    val service by inject<Service>()
    val values by inject<JWTValues>()
    val controller by inject<JWTController>()
    authentication {
        jwt {
            realm = values.realm
            verifier(
                JWT
                    .require(controller.getAlgorithm(values.secret))
                    .withAudience(values.audience)
                    .withIssuer(values.issuer)
                    .build()
            )
            validate { jwtCredential ->
                val isTokenNotValid = service.sessionsService.checkSession(
                    userID = jwtCredential.payload.getClaim(USER_ID).asInt()
                        ?: return@validate null,
                    sessionID = jwtCredential.payload.getClaim(SESSION_ID).asString()?.decipher()?.toInt()
                        ?: return@validate null,
                    endOfValidity = jwtCredential.payload.expiresAt.time
                ) != Result.OK
                if (isTokenNotValid) return@validate null
                return@validate JWTPrincipal(jwtCredential.payload)
            }
            challenge { _, _ ->
                call.response.status(Result.TOKEN_IS_NOT_VALID_OR_EXPIRED.toHttpStatusCode())
            }
        }
    }
}
