package org.penakelex.routes.user

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.MessagingException
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.penakelex.database.models.*
import org.penakelex.database.services.Service
import org.penakelex.response.Result
import org.penakelex.response.toResponse
import org.penakelex.response.toResultResponse
import org.penakelex.session.JWTValues
import org.penakelex.session.UserEmailValues
import org.penakelex.session.generateToken
import java.util.Properties


class UsersControllerImplementation(
    private val service: Service,
    private val valuesJWT: JWTValues,
    private val properties: Properties,
    private val userEmailValues: UserEmailValues,
    private val authenticator: Authenticator
) : UsersController {
    override suspend fun sendEmailCode(call: ApplicationCall) {
        val userEmail = call.receive<UserEmail>()
        call.respond(Result.SENDING_VERIFICATION_CODE_STARTED.toResultResponse())
        val code = buildString {
            val range = 0..9
            repeat(6) { append(range.random()) }
        }
        try {
            Transport.send(
                MimeMessage(Session.getInstance(properties, authenticator)).apply {
                    setFrom(InternetAddress(userEmailValues.email, userEmailValues.personal))
                    addRecipient(Message.RecipientType.TO, InternetAddress(userEmail))
                    subject = userEmailValues.subject
                    setText(userEmailValues.getMessageBody(code))
                }
            )
        } catch (_: MessagingException) {
        }
        service.usersEmailCodesService.insertCode(userEmail, code)
    }

    override suspend fun verifyEmailCode(call: ApplicationCall) {
        call.respond(
            service.usersEmailCodesService.verifyCode(call.receive<UserEmailCode>()).toResultResponse()
        )
    }

    override suspend fun registerUser(call: ApplicationCall) {
        val user = call.receive<UserRegister>()
        val codeVerificationResult = service.usersEmailCodesService.verifyAndDeleteCode(
            user.email, user.code
        )
        if (codeVerificationResult != Result.OK) return call.respond(codeVerificationResult.toResponse())
        val (insertionResult, userID) = service.usersService.insertNewUser(user)
        if (userID == null) return call.respond(insertionResult.toResponse())
        call.respond(
            Pair(
                insertionResult, generateToken(valuesJWT, userID, user.password)
            ).toResponse()
        )

    }

    override suspend fun loginUser(call: ApplicationCall) {
        val user = call.receive<UserLogin>()
        val (checkResult, userID) = service.usersService.isEmailAndPasswordCorrect(user)
        if (userID == null) return call.respond(checkResult)
        call.respond(
            Pair(
                checkResult, generateToken(valuesJWT, userID, user.password)
            ).toResponse()
        )
    }
}