
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.json.Json
import org.penakelex.database.models.*
import java.io.File
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class User {
    private val httpClientHelper = HttpClientHelper()

    private val basicURL = "${httpClientHelper.basicURL}/user"
    private val updateBasicURL = "$basicURL/update"
    private val feedbackBasicURL = "$basicURL/feedback"

    private val email1 = "alexeykoninsky129@gmail.com"
    private val password1 = "password"
    private val token1 =
        "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtZXZlbnRlciB1c2VycyIsImlzcyI6InBlbmFrZWxleCIsInVzZXJJRCI6MiwicGFzc3dvcmQiOiJDdVciLCJleHAiOjE3NDM3ODQ0ODF9.45iiVrQfBjaxDokgJTPyV2xSzKe5cGQQXi7_hYhBWwjzHFpa5GK5ofqQBEGbbv0Xl-ci26wGmbW1XgznlPZSNA"

    private val email2 = "cool.teamer.alex@gmail.com"
    private val password2 = "12345678"
    private val token2 =
        "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJtZXZlbnRlciB1c2VycyIsImlzcyI6InBlbmFrZWxleCIsInVzZXJJRCI6MywicGFzc3dvcmQiOiJBWW0iLCJleHAiOjE3NDM3OTI2MDZ9.UhC2_zFYB_jf7FoiBxlCroafIDOb0xEDOx8tJV32epZLAZoFacS4W3qfRnASbSwL4tTwdziZDT5WMxrh1LJ1Uw"

    @Test
    fun sendEmailCodeTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/sendEmailCode") {
                setBody(email2)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun verifyEmailCode() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/verifyEmailCode") {
                setBody(
                    UserEmailCode(
                        email = email1,
                        code = "779876"
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
    fun registerTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/register") {
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            append(
                                "user",
                                Json.encodeToString(
                                    UserRegister.serializer(),
                                    UserRegister(
                                        code = "936135",
                                        email = email2,
                                        password = password2,
                                        nickname = "verycoolman",
                                        name = "Петр",
                                        dateOfBirth = LocalDate.now()
                                    )
                                )
                            )
                        }
                    )
                )
                contentType(ContentType.MultiPart.Mixed)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun loginTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/login") {
                setBody(
                    UserLogin(
                        email = email1,
                        password = password1
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
    fun dataTest() = testApplication {
        val id: Int? = null
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/data") {
                bearerAuth(token1)
                setBody(id, typeInfo<Int?>())
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun byNicknameTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/byNickname") {
                bearerAuth(token1)
                setBody("783d1f3f")
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun verifyTokenTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            get("$basicURL/verifyToken") {
                bearerAuth(token1)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun updateDataTest() = testApplication {
        val avatar = File(
            "C:\\Users\\User\\OneDrive\\Рабочий стол\\Сборник\\B\\Картинки\\Экран блокировки.jpg"
        )
        val response = httpClientHelper.withHttpClient {
            post("$updateBasicURL/data") {
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            append(
                                key = "user",
                                value = Json.encodeToString(
                                    UserUpdate.serializer(),
                                    UserUpdate(
                                        nickname = "penakelex",
                                        name = null
                                    )
                                )
                            )
                            append(
                                key = "avatar",
                                value = avatar.readBytes(),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "image/${avatar.extension}")
                                    append(HttpHeaders.ContentDisposition, "filename=\"${avatar.name}\"")
                                }
                            )
                        }
                    )
                )
                bearerAuth(token1)
                contentType(ContentType.MultiPart.Mixed)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun updateEmailTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$updateBasicURL/email") {
                bearerAuth(token1)
                setBody(
                    UserUpdateEmail(
                        emailCode = "378974",
                        email = email1
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
    fun updatePasswordTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$updateBasicURL/password") {
                bearerAuth(token1)
                setBody(
                    UserUpdatePassword(
                        emailCode = "743510",
                        oldPassword = password1,
                        newPassword = password1
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
    fun feedbackCreateTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$feedbackBasicURL/create") {
                bearerAuth(token2)
                setBody(
                    UserFeedbackCreate(
                        toUserID = 2,
                        rating = 5f,
                        comment = "Энакин, отлично!"
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
    fun feedbackGetTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$feedbackBasicURL/get") {
                setBody(null, typeInfo<Int?>())
                bearerAuth(token2)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun feedbackUpdateTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$feedbackBasicURL/update") {
                setBody(
                    UserFeedbackUpdate(
                        id = 1,
                        rating = 4.9f,
                        comment = "Энакин, почти отлично!"
                    )
                )
                bearerAuth(token2)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun feedbackDeleteTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$feedbackBasicURL/delete") {
                setBody(1)
                bearerAuth(token2)
                contentType(ContentType.Application.Json)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }

    @Test
    fun logOutTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/logOut") {
                bearerAuth(token2)
            }
        }
        assertEquals(
            200,
            response?.status?.value
        )
    }
}