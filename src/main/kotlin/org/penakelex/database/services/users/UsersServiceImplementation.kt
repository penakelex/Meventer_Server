package org.penakelex.database.services.users

import org.jetbrains.exposed.sql.insertIgnoreAndGetId
import org.jetbrains.exposed.sql.select
import org.penakelex.database.models.User
import org.penakelex.database.models.UserEmail
import org.penakelex.database.models.UserLogin
import org.penakelex.database.models.UserRegister
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.Users
import org.penakelex.ecnryption.cipher
import org.penakelex.ecnryption.unCipher
import org.penakelex.response.Result
import java.util.*

/**
 * Users table service implementation
 * */
class UsersServiceImplementation(
    private val basicAvatar: String
) : UsersService, TableService() {
    override suspend fun checkIfEmailIsTaken(userEmail: UserEmail): Result = databaseQuery {
        Users.select {
            Users.email.eq(userEmail)
        }.singleOrNull() ?: return@databaseQuery Result.OK
        return@databaseQuery Result.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS
    }

    override suspend fun insertNewUser(user: UserRegister, avatar: String?): Pair<Result, Int?> = databaseQuery {
        val checkResult = checkIfEmailIsTaken(user.email)
        if (checkResult != Result.OK) return@databaseQuery checkResult to null
        val userWithSameNickname = if (user.nickname == null) null else {
            Users.select { Users.nickname.eq(user.nickname) }.singleOrNull()
        }
        if (userWithSameNickname != null) return@databaseQuery Result.USER_WITH_SUCH_NICKNAME_ALREADY_EXISTS to null
        return@databaseQuery Result.OK to Users.insertIgnoreAndGetId {
            it[email] = user.email
            it[password] = user.password.cipher()
            it[name] = user.name
            it[nickname] = user.nickname ?: UUID.randomUUID().toString()
            it[Users.avatar] = avatar ?: basicAvatar
            it[date_of_birth] = user.dateOfBirth
        }?.value
    }

    override suspend fun getUserData(id: Int): Pair<Result, User?> = databaseQuery {
        val user = Users.select { Users.id.eq(id) }.singleOrNull()?.let {
            User(
                id = it[Users.id].value,
                email = it[Users.email],
                avatar = it[Users.avatar],
                dateOfBirth = it[Users.date_of_birth]
            )
        } ?: return@databaseQuery Result.NO_USER_WITH_SUCH_ID to null
        return@databaseQuery Result.OK to user
    }

    override suspend fun isEmailAndPasswordCorrect(user: UserLogin): Pair<Result, Int?> = databaseQuery {
        val (id, passwordFromDatabase) = Users.select {
            Users.email.eq(user.email)
        }.singleOrNull()?.let {
            it[Users.id] to it[Users.password]
        } ?: return@databaseQuery Result.NO_USER_WITH_SUCH_EMAIL to null
        if (passwordFromDatabase.unCipher() != user.password) {
            return@databaseQuery Result.USER_PASSWORD_DOES_NOT_MATCH to null
        }
        return@databaseQuery Result.OK to id.value
    }

    override suspend fun isTokenValid(userID: Int, password: String): Result = databaseQuery {
        val userPasswordFromDatabase = Users.select {
            Users.id.eq(userID)
        }.singleOrNull()?.let {
            it[Users.password]
        } ?: return@databaseQuery Result.NO_USER_WITH_SUCH_ID
        if (userPasswordFromDatabase.unCipher() != password) {
            return@databaseQuery Result.USER_PASSWORD_DOES_NOT_MATCH
        }
        return@databaseQuery Result.OK
    }
}