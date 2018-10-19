package au.org.ala.sc.auth

import arrow.core.Either
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import retrofit2.http.Field
import java.io.IOException
import java.lang.reflect.*
import java.util.concurrent.CompletableFuture

interface OidcClient {
    @GET(".well-known")
    fun wellKnownConfig(): WellKnownResponse

    @GET(".well-known")
    fun wellKnownConfigAsync(): CompletableFuture<WellKnownResponse>

    @GET
    fun userInfo(
        @Url url: HttpUrl,
        @Header("Authorization") bearerToken: String?
//        @Query("access_token") accessToken: String?
    ): UserInfo

    @GET
    fun userInfoAsync(
        @Url url: HttpUrl,
        @Header("Authorization") bearerToken: String?
//        @Query("access_token") accessToken: String?
    ): CompletableFuture<UserInfo>

    @POST
    @FormUrlEncoded
    fun introspect(
        @Url url: HttpUrl,
        @Header("Authorization") clientIdAndSecret: String,
        @Field("token") token: String,
        @Field("token_type_hint") tokenTypeHint: String?
    ): IntrospectResponse

    @POST
    @FormUrlEncoded
    fun introspectAsync(
        @Url url: HttpUrl,
        @Header("Authorization") clientIdAndSecret: String,
        @Field("token") token: String,
        @Field("token_type_hint") tokenTypeHint: String?
    ): CompletableFuture<IntrospectResponse>
}

@JsonClass(generateAdapter = true)
data class IntrospectResponse(
    val active: Boolean,
    @Json(name = "client_id") val clientId: String?,
    val username: String?,
    val scope: String?,
    val sub: String?,
    val aud: String?,
    val iss: String?,
    val exp: Long?,
    val nbf: Long?,
    val iat: Long?,
    // CAS extensions
    val realmName: String?,
    val uniqueSecurityName: String?,
    val tokenType: String?,
    @Json(name="grant_type") val grantType: String?
)

@JsonClass(generateAdapter = true)
data class WellKnownResponse(
    val issuer: String,
    @Json(name="scopes_supported") val scopesSupported: List<String>,
    @Json(name="response_types_supported") val responseTypesSupported: List<String>,
    @Json(name="subject_types_supported") val subjectTypesSupported: List<String>,
    @Json(name="claim_types_supported") val claimTypesSupported: List<String>,
    @Json(name="claims_supported") val claimsSupported: List<String>,
    @Json(name="grant_types_supported") val grantTypesSupported: List<String>,
    @Json(name="id_token_signing_alg_values_supported") val idTokenSigningAlgValuesSupported: List<String>,
    @Json(name="introspection_endpoint_auth_methods_supported") val introspectionEndpointAuthMethodsSupported: List<String>,
    @Json(name="authorization_endpoint") val authorizationEndpoint: String,
    @Json(name="token_endpoint") val tokenEndpoint: String,
    @Json(name="userinfo_endpoint") val userinfoEndpoint: String,
    @Json(name="registration_endpoint") val registrationEndpoint: String,
    @Json(name="end_session_endpoint") val endSessionEndpoint: String,
    @Json(name="introspection_endpoint") val introspectionEndpoint: String,
    @Json(name="revocation_endpoint") val revocationEndpoint: String,
    @Json(name="jwks_uri") val jwksEndpoint: String
)

@JsonClass(generateAdapter = true)
data class UserInfo(
    val sub: String,
    val name: String?,
    @Json(name="given_name") val givenName: String?,
    @Json(name="family_name") val familyName: String?,
    @Json(name="middle_name") val middleName: String?,
    val nickname: String?,
    @Json(name="preferred_username") val preferredUsername: String?,
    val profile: String?,
    val picture: String?,
    val website: String?,
    val email: String?,
    @Json(name="email_verified") val emailVerified: String?,
    val gender: String?,
    val birthdate: String?,
    val zoneinfo: String?,
    val locale: String?,
    @Json(name="phone_number") val phoneNumber: String?,
    @Json(name="phone_number_verified") val phoneNumberVerified: String?,
    val address: Address?,
    @Json(name = "updated_at") val updatedAt: Long?,
    /* ALA extended */
    val organisation: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    @Json(name="auth_time") val authTime: Long?,
    val role: List<String>
)

@JsonClass(generateAdapter = true)
data class Address(
    val formatted: String?,
    @Json(name = "street_address") val streetAddress: String?,
    val locality: String?,
    val region: String?,
    @Json(name = "post_code") val postCode: String?,
    val country: String?
)

typealias ExceptionFactory = (Either<IOException, Response<*>>) -> RuntimeException
private val defaultExceptionFactory: ExceptionFactory = { responseOrException ->
    responseOrException
        .fold({ e -> RuntimeException(e) }) { r -> RuntimeException("HTTP ${r.code()}: ${r.errorBody()?.string()}") }
}
class SimpleCallAdapterFactory(
    val exceptionFactory: ExceptionFactory = defaultExceptionFactory
): CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        val raw = getRawType(returnType)
        if (raw == Call::class.java || raw == CompletableFuture::class.java) {
            return null
        }
        return object : CallAdapter<Any, Any> {
            override fun responseType(): Type {
                return returnType
            }

            override fun adapt(call: Call<Any>): Any {
                try {
                    val response = call.execute()
                    return if (response.isSuccessful) response.body()!!
                    else throw exceptionFactory(Either.right(response))
                    //RuntimeException("${response.code()}: ${response.errorBody()?.string()}")
                } catch (e: IOException) {
                    throw exceptionFactory(Either.left(e))
                    //RuntimeException(e)
                }
            }
        }
    }

}

val SIMPLE_CALL_ADAPTER_FACTORY = SimpleCallAdapterFactory()

fun String.httpUrl() = HttpUrl.get(this)!!
fun String.baseUrl() = HttpUrl.get(if (this.endsWith("/")) this else "$this/")!!

fun String.bearerToken() = "Bearer $this"

fun getRawType(type: Type):Class<*> {
    return when (type) {
        is Class<*> -> type // Type is a normal class.

        is ParameterizedType -> {
            // I'm not exactly sure why getRawType() returns Type instead of Class. Neal isn't either but
            // suspects some pathological case related to nested classes exists.
            type.rawType as? Class<*> ?: throw IllegalArgumentException()
        }
        is GenericArrayType -> {
            val componentType = type.genericComponentType
            java.lang.reflect.Array.newInstance(getRawType(componentType), 0).javaClass
        }
        is TypeVariable<*> -> // We could use the variable's bounds, but that won't work if there are multiple. Having a raw
            // type that's more general than necessary is okay.
            Any::class.java
        is WildcardType -> return getRawType(type.upperBounds[0])
        else -> throw IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <$type> is of type ${type.javaClass.name}")
    }

}