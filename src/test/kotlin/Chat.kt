import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Test
import org.penakelex.database.models.ChatAdministratorUpdate
import org.penakelex.database.models.ChatCreate
import org.penakelex.database.models.ChatNameUpdate
import org.penakelex.database.models.ChatParticipantUpdate
import kotlin.test.assertEquals

class Chat {
    private val httpClientHelper = HttpClientHelper()

    private val basicURL = "${httpClientHelper.basicURL}/chat"
    private val createBasicURL = "$basicURL/create"
    private val getAllBasicURL = "$basicURL/getAll"
    private val changeBasicURL = "$basicURL/change"

    @Test
    fun createClosedChatTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$createBasicURL/closed") {
                bearerAuth(token1)
                setBody(
                    ChatCreate(
                        name = "My First Chat",
                        administrators = listOf()
                    )
                )
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun createDialogTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$createBasicURL/dialog") {
                bearerAuth(token1)
                setBody(3)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun participantsTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/participants") {
                bearerAuth(token1)
                setBody(3)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun getAllChatsTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$getAllBasicURL/chats") {
                bearerAuth(token1)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun changeUserAsParticipantTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$changeBasicURL/participant") {
                bearerAuth(token1)
                setBody(
                    ChatParticipantUpdate(
                        chatID = 3,
                        changingID = 3
                    )
                )
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun changeUserAsAdministratorTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$changeBasicURL/administrator") {
                bearerAuth(token1)
                setBody(
                    ChatAdministratorUpdate(
                        updatingID = 3,
                        chatID = 3
                    )
                )
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun changeChatNameTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$changeBasicURL/name") {
                bearerAuth(token1)
                setBody(
                    ChatNameUpdate(
                        id = 3,
                        name = "\"My First Chat\""
                    )
                )
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun getAllMessagesTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$getAllBasicURL/messages") {
                bearerAuth(token1)
                setBody(3L)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun deleteChatTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/delete") {
                bearerAuth(token1)
                setBody(4)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }
}