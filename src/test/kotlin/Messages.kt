import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.junit.Test
import org.penakelex.database.models.MessageDelete
import org.penakelex.database.models.MessageSend
import org.penakelex.database.models.MessageUpdate
import java.time.Instant
import kotlin.test.assertNotEquals

class Messages {
    private val httpClientHelper = HttpClientHelper()
    private val websocketURL = "wss://127.0.0.1:8080/chat/socket"

    @Test
    fun socketTest() = testApplication {
        val session1 = httpClientHelper.withHttpClientForWebsockets {
            webSocketSession {
                url(websocketURL)
                bearerAuth(token1)
            }
        }
        assertNotEquals(
            null,
            session1
        )
        val session2 = httpClientHelper.withHttpClientForWebsockets {
            webSocketSession {
                url(websocketURL)
                bearerAuth(token2)
            }
        }
        assertNotEquals(
            null,
            session2
        )
        with(CoroutineScope(Dispatchers.Default)) {
            launch {
                session1!!.incoming.consumeEach { frame ->
                    println(
                        if (frame is Frame.Text) "Session 1: ${frame.readText()}"
                        else "Session 1: Я не знаю, что за тип пришел: ${frame::class}"
                    )
                }
            }
            launch {
                session2!!.incoming.consumeEach { frame ->
                    println(
                        if (frame is Frame.Text) "Session 2: ${frame.readText()}"
                        else "Session 2: Я не знаю, что за тип пришел: ${frame::class}"
                    )
                }
            }
        }
        val messageID = 8L
        session1!!.send(
            Json.encodeToString(
                MessageSend.serializer(),
                MessageSend(
                    chatID = 3,
                    body = "Hello There!",
                    timestamp = Instant.now(),
                    attachment = null
                )
            )
        )
        session1.send(
            Json.encodeToString(
                MessageUpdate.serializer(),
                MessageUpdate(
                    id = messageID,
                    chatID = 3,
                    body = "Oh, Hello There!"
                )
            )
        )
        session1.send(
            Json.encodeToString(
                MessageDelete.serializer(),
                MessageDelete(
                    id = messageID,
                    chatID = 3
                )
            )
        )
    }
}