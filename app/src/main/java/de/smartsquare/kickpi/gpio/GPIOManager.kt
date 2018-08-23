package de.smartsquare.kickpi.gpio

import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.StartGameEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class GPIOManager @Inject constructor() {

    @Inject
    lateinit var peripheralManager: PeripheralManager

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onGameStartEvent(startGameEvent: StartGameEvent) {
        val leftGoal = peripheralManager.openGpio("BCM23")
        GoalCallback(startGameEvent.startGameMessage.scoreLeft).also(leftGoal::registerGpioCallback)

        val rightGoal = peripheralManager.openGpio("BCM23")
        GoalCallback(startGameEvent.startGameMessage.scoreLeft).also(rightGoal::registerGpioCallback)
    }
}
