package org.penakelex.database.services.users

import org.penakelex.database.models.*
import org.penakelex.response.Result

/**
 * Users table service
 * */
interface UsersService {
    /**
     * Inserts new user into Users table with checking if the email is free
     * @param user user data to insert
     * @return if email isn`t free [Result.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS] to null else [Result.OK] to
     * user ID from database
     * */
    suspend fun insertNewUser(user: UserRegister, avatar: String?): Pair<Result, Int?>
    /**
     * Gets from Users table data about user by his ID
     * @param userID user ID to get his data
     * @return
     * */
    suspend fun getUserData(userID: Int): Pair<Result, User?>
    suspend fun getUsersByNickname(nickname: String): Pair<Result, List<UserShort>>
    suspend fun getUserEmail(userID: Int): Pair<Result, String?>
    suspend fun getUserAvatar(userID: Int): Pair<Result, String?>
    suspend fun updateUserData(userID: Int, userData: UserUpdate, avatar: String?): Result
    suspend fun updateEmail(userID: Int, email: UserEmail): Result
    suspend fun updatePassword(userID: Int, oldPassword: String, newPassword: String): Result

    /**
     * Checks if user email and password matches those from database
     * @param user user data to check
     * @return if user with such email doesn`t exists [Result.NO_USER_WITH_SUCH_EMAIL] to null
     * if user password doesn`t match password from database [Result.USER_PASSWORD_DOES_NOT_MATCH] to null
     * else [Result.OK] to user ID from database
     * */
    suspend fun isEmailAndPasswordCorrect(user: UserLogin): Pair<Result, Int?>
}