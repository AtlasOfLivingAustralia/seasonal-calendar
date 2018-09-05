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
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.flyway.FlywayBundle
import io.dropwizard.flyway.FlywayFactory
import io.dropwizard.forms.MultiPartBundle
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import okhttp3.OkHttpClient
import org.jooq.impl.DSL
import java.io.File
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.jooq.DSLContext
import javax.servlet.DispatcherType
import java.util.EnumSet

class SeasonalCalendarApplication : Application<SeasonalCalendarConfiguration>() {

    override fun initialize(bootstrap: Bootstrap<SeasonalCalendarConfiguration>) {
        bootstrap.objectMapper.findAndRegisterModules()
        bootstrap.addBundle(MultiPartBundle())
        bootstrap.addBundle(object : FlywayBundle<SeasonalCalendarConfiguration>() {
            override fun getDataSourceFactory(configuration: SeasonalCalendarConfiguration): PooledDataSourceFactory {
                return configuration.database
            }

            override fun getFlywayFactory(configuration: SeasonalCalendarConfiguration): FlywayFactory {
                return configuration.flyway
            }
        })
    }

    override fun run(configuration: SeasonalCalendarConfiguration, environment: Environment) {

        addCors(environment, configuration)

        // TODO Daggerise
        val okHttpClient = OkHttpClient.Builder().build()
        val profilesServiceClient = ProfileServiceClient.Builder(okHttpClient, configuration.profileServiceBaseUrl).apiKey(configuration.profileServiceApiKey).build()

        val dsl = configureJooq(configuration, environment, "seasonal-calendars")

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

    private fun configureJooq(configuration: SeasonalCalendarConfiguration, environment: Environment, name: String): DSLContext {
        val dataSource = configuration.database.apply { healthCheckRegistry = environment.healthChecks() }.build(environment.metrics(), name)
        environment.lifecycle().manage(dataSource)

        val dsl = DSL.using(dataSource, configuration.jooq.dialect)
        environment.lifecycle().manage(object: Managed {
            override fun start() {}

            override fun stop() {
                dsl.close()
            }
        })

        return dsl
    }

    private fun addCors(environment: Environment, configuration: SeasonalCalendarConfiguration) {
        // CORS configuration
        val corsFilter = environment.servlets().addFilter("CORS", CrossOriginFilter::class.java)
        corsFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType::class.java), true, "/*")
        corsFilter.setInitParameter(
            CrossOriginFilter.ALLOWED_HEADERS_PARAM,
            "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin"
        )
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS")
        corsFilter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, configuration.corsOrigins)
    }
}

fun main(args: Array<String>) {
    SeasonalCalendarApplication().run(*args)
}