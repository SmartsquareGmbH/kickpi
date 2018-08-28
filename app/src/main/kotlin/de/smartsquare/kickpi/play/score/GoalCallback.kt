package de.smartsquare.kickpi.play.score

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback

class GoalCallback : GpioCallback {

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        Log.i("GPIO Callback", "${gpio?.name} changed the state to ${gpio?.value}")
        return true
    }
}