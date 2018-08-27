package de.smartsquare.kickpi.join

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.create.AuthorizationInterface
import de.smartsquare.kickpi.create.Credentials
import javax.inject.Inject

class JoinLobbyUseCase @Inject constructor(
    private val authorizationInterface: AuthorizationInterface
) : MessageListener() {
    override fun onFound(message: Message) {
        if (message.type != "JOIN_LOBBY") return

        val joinLobbyMessage = NearbyAdapter.fromNearby(message, JoinLobbyMessage::class.java)
        val credentials = Credentials(joinLobbyMessage.playerDeviceId, joinLobbyMessage.playerName)
        val authorized = authorizationInterface.authorize(credentials).execute().isSuccessful
        if (authorized.not()) return
    }
}