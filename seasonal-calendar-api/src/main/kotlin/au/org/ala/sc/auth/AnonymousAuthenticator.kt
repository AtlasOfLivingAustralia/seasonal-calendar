package au.org.ala.sc.auth

import io.dropwizard.auth.AuthFilter
import io.dropwizard.auth.Authenticator
import io.dropwizard.auth.oauth.OAuthCredentialAuthFilter.OAUTH_ACCESS_TOKEN_PARAM
import java.security.Principal
import java.util.*
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.SecurityContext


object AnonymousAuthenticator : Authenticator<String, User> {
    /**
     * If there are any credentials at all then this is not an anonymous request
     */
    override fun authenticate(credentials: String?): Optional<User> {
        return if (credentials.isNullOrBlank()) User.Anonymous.asOptional else Optional.empty()
    }
}

class AnonymousAuthFilter<P: Principal> : AuthFilter<String, P>() {
    override fun filter(requestContext: ContainerRequestContext) {

        var credentials = getCredentials(requestContext.headers.getFirst(HttpHeaders.AUTHORIZATION))

        // If Authorization header is not used, check query parameter where token can be passed as well
        if (credentials == null) {
            credentials = requestContext.uriInfo.queryParameters.getFirst(OAUTH_ACCESS_TOKEN_PARAM)
        }


        // authenticate will automatically  reject null, but the anonymous authenticator needs to check if the credentials are blank
        // so turn null credentials into ""
        authenticate(requestContext, credentials ?: "", SecurityContext.BASIC_AUTH)
    }

    /**
     * Parses a value of the `Authorization` header in the form of `Bearer a892bf3e284da9bb40648ab10`.
     *
     * @param header the value of the `Authorization` header
     * @return a token
     */
    private fun getCredentials(header: String?): String? {
        if (header == null) {
            return null
        }

        val space = header.indexOf(' ')
        if (space <= 0) {
            return null
        }

        val method = header.substring(0, space)
        return if (!prefix.equals(method, ignoreCase = true)) {
            null
        } else header.substring(space + 1)

    }

    class Builder<P: Principal> : AuthFilter.AuthFilterBuilder<String, P, AnonymousAuthFilter<P>>() {
        override fun newInstance(): AnonymousAuthFilter<P> = AnonymousAuthFilter()
    }
}

val ANONYMOUS_AUTHENTICATOR = Authenticator { credentials: String -> if (credentials.isNullOrBlank()) User.Anonymous.asOptional else Optional.empty() }

