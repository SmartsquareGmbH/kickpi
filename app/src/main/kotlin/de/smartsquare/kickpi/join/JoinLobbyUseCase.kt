package de.smartsquare.kickpi.join

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener
import de.smartsquare.kickpi.DuplicateNameException
import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.UnauthorizedException
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.join.JoinLobbyMessage.Team.LEFT
import de.smartsquare.kickpi.join.JoinLobbyMessage.Team.RIGHT
import de.smartsquare.kickpi.leave.PlayerLeavedEvent
import de.smartsquare.kickpi.throwIllegalArgumentExceptionIfBlank
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class JoinLobbyUseCase @Inject constructor(
    private val authorizationService: AuthorizationService,
    private val eventBus: EventBus
) : MessageListener() {

    override fun onFound(message: Message) {
        val joinLobbyMessage = NearbyAdapter.fromNearby(message, JoinLobbyMessage::class.java)

        joinLobbyMessage.playerName.throwIllegalArgumentExceptionIfBlank()
        joinLobbyMessage.playerDeviceId.throwIllegalArgumentExceptionIfBlank()

        val credentials = Credentials(joinLobbyMessage.playerDeviceId, joinLobbyMessage.playerName)
        if (authorizationService.isAuthorized(credentials).not()) throw UnauthorizedException()

        eventBus.removeStickyEvent(NewPlayerJoinedEvent::class.java)?.also {
            val lobbyWithNewPlayer = joinLobby(joinLobbyMessage, it.lobby)
            eventBus.postSticky(NewPlayerJoinedEvent(lobbyWithNewPlayer))
        }
        eventBus.removeStickyEvent(LobbyCreatedEvent::class.java)?.also {
            val lobbyWithNewPlayer = joinLobby(joinLobbyMessage, it.lobby)
            eventBus.postSticky(NewPlayerJoinedEvent(lobbyWithNewPlayer))
        }
        eventBus.removeStickyEvent(PlayerLeavedEvent::class.java)?.also {
            val lobbyWithNewPlayer = joinLobby(joinLobbyMessage, it.lobby)
            eventBus.postSticky(NewPlayerJoinedEvent(lobbyWithNewPlayer))
        }
    }

    private fun joinLobby(joinLobbyMessage: JoinLobbyMessage, it: Lobby): Lobby {
        if (joinLobbyMessage.team == LEFT && it.leftTeam.contains(joinLobbyMessage.playerName))
            throw DuplicateNameException()
        if (joinLobbyMessage.team == RIGHT && it.rightTeam.contains(joinLobbyMessage.playerName))
            throw DuplicateNameException()

        return if (joinLobbyMessage.team == LEFT)
            it.copy(leftTeam = it.leftTeam + joinLobbyMessage.playerName)
        else
            it.copy(rightTeam = it.rightTeam + joinLobbyMessage.playerName)
    }
}
