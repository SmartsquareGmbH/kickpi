package de.smartsquare.kickpi.gpio

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import de.smartsquare.kickpi.MainActivity

class GoalCallback(private val name: String) : GpioCallback {

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        Log.i(name, gpio?.value.toString())
        return true
    }

    override fun onGpioError(gpio: Gpio?, error: Int) {
        Log.i("", error.toString())
    }
}