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
    ),
    FEEDBACK_FROM_SAME_USER_AND_TO_SAME_USER(
        HttpStatusCode.Forbidden.value.toUShort(),
        "Feedback from user with such ID to user with such ID already exists"
    ),
    AS_ORIGINATOR_YOU_CAN_NOT_BE_SOMEONE_ELSE(
        HttpStatusCode.Forbidden.value.toUShort(),
        "As originator you can`t be someone else"
    ),
    YOU_ARE_ALREADY_ORGANIZER_OF_THIS_EVENT(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You are already organizer of this event"
    ),
    USER_WITH_SUCH_NICKNAME_ALREADY_EXISTS(
        HttpStatusCode.Found.value.toUShort(),
        "User with such nickname already exists"
    ),
    YOU_ALREADY_HAVE_CHAT_WITH_THIS_USER(
        HttpStatusCode.Found.value.toUShort(),
        "You already have chat with this user"
    ),
    CHAT_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value.toUShort(),
        "Chat with such ID not found"
    ),
    YOU_CAN_NOT_MANAGE_THIS_CHAT(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You can`t manage this chat"
    ),
    YOU_CAN_NOT_DELETE_ADMINISTRATOR_FROM_CHAT_AS_ADMINISTRATOR(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You can`t delete administrator from chat as administrator"
    ),
    ADMINISTRATOR_MUST_BE_PARTICIPANT_OF_THE_CHAT(
        HttpStatusCode.Forbidden.value.toUShort(),
        "Administrator must be participant of the chat"
    ),
    YOU_CAN_NOT_SEND_MESSAGES_IN_THIS_CHAT(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You can`t send messages in this chat"
    ),
    MESSAGE_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT(
        HttpStatusCode.NotAcceptable.value.toUShort(),
        "Message with such ID not found or you can`t change it"
    ),
    CHAT_SESSION_FOR_USER_WITH_SUCH_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value.toUShort(),
        "Chat session for user with such ID not found"
    ),
    CHAT_SESSION_FOR_USER_WITH_SUCH_ID_AND_SUCH_SESSION_ID_NOT_FOUND(
        HttpStatusCode.NotFound.value.toUShort(),
        "Chat session for user with such ID and such session ID not found"
    ),
    USER_WITH_SUCH_ID_IS_ALREADY_CHAT_CLIENT(
        HttpStatusCode.Conflict.value.toUShort(),
        "User with such ID is already chat client"
    ),
    CAN_NOT_CREATE_FILE_FROM_GIVEN_BYTES(
        HttpStatusCode.Conflict.value.toUShort(),
        "Can`t create file from given bytes"
    ),
    FEEDBACK_WITH_SUCH_ID_NOT_FOUND_OR_YOU_CAN_NOT_CHANGE_IT(
        HttpStatusCode.NotFound.value.toUShort(),
        "Feedback with such ID not found or you can`t change it"
    ),
    YOU_ARE_NOT_PARTICIPANT_OF_THIS_CHAT(
        HttpStatusCode.Forbidden.value.toUShort(),
        "You aren`t participant of this chat"
    )
}