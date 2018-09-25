package au.org.ala.sc

import au.org.ala.sc.db.DataSourceFactory
import au.org.ala.sc.modules.InstrumentedOkHttpClient
import com.codahale.metrics.MetricRegistry
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.flyway.FlywayFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.jooq.SQLDialect
import java.time.Duration
import java.util.concurrent.TimeUnit.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

class SeasonalCalendarConfiguration : Configuration() {

    @field:Valid
    @field:NotNull
    @JsonProperty("database")
    var database = DataSourceFactory()

    @field:Valid
    @field:NotNull
    @JsonProperty("flyway")
    var flyway = FlywayFactory()

    @field:Valid
    @field:NotNull
    @JsonProperty
    var jooq = JooqFactory()

    @JsonProperty("corsOrigins")
    var corsOrigins = "*"

    @JsonProperty("profileServiceBaseUrl")
    var profileServiceBaseUrl = "https://devt.ala.org.au/profile-service/"

    @JsonProperty("dataResourceUid")
    var dataResourceUid = "dr382"

    @JsonProperty("calendarTag")
    var calendarTag = "IEK"

    @JsonProperty("imagesBaseDir")
    var imagesBaseDir: String = "/data/seasonal-calendar/images"

    @JsonProperty("bieBaseUrl")
    val bieBaseUrl: String = "https://bie.ala.org.au/"

    @JsonProperty("httpClients")
    val httpClientFactory: OkHttpClientFactory = OkHttpClientFactory()
}

class JooqFactory {
    @field:Valid
    @field:NotNull
    @JsonProperty("dialect")
    var dialect: SQLDialect = SQLDialect.POSTGRES_10
}

open class OkHttpClientConfiguration {
    @JsonProperty("connectionTimeout")
    var connectionTimeout: Duration = Duration.ofMillis(2_000)

    @JsonProperty("readTimeout")
    var readTimeout: Duration = Duration.ofMillis(15_000)

    @JsonProperty("headers")
    var headers: Map<String,String> = emptyMap()
}

class OkHttpClientFactory : OkHttpClientConfiguration() {

    @JsonProperty
    var named: MutableMap<String, OkHttpClientConfiguration> = mutableMapOf()

    fun build(metrics: MetricRegistry, name: String, additionalHeaders: Map<String,String> = emptyMap()): InstrumentedOkHttpClient {
        val factory = named[name] ?: this
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(factory.connectionTimeout.toMillis(), MILLISECONDS)
            .readTimeout(factory.readTimeout.toMillis(), MILLISECONDS)

        val headers = when (factory) {
            this -> factory.headers + additionalHeaders
            else -> this.headers + factory.headers + additionalHeaders
        }
        headers
            .map { (k,v) -> Interceptor { chain -> chain.proceed(chain.request().newBuilder().addHeader(k, v).build()) } }
            .onEach { interceptor ->  clientBuilder.addInterceptor(interceptor) }

        return InstrumentedOkHttpClient(clientBuilder.build(), metrics, name)
    }
}