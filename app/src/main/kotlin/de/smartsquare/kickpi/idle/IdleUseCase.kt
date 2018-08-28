package de.smartsquare.kickpi.idle

import android.util.Log
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.leave.GameCanceledEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class IdleUseCase @Inject constructor(private val messagesClient: MessagesClient) {

    private val TAG = "Idle"

    fun publishIdleMessage() {
        val idleMessage = NearbyAdapter.toNearby(InIdleBroadcast(), "IDLE")
        messagesClient.publish(idleMessage)
        Log.i(TAG, "Broadcast idle state")
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun unpublishIdleMessageOnLobbyCreatedEvent(lobbyCreatedEvent: LobbyCreatedEvent) {
        val idleMessage = NearbyAdapter.toNearby(InIdleBroadcast(), "IDLE")
        messagesClient.unpublish(idleMessage)
        Log.i(TAG, "Unpublish idle broadcast")
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun publishIdleMessageOnGameCanceledEvent(gameCanceledEvent: GameCanceledEvent) {
        publishIdleMessage()
    }
}
