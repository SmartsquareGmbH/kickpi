package de.smartsquare.kickpi.matchmaking

import android.util.Log
import de.smartsquare.kickpi.Endpoints
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.State
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.IdleMessage
import de.smartsquare.kickprotocol.message.LeaveLobbyMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer

class LeaveLobbyUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpoints: Endpoints,
    private val lobby: LobbyViewModel
) : Consumer<MessageEvent.Message<LeaveLobbyMessage>> {

    private val TAG = "Leave Lobby Use Case"

    override fun accept(message: MessageEvent.Message<LeaveLobbyMessage>) {
        endpoints.getIfAuthorized(message.endpointId)
            ?.let(lobby::leave)
            ?.let {
                when {
                    lobby currentlyIn State.Idle -> IdleMessage()
                    else -> MatchmakingMessage(lobby.toKickprotocolLobby())
                }
            }
            ?.also {
                kickprotocol.broadcastAndAwait(it).subscribe()
            }
            ?.also { Log.i(TAG, "${endpoints.getIfAuthorized(message.endpointId)} left the game.") }
    }
}