package de.smartsquare.kickpi.nearby

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

class StartGameMessageListener(private val messageStartedCallback: () -> Unit) : MessageListener() {

    override fun onFound(message: Message?) {
        messageStartedCallback.invoke()
    }
}