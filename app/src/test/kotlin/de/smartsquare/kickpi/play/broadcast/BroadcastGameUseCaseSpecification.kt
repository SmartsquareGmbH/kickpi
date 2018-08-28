package de.smartsquare.kickpi.play.broadcast

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.play.score.GoalScoredEvent
import de.smartsquare.kickpi.play.start.GameStartedEvent
import io.mockk.mockk
import io.mockk.verify
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BroadcastGameUseCaseSpecification {

    val eventBus = EventBus.getDefault()
    val messagesClient = mockk<MessagesClient>(relaxed = true)
    val broadcastGameUseCase = BroadcastGameUseCase(messagesClient)

    @BeforeEach fun setup() {
        eventBus.register(broadcastGameUseCase)
        eventBus.removeAllStickyEvents()
    }

    @Test fun `broadcast game on scored goal`() {
        eventBus.postSticky(GoalScoredEvent(Lobby("deen")))

        verify { messagesClient.publish(any()) }
    }

    @Test fun `broadcast game on creation`() {
        eventBus.postSticky(GameStartedEvent(Lobby("deen")))

        verify { messagesClient.publish(any()) }
    }
}
