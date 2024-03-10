
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.penakelex.database.models.*
import org.penakelex.database.models.Event
import org.penakelex.response.Response
import org.penakelex.response.ResultResponse
import java.io.File
import java.time.Instant
import kotlin.test.assertContains
import kotlin.test.assertEquals

class Event {
    private val httpClientHelper = HttpClientHelper()
    private val basicURL = httpClientHelper.basicURL

    @Test
    fun createEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/create") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            val images = listOf(
                                File(
                                    "C:\\Users\\User\\Downloads\\ce6ac9779c585e581971588ecb5c2fe9.jpg"
                                ),
                                File(
                                    "C:\\Users\\User\\Downloads\\1696573074_gas-kvas-com-p-kartinki-molodezhnie-16.jpg"
                                )
                            )
                            append(
                                key = "event",
                                value = Json.encodeToString(
                                    EventCreate.serializer(),
                                    EventCreate(
                                        name = "Event",
                                        description = "Event - mevent",
                                        startTime = Instant.ofEpochMilli(
                                            System.currentTimeMillis() + 1_000_000
                                        ),
                                        minimalAge = 0,
                                        maximalAge = null,
                                        price = 2_000_000_000
                                    )
                                ),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "application/json")
                                }
                            )
                            for ((index, image) in images.withIndex()) {
                                append(
                                    key = "image$index",
                                    value = image.readBytes(),
                                    headers = Headers.build {
                                        append(HttpHeaders.ContentType, "image/${image.extension}")
                                        append(HttpHeaders.ContentDisposition, "filename=\"${image.name}\"")
                                    }
                                )
                            }
                        }
                    )
                )
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<ResultResponse>()
        println(body)
        assertEquals(
            200.toUShort(),
            body.code
        )
    }

    @Test
    fun addParticipantTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/addParticipant") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(3)
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<ResultResponse>()
        println(body)
        assertEquals(
            200.toUShort(),
            body.code
        )
    }

    @Test
    fun addOrganizerTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/addOrganizer") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(
                    Json.encodeToString(
                        EventOrganizer.serializer(),
                        EventOrganizer(
                            eventID = 3,
                            addingID = 2
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<ResultResponse>()
        println(body)
        assertNotEquals(
            200.toUShort(),
            body.code
        )
    }

    @Test
    fun addInFeaturedEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/addInFavourites") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(3)
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<ResultResponse>()
        println(body)
        assertEquals(
            200.toUShort(),
            body.code
        )
    }

    @Test
    fun updateEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/update") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(
                    Json.encodeToString(
                        EventUpdate.serializer(),
                        EventUpdate(
                            eventID = 3,
                            name = "Event updated",
                            description = "Event - mevent (upd)",
                            startTime = Instant.ofEpochMilli(
                                System.currentTimeMillis() + 10_000_000
                            ),
                            minimalAge = 0,
                            maximalAge = 100,
                            price = 100
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<ResultResponse>()
        println(body)
        assertEquals(
            200.toUShort(),
            body.code
        )
    }

    @Test
    fun deleteEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/delete") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(1)
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<ResultResponse>()
        println(body)
        assertEquals(
            200.toUShort(),
            body.code
        )
    }

    @Test
    fun getUserEvents() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/user") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3dvcmQiLCJleHAiOjE3NDE1MjEwODd9.3Hxes770K5cPe1gbbM14JnKoNDBiZ3ipXzM8lXges89qwhJ04shmBHqXh4lYFi_8L82gMXEDhigbhva_y-5zxg"
                setBody(
                    Json.encodeToString(
                        EventsGet.serializer(),
                        EventsGet(
                            userID = 1,
                            actual = null,
                            aforetime = null,
                            type = "featured"
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<Response<List<Event>>>()
        println(body)
        assertContains(
            listOf(200.toUShort(), 204.toUShort()),
            body.result.code
        )
    }

    @Test
    fun getEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            val eventID = 3
            get("$basicURL/event/$eventID")
        }
        val body = response.body<Response<Event>>()
        println(body)
        assertEquals(
            200.toUShort(),
            body.result.code
        )
    }

    @Test
    fun getGlobalEventsTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/event/global") {
                setBody(
                    Json.encodeToString(
                        EventSelection.serializer(),
                        EventSelection(
                            tags = listOf("event"),
                            age = 10,
                            minimalPrice = null,
                            maximalPrice = null,
                            sortBy = null
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }
        }
        val body = response.body<Response<List<Event>>>()
        println(body.data)
        assertEquals(
            200.toUShort(),
            body.result.code
        )
    }
}