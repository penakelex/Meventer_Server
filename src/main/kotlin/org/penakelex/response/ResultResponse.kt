package org.penakelex.response

import kotlinx.serialization.Serializable

/**
 * Data transfer object for responds
 * @property code HTTP status code from the [Result] value
 * @property message message from the [Result] value
 * */
@Serializable
data class ResultResponse(
    val code: UShort,
    val message: String
)

fun Result.toResultResponse() = ResultResponse(code, message)