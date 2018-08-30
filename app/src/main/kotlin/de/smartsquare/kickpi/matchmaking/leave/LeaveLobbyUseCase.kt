package de.smartsquare.kickpi.matchmaking.leave

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.AuthorizationService
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.getLastModifiedLobby
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

        with(leaveLobbyMessage) {
            this.playerName.throwIllegalArgumentExceptionIfBlank()
            this.playerDeviceId.throwIllegalArgumentExceptionIfBlank()
            authorizationService.authorize(this.playerName, this.playerDeviceId)
        }

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