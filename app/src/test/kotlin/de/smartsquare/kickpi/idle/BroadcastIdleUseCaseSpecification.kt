package de.smartsquare.kickpi.idle

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class BroadcastIdleUseCaseSpecification {

    val messagesClient = mockk<MessagesClient>(relaxed = true)
    val idleUseCase = BroadcastIdleUseCase(messagesClient)

    @Test fun `unpublish idle message on lobby created event`() {
        idleUseCase.unpublishIdleMessageOnLobbyCreatedEvent(mockk<LobbyCreatedEvent>())

        verify { messagesClient.unpublish(any()) }
    }
}
