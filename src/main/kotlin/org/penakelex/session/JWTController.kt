package org.penakelex.session

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

const val USER_ID = "userID"
const val PASSWORD = "password"
private const val One_Year_In_Millis = 31_536_000_000

/**
 * Generates JWT token
 * @param values JWT values from the config
 * @param id user id
 * @param userPassword user password
 * @return [String] JWT token
 * */
fun generateToken(
    values: JWTValues,
    id: Int,
    userPassword: String
): String = JWT.create()
    .withAudience(values.audience)
    .withIssuer(values.issuer)
    .withClaim(USER_ID, id)
    .withClaim(PASSWORD, userPassword)
    .withExpiresAt(Date(System.currentTimeMillis() + One_Year_In_Millis))
    .sign(getAlgorithm(values.secret))

/**
 * @param secret secret from the config
 * @return JWT [Algorithm]
 * */
fun getAlgorithm(secret: String): Algorithm = Algorithm.HMAC512(secret)