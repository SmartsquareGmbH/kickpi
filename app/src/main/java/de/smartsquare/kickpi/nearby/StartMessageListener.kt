package de.smartsquare.kickpi.nearby

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

class StartMessageListener(private val messageStartedCallback: () -> Unit) : MessageListener() {

    override fun onFound(message: Message?) {
        messageStartedCallback.invoke()
    }
}