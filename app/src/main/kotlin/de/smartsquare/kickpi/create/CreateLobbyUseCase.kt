package de.smartsquare.kickpi.create

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.AuthorizationService
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.MatchInProgressException
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.NearbyAdapter.Companion.fromNearby
import de.smartsquare.kickpi.join.NewPlayerJoinedEvent
import de.smartsquare.kickpi.leave.PlayerLeavedEvent
import de.smartsquare.kickpi.play.score.GoalScoredEvent
import de.smartsquare.kickpi.play.start.GameStartedEvent
import de.smartsquare.kickpi.throwIllegalArgumentExceptionIfBlank
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class CreateLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus,
    private val messagesClient: MessagesClient
) : MessageListener() {

    private val TAG = "Create Lobby"

    override fun onFound(message: Message) {
        if (eventBus.isGameInProgress()) throw MatchInProgressException()
        val createLobbyMessage = fromNearby(message, CreateLobbyMessage::class.java)

        with(createLobbyMessage) {
            this.ownerDeviceId.throwIllegalArgumentExceptionIfBlank()
            this.ownerName.throwIllegalArgumentExceptionIfBlank()
            authorizationService.authorize(this.ownerName, this.ownerDeviceId)
        }

        val lobby = Lobby(createLobbyMessage.ownerName)
        eventBus.postSticky(LobbyCreatedEvent(lobby))
        Log.i(TAG, "Post LobbyCreatedEvent for lobby: $lobby")

        val inLobbyCreationBroadcast = InLobbyCreationBroadcast(lobby)
        messagesClient.publish(NearbyAdapter.toNearby(inLobbyCreationBroadcast, "LOBBY_CREATION"))
        Log.i(TAG, "Broadcast in lobby creation state")
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun EventBus.isGameInProgress() =
        listOf(
            NewPlayerJoinedEvent::class.java,
            LobbyCreatedEvent::class.java,
            PlayerLeavedEvent::class.java,
            GameStartedEvent::class.java,
            GoalScoredEvent::class.java
        ).map { this.getStickyEvent(it) }
            .any { it != null }

}
