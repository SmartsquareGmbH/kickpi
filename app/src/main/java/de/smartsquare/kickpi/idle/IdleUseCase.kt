package de.smartsquare.kickpi.idle

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class IdleUseCase @Inject constructor(private val messagesClient: MessagesClient) {

    fun publishIdleMessage() {
        val idleMessage = NearbyAdapter.toNearby(InIdleBroadcast(), "IDLE")
        messagesClient.publish(idleMessage)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun unpublishIdleMessageOnLobbyCreatedEvent(lobbyCreatedEvent: LobbyCreatedEvent) {
        val idleMessage = NearbyAdapter.toNearby(InIdleBroadcast(), "IDLE")
        messagesClient.unpublish(idleMessage)
    }
}
