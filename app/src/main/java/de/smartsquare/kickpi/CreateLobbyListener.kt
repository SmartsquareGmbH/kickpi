package de.smartsquare.kickpi

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

/**
 * This listener should respond with the unique id
 * of the host device.
 */
class CreateLobbyListener : MessageListener() {

    /**
     * @param message is a general message from the mobile
     * device
     */
    override fun onFound(message: Message?) {
    }
}
