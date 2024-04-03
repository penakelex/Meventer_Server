package org.penakelex.session

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.penakelex.ecnryption.cipher
import java.util.*

const val USER_ID = "userID"
const val SESSION_ID = "password"
private const val One_Year_In_Millis = 31_536_000_000

class JWTController {
    /**
     * Generates JWT token
     * @param values JWT values from the config
     * @param id user id
     * @param sessionID session id
     * @return [String] JWT token
     * */
    fun generateToken(
        values: JWTValues,
        id: Int,
        sessionID: Int,
        expirationTime: Long
    ): String = JWT.create()
        .withAudience(values.audience)
        .withIssuer(values.issuer)
        .withClaim(USER_ID, id)
        .withClaim(SESSION_ID, sessionID.cipher())
        .withExpiresAt(Date(expirationTime))
        .sign(getAlgorithm(values.secret))

    /**
     * @param secret secret from the config
     * @return JWT [Algorithm]
     * */
    fun getAlgorithm(secret: String): Algorithm = Algorithm.HMAC512(secret)

    fun getExpirationTime() = System.currentTimeMillis() + One_Year_In_Millis
}