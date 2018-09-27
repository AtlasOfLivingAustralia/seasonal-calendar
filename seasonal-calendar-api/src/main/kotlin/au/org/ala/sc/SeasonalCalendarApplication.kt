package au.org.ala.sc

import au.org.ala.profiles.service.ProfileServiceClient
import au.org.ala.sc.domain.jooq.tables.daos.CalendarDao
import au.org.ala.sc.domain.jooq.tables.daos.RoleDao
import au.org.ala.sc.domain.jooq.tables.daos.SeasonDao
import au.org.ala.sc.domain.jooq.tables.daos.UserRoleDao
import au.org.ala.sc.resources.CalendarExceptionMapper
import au.org.ala.sc.resources.CalendarResource
import au.org.ala.sc.resources.ImageResource
import au.org.ala.sc.resources.SearchResource
import au.org.ala.sc.resources.LanguageResource
import au.org.ala.sc.services.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.dropwizard.Application
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.flyway.FlywayBundle
import io.dropwizard.flyway.FlywayFactory
import io.dropwizard.forms.MultiPartBundle
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import okhttp3.Call
import org.jooq.impl.DSL
import java.io.File
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.jooq.DSLContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.servlet.DispatcherType

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
        val profilesServiceClient = profileServiceClient(configuration.profileServiceBaseUrl, configuration.httpClientFactory.build(environment.metrics(), "profile-service"))
        val searchClient = searchClient(configuration.bieBaseUrl, configuration.httpClientFactory.build(environment.metrics(), "bie"))

        val dsl = configureJooq(configuration, environment, "seasonal-calendars")

        val imagesBaseDir = File(configuration.imagesBaseDir)

        val calendarDao = CalendarDao(dsl.configuration())
        val seasonDao = SeasonDao(dsl.configuration())
        val userRoleDao = UserRoleDao(dsl.configuration())
        val roleDao = RoleDao(dsl.configuration())
        val featureService = FeatureService(profilesServiceClient)
        val seasonService = SeasonService(seasonDao, dsl, featureService)
        val calendarService = CalendarService(calendarDao, dsl, seasonService, profilesServiceClient, configuration.dataResourceUid, configuration.calendarTag)
        val imageService = ImageService(imagesBaseDir)

        val calendarResource = CalendarResource(calendarService)
        val imageResource = ImageResource(imagesBaseDir, imageService)
        val searchResource = SearchResource(searchClient)
        val languageResource = LanguageResource(calendarService)

        environment.jersey().apply {
            register(CalendarExceptionMapper::class.java)
            register(calendarResource)
            register(imageResource)
            register(searchResource)
            register(languageResource)
        }
    }

    /**
     * Use a custom profile service client builder to use an instrumented client
     */
    private fun profileServiceClient(profileServiceBaseUrl: String, callFactory: Call.Factory): ProfileServiceClient {
        val moshi = Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()
        val retrofit = Retrofit.Builder()
            .baseUrl(profileServiceBaseUrl)
            .callFactory(callFactory)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(ProfileServiceClient::class.java)
    }

    private fun searchClient(bieBaseUrl: String, callFactory: Call.Factory): BieSearchClient {
        val retrofit = Retrofit.Builder()
            .baseUrl(bieBaseUrl)
            .callFactory(callFactory)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        return retrofit.create(BieSearchClient::class.java)
    }

    private fun configureJooq(configuration: SeasonalCalendarConfiguration, environment: Environment, name: String): DSLContext {
        val dataSource = configuration.database.apply { healthCheckRegistry = environment.healthChecks() }.build(environment.metrics(), name)
        environment.lifecycle().manage(dataSource)

        val dsl = DSL.using(dataSource, configuration.jooq.dialect)
//        dsl.configuration().set(ThreadLocalTransactionProvider(dsl.configuration().connectionProvider()))
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