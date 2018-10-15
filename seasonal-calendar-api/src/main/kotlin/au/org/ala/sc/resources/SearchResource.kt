package au.org.ala.sc.resources

import au.org.ala.sc.auth.ROLE_ADMIN
import au.org.ala.sc.auth.ROLE_CALENDAR_ADMIN
import au.org.ala.sc.auth.ROLE_SC_ADMIN
import au.org.ala.sc.services.BieSearchClient
import au.org.ala.sc.services.BieSearchResponse
import au.org.ala.sc.util.logger
import au.org.ala.userdetails.UserDetailsClient
import au.org.ala.userdetails.UserDetailsFromIdListRequest
import au.org.ala.web.UserDetails
import retrofit2.http.Query
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Path("search")
class SearchResource @Inject constructor(private val searchClient: BieSearchClient, private val userDetailsClient: UserDetailsClient) {

    companion object {
        val log = logger()
    }

    @GET
    @Path("bie")
    fun searchBie(@QueryParam("q") searchTerm: String, @QueryParam("limit") @DefaultValue("10") limit: Int): BieSearchResponse {
        val response = searchClient.search(searchTerm, limit).execute()
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            log.error("Error calling bie search: {}", response.errorBody()?.string())
            val code = response.code()
            throw WebApplicationException("error calling bie search", if (code in 300..500) code else 500)
        }
    }

    @GET
    @Path("users")
    @RolesAllowed(ROLE_ADMIN, ROLE_SC_ADMIN, ROLE_CALENDAR_ADMIN)
    fun searchUsers(@QueryParam("q") searchTerm: String): List<UserDetails> {
        val response = userDetailsClient.searchUserDetails(searchTerm, 10).execute()
        if (response.isSuccessful)
            return response.body()!!
        else {
            val code = response.code()
            log.error("Error calling user details search ({}): {}", code, response.errorBody()?.string())
            throw WebApplicationException("error calling userdetails serarch", if (code in 300..500) code else 500)
        }
    }
}