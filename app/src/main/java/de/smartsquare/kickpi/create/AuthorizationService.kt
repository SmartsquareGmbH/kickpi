package de.smartsquare.kickpi.create

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST

interface AuthorizationService {

    @POST("authorization")
    fun authorize(@Body credentials: Credentials): Call<ResponseBody>

    @DELETE("authorization")
    fun unauthorize(@Body credentials: Credentials): Call<ResponseBody>
}

data class Credentials(val deviceId: String, val name: String)
