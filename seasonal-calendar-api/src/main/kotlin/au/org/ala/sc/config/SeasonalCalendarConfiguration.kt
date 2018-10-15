package au.org.ala.sc.config

import au.org.ala.sc.db.DataSourceFactory
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.flyway.FlywayFactory
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
    var bieBaseUrl: String = "https://bie.ala.org.au/"

    @JsonProperty("userDetailsBaseUrl")
    var userDetailsBaseUrl: String = "https://auth.ala.org.au/userdetails/userDetails/"

    @JsonProperty("oidcBaseUrl")
    var oidcBaseUrl: String = "https://auth.ala.org.au/cas/oidc/"

    @JsonProperty("oidcClientId")
    var oidcClientId: String = "client-id"

    @JsonProperty("oidcClientSecret")
    var oidcClientSecret: String = "secret"

    @JsonProperty("warnOnUnknownJsonProperties")
    var warnOnUnknownJsonProperties: Boolean = false
    @JsonProperty("httpClients")
    val httpClientFactory: OkHttpClientFactory = OkHttpClientFactory()

}

