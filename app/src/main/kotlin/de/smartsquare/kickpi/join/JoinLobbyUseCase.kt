package de.smartsquare.kickpi.join

import android.util.Log
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.DuplicateNameException
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.TeamAlreadyFullException
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.join.JoinLobbyMessage.Team.LEFT
import de.smartsquare.kickpi.removeStickyModifiedLobbyEvent
import de.smartsquare.kickpi.throwIllegalArgumentExceptionIfBlank
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class JoinLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus
) : MessageListener() {

    private val TAG = "Join Lobby"

    override fun onFound(message: Message) {
        val joinLobbyMessage = NearbyAdapter.fromNearby(message, JoinLobbyMessage::class.java)

        joinLobbyMessage.playerName.throwIllegalArgumentExceptionIfBlank()
        joinLobbyMessage.playerDeviceId.throwIllegalArgumentExceptionIfBlank()

        val credentials = Credentials(joinLobbyMessage.playerDeviceId, joinLobbyMessage.playerName)
        if (authorizationService.isAuthorized(credentials).not()) throw UnauthorizedException()

        eventBus.removeStickyModifiedLobbyEvent()?.also {
            val lobbyWithNewPlayer = joinLobby(joinLobbyMessage, it)
            eventBus.postSticky(NewPlayerJoinedEvent(lobbyWithNewPlayer))
        }
    }

    private fun joinLobby(joinLobbyMessage: JoinLobbyMessage, it: Lobby): Lobby {
        return if (joinLobbyMessage.team == LEFT) {
            throwExceptionIfFullOrAlreadyJoined(joinLobbyMessage.playerName, it.leftTeam)
            it.copy(leftTeam = it.leftTeam + joinLobbyMessage.playerName)
        } else {
            throwExceptionIfFullOrAlreadyJoined(joinLobbyMessage.playerName, it.rightTeam)
            it.copy(rightTeam = it.rightTeam + joinLobbyMessage.playerName)
        }.also {
            Log.i(TAG, "${joinLobbyMessage.playerName} joined")
        }
    }

    private fun throwExceptionIfFullOrAlreadyJoined(player: String, players: List<String>) {
        if (players.contains(player)) throw DuplicateNameException()
        if (players.size > 1) throw TeamAlreadyFullException()
    }
}