package org.penakelex.database.services.usersEmailCodes

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.penakelex.database.models.UserEmail
import org.penakelex.database.models.UserEmailCode
import org.penakelex.database.services.TableService
import org.penakelex.database.tables.UsersEmailCodes
import org.penakelex.response.Result


private const val TEN_MINUTES_IN_MILLIS = 600_000
/**
 * UsersEmailCodes table service implementation
 * */
class UsersEmailCodesServiceImplementation : UsersEmailCodesService, TableService() {
    override suspend fun insertCode(email: UserEmail, code: String): Unit = databaseQuery {
        val codeWithSameEmail = UsersEmailCodes.select {
            UsersEmailCodes.email.eq(email)
        }.singleOrNull()
        if (codeWithSameEmail != null) {
            UsersEmailCodes.update({ UsersEmailCodes.email.eq(email) }) {
                it[UsersEmailCodes.code] = code
                it[expiration_time] = System.currentTimeMillis() + TEN_MINUTES_IN_MILLIS
            }
        } else {
            UsersEmailCodes.insert {
                it[UsersEmailCodes.email] = email
                it[UsersEmailCodes.code] = code
                it[expiration_time] = System.currentTimeMillis() + TEN_MINUTES_IN_MILLIS
            }
        }
    }

    override suspend fun verifyCode(emailCode: UserEmailCode): Result = databaseQuery {
        val (code, expirationTime) = UsersEmailCodes.select {
            UsersEmailCodes.email.eq(emailCode.email)
        }.singleOrNull().let {
            if (it == null) return@databaseQuery Result.VERIFICATION_CODE_IS_INCORRECT
            else it[UsersEmailCodes.code] to it[UsersEmailCodes.expiration_time]
        }
        return@databaseQuery if (code != emailCode.code
            || System.currentTimeMillis() >= expirationTime
        ) Result.VERIFICATION_CODE_IS_INCORRECT
        else Result.OK
    }

    override suspend fun verifyAndDeleteCode(email: UserEmail, code: String): Result = databaseQuery {
        val (codeFromDatabase, expirationTime) = UsersEmailCodes.select {
            UsersEmailCodes.email.eq(email)
        }.singleOrNull().let {
            if (it == null) return@databaseQuery Result.VERIFICATION_CODE_IS_INCORRECT
            else it[UsersEmailCodes.code] to it[UsersEmailCodes.expiration_time]
        }
        UsersEmailCodes.deleteWhere { UsersEmailCodes.email.eq(email) }
        return@databaseQuery if (code != codeFromDatabase
            || expirationTime <= System.currentTimeMillis()
        ) Result.VERIFICATION_CODE_IS_INCORRECT
        else Result.OK
    }
}