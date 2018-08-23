package de.smartsquare.kickpi.nearby

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.ioc.Kickchain

class StartGameMessageListener(private val kickchain: Kickchain, private val messageStartedCallback: () -> Unit) : MessageListener() {

    override fun onFound(message: Message?) {
        message?.let {
            StartGameMessage.fromNearbyMessage(it)
        }?.let {
            kickchain.startGame(it.scoreLeft,  it.scoreRight, it.spectate)
        }.also {
            messageStartedCallback.invoke()
        }
    }
}