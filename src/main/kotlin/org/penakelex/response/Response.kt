package org.penakelex.response

import kotlinx.serialization.Serializable

@Serializable
data class Response<Type>(
    val result: ResultResponse,
    val data: Type?
)