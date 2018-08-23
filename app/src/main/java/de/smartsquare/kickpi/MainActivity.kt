package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.nearby.Nearby
import com.google.android.things.pio.Gpio.DIRECTION_IN
import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.gpio.GoalCallback
import de.smartsquare.kickpi.nearby.IdleMessage
import de.smartsquare.kickpi.nearby.StartGameMessageListener

class MainActivity : Activity() {

    val peripheralManager = PeripheralManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val messageClient = Nearby.getMessagesClient(this)
        val idle = IdleMessage(name = "Smartsquare Kicker", id = "1").toNearbyMessage()
        messageClient.publish(idle)

        messageClient.subscribe(
                StartGameMessageListener(messageStartedCallback = {
                    messageClient.unpublish(idle)
                })
        )

        val gpio = peripheralManager.openGpio("BCM23")
        gpio.setDirection(DIRECTION_IN)
        gpio.registerGpioCallback(GoalCallback("Left Goal", this))
    }
}
