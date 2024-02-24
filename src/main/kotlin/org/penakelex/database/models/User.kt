package org.penakelex.database.models

import kotlinx.serialization.Serializable

/**
 * Data transfer object for user registration
 * @property code code sent by email
 * @property email user email
 * @property password user password
 * @property nickname user nickname
 * @property avatar file path to the picture
 * @property dateOfBirth date of birth user
 * */
@Serializable
data class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String?,
    val dateOfBirth: String,
)


/**
 * Data transfer object for user login
 * @property email registered user email
 * @property password registered user login
 * */
@Serializable
data class UserLogin(
    val email: String,
    val password: String
)