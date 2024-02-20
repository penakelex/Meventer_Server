package org.penakelex.response

import kotlinx.serialization.Serializable

@Serializable
data class ResultResponse(
    val code: UShort,
    val message: String
)