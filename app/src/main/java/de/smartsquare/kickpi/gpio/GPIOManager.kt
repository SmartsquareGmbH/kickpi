package de.smartsquare.kickpi.gpio

import android.util.Log
import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.StartGameEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class GPIOManager @Inject constructor(private val peripheralManager: PeripheralManager) {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGameStartEvent(startGameEvent: StartGameEvent) {
        val leftGoal = peripheralManager.openGpio("BCM23")
        Log.i("GPIO Manager", "Register callback for GPIO BCM23")
        GoalCallback(startGameEvent.startGameMessage.scoreLeft).also(leftGoal::registerGpioCallback)

        val rightGoal = peripheralManager.openGpio("BCM24")
        Log.i("GPIO Manager", "Register callback for GPIO BCM24")
        GoalCallback(startGameEvent.startGameMessage.scoreLeft).also(rightGoal::registerGpioCallback)
    }
}
