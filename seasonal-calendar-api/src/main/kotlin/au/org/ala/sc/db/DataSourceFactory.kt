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
import org.slf4j.LoggerFactory
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

    companion object {
        val log = LoggerFactory.getLogger(DataSourceFactory::class.java)
    }

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

    @Deprecated("Leaving this unset will use Connection.isValid() instead")
    @JsonIgnore
    override fun getHealthCheckValidationQuery(): String? {
        return this.getValidationQuery()
    }

    @Deprecated("")
    @JsonIgnore
    override fun getHealthCheckValidationTimeout(): Optional<Duration> {
        return this.validationQueryTimeout
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

        fun <T> ((T) -> Unit).ifNotNull(value: T?) { value?.let { this@ifNotNull(value) } }

        config::setDataSourceClassName.ifNotNull(dataSourceClass)
        config::setJdbcUrl.ifNotNull(url)

        config::setUsername.ifNotNull(username)
        config::setPassword.ifNotNull(password)

        config::setAutoCommit.ifNotNull(isAutoCommit)

        config::setConnectionTimeout.ifNotNull(connectionTimeout?.toMilliseconds())
        config::setIdleTimeout.ifNotNull(idleTimeout?.toMilliseconds())

        config::setMaxLifetime.ifNotNull(maxLifetime?.toMilliseconds())

        validationQuery?.let { validationQuery ->
            log.warn("A validation query has been set, it's generally better to leave the connection validation to the Driver")
            config.connectionTestQuery = validationQuery
        }

        config::setMaximumPoolSize.ifNotNull(maximumPoolSize)
        config::setMinimumIdle.ifNotNull(minimumIdle)

        config::setHealthCheckRegistry.ifNotNull(healthCheckRegistry)
        config.metricRegistry = metricRegistry

        config.poolName = name

        // infrequently used
        config::setInitializationFailTimeout.ifNotNull(initializationFailTimeout?.toMilliseconds())
        config::setIsolateInternalQueries.ifNotNull(isolateInternalQueries)

        //allowPoolSuspension

        config::setReadOnly.ifNotNull(readOnly)

        //registerMBeans
        config::setCatalog.ifNotNull(catalog)
        config::setConnectionInitSql.ifNotNull(connectionInitSql)

        config::setDriverClassName.ifNotNull(driverClass)

        config::setTransactionIsolation.ifNotNull(transactionIsolation?.toString())

        config::setValidationTimeout.ifNotNull(validationTimeout?.toMilliseconds())
        config::setLeakDetectionThreshold.ifNotNull(leakDetectionThreshold?.toMilliseconds())

        //dataSource

        config::setSchema.ifNotNull(schema)

        //threadFactory

        config::setScheduledExecutor.ifNotNull(scheduledExecutor)

        return HikariManagedDataSource(config)
    }
}

class HikariManagedDataSource(private val hikariConfig: HikariConfig, lazyMode: LazyThreadSafetyMode = LazyThreadSafetyMode.NONE) : ManagedDataSource, DataSource {

    companion object {
        private val log = LoggerFactory.getLogger(HikariManagedDataSource::class.java)
    }

    private val dataSource: HikariDataSource by lazy(lazyMode) { HikariDataSource(hikariConfig) }

    override fun start() {
        val poolName = dataSource.poolName
        log.info("Datasource {} is running {}", poolName, dataSource.isRunning)
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