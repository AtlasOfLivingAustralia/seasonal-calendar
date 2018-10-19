package au.org.ala.sc

import arrow.syntax.function.pipe
import au.org.ala.profiles.service.ProfileServiceClient
import au.org.ala.sc.auth.*
import au.org.ala.sc.config.SeasonalCalendarConfiguration
import au.org.ala.sc.resources.*
import au.org.ala.sc.services.*
import au.org.ala.sc.util.logger
import au.org.ala.userdetails.UserDetailsClient
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.dropwizard.Application
import io.dropwizard.auth.AuthDynamicFeature
import io.dropwizard.auth.AuthValueFactoryProvider
import io.dropwizard.auth.chained.ChainedAuthFilter
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.flyway.FlywayBundle
import io.dropwizard.flyway.FlywayFactory
import io.dropwizard.forms.MultiPartBundle
import io.dropwizard.lifecycle.Managed
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import okhttp3.Call
import okhttp3.HttpUrl
import org.jooq.impl.DSL
import java.io.File
import org.eclipse.jetty.servlets.CrossOriginFilter
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature
import org.jooq.DSLContext
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.java8.Java8CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Singleton
import javax.servlet.DispatcherType

class SeasonalCalendarApplication : Application<SeasonalCalendarConfiguration>() {

    companion object {
        val log = logger()
    }

    override fun initialize(bootstrap: Bootstrap<SeasonalCalendarConfiguration>) {
        bootstrap.objectMapper.findAndRegisterModules()
        bootstrap.addBundle(MultiPartBundle())
        bootstrap.addBundle(object : FlywayBundle<SeasonalCalendarConfiguration>() {
            override fun getDataSourceFactory(configuration: SeasonalCalendarConfiguration): PooledDataSourceFactory {
                return configuration.database.flyway
            }

            override fun getFlywayFactory(configuration: SeasonalCalendarConfiguration): FlywayFactory {
                return configuration.flyway
            }
        })
    }

    override fun run(configuration: SeasonalCalendarConfiguration, environment: Environment) {

        // Setup the OIDC well known endpoints call first
        val moshi = moshi()
        val oidcHttpClient = configuration.httpClientFactory.build(environment.metrics(), "oidc")
        val oidcClient = oidcClient(
            configuration.oidcBaseUrl,
            oidcHttpClient,
            moshi
        )
        // TODO replace with suspend fun after Kotlin 1.3 and Retrofit support is added
        val wk = oidcClient.wellKnownConfigAsync()

        if (configuration.warnOnUnknownJsonProperties) {
            log.warn("Disabling errors on unknown JSON properties")
            environment.objectMapper.addHandler(object: DeserializationProblemHandler() {
                override fun handleUnknownProperty(ctxt: DeserializationContext?, p: JsonParser?, deserializer: JsonDeserializer<*>?, beanOrClass: Any?, propertyName: String?): Boolean {
                    val typeName = when(beanOrClass) {
                        is Class<*> -> beanOrClass.name
                        null -> "null"
                        else -> beanOrClass::class.java
                    }
                    log.warn("Unknown json property {} for type {}", propertyName, typeName)
                    return true
                }
            })
        }

        val migrations = configuration.database.flyway.build(environment.metrics(), "flyway").use { dataSource -> configuration.flyway.build(dataSource).migrate() }
        log.info("Applied {} Flyway migrations", migrations)

        addCors(environment, configuration)

        val profileServiceClient = profileServiceClient(
            configuration.profileServiceBaseUrl,
            configuration.httpClientFactory.build(environment.metrics(), "profile-service"),
            moshi
        )
        val userDetailsClient = userDetailsClient(
            configuration.userDetailsBaseUrl,
            configuration.httpClientFactory.build(environment.metrics(), "userdetails"),
            moshi
        )
        val searchClient = searchClient(
            configuration.bieBaseUrl,
            configuration.httpClientFactory.build(environment.metrics(), "bie"),
            moshi
        )

        val dsl = configureJooq(configuration, environment, "seasonal-calendars")

        val imagesBaseDir = configuration.imagesBaseDir.asDirectory()

        val jooqConfiguration = dsl.configuration()
        val calendarUuidResolver = DefaultCalendarUuidResolver(profileServiceClient)
        val userService = DefaultUserService(jooqConfiguration, calendarUuidResolver)
        val featureServiceFactory = DefaultFeatureServiceFactory(profileServiceClient)
        val seasonServiceFactory = DefaultSeasonServiceFactory(jooqConfiguration, featureServiceFactory)
        val calendarServiceFactory = DefaultCalendarServiceFactory(
            jooqConfiguration,
            profileServiceClient,
            calendarUuidResolver,
            userService,
            seasonServiceFactory,
            configuration.dataResourceUid,
            configuration.calendarTag
        )

        val tokenAuthenticator = TokenAuthenticator(userDetailsClient, oidcClient, wk.get(), configuration.oidcClientId, configuration.oidcClientSecret, configuration.allowedTimestampDrift)
        val userAuthorizer = UserAuthorizer(userService)

        val oauthFilter = OAuthCredentialAuthFilter.Builder<User>()
                .setAuthenticator(tokenAuthenticator)
                .setAuthorizer(userAuthorizer)
                .setPrefix("Bearer")
                .buildAuthFilter()

        val defaultFilter = AnonymousAuthFilter.Builder<User>()
            .setAuthenticator(ANONYMOUS_AUTHENTICATOR)
            .setAuthorizer(userAuthorizer)
            .setPrefix("Bearer")
            .buildAuthFilter()

        val filters = listOf(oauthFilter, defaultFilter)
        val chainedAuthFilter = ChainedAuthFilter<String,User>(filters)

        environment.jersey().apply {

            // register singletons
            register(object: AbstractBinder() {
                override fun configure() {
                    bind(calendarServiceFactory).to(ICalendarServiceFactory::class.java)
                    bind(imagesBaseDir).named("imagesBaseDir").to(File::class.java)
                    bind(searchClient).to(BieSearchClient::class.java)
                    bind(userDetailsClient).to(UserDetailsClient::class.java)
                    bind(DefaultImageService::class.java).to(ImageService::class.java).`in`(Singleton::class.java)
                    bind(userService).to(UserService::class.java)
                }
            })

            register(AuthDynamicFeature(chainedAuthFilter))
            register(RolesAllowedDynamicFeature::class.java)
            register(CalendarACLDynamicFeature::class.java)
            //If you want to use @Auth to inject a custom Principal type into your resource
            register(AuthValueFactoryProvider.Binder(User::class.java))

            // Register as classes so that they can have per-request resources injected
            register(CalendarExceptionMapper::class.java)
            register(CalendarResource::class.java)
            register(ImageResource::class.java)
            register(LanguageResource::class.java)
            register(RolesResource::class.java)
            register(SearchResource::class.java)
        }
    }

