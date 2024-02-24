package org.penakelex.database.models

import kotlinx.serialization.Serializable

/**
 * [String] typealias for user email
 * */
typealias UserEmail = String

/**
 * Data transfer object for checking user email code
 * @property email email to which the code was sent
 * @property code code sent by email
 * */
@Serializable
data class UserEmailCode(
    val email: String,
    val code: String
)