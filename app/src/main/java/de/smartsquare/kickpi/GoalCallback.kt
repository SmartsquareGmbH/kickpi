package de.smartsquare.kickpi

import android.util.Log
import android.widget.TextView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback

class GoalCallback : GpioCallback {
    override fun onGpioEdge(p0: Gpio?): Boolean {
        Log.i("", p0?.value.toString())
        return true;
    }

    override fun onGpioError(gpio: Gpio?, error: Int) {
        Log.i("", error.toString())
    }
}