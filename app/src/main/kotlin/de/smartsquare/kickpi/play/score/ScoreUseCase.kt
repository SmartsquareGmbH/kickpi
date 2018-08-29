package de.smartsquare.kickpi.play.score

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.Gpio.DIRECTION_IN
import com.google.android.things.pio.Gpio.EDGE_FALLING
import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.play.start.GameStartedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.Optional
import javax.inject.Inject

class ScoreUseCase @Inject constructor(
    private val peripheralManager: PeripheralManager,
    private val eventBus: EventBus
) {

    private val gpios = emptyList<Optional<Gpio>>()

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true, priority = 3)
    fun registerGPIOCallbacksForBothGoalsOnGameStartedEvent(gameStartedEvent: GameStartedEvent) {
        mapOf(
            "BCM23" to GoalCallback(eventBus, goalFunction = { it.copy(scoreLeftTeam = it.scoreLeftTeam + 1) }),
            "BCM24" to GoalCallback(eventBus, goalFunction = { it.copy(scoreRightTeam = it.scoreRightTeam + 1) })
        ).forEach { gpioPinout, callback ->
            val gpio = peripheralManager.openGpio(gpioPinout)
            gpio.setDirection(DIRECTION_IN)
            gpio.setEdgeTriggerType(EDGE_FALLING)
            gpio.registerGpioCallback(callback)

            gpios + Optional.of(gpio)
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun closeGpiosOnGameFinishedEvent(gameFinishedEvent: GameFinishedEvent) {
        gpios.filter(Optional<Gpio>::isPresent)
            .map(Optional<Gpio>::get)
            .forEach(Gpio::close)
    }
}
