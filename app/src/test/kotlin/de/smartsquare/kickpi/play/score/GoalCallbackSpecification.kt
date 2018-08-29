package de.smartsquare.kickpi.play.score

import de.smartsquare.kickpi.Lobby
import io.mockk.mockk
import org.amshove.kluent.shouldEqual
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

    @Test fun `publish GoalScoredEvent if the is still in progress`() {
        eventBus.postSticky(GoalScoredEvent(lobby))

        GoalCallback(eventBus) { it.copy(scoreRightTeam = it.scoreRightTeam + 1) }
            .onGpioEdge(mockk(relaxed = true))

        eventBus.getStickyEvent(GoalScoredEvent::class.java) shouldNotEqual null
    }

    @Test fun `publish GameFinishedEvent instead of GoalScoredEvent if one team has won`() {
        eventBus.postSticky(GoalScoredEvent(lobby.copy(scoreRightTeam = 10)))

        GoalCallback(eventBus) { it }
            .onGpioEdge(mockk(relaxed = true))

        eventBus.getStickyEvent(GoalScoredEvent::class.java) shouldEqual null
        eventBus.getStickyEvent(GameFinishedEvent::class.java) shouldNotEqual null
    }
}
