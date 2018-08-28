package de.smartsquare.kickpi.play.start

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.AuthorizationService
import de.smartsquare.kickpi.MissingOpponentsException
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.getLastModifiedLobby
import de.smartsquare.kickpi.removeStickyModifiedLobbyEvent
import de.smartsquare.kickpi.throwIllegalArgumentExceptionIfBlank
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class StartGameUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus
) : MessageListener() {

    private val TAG = "Start Game"

    override fun onFound(message: Message) {
        val startGameMessage = NearbyAdapter.fromNearby(message, StartGameMessage::class.java)

        with(startGameMessage) {
            this.ownerName.throwIllegalArgumentExceptionIfBlank()
            this.ownerDeviceId.throwIllegalArgumentExceptionIfBlank()
            authorizationService.authorize(this.ownerName, this.ownerDeviceId)
        }

        eventBus.getLastModifiedLobby()
            ?.also {
                if (it.leftTeam.isEmpty().or(it.rightTeam.isEmpty())) throw MissingOpponentsException()
                if (it.owner != startGameMessage.ownerName) throw UnauthorizedException()

                with(eventBus) {
                    removeStickyModifiedLobbyEvent()
                    postSticky(GameStartedEvent(it))
                }
                Log.i(TAG, "Game started with lobby: $it")
            }
    }
}
