package org.penakelex.database.services.users

import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.penakelex.database.models.UserEmail
import org.penakelex.database.models.UserLogin
import org.penakelex.database.models.UserRegister
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Users
import org.penakelex.response.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val Date_Pattern = "yyyy-MM-dd"

/**
 * Users table service implementation
 * */
class UsersServiceImplementation : UsersService, TableService() {
    override suspend fun checkIfEmailIsTaken(userEmail: UserEmail): Result = databaseQuery {
        val userWithSameEmail = Users.select { Users.email.eq(userEmail) }.singleOrNull()
        return@databaseQuery if (userWithSameEmail != null) {
            Result.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS
        } else Result.OK
    }

    override suspend fun insertNewUser(user: UserRegister): Pair<Result, Int?> = databaseQuery {
        val checkResult = checkIfEmailIsTaken(user.email)
        if (checkResult != Result.OK) return@databaseQuery checkResult to null
        return@databaseQuery Result.OK to Users.insertIgnoreAndGetId {
            it[email] = user.email
            it[password] = user.password
            it[nickname] = user.nickname
            it[avatar] = user.avatar ?: ""
            it[date_of_birth] = LocalDate.parse(
                user.dateOfBirth, DateTimeFormatter.ofPattern(Date_Pattern)
            )
        }?.value
    }

    override suspend fun isEmailAndPasswordCorrect(user: UserLogin): Pair<Result, Int?> = databaseQuery {
        val (id, passwordFromDatabase) = Users.select {
            Users.email.eq(user.email)
        }.singleOrNull().let {
            if (it == null) return@databaseQuery Result.NO_USER_WITH_SUCH_EMAIL to null
            else it[Users.id] to it[Users.password]
        }
        if (passwordFromDatabase != user.password) {
            return@databaseQuery Result.USER_PASSWORD_DOES_NOT_MATCH to null
        }
        return@databaseQuery Result.OK to id.value
    }

    override suspend fun isTokenValid(userID: Int, password: String): Result = databaseQuery {
        val userPasswordFromDatabase = Users.select { Users.id.eq(userID) }.singleOrNull().let {
            if (it == null) return@databaseQuery Result.NO_USER_WITH_SUCH_ID
            else it[Users.password]
        }
        if (userPasswordFromDatabase != password) {
            return@databaseQuery Result.USER_PASSWORD_DOES_NOT_MATCH
        }
        return@databaseQuery Result.OK
    }
}