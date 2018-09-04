package au.org.ala.sc

import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
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

    @JsonProperty("corsOrigins")
    var corsOrigins = "*"

    @JsonProperty("profileServiceBaseUrl")
    var profileServiceBaseUrl = "https://devt.ala.org.au/profile-service/"
    @JsonProperty("profileServiceApiKey")
    var profileServiceApiKey: String? = null

    @JsonProperty("imagesBaseDir")
    var imagesBaseDir: String = "/data/seasonal-calendar/images"

}