package org.penakelex.di

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*
import jakarta.mail.Authenticator
import jakarta.mail.PasswordAuthentication
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.penakelex.database.services.Service
import org.penakelex.database.services.chats.ChatsServiceImplementation
import org.penakelex.database.services.events.EventsServiceImplementation
import org.penakelex.database.services.messages.MessagesServiceImplementation
import org.penakelex.database.services.sessions.ChatSessionsServiceImplementation
import org.penakelex.database.services.users.UsersServiceImplementation
import org.penakelex.database.services.usersEmailCodes.UsersEmailCodesServiceImplementation
import org.penakelex.database.services.usersFeedback.UsersFeedbackServiceImplementation
import org.penakelex.fileSystem.FileManager
import org.penakelex.fileSystem.FileManagerImplementation
import org.penakelex.routes.Controller
import org.penakelex.routes.chat.ChatsControllerImplementation
import org.penakelex.routes.event.EventsControllerImplementation
import org.penakelex.routes.file.FilesControllerImplementation
import org.penakelex.routes.user.UsersControllerImplementation
import org.penakelex.session.JWTValues
import org.penakelex.session.UserEmailValues
import java.util.*

val databaseModule = module {
    val config = HoconApplicationConfig(ConfigFactory.load())
    single<Database> {
        Database.connect(
            url = config.property("database.url").getString(),
            user = config.property("database.user").getString(),
            password = config.property("database.password").getString(),
            driver = config.property("database.driver").getString()
        )
    }
    single<Service> {
        Service(
            usersService = UsersServiceImplementation(
                basicAvatar = "Аватарка.jpg"
            ),
            usersEmailCodesService = UsersEmailCodesServiceImplementation(),
            eventsService = EventsServiceImplementation(),
            usersFeedbackService = UsersFeedbackServiceImplementation(),
            chatsService = ChatsServiceImplementation(),
            messagesService = MessagesServiceImplementation(),
            chatSessionsService = ChatSessionsServiceImplementation(),
            database = get()
        )
    }
}

val securityModule = module {
    val config = HoconApplicationConfig(ConfigFactory.load())
    single {
        JWTValues(
            audience = config.property("jwt.audience").getString(),
            issuer = config.property("jwt.issuer").getString(),
            secret = config.property("jwt.secret").getString(),
            realm = config.property("jwt.realm").getString()
        )
    }
}

val emailModule = module {
    val config = HoconApplicationConfig(ConfigFactory.load())
    single<Properties> {
        Properties().apply {
            set("mail.smtp.auth", "true")
            set("mail.smtp.starttls.enable", "true")
            set("mail.smtp.host", "smtp.gmail.com")
            set("mail.smtp.port", "587")
        }
    }
    single<UserEmailValues> {
        UserEmailValues(
            email = config.property("email.email").getString(),
            password = config.property("email.password").getString(),
            personal = config.property("email.personal").getString(),
            subject = "Подтверждение почты",
            body = "Код подтверждения почты: "
        )
    }
    single<Authenticator> {
        val emailValues = get<UserEmailValues>()
        object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication =
                PasswordAuthentication(emailValues.email, emailValues.password)
        }
    }
}

val fileSystemModule = module {
    val config = HoconApplicationConfig(ConfigFactory.load())
    single<FileManager> {
        FileManagerImplementation(
            directory = config.property("file.directory").getString()
        )
    }
}

val routingModule = module {
    single<Controller> {
        Controller(
            usersController = UsersControllerImplementation(
                service = get(),
                valuesJWT = get(),
                properties = get(),
                userEmailValues = get(),
                authenticator = get(),
                fileManager = get()
            ),
            eventsController = EventsControllerImplementation(
                service = get(),
                fileManager = get()
            ),
            filesController = FilesControllerImplementation(
                fileManager = get()
            ),
            chatsController = ChatsControllerImplementation(
                service = get()
            )
        )
    }
}