package au.org.ala.sc.services

import com.squareup.moshi.JsonClass
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BieSearchClient {

    @GET("ws/search/auto?idxType=TAXON")
    fun search(@Query("q") searchTerm: String, @Query("limit") limit: Int = 10): Call<BieSearchResponse>
}

@JsonClass(generateAdapter = true)
data class BieSearchResponse(val autoCompleteList: List<BieSearchResult>)

@JsonClass(generateAdapter = true)
data class BieSearchResult(
    val commonNameMatches: List<String>,
    val name: String,
    val occurenceCount: Int?,
    val guid: String,
    val scientificNameMatches: List<String>,
    val rankString: String,
    val matchedNames: List<String>,
    val rankId: Int?,
    val commonName: String?,
    val georeferencedCount: Int)
