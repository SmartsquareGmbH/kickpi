package de.smartsquare.kickpi.nearby

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.ioc.Context

class StartGameMessageListener(private val context: Context, private val messageStartedCallback: () -> Unit) : MessageListener() {

    override fun onFound(message: Message?) {
        message?.let {
            StartGameMessage.fromNearbyMessage(it)
        }?.let {
            context.scoreLeft = it.scoreLeft
            context.scoreRight = it.scoreRight
            context.spectate = it.spectate
        }.also {
            messageStartedCallback.invoke()
        }
    }
}