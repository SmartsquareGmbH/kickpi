package de.smartsquare.kickpi

import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import javax.inject.Inject

class AuthorizationService @Inject constructor(private val authorizationRepository: AuthorizationRepository) {

    fun authorize(name: String, deviceId: String) {
        val credentials = Credentials(deviceId, name)
        val authorizationResponse = authorizationRepository.authorize(credentials).execute()

        if (authorizationResponse.isSuccessful.not()) throw UnauthorizedException()
    }
}

interface AuthorizationRepository {

    @POST("authorization")
    fun authorize(@Body credentials: Credentials): Call<ResponseBody>

    @DELETE("authorization")
    fun unauthorize(@Body credentials: Credentials): Call<ResponseBody>
}

@JsonClass(generateAdapter = true)
data class Credentials(val deviceId: String, val name: String)