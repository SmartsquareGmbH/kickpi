package de.smartsquare.kickpi.play.score

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.isStuck
import de.smartsquare.kickpi.removeStickyModifiedGameEvent
import org.greenrobot.eventbus.EventBus

class GoalCallback(
    private val eventBus: EventBus,
    private val goalFunction: (Lobby) -> Lobby
) : GpioCallback {

    private val TAG = "GPIO Callback"
    private var lastGoal: Long = System.currentTimeMillis() - 5000

    override fun onGpioEdge(gpio: Gpio?): Boolean {
        if(gpio!!.isStuck()) return true;

        if (System.currentTimeMillis() - lastGoal < 5000) {
            Log.i(TAG, "Goals must be at least 5 seconds apart but was ${System.currentTimeMillis() - lastGoal}ms")
            return true
        } else {
            lastGoal = System.currentTimeMillis()
        }

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