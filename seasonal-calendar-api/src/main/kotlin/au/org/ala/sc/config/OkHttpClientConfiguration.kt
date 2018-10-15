package au.org.ala.sc.config

import au.org.ala.sc.modules.InstrumentedOkHttpClient
import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.annotation.JsonProperty
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.time.Duration
import java.util.concurrent.TimeUnit

open class OkHttpClientConfiguration {

    companion object {
        val DEFAULT_CONNECTION_TIMEOUT = Duration.ofMillis(2_000)!!
        val DEFAULT_READ_TIMEOUT = Duration.ofMillis(15_000)!!
        const val DEFAULT_LOGGING_ENABLED = false
        val DEFAULT_LOGGING_LEVEL = HttpLoggingInterceptor.Level.NONE
    }

    @JsonProperty("connectionTimeout")
    var connectionTimeout: Duration? = null

    @JsonProperty("readTimeout")
    var readTimeout: Duration? = null

    @JsonProperty("logging")
    var logging = HttpLoggingConfiguration()

    @JsonProperty("basic")
    var basic = BasicCredentialsConfiguration()

    @JsonProperty("headers")
    var headers: Map<String,String> = mutableMapOf()

    class BasicCredentialsConfiguration {
        var username: String? = null
        var password: String? = null
    }

    class HttpLoggingConfiguration {
        var enabled: Boolean? = null

        var level: HttpLoggingInterceptor.Level? = null
    }
}

class OkHttpClientFactory : OkHttpClientConfiguration() {

    @JsonProperty
    var named: MutableMap<String, OkHttpClientConfiguration> = mutableMapOf()

    fun build(metrics: MetricRegistry, name: String, additionalHeaders: Map<String,String> = emptyMap()): InstrumentedOkHttpClient {
        val factory = named[name]
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout((factory?.connectionTimeout ?: this.connectionTimeout ?: DEFAULT_CONNECTION_TIMEOUT).toMillis(),
                TimeUnit.MILLISECONDS
            )
            .readTimeout((factory?.readTimeout ?: this.readTimeout ?: DEFAULT_READ_TIMEOUT).toMillis(),
                TimeUnit.MILLISECONDS
            )

        val headers = when (factory) {
            null -> this.headers + additionalHeaders
            else -> this.headers + factory.headers + additionalHeaders
        }
        headers
            .map { (k,v) -> Interceptor { chain -> chain.proceed(chain.request().newBuilder().addHeader(k, v).build()) } }
            .onEach { interceptor ->  clientBuilder.addInterceptor(interceptor) }

        val loggingEnabled = factory?.logging?.enabled ?: this.logging.enabled ?: DEFAULT_LOGGING_ENABLED
        if (loggingEnabled) {
            val loggingLevel = factory?.logging?.level ?: this.logging.level ?: DEFAULT_LOGGING_LEVEL
            clientBuilder.addNetworkInterceptor(HttpLoggingInterceptor().apply { level = loggingLevel })
        }

        val username = factory?.basic?.username
        val password = factory?.basic?.password
        if (username != null && password != null) {
            clientBuilder.addInterceptor { chain -> chain.proceed(chain.request().newBuilder().addHeader("Authorization",
                Credentials.basic(username, password)
            ).build()) }
        }

        return InstrumentedOkHttpClient(clientBuilder.build(), metrics, name)
    }
}