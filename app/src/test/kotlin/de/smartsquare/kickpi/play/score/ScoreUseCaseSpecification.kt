package de.smartsquare.kickpi.play.score

import com.google.android.things.pio.PeripheralManager
import io.mockk.mockk
import io.mockk.verify
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.Test

class ScoreUseCaseSpecification {

    private val peripheralManager = mockk<PeripheralManager>(relaxed = true)
    private val eventBus = mockk<EventBus>()

    private val scoreUseCase = ScoreUseCase(peripheralManager, eventBus)

    @Test fun `open gpio BCM23 and BCM24 on game started event`() {
        scoreUseCase.registerGPIOCallbacksForBothGoalsOnGameStartedEvent(mockk())

        verify { peripheralManager.openGpio("BCM23") }
        verify { peripheralManager.openGpio("BCM24") }
    }
}
