import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class HttpClientHelper {
    val basicURL = "https://127.0.0.1:8080"
    val basicConfigForHttps: HttpClientConfig<CIOEngineConfig>.() -> Unit = {
        install(ContentNegotiation) {
            json()
        }
        engine {
            https {
                trustManager = getTrustManager()
            }
        }
    }

    suspend fun withHttpClient(
        config: HttpClientConfig<CIOEngineConfig>.() -> Unit = basicConfigForHttps,
        body: suspend HttpClient.() -> HttpResponse?
    ): HttpResponse? = HttpClient(CIO, config).use { httpClient ->
        try {
            httpClient.body()
        } catch (exception: Exception) {
            exception.printStackTrace()
            null
        }
    }.also {
        try {
            println("Status - ${it?.status}")
            println("Body - \"${it?.body<String>()}\"")
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getKeyStore(): KeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).apply {
        load(
            FileInputStream("keystore.jks"),
            "XgfX231TufOvGaeTU3Rwvjuf3k6jnvdsesRycToF0BQZ7tkzZ8qsd4yTtc5oNgql".toCharArray()
        )
    }

    private fun getTrustManagerFactory(): TrustManagerFactory? = TrustManagerFactory
        .getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply { init(getKeyStore()) }

    private fun getTrustManager(): X509TrustManager = getTrustManagerFactory()?.trustManagers?.first {
        it is X509TrustManager
    } as X509TrustManager
}