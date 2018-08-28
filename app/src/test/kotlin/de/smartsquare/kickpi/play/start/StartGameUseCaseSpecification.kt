package de.smartsquare.kickpi.play.start

import de.smartsquare.kickpi.AuthorizationService
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.MissingOpponentsException
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.join.NewPlayerJoinedEvent
import io.mockk.mockk
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.amshove.kluent.shouldThrow
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class StartGameUseCaseSpecification {

    private val eventBus = EventBus.getDefault()
    private val authorizationService = mockk<AuthorizationService>(relaxed = true)
    private val startGameUseCase = StartGameUseCase(authorizationService, eventBus)

    @BeforeEach fun cleanupEventBus() {
        eventBus.removeAllStickyEvents()
    }

    @Test fun `start game with a duoq lobby`() {
        val lobby = Lobby("deen", listOf("saschar"), listOf("drs", "ruby"))
        eventBus.postSticky(NewPlayerJoinedEvent(lobby))

        val startGameMessage = StartGameMessage("deen", "1337")
        val nearbyStartGameMessage = NearbyAdapter.toNearby(startGameMessage, "START_GAME")
        startGameUseCase.onFound(nearbyStartGameMessage)

        val gameStartedEvent = eventBus.removeStickyEvent(GameStartedEvent::class.java)
        gameStartedEvent shouldNotEqual null
        gameStartedEvent.lobby shouldEqual lobby
    }

    @Test fun `start game without opponents`() {
        val lobby = Lobby("deen", listOf("saschar"), emptyList())
        eventBus.postSticky(NewPlayerJoinedEvent(lobby))

        val startGameMessage = StartGameMessage("deen", "1337")
        val nearbyStartGameMessage = NearbyAdapter.toNearby(startGameMessage, "START_GAME")
        val startGameFunction = { startGameUseCase.onFound(nearbyStartGameMessage) }

        startGameFunction shouldThrow MissingOpponentsException::class
    }

    @Test fun `start game without owner role`() {
        val lobby = Lobby("deen", listOf("saschar"), listOf("drs", "ruby"))
        eventBus.postSticky(NewPlayerJoinedEvent(lobby))

        val startGameMessage = StartGameMessage("saschar", "1337")
        val nearbyStartGameMessage = NearbyAdapter.toNearby(startGameMessage, "START_GAME")
        val startGameFunction = { startGameUseCase.onFound(nearbyStartGameMessage) }

        startGameFunction shouldThrow UnauthorizedException::class
    }
}
