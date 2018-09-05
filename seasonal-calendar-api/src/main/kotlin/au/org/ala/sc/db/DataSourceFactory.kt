package au.org.ala.sc.db

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.health.HealthCheckRegistry
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.dropwizard.db.ManagedDataSource
import io.dropwizard.db.PooledDataSourceFactory
import io.dropwizard.util.Duration
import io.dropwizard.validation.MinDuration
import io.dropwizard.validation.ValidationMethod
import java.io.PrintWriter
import java.sql.Connection
import java.util.*
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import javax.sql.DataSource
import javax.validation.constraints.Min

@JsonIgnoreProperties(ignoreUnknown = true)
class DataSourceFactory : PooledDataSourceFactory {

    var _driverClass: String? = null
    @JsonProperty override fun getDriverClass(): String? = _driverClass
    @JsonProperty fun setDriverClass(driverClass: String?) { this._driverClass = driverClass }

    @get:JsonProperty
    @set:JsonProperty
    var dataSourceClass: String? = null

    @get:JsonProperty
    @set:JsonProperty
    var isAutoCommit: Boolean? = null

    override fun isAutoCommentsEnabled(): Boolean = isAutoCommit ?: true

    @get:JsonProperty
    @set:JsonProperty
    var readOnly: Boolean? = null

    @get:JsonProperty
    @set:JsonProperty
    var username: String? = null

    @get:JsonProperty
    @set:JsonProperty
    var password: String? = null

    private var _url: String? = null
    @JsonProperty override fun getUrl(): String? = _url
    @JsonProperty fun setUrl(url: String?) { this._url = url }

    private var _properties: MutableMap<String, String> = mutableMapOf()
    @JsonProperty override fun getProperties(): MutableMap<String, String> = _properties
    @JsonProperty fun setProperties(properties: MutableMap<String, String>) { this._properties = properties }

    @get:JsonProperty
    @set:JsonProperty
    var healthCheckProperties: Map<String, String> = mutableMapOf()

    @get:JsonProperty
    @set:JsonProperty
    var catalog: String? = null

    @get:JsonProperty
    @set:JsonProperty
    var transactionIsolation: TransactionIsolation? = null

    @Min(1)
    @get:JsonProperty
    @set:JsonProperty
    var minimumIdle: Int? = null

    @Min(1)
    @get:JsonProperty
    @set:JsonProperty
    var maximumPoolSize: Int? = null

    @get:JsonProperty
    @set:JsonProperty
    var connectionInitSql: String? = null

    @MinDuration(value = 1, unit = TimeUnit.SECONDS)
    @get:JsonProperty
    @set:JsonProperty
    var maxLifetime: Duration? = null

    private var _validationQuery: String? = null
    @JsonProperty
    @Deprecated("Leaving this unset will use Connection.isValid() instead")
    override fun getValidationQuery() = _validationQuery
    @JsonProperty
    fun setValidationQuery(validationQuery: String?) { this._validationQuery = validationQuery }

    @MinDuration(value = 1, unit = TimeUnit.SECONDS)
    @get:JsonProperty
    @set:JsonProperty
    var validationTimeout: Duration? = null

    val isMinSizeLessThanMaxSize: Boolean
        @JsonIgnore
        @ValidationMethod(message = ".minimumIdle must be less than or equal to maximumPoolSize")
        get() = this.minimumIdle ?: 0 <= this.maximumPoolSize ?: 0

    enum class TransactionIsolation {
        TRANSACTION_NONE, TRANSACTION_READ_UNCOMMITTED, TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE
    }

    @Deprecated("")
    @JsonIgnore
    override fun getHealthCheckValidationQuery(): String? {
        return this.getValidationQuery()
    }

    @Deprecated("")
    @JsonIgnore
    override fun getHealthCheckValidationTimeout(): Optional<Duration> {
        return this.getValidationQueryTimeout()
    }

    @JsonProperty
    override fun getValidationQueryTimeout(): Optional<Duration> {
        return Optional.ofNullable(this.validationTimeout)
    }

    override fun asSingleConnectionPool() {
        this.minimumIdle = 1
        this.maximumPoolSize = 1
    }


