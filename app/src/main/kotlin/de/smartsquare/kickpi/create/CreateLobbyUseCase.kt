package de.smartsquare.kickpi.create

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.NearbyAdapter.Companion.fromNearby
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class CreateLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus,
    private val messagesClient: MessagesClient
) : MessageListener() {

    private val TAG = "Create Lobby"

    override fun onFound(message: Message) {
        val createLobbyMessage = fromNearby(message, CreateLobbyMessage::class.java)
        val credentials = Credentials(createLobbyMessage.ownerDeviceId, createLobbyMessage.ownerName)
        if (authorizationService.isAuthorized(credentials).not()) return

        val lobby = Lobby(createLobbyMessage.ownerName)
        eventBus.postSticky(LobbyCreatedEvent(lobby))
        Log.i(TAG, "Post LobbyCreatedEvent for lobby: $lobby")

        val inLobbyCreationBroadcast = InLobbyCreationBroadcast(lobby)
        messagesClient.publish(NearbyAdapter.toNearby(inLobbyCreationBroadcast, "LOBBY_CREATION"))
        Log.i(TAG, "Broadcast in lobby creation state")
    }
}
