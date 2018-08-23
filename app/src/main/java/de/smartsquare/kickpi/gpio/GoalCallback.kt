package de.smartsquare.kickpi.gpio

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import de.smartsquare.kickpi.GoalEvent
import okhttp3.HttpUrl
import org.greenrobot.eventbus.EventBus

class GoalCallback(private val score: HttpUrl) : GpioCallback {

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        Log.i("GPIO Callback", "${gpio?.name} changed the state to ${gpio?.value}")
        EventBus.getDefault().post(GoalEvent(score))
        return true
    }
}