package de.smartsquare.kickpi.join

import de.smartsquare.kickpi.DuplicateNameException
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.leave.PlayerLeavedEvent
import io.mockk.every
import io.mockk.mockk
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldNotEqual
import org.amshove.kluent.shouldThrow
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JoinLobbyUseCaseSpecification {

    private val eventBus = EventBus.getDefault()
    private val authorizationService = mockk<AuthorizationService>()
    private val joinLobbyUseCase = JoinLobbyUseCase(authorizationService, eventBus)

    @BeforeEach
    fun cleanupEventBus() {
        eventBus.removeAllStickyEvents()
    }

    @Test
    fun `publish NewPlayerJoinedEvent after joining a created lobby`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("saschar", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        joinLobbyUseCase.onFound(nearbyJoinLobbyMessage)

        val newPlayerJoinedEvent = eventBus.removeStickyEvent(NewPlayerJoinedEvent::class.java)
        newPlayerJoinedEvent shouldNotEqual null
        newPlayerJoinedEvent.lobby.leftTeam shouldContain "saschar"
    }

    @Test
    fun `publish NewPlayerJoinedEvent after joining a existent lobby`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("saschar", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        joinLobbyUseCase.onFound(nearbyJoinLobbyMessage)

        val newPlayerJoinedEvent = eventBus.removeStickyEvent(NewPlayerJoinedEvent::class.java)
        newPlayerJoinedEvent shouldNotEqual null
        newPlayerJoinedEvent.lobby.leftTeam shouldContain "saschar"
    }

    @Test
    fun `unpublish created lobby after joining`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(LobbyCreatedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("saschar", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        joinLobbyUseCase.onFound(nearbyJoinLobbyMessage)

        eventBus.getStickyEvent(LobbyCreatedEvent::class.java) shouldEqual null
    }

    @Test
    fun `join lobby with a duplicated name should throw a exception`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("deen", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        val joinLobbyFunction = { joinLobbyUseCase.onFound(nearbyJoinLobbyMessage) }

        joinLobbyFunction shouldThrow DuplicateNameException::class
    }

    @Test
    fun `join lobby with a empty name should throw a exception`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        val joinLobbyFunction = { joinLobbyUseCase.onFound(nearbyJoinLobbyMessage) }

        joinLobbyFunction shouldThrow IllegalArgumentException::class
    }

    @Test
    fun `join lobby with a empty device id should throw a exception`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("saschar", "", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        val joinLobbyFunction = { joinLobbyUseCase.onFound(nearbyJoinLobbyMessage) }

        joinLobbyFunction shouldThrow IllegalArgumentException::class
    }

    @Test
    fun `unauthorized join attempt should throw a exception`() {
        every { authorizationService.isAuthorized(any()) } returns false
        eventBus.postSticky(NewPlayerJoinedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("deen", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        val joinLobbyFunction = { joinLobbyUseCase.onFound(nearbyJoinLobbyMessage) }

        joinLobbyFunction shouldThrow UnauthorizedException::class
    }

    @Test
    fun `join lobby after someone leaved`() {
        every { authorizationService.isAuthorized(any()) } returns true
        eventBus.postSticky(PlayerLeavedEvent(Lobby("deen")))

        val joinLobbyMessage = JoinLobbyMessage("saschar", "1337", JoinLobbyMessage.Team.LEFT)
        val nearbyJoinLobbyMessage = NearbyAdapter.toNearby(joinLobbyMessage, "JOIN_LOBBY")
        joinLobbyUseCase.onFound(nearbyJoinLobbyMessage)

        val newPlayerJoinedEvent = eventBus.removeStickyEvent(NewPlayerJoinedEvent::class.java)
        newPlayerJoinedEvent shouldNotEqual null
        newPlayerJoinedEvent.lobby.leftTeam shouldContain "saschar"
    }
}