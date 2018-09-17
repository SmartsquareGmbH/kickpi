@file:Suppress("NOTHING_TO_INLINE")

package de.smartsquare.kickpi

import com.squareup.moshi.JsonClass
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.CreateGameMessage
import de.smartsquare.kickprotocol.message.JoinLobbyMessage
import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

class EndpointStore {
    private val endpoints = mutableMapOf<String, String>()

    fun register(endpointId: String, username: String) {
        endpoints[endpointId] = username
    }

    fun getIfAuthorized(endpointId: String) = endpoints[endpointId]
}


interface AuthorizationRepository {

    @POST("authorization")
    fun authorize(@Body credentials: Credentials): Single<ResponseBody>
}

@JsonClass(generateAdapter = true)
data class Credentials(val deviceId: String, val name: String)

inline fun Observable<MessageEvent.Message<JoinLobbyMessage>>.filterAuthenticatedJoinLobbyMessages(
    authorizationRepository: AuthorizationRepository
): Observable<MessageEvent.Message<JoinLobbyMessage>> = this
    .flatMapSingle { event ->
        authorizationRepository
            .authorize(Credentials(event.endpointId, event.message.username))
            .map { true to event }
            .onErrorReturn { false to event }
    }
    .filter { it.first }
    .map { it.second }

inline fun Observable<MessageEvent.Message<CreateGameMessage>>.filterAuthenticatedCreateGameMessages(
    authorizationRepository: AuthorizationRepository
): Observable<MessageEvent.Message<CreateGameMessage>> = this
    .flatMapSingle { event ->
        authorizationRepository
            .authorize(Credentials(event.endpointId, event.message.username))
            .map { true to event }
            .onErrorReturn { false to event }
    }
    .filter { it.first }
    .map { it.second }
