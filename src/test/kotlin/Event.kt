import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.penakelex.database.models.*
import org.penakelex.routes.event.EventsType
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class Event {
    private val httpClientHelper = HttpClientHelper()
    private val basicURL = "${httpClientHelper.basicURL}/event"
    private val changeUsersBasicURL = "$basicURL/changeUsers"

    @Test
    fun createEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/create") {
                bearerAuth(token1)
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            append(
                                "event",
                                Json.encodeToString(
                                    EventCreate.serializer(),
                                    EventCreate(
                                        name = "Мероприятие",
                                        description = "Самое лучшее мероприятие",
                                        startTime = Instant.ofEpochMilli(
                                            System.currentTimeMillis() + 1_000_000_000
                                        ),
                                        minimalAge = null,
                                        maximalAge = null,
                                        price = 1000,
                                        tags = listOf(
                                            "Тег 1", "Тег 2", "Тег 3"
                                        )
                                    )
                                )
                            )
                        }
                    )
                )
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun allUserEventsTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user") {
                bearerAuth(token1)
                setBody(
                    EventsGet(
                        userID = null,
                        actual = true,
                        aforetime = false,
                        type = EventsType.All.type
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
    fun changeUserAsParticipant() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$changeUsersBasicURL/participant") {
                bearerAuth(token1)
                setBody(
                    EventParticipant(
                        changingID = 3,
                        eventID = 1
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
    fun changeUserAsOrganizer() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$changeUsersBasicURL/organizer") {
                bearerAuth(token1)
                setBody(
                    EventOrganizer(
                        eventID = 1,
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
    fun changeEventInFavouritesTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$changeUsersBasicURL/inFavourites") {
                bearerAuth(token2)
                setBody(1)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun updateEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/update") {
                bearerAuth(token2)
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            append(
                                "event",
                                Json.encodeToString(
                                    EventUpdate.serializer(),
                                    EventUpdate(
                                        id = 1,
                                        name = "Мероприятие (изменённое)",
                                        description = null,
                                        startTime = Instant.ofEpochMilli(
                                            System.currentTimeMillis() + 10_000_000
                                        ),
                                        minimalAge = null,
                                        maximalAge = 1000.toUShort(),
                                        price = 10_000,
                                        tags = null,
                                        deletedImages = null
                                    )
                                )
                            )
                        }
                    )
                )
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun userEventsInFavouritesTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user") {
                bearerAuth(token1)
                setBody(
                    EventsGet(
                        userID = 2,
                        actual = true,
                        aforetime = null,
                        type = EventsType.InFavourites.type
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
    fun userEventsOrganizerTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user") {
                bearerAuth(token1)
                setBody(
                    EventsGet(
                        userID = null,
                        actual = true,
                        aforetime = null,
                        type = EventsType.Organizer.type
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
    fun userEventsParticipantTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user") {
                bearerAuth(token2)
                setBody(
                    EventsGet(
                        userID = null,
                        actual = true,
                        aforetime = null,
                        type = EventsType.Participant.type
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
    fun globalEventsTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/global") {
                setBody(
                    EventSelection(
                        tags = listOf(
                            "Тег 1",
                            "Тег 12"
                        ),
                        age = null,
                        minimalPrice = null,
                        maximalPrice = null,
                        sortBy = EventSelection
                            .SortingStates.NEAREST_ONES_FIRST.state
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
    fun getEventTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            get("$basicURL/1")
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }
}