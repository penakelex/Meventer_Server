@file:UseSerializers(LocalDateSerializer::class)
package org.penakelex.database.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.penakelex.database.extenstions.LocalDateSerializer
import java.time.LocalDate

/**
 * Data transfer object for user registration
 * @property code code sent by email
 * @property email user email
 * @property password user password
 * @property nickname user nickname
 * @property dateOfBirth date of birth user
 * */
@Serializable
data class UserRegister(
    val code: String,
    val email: String,
    val password: String,
    val nickname: String?,
    val name: String,
    //TODO: String to LocalDate
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

@Serializable
data class User(
    val id: Int,
    val email: String,
    val avatar: String,
    val dateOfBirth: LocalDate
)

@Serializable
data class NullableUserID(
    val id: Int?
)