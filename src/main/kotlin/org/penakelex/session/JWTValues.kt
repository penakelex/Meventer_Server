package org.penakelex.session

/**
 * Data transfer object for JWT values from the config
 * */
data class JWTValues(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)
