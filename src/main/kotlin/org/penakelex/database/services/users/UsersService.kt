package org.penakelex.database.services.users

import org.penakelex.database.models.UserRegister
import org.penakelex.response.Result

interface UsersService {
    suspend fun insertNewUser(user: UserRegister): Result
    suspend fun loginUser(userID: Int, password: String): Result
}