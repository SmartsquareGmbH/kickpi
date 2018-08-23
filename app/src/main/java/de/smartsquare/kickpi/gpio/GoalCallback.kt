package de.smartsquare.kickpi.gpio

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import okhttp3.HttpUrl

class GoalCallback(private val score: HttpUrl) : GpioCallback {

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        return true
    }
}