package org.penakelex.response

import kotlinx.serialization.Serializable

/**
 * Data transfer object for responds
 * @property result [ResultResponse] from [Result] value
 * @property data data to respond
 * */
@Serializable
data class Response<Type>(
    val result: ResultResponse,
    val data: Type? = null
)

fun <Type> Pair<Result, Type>.toResponse() = Response(
    result = first.toResultResponse(),
    data = second
)

fun Result.toResponse() = Response<Nothing>(toResultResponse())