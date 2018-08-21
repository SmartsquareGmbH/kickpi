package de.smartsquare.kickpi

import android.widget.TextView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback

class GoalCallback : GpioCallback {
    override fun onGpioEdge(p0: Gpio?): Boolean {
        print(p0)
        return true;
    }

    override fun onGpioError(gpio: Gpio?, error: Int) {
        print(error)
    }
}