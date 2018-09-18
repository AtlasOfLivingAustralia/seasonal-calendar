package au.org.ala.sc.resources

import au.org.ala.sc.services.BieSearchClient
import au.org.ala.sc.services.BieSearchResponse
import org.slf4j.LoggerFactory
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

@Produces(MediaType.APPLICATION_JSON)
@Path("search")
class SearchResource(private val searchClient: BieSearchClient) {

    companion object {
        val log = LoggerFactory.getLogger(SearchResource::class.java)
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
            throw WebApplicationException("error calling bie search", if (code >= 300) code else 500)
        }
    }
}