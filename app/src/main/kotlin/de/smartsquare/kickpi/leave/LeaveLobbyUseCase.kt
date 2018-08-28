package de.smartsquare.kickpi.leave

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.getLastModifiedLobby
import de.smartsquare.kickpi.join.AuthorizationService
import de.smartsquare.kickpi.join.Credentials
import de.smartsquare.kickpi.removeStickyModifiedLobbyEvent
import de.smartsquare.kickpi.throwIllegalArgumentExceptionIfBlank
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class LeaveLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus
) : MessageListener() {

    private val TAG = "Leave Lobby"

    override fun onFound(message: Message) {
        val leaveLobbyMessage = NearbyAdapter.fromNearby(message, LeaveLobbyMessage::class.java)

        leaveLobbyMessage.playerName.throwIllegalArgumentExceptionIfBlank()
        leaveLobbyMessage.playerDeviceId.throwIllegalArgumentExceptionIfBlank()

        val credentials = Credentials(leaveLobbyMessage.playerDeviceId, leaveLobbyMessage.playerName)
        if (authorizationService.isAuthorized(credentials).not()) throw UnauthorizedException()

        eventBus.getLastModifiedLobby()?.also {
            val playerName = leaveLobbyMessage.playerName

            if (it.contains(playerName)) {
                eventBus.removeStickyModifiedLobbyEvent()

                if (it.isLastPlayer(playerName)) {
                    eventBus.postSticky(GameCanceledEvent())
                } else {
                    val lobbyWithoutPlayer = leaveLobby(playerName, it)
                    eventBus.postSticky(PlayerLeavedEvent(lobbyWithoutPlayer))
                }
            } else {
                Log.i(TAG, "Player $playerName not found in lobby")
            }
        }
    }

    private fun leaveLobby(playerName: String, it: Lobby): Lobby {
        Log.i(TAG, "$playerName has left")

        return if (playerName == it.owner) {
            it.copy(owner = (it.leftTeam + it.rightTeam - playerName).first(), leftTeam = it.leftTeam - playerName, rightTeam = it.rightTeam - playerName)
        } else {
            it.copy(leftTeam = it.leftTeam - playerName, rightTeam = it.rightTeam - playerName)
        }
    }
}