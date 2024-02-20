package org.penakelex.database.models

data class UserRegister(
    val email: String,
    val password: String,
    val nickname: String,
    val avatar: String?,
    val dateOfBirth: String,
)