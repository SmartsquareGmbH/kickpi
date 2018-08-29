package de.smartsquare.kickpi.play.score

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.removeStickyModifiedGameEvent
import org.greenrobot.eventbus.EventBus

class GoalCallback(
    private val eventBus: EventBus,
    private val goalFunction: (Lobby) -> Lobby
) : GpioCallback {

    private val TAG = "GPIO Callback"

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        Log.i(TAG, "${gpio?.name} changed the state to ${gpio?.value}")

        eventBus.removeStickyModifiedGameEvent()
            ?.let(goalFunction)
            ?.let {
                when {
                    it.scoreLeftTeam == 10 || it.scoreRightTeam == 10 -> GameFinishedEvent(it)
                    else -> GoalScoredEvent(it)
                }
            }
            .also(eventBus::postSticky)
            .also { Log.i(TAG, "$it") }

        return true
    }
}