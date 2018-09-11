package au.org.ala.sc

import au.org.ala.sc.db.DataSourceFactory
import com.fasterxml.jackson.annotation.JsonProperty
import io.dropwizard.Configuration
import io.dropwizard.flyway.FlywayFactory
import org.jooq.SQLDialect
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
    @JsonProperty("profileServiceApiKey")
    var profileServiceApiKey: String? = null

    @JsonProperty("imagesBaseDir")
    var imagesBaseDir: String = "/data/seasonal-calendar/images"

    @JsonProperty("bieBaseUrl")
    val bieBaseUrl: String = "https://bie.ala.org.au/"

}

class JooqFactory {
    @field:Valid
    @field:NotNull
    @JsonProperty("dialect")
    var dialect: SQLDialect = SQLDialect.POSTGRES_10
}