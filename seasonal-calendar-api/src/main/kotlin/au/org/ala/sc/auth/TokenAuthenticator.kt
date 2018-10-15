package au.org.ala.sc.auth

import au.org.ala.sc.util.logger
import au.org.ala.userdetails.UserDetailsClient
import au.org.ala.web.UserDetails
import com.github.benmanes.caffeine.cache.Caffeine
import com.google.common.util.concurrent.UncheckedExecutionException
import io.dropwizard.auth.AuthenticationException
import io.dropwizard.auth.Authenticator
import okhttp3.Credentials
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.ExecutionException

class TokenAuthenticator(
    private val userDetailsClient: UserDetailsClient,
    private val oidcClient: OidcClient,
    private val wk: WellKnownResponse,
    private val clientId: String,
    private val clientSecret: String
): Authenticator<String, User> {

    companion object {
        val log = logger()
    }

    private val userDetailsCache = Caffeine.from("expireAfterWrite=10m").buildAsync(this::userDetails)
    private val introspectCache = Caffeine.from("expireAfterWrite=5m").buildAsync(this::introspect)
    private val userinfoCache = Caffeine.from("expireAfterWrite=5m").buildAsync(this::userinfo)

    override fun authenticate(credentials: String?): Optional<User> {
        if (credentials == null || credentials.isBlank()) {
            return User.Anonymous.asOptional
        }

        log.error("Got token {}", credentials)

        try {
            val now = Instant.now()
            val introspect = introspectAsync(credentials) //introspectCache.get(credentials)
            val userinfo = userinfoAsync(credentials)//userinfoCache.get(credentials)
            val username = introspect.thenApply { i -> i.sub ?: i.uniqueSecurityName ?: "" }
//            val userdetails = username.thenCompose { name -> userDetailsCache.get(name) }
            val userdetails = username.thenApply { name -> userDetails(name) }
//            val username = introspect.sub ?: introspect.uniqueSecurityName ?: ""
//            val userdetails = userDetailsCache.get(username)
            val introspectResponse = introspect.get()
            if (!introspectResponse.active) {
                log.error("Token {} is inactive", credentials)
                throw AuthenticationException("Token inactive")
            }
            if (introspectResponse.exp != null) {
                val expiry = Instant.ofEpochSecond(introspectResponse.exp)
                if (now.plusSeconds(3).isBefore(expiry)) {
                    log.error("Token {} is expired.  Expired at {}", credentials, expiry)
                    throw AuthenticationException("Token expired")
                }
            }
            log.info("Token audience: {}", introspectResponse.aud)
            log.info("Token issuer: {}", introspectResponse.iss)
//            log.info("Token sub: {}", introspectResponse.sub)
            CompletableFuture.allOf(userinfo, username, userdetails).join()
            return Optional.of(User.Authenticated(username.get(), userinfo.get(), userdetails.get()))
        } catch (e: UncheckedExecutionException) {
            val cause = e.cause
            when (cause) {
                is AuthenticationException -> throw cause
                else -> throw AuthenticationException(cause)
            }
        } catch (e: ExecutionException) {
            val cause = e.cause
            when (cause) {
                is AuthenticationException -> throw cause
                else -> throw AuthenticationException(cause)
            }
        }
        catch (e: CompletionException) {
            val cause = e.cause
            when (cause) {
                is AuthenticationException -> throw cause
                else -> throw AuthenticationException(cause)
            }
        }
    }

    private fun userinfoAsync(token: String?): CompletableFuture<UserInfo> {
        if (token == null || token.isBlank()) throw IllegalStateException()

        return oidcClient.userInfoAsync(wk.userinfoEndpoint.httpUrl(), token.bearerToken())//, token)
    }

    private fun userinfo(token: String?): UserInfo {
        if (token == null || token.isBlank()) throw IllegalStateException()

        return oidcClient.userInfo(wk.userinfoEndpoint.httpUrl(), token.bearerToken())//, token)
    }

    private fun introspectAsync(token: String?): CompletableFuture<IntrospectResponse> {
        if (token == null || token.isBlank()) throw IllegalStateException()

        return oidcClient.introspectAsync(
            wk.introspectionEndpoint.httpUrl(),
            Credentials.basic(clientId, clientSecret),
            token,
            null
        )
    }

    private fun introspect(token: String?): IntrospectResponse {
        if (token == null || token.isBlank()) throw IllegalStateException()

        return oidcClient.introspect(
            wk.introspectionEndpoint.httpUrl(),
            Credentials.basic(clientId, clientSecret),
            token,
            null)
    }

    private fun userDetails(name: String?): UserDetails {
        if (name == null || name.isBlank()) throw IllegalStateException()
        val userDetailsResponse = userDetailsClient.getUserDetails(name, true).execute()
        return if (userDetailsResponse.isSuccessful) userDetailsResponse.body()!! else {
            log.error("user details returned an error querying name {}, response {}: {}", name, userDetailsResponse.code(), userDetailsResponse.errorBody()?.string())
            throw AuthenticationException("UserDetails returned an error")
        }
    }

}


