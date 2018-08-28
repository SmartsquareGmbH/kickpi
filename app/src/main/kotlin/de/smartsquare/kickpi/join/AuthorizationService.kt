package de.smartsquare.kickpi.join

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import javax.inject.Inject

class AuthorizationService @Inject constructor(private val authorizationInterface: AuthorizationInterface) {

    fun isAuthorized(credentials: Credentials) = authorizationInterface.authorize(credentials).execute().isSuccessful
}

interface AuthorizationInterface {

    @POST("authorization")
    fun authorize(@Body credentials: Credentials): Call<ResponseBody>

    @DELETE("authorization")
    fun unauthorize(@Body credentials: Credentials): Call<ResponseBody>
}

data class Credentials(val deviceId: String, val name: String)