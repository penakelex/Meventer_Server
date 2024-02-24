package org.penakelex.response

import io.ktor.http.*

/**
 * Enum class with services functions results responses
 * @property code HTTP status code
 * @property message result message
 * */
enum class Result(
    val code: UShort,
    val message: String
) {
    OK(
        HttpStatusCode.OK.value.toUShort(), "OK"
    ),
    USER_WITH_SUCH_EMAIL_ALREADY_EXISTS(
        HttpStatusCode.Conflict.value.toUShort(),
        "User with such email already exists"
    ),
    NO_USER_WITH_SUCH_ID(
        HttpStatusCode.NotFound.value.toUShort(),
        "User with such ID not found"
    ),
    USER_PASSWORD_DOES_NOT_MATCH(
        HttpStatusCode.Conflict.value.toUShort(),
        "User password doesn`t match"
    ),
    TOKEN_IS_NOT_VALID_OR_EXPIRED(
        HttpStatusCode.Conflict.value.toUShort(),
        "Token is not valid or expired"
    ),
    NO_USER_WITH_SUCH_EMAIL(
        HttpStatusCode.NotFound.value.toUShort(),
        "User with such email not found"
    ),
    VERIFICATION_CODE_IS_INCORRECT(
        HttpStatusCode.Conflict.value.toUShort(),
        "Verification code is incorrect"
    ),
    /*VERIFICATION_CODE_SENDING_FAILED(
        HttpStatusCode.ExpectationFailed.value.toUShort(),
        "Verification code sending failed"
    ),*/
    SENDING_VERIFICATION_CODE_STARTED(
        HttpStatusCode.OK.value.toUShort(),
        "Verification code sending started"
    ),
}