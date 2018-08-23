package de.smartsquare.kickpi.nearby

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.StartGameEvent
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartGameMessageListener @Inject constructor() : MessageListener() {

    override fun onFound(message: Message?) {
        Log.i("StartGameMessageListener", "Found StartGameMessage")

        message?.let {
            StartGameMessage.fromNearbyMessage(it)
        }?.let {
            EventBus.getDefault().post(StartGameEvent(it))
        }
    }
}