    private fun String.asDirectory(): File {
        val result = File(this)
        if (result.exists() && !result.isDirectory) {
            throw IllegalStateException("$this already exists and is not a directory")
        }
        if (!result.exists() && !result.mkdirs()) {
            throw IllegalStateException("Couldn't create $this")
        }
        return result
    }

    private fun moshi() =
        Moshi.Builder().add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe()).build()

    private fun oidcClient(oidcBaseUrl: String, oidcClient: Call.Factory, moshi: Moshi): OidcClient {
        return moshiRetrofitClient(OidcClient::class.java, oidcBaseUrl.baseUrl(), oidcClient, moshi, listOf(SIMPLE_CALL_ADAPTER_FACTORY, Java8CallAdapterFactory.create()))
    }

    private fun userDetailsClient(userDetailsBaseUrl: String, callFactory: Call.Factory, moshi: Moshi) =
        UserDetailsClient.Builder(callFactory, userDetailsBaseUrl).moshi(moshi).build()

    /**
     * Use a custom profile service client builder to use an instrumented client
     */
    private fun profileServiceClient(profileServiceBaseUrl: String, callFactory: Call.Factory, moshi: Moshi): ProfileServiceClient {
        return moshiRetrofitClient(ProfileServiceClient::class.java, profileServiceBaseUrl.baseUrl(), callFactory, moshi)
    }

    private fun searchClient(bieBaseUrl: String, callFactory: Call.Factory, moshi: Moshi): BieSearchClient {
        return moshiRetrofitClient(BieSearchClient::class.java, bieBaseUrl.baseUrl(), callFactory, moshi)
    }

    private fun <T> moshiRetrofitClient(type: Class<T>, baseUrl: HttpUrl, callFactory: Call.Factory, moshi: Moshi, callAdapterFactories: List<CallAdapter.Factory> = emptyList()): T {
        return retrofitClient(type, baseUrl, callFactory, listOf(MoshiConverterFactory.create(moshi)), callAdapterFactories)
    }

    private fun <T> retrofitClient(type: Class<T>, baseUrl: HttpUrl, callFactory: Call.Factory, converterFactories: List<Converter.Factory>, callAdapterFactories: List<CallAdapter.Factory> = emptyList()): T {
        val builder = Retrofit.Builder()
            .baseUrl(baseUrl)
            .callFactory(callFactory)

        val retrofit = (
                converterFactories.fold(builder, Retrofit.Builder::addConverterFactory)
                pipe { callAdapterFactories.fold(it, Retrofit.Builder::addCallAdapterFactory) }
                pipe Retrofit.Builder::build
                )

        return retrofit.create(type)

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