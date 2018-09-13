package de.smartsquare.kickpi

import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class Endpoints {
    private val endpoints = mutableMapOf<String, String>()

    fun register(endpointId: String, username: String) {
        endpoints[endpointId] = username
    }

    fun getIfAuthorized(endpointId: String) = endpoints[endpointId]
}

class AuthorizationService(private val authorizationRepository: KickwayAuthorizationRepository) {

    fun isAuthorized(name: String, deviceId: String) =
        authorizationRepository.authorize(Credentials(deviceId, name)).execute().isSuccessful
}

interface KickwayAuthorizationRepository {

    @POST("authorization")
    fun authorize(@Body credentials: Credentials): Call<ResponseBody>
}

@JsonClass(generateAdapter = true)
data class Credentials(val deviceId: String, val name: String)