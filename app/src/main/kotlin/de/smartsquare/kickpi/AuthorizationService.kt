package de.smartsquare.kickpi

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import javax.inject.Inject

class AuthorizationService @Inject constructor(private val authorizationInterface: AuthorizationInterface) {

    fun authorize(name: String, deviceId: String) {
        val credentials = Credentials(deviceId, name)
        val authorizationResponse = authorizationInterface.authorize(credentials).execute()

        if (authorizationResponse.isSuccessful.not()) throw UnauthorizedException()
    }
}

interface AuthorizationInterface {

    @POST("authorization")
    fun authorize(@Body credentials: Credentials): Call<ResponseBody>

    @DELETE("authorization")
    fun unauthorize(@Body credentials: Credentials): Call<ResponseBody>
}

data class Credentials(val deviceId: String, val name: String)