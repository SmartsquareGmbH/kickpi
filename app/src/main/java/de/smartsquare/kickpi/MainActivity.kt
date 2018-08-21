package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager

class MainActivity : Activity() {

    private val gpio = PeripheralManager.getInstance().openGpio("BCM23")
    private val callback = GoalCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        gpio.setDirection(Gpio.DIRECTION_IN)
        gpio.registerGpioCallback(callback)
    }

    override fun onDestroy() {
        super.onDestroy()

        gpio.unregisterGpioCallback(callback)
    }

}
