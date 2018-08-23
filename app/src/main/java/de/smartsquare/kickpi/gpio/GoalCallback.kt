package de.smartsquare.kickpi.gpio

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import de.smartsquare.kickpi.GoalEvent
import okhttp3.HttpUrl
import org.greenrobot.eventbus.EventBus

class GoalCallback(private val score: HttpUrl) : GpioCallback {

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        EventBus.getDefault().post(GoalEvent(score))
        return true
    }
}
