package org.penakelex.session

/**
 * Data transfer object for sending messages with code to verify email
 * @property personal sender name
 * */
data class UserEmailValues(
    val email: String,
    val password: String,
    val personal: String,
    val subject: String,
    val former: String
) {
    /**
     * @param code generated code to verify user`s email
     * @return [String] email message body
     * */
    fun getMessageBody(code: String) = former + code
}
