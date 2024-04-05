package org.penakelex.response

import io.ktor.http.*

/**
 * Enum class with services functions results responses
 * @property code HTTP status code
 * @property message result message
 * */
enum class Result(
    val code: Int,
    val message: String
) {
    OK(
        HttpStatusCode.OK.value, "OK"
    ),
    USER_WITH_SUCH_EMAIL_ALREADY_EXISTS(
        HttpStatusCode.Conflict.value,
        "User with such email already exists"
    ),
    NO_USER_WITH_SUCH_ID(
        HttpStatusCode.NotFound.value,
        "User with such ID not found"
    ),
    USER_PASSWORD_DOES_NOT_MATCH(
        HttpStatusCode.Conflict.value,
        "User password doesn`t match"
    ),
    TOKEN_IS_NOT_VALID_OR_EXPIRED(
        HttpStatusCode.Conflict.value,
        "Token is not valid or expired"
    ),
    NO_USER_WITH_SUCH_EMAIL(
        HttpStatusCode.NotFound.value,
        "User with such email not found"
    ),
    VERIFICATION_CODE_IS_INCORRECT(
        HttpStatusCode.Conflict.value,
        "Verification code is incorrect"
    ),
    SENDING_VERIFICATION_CODE_STARTED(
        HttpStatusCode.OK.value,
        "Verification code sending started"
    ),
    EMPTY_FORM_ITEM_OF_MULTI_PART_DATA(
        HttpStatusCode.ExpectationFailed.value,
        "Empty form item of multi part data"
    ),
    UNRESOLVED_EVENT_TYPE(
        HttpStatusCode.Conflict.value,
        "Unresolved event type"
    ),
    EVENT_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value,
        "Event with such ID not found"
    ),
    YOU_CAN_NOT_MANAGE_THIS_EVENT(
        HttpStatusCode.Forbidden.value,
        "You can`t manage this event"
    ),
    YOU_CAN_NOT_FEEDBACK_YOURSELF(
        HttpStatusCode.Forbidden.value,
        "You can`t feedback yourself"
    ),
    FEEDBACKS_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value,
        "Feedbacks for user with such ID not found"
    ),
    YOUR_AGE_DOES_NOT_MATCH_THE_REQUIRED_BY_THE_EVENT_ORGANIZERS(
        HttpStatusCode.Forbidden.value,
        "Your age doesn`t match the required by the event organizers"
    ),
    USER_CAN_NOT_HAVE_MORE_THAN_ONE_AVATAR(
        HttpStatusCode.Forbidden.value,
        "User can`t have more than one avatar"
    ),
    EMPTY_FILENAME(
        HttpStatusCode.NotFound.value,
        "Empty filename"
    ),
    EMPTY_EVENT_ID(
        HttpStatusCode.NotFound.value,
        "Empty event ID"
    ),
    FEEDBACK_FROM_SAME_USER_AND_TO_SAME_USER(
        HttpStatusCode.Forbidden.value,
        "Feedback from user with such ID to user with such ID already exists"
    ),
    ORIGINATOR_CAN_NOT_BE_SOMEONE_ELSE(
        HttpStatusCode.Forbidden.value,
        "Originator can`t be someone else"
    ),
    USER_WITH_SUCH_NICKNAME_ALREADY_EXISTS(
        HttpStatusCode.Found.value,
        "User with such nickname already exists"
    ),
    YOU_ALREADY_HAVE_CHAT_WITH_THIS_USER(
        HttpStatusCode.Found.value,
        "You already have chat with this user"
    ),
    CHAT_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value,
        "Chat with such ID not found"
    ),
    YOU_CAN_NOT_MANAGE_THIS_CHAT(
        HttpStatusCode.Forbidden.value,
        "You can`t manage this chat"
    ),
    YOU_CAN_NOT_DELETE_ADMINISTRATOR_FROM_CHAT_AS_ADMINISTRATOR(
        HttpStatusCode.Forbidden.value,
        "You can`t delete administrator from chat as administrator"
    ),
    ADMINISTRATOR_MUST_BE_PARTICIPANT_OF_THE_CHAT(
        HttpStatusCode.Forbidden.value,
        "Administrator must be participant of the chat"
    ),
    YOU_CAN_NOT_SEND_MESSAGES_IN_THIS_CHAT(
        HttpStatusCode.Forbidden.value,
        "You can`t send messages in this chat"
    ),
    MESSAGE_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT(
        HttpStatusCode.NotAcceptable.value,
        "Message with such ID not found or you can`t change it"
    ),
    USER_WITH_SUCH_ID_IS_ALREADY_CHAT_CLIENT(
        HttpStatusCode.Conflict.value,
        "User with such ID is already chat client"
    ),
    CAN_NOT_CREATE_FILE_FROM_GIVEN_BYTES(
        HttpStatusCode.Conflict.value,
        "Can`t create file from given bytes"
    ),
    FEEDBACK_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT(
        HttpStatusCode.NotFound.value,
        "Feedback with such ID not found or you can`t change it"
    ),
    YOU_ARE_NOT_PARTICIPANT_OF_THIS_CHAT(
        HttpStatusCode.Forbidden.value,
        "You aren`t participant of this chat"
    ),
    NOTHING_TO_CHANGE(
        HttpStatusCode.NotAcceptable.value,
        "Nothing to change"
    ),
    AVATAR_IS_BASIC(
        HttpStatusCode.NotModified.value,
        "Avatar is basic"
    ),
    YOU_CAN_NOT_CHANGE_OTHER_USERS_ON_THIS_EVENT(
        HttpStatusCode.Forbidden.value,
        "You can`t change other users on this event"
    ),
    SESSION_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value,
        "Session with such ID not found"
    ),
    SESSION_INVALID(
        HttpStatusCode.ExpectationFailed.value,
        "Session invalid"
    ),
    EMPTY_CONTENT_TYPE(
        HttpStatusCode.ExpectationFailed.value,
        "Empty content type"
    )
}