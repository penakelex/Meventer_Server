
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.isActive
import org.junit.Test

class Chat {
    private val httpClientHelper = HttpClientHelper()
    @Test
    fun chatSocketTest() = testApplication {
        val webSocketSession = httpClientHelper.withHttpClient(httpClientHelper.configForWebsockets) {
            webSocketSession(method = HttpMethod.Get, host = "127.0.0.1", port = 8085, path = "/chat/socket") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjIsInBhc3N3b3JkIjoiMTIzNDU2NzgiLCJleHAiOjE3NDIxMjIyMjJ9.XMP3q52y9T9kAXAWzRnMiS1qfKPfBRWBwRbJ_1IWO4hj4zJAfkXZ6Lc4ZgvnnULxWLXm70o6RY6ecnZAGthiXQ"
                bearerAuth(token)
            }
        }
        assert(webSocketSession.isActive)
    }
}