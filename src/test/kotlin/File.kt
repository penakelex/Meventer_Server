
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class File {
    private val httpClientHelper = HttpClientHelper()
    private val basicURL = "${httpClientHelper.basicURL}/file"

    @Test
    fun uploadTest() = testApplication {
        val image = File(
            "C:\\Users\\User\\OneDrive\\Рабочий стол\\Помойка с Андроида\\1.11.2_Banner.png"
        )
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/upload") {
                bearerAuth(token1)
                setBody(image.readBytes())
                contentType(ContentType("application", image.extension))
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun getFileTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            get("$basicURL/00000000000000000007.png") {
                bearerAuth(token1)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }
}