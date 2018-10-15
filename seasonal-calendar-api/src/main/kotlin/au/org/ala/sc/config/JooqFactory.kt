package au.org.ala.sc.config

import com.fasterxml.jackson.annotation.JsonProperty
import org.jooq.SQLDialect
import javax.validation.Valid
import javax.validation.constraints.NotNull

class JooqFactory {
    @field:Valid
    @field:NotNull
    @JsonProperty("dialect")
    var dialect: SQLDialect = SQLDialect.POSTGRES_10
}