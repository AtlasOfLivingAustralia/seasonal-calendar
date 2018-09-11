package au.org.ala.sc.modules

import com.codahale.metrics.*
import com.codahale.metrics.MetricRegistry.name
import okhttp3.*
import org.slf4j.LoggerFactory
import java.io.IOException

class InstrumentedOkHttpClient(var rawClient: OkHttpClient, private val registry: MetricRegistry, val name: String) : Cloneable, Call.Factory by rawClient, WebSocket.Factory by rawClient {
    companion object {
        val LOG = LoggerFactory.getLogger(InstrumentedOkHttpClient::class.java)
    }

    init {
        instrumentHttpCache()
        instrumentConnectionPool()
        instrumentNetworkRequests()
    }

    /**
     * Generates an identifier, with a common prefix, in order to uniquely
     * identify the `metric` in the registry.
     *
     *
     * The generated identifier includes:
     *
     *
     *  * the fully qualified name of the [OkHttpClient] class
     *  * the name of the instrumented client, if provided
     *  * the given `metric`
     *
     */
    private fun metricId(metric: String) = name(OkHttpClient::class.java, name, metric)

    private fun instrumentHttpCache() {
        if (rawClient.cache() == null) return

        registry.register(metricId("cache-request-count"), Gauge<Int> { rawClient.cache()!!.requestCount() })
        registry.register(metricId("cache-hit-count"), Gauge<Int> { rawClient.cache()!!.hitCount() })
        registry.register(metricId("cache-network-count"), Gauge<Int> { rawClient.cache()!!.networkCount() })
        registry.register(metricId("cache-write-success-count"), Gauge<Int> { rawClient.cache()!!.writeSuccessCount() })
        registry.register(metricId("cache-write-abort-count"), Gauge<Int> { rawClient.cache()!!.writeAbortCount() })
        val currentCacheSize = Gauge<Long> {
            try {
                rawClient.cache()!!.size()
            } catch (ex: IOException) {
                LOG.error(ex.message, ex)
                -1L
            }

        }
        val maxCacheSize = Gauge<Long> { rawClient.cache()!!.maxSize() }
        registry.register(metricId("cache-current-size"), currentCacheSize)
        registry.register(metricId("cache-max-size"), maxCacheSize)
        registry.register(metricId("cache-size"), object : RatioGauge() {
            override fun getRatio(): Ratio {
                return Ratio.of(currentCacheSize.value.toDouble(), maxCacheSize.value.toDouble())
            }
        })
    }

    private fun instrumentConnectionPool() {
        if (rawClient.connectionPool() == null) {
            rawClient = rawClient.newBuilder().connectionPool(ConnectionPool()).build()
        }

        registry.register(
            metricId("connection-pool-total-count"),
            Gauge<Int> { rawClient.connectionPool().connectionCount() })
        registry.register(
            metricId("connection-pool-idle-count"),
            Gauge<Int> { rawClient.connectionPool().idleConnectionCount() })
    }

    private fun instrumentNetworkRequests() {
        rawClient = rawClient.newBuilder()
            .addNetworkInterceptor(InstrumentedInterceptor(registry, name(OkHttpClient::class.java, this.name))).build()
    }

    override fun newCall(request: Request) = rawClient.newCall(request)

    override fun newWebSocket(request: Request, listener: WebSocketListener) = rawClient.newWebSocket(request, listener)

    override fun hashCode(): Int = rawClient.hashCode()

    override fun equals(other: Any?): Boolean {
        return if (other is InstrumentedOkHttpClient) {
            rawClient == other.rawClient
        } else {
            rawClient == other
        }
    }

    override fun toString() = rawClient.toString()


}

class InstrumentedInterceptor(registry: MetricRegistry, name: String) : Interceptor {
    private val submitted: Meter = registry.meter(name(name, "network-requests-submitted"))
    private val running: Counter = registry.counter(name(name, "network-requests-running"))
    private val completed: Meter = registry.meter(name(name, "network-requests-completed"))
    private val duration: Timer = registry.timer(name(name, "network-requests-duration"))

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        submitted.mark()
        running.inc()
        val context = duration.time()
        try {
            return chain.proceed(chain.request())
        } finally {
            context.stop()
            running.dec()
            completed.mark()
        }
    }
}