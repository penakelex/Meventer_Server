package org.penakelex.database.services.usersEmailCodes

import org.penakelex.database.models.UserEmail
import org.penakelex.database.models.UserEmailCode
import org.penakelex.response.Result

/**
 * UserEmailCodes table service
 * */
interface UsersEmailCodesService {
    /**
     * Inserts email verification code into the database or updates previous
     * @param email user email to identify code
     * @param code generated code to verify email address
     * */
    suspend fun insertCode(email: UserEmail, code: String)

    /**
     * Checks email and code with those from the database
     * @param emailCode user email and code to check
     * @return [Result.VERIFICATION_CODE_IS_INCORRECT] if code doesn`t exists,
     * doesn`t match code from the database or expired
     * else [Result.OK] if everything is fine
     * */
    suspend fun verifyCode(emailCode: UserEmailCode): Result

    /**
     * Checks email and code with those from the database and deletes it from the database
     * @param email email to check
     * @param code code to check
     * @return [Result.VERIFICATION_CODE_IS_INCORRECT] if code doesn`t exists,
     * doesn`t match code from the database or expired
     * else [Result.OK] if everything is fine
     * */
    suspend fun verifyAndDeleteCode(email: UserEmail, code: String): Result

    /**
     * Deletes expired codes from the database
     * */
    suspend fun deleteExpiredCodes()
}