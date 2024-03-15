package org.penakelex.routes.user

import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import jakarta.mail.*
import jakarta.mail.Message
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.serialization.json.Json
import org.penakelex.database.models.*
import org.penakelex.database.services.Service
import org.penakelex.fileSystem.FileManagerImplementation
import org.penakelex.response.Result
import org.penakelex.response.toResponse
import org.penakelex.response.toResultResponse
import org.penakelex.routes.extensions.getIntJWTPrincipalClaim
import org.penakelex.session.JWTValues
import org.penakelex.session.USER_ID
import org.penakelex.session.UserEmailValues
import org.penakelex.session.generateToken
import java.util.*


class UsersControllerImplementation(
    private val service: Service,
    private val valuesJWT: JWTValues,
    private val properties: Properties,
    private val userEmailValues: UserEmailValues,
    private val authenticator: Authenticator,
    private val fileManager: FileManagerImplementation
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
        service.usersEmailCodesService.insertCode(email = userEmail, code = code)
    }

    override suspend fun verifyEmailCode(call: ApplicationCall) = call.respond(
        service.usersEmailCodesService.verifyCode(
            call.receive<UserEmailCode>()
        ).toResultResponse()
    )

    override suspend fun verifyToken(call: ApplicationCall) =
        call.respond(Result.OK.toResultResponse())

    override suspend fun registerUser(call: ApplicationCall) {
        val multiPartData = call.receiveMultipart().readAllParts()
        val user: UserRegister = multiPartData.filterIsInstance<PartData.FormItem>().singleOrNull()?.let {
            Json.decodeFromString(it.value)
        } ?: return call.respond(
            Result.EMPTY_FORM_ITEM_OF_MULTI_PART_DATA.toResponse()
        )
        val images = fileManager.uploadFiles(
            fileItems = multiPartData.filterIsInstance<PartData.FileItem>()
        )
        if (images.size > 1) return call.respond(
            Result.USER_CAN_NOT_HAVE_MORE_THAN_ONE_AVATAR.toResponse()
        )
        val codeVerificationResult = service.usersEmailCodesService.verifyAndDeleteCode(
            email = user.email, code = user.code
        )
        if (codeVerificationResult != Result.OK) return call.respond(
            codeVerificationResult.toResponse()
        )
        val (insertionResult, userID) = service.usersService.insertNewUser(
            user = user, avatar = images.singleOrNull()
        )
        if (userID == null) return call.respond(insertionResult.toResponse())
        call.respond(
            Pair(
                first = insertionResult,
                second = generateToken(valuesJWT, userID, user.password)
            ).toResponse()
        )
    }


    override suspend fun loginUser(call: ApplicationCall) {
        val user = call.receive<UserLogin>()
        val (checkResult, userID) = service.usersService.isEmailAndPasswordCorrect(user)
        if (userID == null) return call.respond(checkResult.toResponse())
        call.respond(
            Pair(
                first = checkResult,
                second = generateToken(valuesJWT, userID, user.password)
            ).toResponse()
        )
    }

    override suspend fun getUserData(call: ApplicationCall) {
        val id = call.receive<NullableUserID>().id
            ?: call.getIntJWTPrincipalClaim(USER_ID)
        call.respond(service.usersService.getUserData(id).toResponse())
    }

    override suspend fun createFeedback(call: ApplicationCall) = call.respond(
        service.usersFeedbackService.insertFeedback(
            fromUserID = call.getIntJWTPrincipalClaim(USER_ID),
            feedback = call.receive<UserFeedbackCreate>()
        ).toResultResponse()
    )

    override suspend fun getFeedbackToUser(call: ApplicationCall) {
        val id = call.receive<NullableUserID>().id
            ?: call.getIntJWTPrincipalClaim(USER_ID)
        call.respond(
            service.usersFeedbackService.getAllFeedbackToUser(id = id).toResponse()
        )
    }
}
