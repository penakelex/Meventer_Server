import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.penakelex.database.models.EventCreate
import org.penakelex.response.Result
import org.penakelex.response.toResultResponse
import java.io.File
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class Test {
    @Test
    fun creationTestSuccess() = testApplication {
        val response = client.post("/event/create") {
            val image1 = File(
                "C:\\Users\\User\\OneDrive\\Рабочий стол\\A\\B\\Картинки\\WhatsApp Image 2022-10-17 at 09.24.59.jpeg"
            )
            val image2 = File(
                "C:\\Users\\User\\OneDrive\\Рабочий стол\\A\\B\\Картинки\\Аватарка.jpg"
            )
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "event",
                            value = Json.encodeToString(
                                serializer = EventCreate.serializer(),
                                value = EventCreate(
                                    name = "Событие",
                                    description = "Описание",
                                    startTime = Instant.now(),
                                    minimalAge = 6,
                                    maximalAge = null,
                                    minimalRating = 3f,
                                    price = 250,
                                    organizers = listOf(
                                        1, 2
                                    )
                                )
                            )
                        )
                        append(
                            key = "image1",
                            value = image1.readBytes(),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"${image1.name}\"")
                            }
                        )
                        append(
                            key = "image2",
                            value = image2.readBytes(),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/jpg")
                                append(HttpHeaders.ContentDisposition, "filename=\"${image2.name}\"")
                            }
                        )
                    }
                )
            )
        }
        assertEquals(
            expected = Result.OK.toResultResponse(),
            actual = Json.decodeFromString(response.bodyAsText())
        )
    }

    @Test
    fun creationTestFailure() = testApplication {
        val response = client.post("/event/create") {
            val image1 = File(
                "C:\\Users\\User\\OneDrive\\Рабочий стол\\A\\B\\Картинки\\WhatsApp Image 2022-10-17 at 09.24.59.jpeg"
            )
            val image2 = File(
                "C:\\Users\\User\\OneDrive\\Рабочий стол\\A\\B\\Картинки\\Аватарка.jpg"
            )
            setBody(
                MultiPartFormDataContent(
                    parts = formData {
                        append(
                            key = "image1",
                            value = image1.readBytes(),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"${image1.name}\"")
                            }
                        )
                        append(
                            key = "image2",
                            value = image2.readBytes(),
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/jpg")
                                append(HttpHeaders.ContentDisposition, "filename=\"${image2.name}\"")
                            }
                        )
                    }
                )
            )
        }
        assertNotEquals(
            illegal = Result.OK.toResultResponse(),
            actual = Json.decodeFromString(response.bodyAsText())
        )
    }
}