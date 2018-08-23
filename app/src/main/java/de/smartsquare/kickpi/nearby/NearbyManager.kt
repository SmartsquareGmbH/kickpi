package de.smartsquare.kickpi.nearby

import android.util.Log
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.StartGameEvent
import de.smartsquare.kickpi.StartIdleEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class NearbyManager @Inject constructor(private val nearby: MessagesClient, private val idGenerator: UniqueAndroidIDGenerator,
                                        private val startGameMessageListener: StartGameMessageListener) {

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onStartIdleEvent(startIdleEvent: StartIdleEvent) {
        Log.i("Nearby Manager", "Register listener for StartGameMessage")
        nearby.subscribe(startGameMessageListener)

        val raspberryName = "Smartsquare HQ" //TODO: configurable name
        val raspberryId = idGenerator.generate()
        val idleMessage = IdleMessage(raspberryName, raspberryId)
        Log.i("Nearby Manager", "Publish IdleMessage")
        nearby.publish(idleMessage.toNearbyMessage())
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onStartGameEvent(startGameEvent: StartGameEvent) {
        nearby.unsubscribe(startGameMessageListener)

        //TODO: unpublish idle message
    }
}
