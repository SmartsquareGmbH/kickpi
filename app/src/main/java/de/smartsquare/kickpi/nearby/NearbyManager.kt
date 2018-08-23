package de.smartsquare.kickpi.nearby

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.StartGameEvent
import de.smartsquare.kickpi.StartIdleEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class NearbyManager @Inject constructor() {

    @Inject
    lateinit var nearby: MessagesClient

    @Inject
    lateinit var idGenerator: UniqueAndroidIDGenerator

    @Inject
    lateinit var startGameMessageListener: StartGameMessageListener

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onStartIdleEvent(startIdleEvent: StartIdleEvent) {
        nearby.subscribe(startGameMessageListener)

        val raspberryName = "Smartsquare HQ" //TODO: configurable name
        val raspberryId = idGenerator.generate()
        val idleMessage = IdleMessage(raspberryName, raspberryId)
        nearby.publish(idleMessage.toNearbyMessage())
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onStartGameEvent(startGameEvent: StartGameEvent) {
        nearby.unsubscribe(startGameMessageListener)

        //TODO: unpublish idle message
    }
}
