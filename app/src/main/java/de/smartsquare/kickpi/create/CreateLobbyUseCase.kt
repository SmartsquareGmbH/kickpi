package de.smartsquare.kickpi.create

import de.smartsquare.kickpi.NearbyAdapter.Companion.fromNearby
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.Message
import de.smartsquare.kickpi.Lobby
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class CreateLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus
) : MessageListener() {

    override fun onFound(message: Message) {
        if (message.type != "CREATE_LOBBY") return

        val createLobbyMessage = fromNearby(message, CreateLobbyMessage::class.java)
        val credentials = Credentials(createLobbyMessage.ownerDeviceId, createLobbyMessage.ownerName)
        val authorized = authorizationService.authorize(credentials).execute().isSuccessful
        if (authorized.not()) return

        val lobby = Lobby(createLobbyMessage.ownerName)
        eventBus.postSticky(LobbyCreatedEvent(lobby))
    }
}
