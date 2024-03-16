import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import java.io.FileInputStream
import java.security.KeyStore
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class HttpClientHelper {
    val basicURL = "https://127.0.0.1:8080"
    private val basicConfigForHttps: HttpClientConfig<CIOEngineConfig>.() -> Unit = {
        install(ContentNegotiation) {
            json()
        }
        engine {
            https {
                trustManager = getTrustManagerFactory()?.trustManagers?.first {
                    it is X509TrustManager
                } as X509TrustManager
            }
        }
    }

    val configForWebsockets: HttpClientConfig<CIOEngineConfig>.() -> Unit = {
        install(WebSockets) {
            pingInterval = 20_000
        }
        engine {
            https {
                trustManager = getTrustManagerFactory()?.trustManagers?.first {
                    it is X509TrustManager
                } as X509TrustManager
            }
        }
    }

    suspend fun <Type> withHttpClient(
        config: HttpClientConfig<CIOEngineConfig>.() -> Unit = basicConfigForHttps,
        block: suspend HttpClient.() -> Type
    ): Type {
        val client = HttpClient(CIO, config)
        val response = with(client) {
            block()
        }
        return response
    }
    private fun getTrustManagerFactory(): TrustManagerFactory? {
        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(getKeyStore())
        return trustManagerFactory
    }

    private fun getKeyStore(): KeyStore {
        val keyStoreFile = FileInputStream("A:\\Kotlin\\Meventer\\keystore.jks")
        val keyStorePassword = "password".toCharArray()
        val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        keyStore.load(keyStoreFile, keyStorePassword)
        return keyStore
    }
}