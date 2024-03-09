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
    SENDING_VERIFICATION_CODE_STARTED(
        HttpStatusCode.OK.value.toUShort(),
        "Verification code sending started"
    ),
    EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NoContent.value.toUShort(),
        "Events for user with such ID not found"
    ),
    EMPTY_FORM_ITEM_OF_MULTI_PART_DATA(
        HttpStatusCode.ExpectationFailed.value.toUShort(),
        "Empty form item of multi part data"
    ),
    FEATURED_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NoContent.value.toUShort(),
        "Featured events for user with such ID not found"
    ),
    ORGANIZER_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NoContent.value.toUShort(),
        "Events where user with such ID is organizer not found"
    ),
    PARTICIPANT_EVENTS_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NoContent.value.toUShort(),
        "Events where user with such ID is participant not found"
    ),
    UNRESOLVED_EVENT_TYPE(
        HttpStatusCode.Conflict.value.toUShort(),
        "Unresolved event type"
    ),
    EVENT_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value.toUShort(),
        "Event with such ID not found"
    ),
    YOU_CAN_NOT_MANAGE_THIS_EVENT(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You can`t manage this event"
    ),
    YOU_CAN_NOT_FEEDBACK_YOURSELF(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You can`t feedback yourself"
    ),
    FEEDBACKS_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value.toUShort(),
        "Feedbacks for user with such ID not found"
    ),
    YOUR_AGE_DOES_NOT_MATCH_THE_REQUIRED_BY_THE_EVENT_ORGANIZERS(
        HttpStatusCode.Forbidden.value.toUShort(),
        "Your age doesn`t match the required by the event organizers"
    ),
    USER_CAN_NOT_HAVE_MORE_THAN_ONE_AVATAR(
        HttpStatusCode.Forbidden.value.toUShort(),
        "User can`t have more than one avatar"
    ),
    EMPTY_FILENAME(
        HttpStatusCode.NotFound.value.toUShort(),
        "Empty filename"
    ),
    EMPTY_EVENT_ID(
        HttpStatusCode.NotFound.value.toUShort(),
        "Empty event ID"
    )
}