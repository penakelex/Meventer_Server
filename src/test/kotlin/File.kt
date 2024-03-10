
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.server.testing.*
import io.ktor.utils.io.*
import org.junit.Test
import kotlin.test.assertEquals

class File {
    private val httpClientHelper = HttpClientHelper()
    private val basicURL = httpClientHelper.basicURL
    @Test
    fun getFileTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            get("$basicURL/file/0000000000000000000E.jpg")
        }
        val body = response.body<ByteReadChannel>()
        assertEquals(
            200,
            response.status.value
        )
    }
}