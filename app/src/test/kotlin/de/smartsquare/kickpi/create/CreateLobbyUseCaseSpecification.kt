package de.smartsquare.kickpi.create

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.AuthorizationService
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.MatchInProgressException
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.play.score.GoalScoredEvent
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldThrow
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test

class CreateLobbyUseCaseSpecification {

    private val eventBus = EventBus.getDefault()
    private val messagesClient = mockk<MessagesClient>(relaxed = true)
    private val authorizationService = mockk<AuthorizationService>(relaxed = true)
    private val createLobbyUseCase = CreateLobbyUseCase(authorizationService, eventBus, messagesClient)

    @AfterEach fun cleanupEventBus() {
        eventBus.removeAllStickyEvents()
    }

    @Test fun `publish sticky LobbyCreatedEvent if authorization was successful`() {
        val createLobbyMessage = CreateLobbyMessage("deen", "1337")
        createLobbyUseCase.onFound(NearbyAdapter.toNearby(createLobbyMessage, "CREATE_LOBBY"))

        val lobbyCreatedEvent = eventBus.removeStickyEvent(LobbyCreatedEvent::class.java)
        lobbyCreatedEvent.lobby.owner shouldBeEqualTo "deen"
    }

    @Test fun `broadcast in lobby creation state`() {
        val createLobbyMessage = CreateLobbyMessage("deen", "1337")
        createLobbyUseCase.onFound(NearbyAdapter.toNearby(createLobbyMessage, "CREATE_LOBBY"))

        verify { messagesClient.publish(any()) }
    }

    @Test fun `create lobby while a game is in process`() {
        eventBus.postSticky(GoalScoredEvent(Lobby("deen")))

        val createLobbyMessage = CreateLobbyMessage("deen", "1337")
        val nearbyCreateLobbyMessage = NearbyAdapter.toNearby(createLobbyMessage, "CREATE_LOBBY")
        val lobbyCreation = { createLobbyUseCase.onFound(nearbyCreateLobbyMessage) }

        lobbyCreation shouldThrow MatchInProgressException::class
    }
}
