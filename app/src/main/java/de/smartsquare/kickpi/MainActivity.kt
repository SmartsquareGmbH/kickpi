package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import com.google.android.gms.nearby.Nearby
import com.google.android.things.pio.Gpio.DIRECTION_IN
import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.gpio.GoalCallback
import de.smartsquare.kickpi.ioc.Kickchain
import de.smartsquare.kickpi.ioc.DaggerContainer
import de.smartsquare.kickpi.nearby.IdleMessage
import de.smartsquare.kickpi.nearby.StartGameMessageListener
import javax.inject.Inject

class MainActivity : Activity() {

    @Inject
    lateinit var kickchain: Kickchain

    @Inject
    lateinit var peripheralManager: PeripheralManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DaggerContainer.create().inject(this)

        val messageClient = Nearby.getMessagesClient(this)
        val idle = IdleMessage(name = "Smartsquare Kicker", id = "1").toNearbyMessage()
        messageClient.publish(idle)

        messageClient.subscribe(
                StartGameMessageListener(messageStartedCallback = {
                    messageClient.unpublish(idle)
                }, kickchain = kickchain)
        )

        val gpio = peripheralManager.openGpio("BCM23")
        gpio.setDirection(DIRECTION_IN)
        gpio.registerGpioCallback(GoalCallback("Left Goal"))
    }
}