    @MinDuration(250, unit = TimeUnit.MILLISECONDS)
    @get:JsonProperty
    @set:JsonProperty
    var connectionTimeout: Duration? = null

    @MinDuration(10, unit = TimeUnit.SECONDS)
    @get:JsonProperty
    @set:JsonProperty
    var idleTimeout: Duration? = null

    @MinDuration(2, unit = TimeUnit.SECONDS)
    @get:JsonProperty
    @set:JsonProperty
    var leakDetectionThreshold: Duration? = null

    @get:JsonProperty
    @set:JsonProperty
    var schema: String? = null

    @get:JsonProperty
    @set:JsonProperty
    var initializationFailTimeout: Duration? = null

    @get:JsonProperty
    @set:JsonProperty
    var isolateInternalQueries: Boolean? = null

    var scheduledExecutor: ScheduledExecutorService? = null
    var healthCheckRegistry: HealthCheckRegistry? = null

    override fun build(metricRegistry: MetricRegistry, name: String): ManagedDataSource {

        val config = HikariConfig()

        fun Map<String, String>.toProperties(): Properties = Properties().also { props -> this.onEach{ (k,v) -> props.setProperty(k,v) } }
        config.dataSourceProperties = properties.toProperties()
        config.healthCheckProperties = healthCheckProperties.toProperties()

        dataSourceClass?.let { config.dataSourceClassName = it }
        url?.let { config.jdbcUrl = it }

        username?.let { config.username = it }
        password?. let { config.password = it }

        isAutoCommit?.let { config.isAutoCommit = it }

        connectionTimeout?.let { config.connectionTimeout = it.toMilliseconds() }
        idleTimeout?.let { config.idleTimeout = it.toMilliseconds() }

        maxLifetime?.let { config.maxLifetime = it.toMilliseconds() }

        validationQuery?.let { config.connectionTestQuery = it }

        maximumPoolSize?.let { config.maximumPoolSize = it }
        minimumIdle?.let { config.minimumIdle = it }

        healthCheckRegistry?.let { config.healthCheckRegistry = it }
        config.metricRegistry = metricRegistry

        config.poolName = name

        // infrequently used
        initializationFailTimeout?.let { config.initializationFailTimeout = it.toMilliseconds() }
        isolateInternalQueries?.let { config.isIsolateInternalQueries = it }

        //allowPoolSuspension

        readOnly?.let { config.isReadOnly = it }

        //registerMBeans
        catalog?.let { config.catalog = it }
        connectionInitSql?.let { config.connectionInitSql = it }

        driverClass?.let { config.driverClassName = it }

        transactionIsolation?.let { config.transactionIsolation = it.toString() }

        validationTimeout?.let { config.validationTimeout = it.toMilliseconds() }
        leakDetectionThreshold?.let { config.leakDetectionThreshold = it.toMilliseconds() }

        //dataSource

        schema?.let { config.schema = it }

        //threadFactory

        scheduledExecutor?.let { config.scheduledExecutor = it }

        return HikariManagedDataSource(config)
    }
}

class HikariManagedDataSource(private val hikariConfig: HikariConfig) : ManagedDataSource, DataSource {

    private lateinit var dataSource: HikariDataSource

    override fun start() {
        dataSource = HikariDataSource(hikariConfig)
    }

    override fun stop() {
        dataSource.close()
    }

    override fun setLogWriter(out: PrintWriter?) {
        dataSource.logWriter = out
    }

    override fun setLoginTimeout(seconds: Int) {
        dataSource.loginTimeout = seconds
    }

    override fun isWrapperFor(iface: Class<*>?): Boolean = dataSource.isWrapperFor(iface)
    override fun <T : Any?> unwrap(iface: Class<T>?): T = dataSource.unwrap(iface)
    override fun getConnection(): Connection = dataSource.connection
    override fun getConnection(username: String?, password: String?): Connection = dataSource.getConnection(username, password)
    override fun getParentLogger(): Logger = dataSource.parentLogger
    override fun getLogWriter(): PrintWriter = dataSource.logWriter
    override fun getLoginTimeout(): Int = dataSource.loginTimeout

}