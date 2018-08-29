package de.smartsquare.kickpi.play.score

import de.smartsquare.kickpi.Lobby
import io.mockk.mockk
import org.amshove.kluent.shouldEqualTo
import org.amshove.kluent.shouldNotEqual
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GoalCallbackSpecification {

    private val lobby = Lobby(
        owner = "deen",
        leftTeam = listOf("deen", "saschar"),
        scoreLeftTeam = 0,
        rightTeam = listOf("ruby", "drs"),
        scoreRightTeam = 0
    )

    private val eventBus = EventBus.getDefault()

    @BeforeEach fun cleanupEventBus() {
        eventBus.removeAllStickyEvents()
    }

    @Test fun `apply given function on GoalScoredEvent`() {
        eventBus.postSticky(GoalScoredEvent(lobby))

        GoalCallback(eventBus) { it.copy(scoreRightTeam = it.scoreRightTeam + 1) }
            .onGpioEdge(mockk(relaxed = true))

        val goalScoredEvent = eventBus.getStickyEvent(GoalScoredEvent::class.java)
        goalScoredEvent shouldNotEqual null
        goalScoredEvent.lobby.scoreRightTeam shouldEqualTo 1
    }
}
