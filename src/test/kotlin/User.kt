import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.Assert.assertNotEquals
import org.penakelex.database.models.*
import org.penakelex.database.models.User
import org.penakelex.response.Response
import org.penakelex.response.ResultResponse
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class User {
    private val httpClientHelper = HttpClientHelper()
    private val basicURL = httpClientHelper.basicURL

    @Test
    fun sendEmailCodeTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user/sendEmailCode") {
                setBody("vovamarkidonov2006vova@gmail.com")
                headers {
                    append(HttpHeaders.ContentType, ContentType.Application.Json)
                }
            }
        }
        assertEquals(
            200.toUShort(),
            response.body<ResultResponse>().code
        )
    }

    @Test
    fun verifyEmailCodeTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user/verifyEmailCode") {
                setBody(
                    Json.encodeToString(
                        UserEmailCode.serializer(),
                        UserEmailCode(
                            email = "alexeykoninsky129@gmail.com",
                            code = "592977"
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }
        }
        assertEquals(
            200.toUShort(),
            response.body<ResultResponse>().code
        )
    }

    @Test
    fun verifyEmailCodeTestFailure() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user/verifyEmailCode") {
                setBody(
                    Json.encodeToString(
                        UserEmailCode.serializer(),
                        UserEmailCode(
                            email = "alexeykoninsky129@gmail.com",
                            code = "59297"
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }
        }
        assertNotEquals(
            200.toUShort(),
            response.body<ResultResponse>().code
        )
    }

    @Test
    fun registrationTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user/register") {
                setBody(
                    MultiPartFormDataContent(
                        parts = formData {
                            val avatar = File(
                                "C:\\Users\\User\\OneDrive\\Изображения\\Nitro\\Gaming_5000x3125.jpg"
                            )
                            append(
                                key = "avatar",
                                value = avatar.readBytes(),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "image/${avatar.extension}")
                                    append(HttpHeaders.ContentDisposition, "filename=\"${avatar.name}\"")
                                }
                            )
                            append(
                                key = "user",
                                value = Json.encodeToString(
                                    UserRegister.serializer(),
                                    UserRegister(
                                        code = "974648",
                                        email = "vovamarkidonov2006vova@gmail.com",
                                        password = "12345678",
                                        nickname = "sasdasd",
                                        name = "Name1",
                                        dateOfBirth = LocalDate.parse(
                                            "2000-01-02",
                                            DateTimeFormatter.ofPattern("yyyy-MM-dd")
                                        )
                                    )
                                ),
                                headers = Headers.build {
                                    append(HttpHeaders.ContentType, "application/json")
                                }
                            )
                        }
                    )
                )
            }
        }
        val body = response.body<Response<String>>()
        println(body.result)
        println(body.data)
        assertEquals(
            200.toUShort(),
            body.result.code
        )
    }


    @Test
    fun loginTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user/login") {
                setBody(
                    Json.encodeToString(
                        UserLogin.serializer(),
                        UserLogin(
                            email = "vovamarkidonov2006vova@gmail.com",
                            password = "12345678"
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                }
            }
        }
        val body = response.body<Response<String>>()
        println(body.data)
        assertEquals(
            200.toUShort(),
            body.result.code
        )
    }

    @Test
    fun getDataTestWithID() = testApplication {
        val response = httpClientHelper.withHttpClient {
            val token =
                "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjIsInBhc3N3b3JkIjoiMTIzNDU2NzgiLCJleHAiOjE3NDIxMTU0MDN9.kssKIOxMIZYWJRTjdkwxf6V8_aCL1S65-XRI2hWlgdBKUb-mNrCCoc6qpA6k3_HJIzjPPcCnz-y125wCvfXVaQ"
            post("$basicURL/user/data") {
                setBody(
                    Json.encodeToString(
                        NullableUserID.serializer(),
                        NullableUserID(null)
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<Response<User>>()
        println(body.data)
        assertNotEquals(
            200.toUShort(),
            body.result.code.also { println(it) }
        )
    }

    @Test
    fun getDataTestWithoutID() = testApplication {
        val response = httpClientHelper.withHttpClient {
            val token =
                "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjEsInBhc3N3b3JkIjoicGFzc3N3d2RhIiwiZXhwIjoxNzQyMDk3OTQzfQ.gupSu4Cll23G229bC1a9jtB8lvtbKMhphA4bwt7f83jp-UhIA7VKWuRPeEqnCPps72aCpvzahfcf0i0-hh2szA"
            post("$basicURL/user/data") {
                setBody(
                    Json.encodeToString(
                        NullableUserID.serializer(),
                        NullableUserID(null)
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<Response<User>>()
        println(body.data)
        assertEquals(
            200.toUShort(),
            body.result.code
        )
    }

    @Test
    fun createFeedbackTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            post("$basicURL/user/feedback/create") {
                val token =
                    "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjIsInBhc3N3b3JkIjoicGFzc3dvcmQxIiwiZXhwIjoxNzQxNTIyNzg1fQ.YYg9_n0JmnFcNA6tJuW3Hg8BPCMgPF-Xs6PAO6l4vMU88MJSmKp2TZHFMM2LiMbUVc_aF5vuIeAhm5Aa4yqjfg"
                setBody(
                    Json.encodeToString(
                        UserFeedbackCreate.serializer(),
                        UserFeedbackCreate(
                            toUserID = 1,
                            rating = 5f,
                            comment = "Лучший!"
                        )
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        assertNotEquals(
            200.toUShort(),
            response.body<ResultResponse>().code
        )
    }

    @Test
    fun getFeedbackTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            val token =
                "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjIsInBhc3N3b3JkIjoicGFzc3dvcmQxIiwiZXhwIjoxNzQxNTIyNzg1fQ.YYg9_n0JmnFcNA6tJuW3Hg8BPCMgPF-Xs6PAO6l4vMU88MJSmKp2TZHFMM2LiMbUVc_aF5vuIeAhm5Aa4yqjfg"
            post("$basicURL/user/feedback/get") {
                setBody(
                    Json.encodeToString(
                        NullableUserID.serializer(),
                        NullableUserID(null)
                    )
                )
                headers {
                    append(HttpHeaders.ContentType, "application/json")
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
        val body = response.body<Response<List<UserFeedback>>>()
        println(body.data)
        assertEquals(
            200.toUShort(),
            body.result.code
        )
    }

    @Test
    fun verifyTokenTest() = testApplication {
        val response = httpClientHelper.withHttpClient {
            val token =
                "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJqd3QtYXVkaWVuY2UiLCJpc3MiOiJpc3N1ZXIiLCJ1c2VySUQiOjIsInBhc3N3b3JkIjoiMTIzNDU2NzgiLCJleHAiOjE3NDIxMjIyMjJ9.XMP3q52y9T9kAXAWzRnMiS1qfKPfBRWBwRbJ_1IWO4hj4zJAfkXZ6Lc4ZgvnnULxWLXm70o6RY6ecnZAGthiXQ"
            get("$basicURL/user/verifyToken") {
                bearerAuth(token)
            }
        }
        println(response.body<ResultResponse>())
    }
}