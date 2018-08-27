package de.smartsquare.kickpi.create

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.NearbyAdapter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.amshove.kluent.shouldBeEqualTo
import org.greenrobot.eventbus.EventBus
import org.junit.Test


class CreateLobbyUseCaseSpecification {

    private val eventBus = EventBus.getDefault()
    private val authorizationService = mockk<AuthorizationService>()
    private val messagesClient = mockk<MessagesClient>(relaxed = true)
    private val createLobbyUseCase = CreateLobbyUseCase(authorizationService, eventBus, messagesClient)

    @Test
    fun `publish sticky LobbyCreatedEvent if authorization was successful`() {
        every { authorizationService.isAuthorized(allAny()) } returns true

        val createLobbyMessage = CreateLobbyMessage("deen", "1337")
        createLobbyUseCase.onFound(NearbyAdapter.toNearby(createLobbyMessage, "CREATE_LOBBY"))

        val lobbyCreatedEvent = eventBus.removeStickyEvent(LobbyCreatedEvent::class.java)
        lobbyCreatedEvent.lobby.owner shouldBeEqualTo "deen"
    }

    @Test
    fun `broadcast in lobby creation state`() {
        every { authorizationService.isAuthorized(allAny()) } returns true

        val createLobbyMessage = CreateLobbyMessage("deen", "1337")
        createLobbyUseCase.onFound(NearbyAdapter.toNearby(createLobbyMessage, "CREATE_LOBBY"))

        verify { messagesClient.publish(any()) }
    }
}
