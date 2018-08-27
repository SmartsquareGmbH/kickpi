package de.smartsquare.kickpi.idle

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.NearbyAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class IdleUseCase @Inject constructor(private val messagesClient: MessagesClient) {

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onStartIdleEvent(startIdleEvent: StartIdleEvent) {
        val idleMessage = NearbyAdapter.toNearby(InIdleBroadcast(), "IDLE")
        messagesClient.publish(idleMessage)
    }
}
