package org.penakelex.database.services.users

import org.penakelex.database.models.UserRegister
import org.penakelex.response.Result

class UserServiceImplementation : UsersService {
    override suspend fun insertNewUser(user: UserRegister): Result {
        TODO("Not yet implemented")
    }

    override suspend fun loginUser(userID: Int, password: String): Result {
        TODO("Not yet implemented")
    }
}