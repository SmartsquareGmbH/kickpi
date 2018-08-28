package de.smartsquare.kickpi.leave

import de.smartsquare.kickpi.AuthorizationService
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.join.NewPlayerJoinedEvent
import de.smartsquare.kickpi.removeStickyModifiedLobbyEvent
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.amshove.kluent.shouldThrow
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LeaveLobbyUseCaseSpecification {

    private val eventBus = EventBus.getDefault()
    private val authorizationService = mockk<AuthorizationService>(relaxed = true)
    private val leaveLobbyUseCase = LeaveLobbyUseCase(authorizationService, eventBus)

    @BeforeEach fun cleanupEventBus() {
        eventBus.removeAllStickyEvents()
    }

    @Test fun `leave lobby`() {
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen", listOf("saschar"), emptyList())))

        val leaveLobbyMessage = LeaveLobbyMessage("saschar", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage)

        val playerLeavedEvent = eventBus.removeStickyEvent(PlayerLeavedEvent::class.java)
        playerLeavedEvent shouldNotEqual null
        playerLeavedEvent.lobby.leftTeam shouldEqual listOf("deen")
    }

    @Test fun `leave lobby without joining lobby before`() {
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val leaveLobbyMessage = LeaveLobbyMessage("saschar", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage)

        val playerLeavedEvent = eventBus.removeStickyEvent(LobbyCreatedEvent::class.java)
        playerLeavedEvent shouldNotEqual null
        playerLeavedEvent.lobby.leftTeam shouldEqual listOf("deen")
    }

    @Test fun `leave lobby as owner`() {
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen", listOf("saschar"), emptyList())))

        val leaveLobbyMessage = LeaveLobbyMessage("deen", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage)

        val playerLeavedEvent = eventBus.removeStickyEvent(PlayerLeavedEvent::class.java)
        playerLeavedEvent shouldNotEqual null
        playerLeavedEvent.lobby.owner shouldEqual "saschar"
    }

    @Test fun `leave lobby as owner and last man standing in left team`() {
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("saschar", listOf("saschar"), 0, listOf("deen"), 0)))

        val leaveLobbyMessage = LeaveLobbyMessage("saschar", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage)

        val playerLeavedEvent = eventBus.removeStickyEvent(PlayerLeavedEvent::class.java)
        playerLeavedEvent shouldNotEqual null
        playerLeavedEvent.lobby.owner shouldEqual "deen"
    }

    @Test fun `unauthorized attempt to leave lobby`() {
        every { authorizationService.authorize(any(), any()) } throws UnauthorizedException()
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val leaveLobbyMessage = LeaveLobbyMessage("deen", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        val leaveLobbyFunction = { leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage) }

        leaveLobbyFunction shouldThrow UnauthorizedException::class
    }

    @Test fun `leave lobby with empty name`() {
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val leaveLobbyMessage = LeaveLobbyMessage("", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        val leaveLobbyFunction = { leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage) }

        leaveLobbyFunction shouldThrow IllegalArgumentException::class
    }

    @Test fun `leave lobby with empty deviceId`() {
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val leaveLobbyMessage = LeaveLobbyMessage("deen", "")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        val leaveLobbyFunction = { leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage) }

        leaveLobbyFunction shouldThrow IllegalArgumentException::class
    }

    @Test fun `leave lobby as last standing player`() {
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val leaveLobbyMessage = LeaveLobbyMessage("deen", "1337")
        val nearbyLeaveLobbyMessage = NearbyAdapter.toNearby(leaveLobbyMessage, "LEAVE_LOBBY")
        leaveLobbyUseCase.onFound(nearbyLeaveLobbyMessage)

        eventBus.removeStickyModifiedLobbyEvent() shouldEqual null
        eventBus.removeStickyEvent(GameCanceledEvent::class.java) shouldNotEqual null
    }
}
