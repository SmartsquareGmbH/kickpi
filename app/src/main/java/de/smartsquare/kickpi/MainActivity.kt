package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import com.google.android.things.pio.Gpio.DIRECTION_IN
import com.google.android.things.pio.PeripheralManager

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val gpioForLeftGoal = PeripheralManager.getInstance().openGpio("BCM23")
        gpioForLeftGoal.setDirection(DIRECTION_IN)
        gpioForLeftGoal.registerGpioCallback(GoalCallback("Left Goal"))

        val gpioForRightGoal = PeripheralManager.getInstance().openGpio("BCM24")
        gpioForRightGoal.setDirection(DIRECTION_IN)
        gpioForRightGoal.registerGpioCallback(GoalCallback("Right Goal"))
    }

}
