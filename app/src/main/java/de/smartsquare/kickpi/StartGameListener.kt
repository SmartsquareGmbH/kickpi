package de.smartsquare.kickpi

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

/**
 * This listener should store the current scoring link
 * for the GoalCallback
 */
class StartGameListener : MessageListener() {

    /**
     * @param message is a general message from the mobile
     * device
     */
    override fun onFound(message: Message?) {
    }
}
