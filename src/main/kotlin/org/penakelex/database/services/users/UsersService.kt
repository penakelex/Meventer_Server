package org.penakelex.database.services.users

import org.penakelex.database.models.User
import org.penakelex.database.models.UserEmail
import org.penakelex.database.models.UserLogin
import org.penakelex.database.models.UserRegister
import org.penakelex.response.Result

/**
 * Users table service
 * */
interface UsersService {
    /**
     * Checks if email is taken by someone else
     * @param userEmail email to check
     * @return if email is free [Result.OK] else [Result.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS]
     * */
    suspend fun checkIfEmailIsTaken(userEmail: UserEmail): Result

    /**
     * Inserts new user into Users table with checking if the email is free
     * @param user user data to insert
     * @return if email isn`t free [Result.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS] to null else [Result.OK] to
     * user ID from database
     * */
    suspend fun insertNewUser(user: UserRegister): Pair<Result, Int?>
    suspend fun getUserData(id: Int): Pair<Result, User?>

    /**
     * Checks if user email and password matches those from database
     * @param user user data to check
     * @return if user with such email doesn`t exists [Result.NO_USER_WITH_SUCH_EMAIL] to null
     * if user password doesn`t match password from database [Result.USER_PASSWORD_DOES_NOT_MATCH] to null
     * else [Result.OK] to user ID from database
     * */
    suspend fun isEmailAndPasswordCorrect(user: UserLogin): Pair<Result, Int?>

    /**
     * Checks the validity of the token by checking id and password
     * @param userID user id from token
     * @param password user password from token
     * @return [Result.NO_USER_WITH_SUCH_ID] if user with such id doesn`t exists
     * [Result.USER_PASSWORD_DOES_NOT_MATCH] if password doesn`t match password from database
     * else [Result.OK] if everything is fine
     * */
    suspend fun isTokenValid(userID: Int, password: String): Result
}