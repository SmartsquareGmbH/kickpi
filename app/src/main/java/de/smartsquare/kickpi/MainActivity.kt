package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.MessageFilter
import com.google.android.gms.nearby.messages.SubscribeOptions
import com.google.android.things.pio.Gpio.DIRECTION_IN
import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.gpio.GoalCallback
import de.smartsquare.kickpi.nearby.IdleMessage
import de.smartsquare.kickpi.nearby.MessageType.START
import de.smartsquare.kickpi.nearby.StartMessageListener


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val messageClient = Nearby.getMessagesClient(this)
        val idle = IdleMessage(name = "Smartsquare Kicker", id = "1").asNearbyMessage()
        messageClient.publish(idle)

        MessageFilter.Builder().includeNamespacedType("de.smartsquare.kickchain", START.name).build()
                .let { SubscribeOptions.Builder().setFilter(it).build() }
                .let {
                    messageClient.subscribe(
                            StartMessageListener(messageStartedCallback = {
                                messageClient.unpublish(idle)
                            }), it
                    )
                }

        val peripheralManager = PeripheralManager.getInstance()
        val gpio = peripheralManager.openGpio("BCM23")
        gpio.setDirection(DIRECTION_IN)
        gpio.registerGpioCallback(GoalCallback("Left Goal", this))
    }

}
