package au.org.ala.sc

import au.org.ala.profiles.service.ProfileServiceClient
import au.org.ala.sc.domain.jooq.tables.daos.CalendarDao
import au.org.ala.sc.domain.jooq.tables.daos.RoleDao
import au.org.ala.sc.domain.jooq.tables.daos.SeasonDao
import au.org.ala.sc.domain.jooq.tables.daos.UserRoleDao
import au.org.ala.sc.resources.CalendarResource
import au.org.ala.sc.resources.ImageResource
import au.org.ala.sc.services.CalendarService
import au.org.ala.sc.services.FeatureService
import au.org.ala.sc.services.SeasonService
import io.dropwizard.Application
import io.dropwizard.db.DataSourceFactory
import io.dropwizard.flyway.FlywayBundle
import io.dropwizard.flyway.FlywayFactory
import io.dropwizard.forms.MultiPartBundle
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import okhttp3.OkHttpClient
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.io.File

class SeasonalCalendarApplication : Application<SeasonalCalendarConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<SeasonalCalendarConfiguration>) {
        bootstrap.objectMapper.findAndRegisterModules() // TODO necessary?
        bootstrap.addBundle(MultiPartBundle())
        bootstrap.addBundle(object : FlywayBundle<SeasonalCalendarConfiguration>() {
            override fun getDataSourceFactory(configuration: SeasonalCalendarConfiguration): DataSourceFactory {
                return configuration.database
            }

            override fun getFlywayFactory(configuration: SeasonalCalendarConfiguration): FlywayFactory {
                return configuration.flyway
            }
        })
    }

    override fun run(configuration: SeasonalCalendarConfiguration, environment: Environment) {
        // TODO Daggerise
        val okHttpClient = OkHttpClient.Builder().build()
        val profilesServiceClient = ProfileServiceClient.Builder(okHttpClient, configuration.profileServiceBaseUrl, configuration.profileServiceApiKey).build()
        val dataSource = configuration.database.build(environment.metrics(), "seasonal-calendars")
        val dsl = DSL.using(dataSource, SQLDialect.POSTGRES_10)
        val calendarDao = CalendarDao(dsl.configuration())
        val seasonDao = SeasonDao(dsl.configuration())
        val userRoleDao = UserRoleDao(dsl.configuration())
        val roleDao = RoleDao(dsl.configuration())
        val featureService = FeatureService(profilesServiceClient)
        val seasonService = SeasonService(seasonDao, dsl, featureService, profilesServiceClient)
        val calendarService = CalendarService(calendarDao, seasonService, profilesServiceClient)

        val calendarResource = CalendarResource(calendarService)
        val imageResource = ImageResource(File(configuration.imagesBaseDir))

        environment.jersey().apply {
            register(calendarResource)
            register(imageResource)
        }
    }
}

fun main(args: Array<String>) {
    SeasonalCalendarApplication().run(*args)
}