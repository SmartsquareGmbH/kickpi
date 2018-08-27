package de.smartsquare.kickpi.join

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.create.AuthorizationService
import de.smartsquare.kickpi.create.Credentials
import javax.inject.Inject

class JoinLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService
) : MessageListener() {
    override fun onFound(message: Message) {
        if (message.type != "JOIN_LOBBY") return

        val joinLobbyMessage = NearbyAdapter.fromNearby(message, JoinLobbyMessage::class.java)
        val credentials = Credentials(joinLobbyMessage.playerDeviceId, joinLobbyMessage.playerName)
        val authorized = authorizationService.authorize(credentials).execute().isSuccessful
        if (authorized.not()) return
    }
}