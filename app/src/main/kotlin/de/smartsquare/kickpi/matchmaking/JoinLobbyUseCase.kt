package de.smartsquare.kickpi.matchmaking

import android.util.Log
import de.smartsquare.kickpi.Endpoints
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.Position
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.JoinLobbyMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer

class JoinLobbyUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpoints: Endpoints,
    private val lobby: LobbyViewModel
) : Consumer<MessageEvent.Message<JoinLobbyMessage>> {

    private val TAG = "KICKPI"

    override fun accept(message: MessageEvent.Message<JoinLobbyMessage>) {
        endpoints.register(message.endpointId, message.message.username)

        message.message.position
            .let { Position.valueOf(it.name) }
            .also { lobby.join(position = it, name = message.message.username) }
            .also { Log.i(TAG, "${message.message.username} joined the game.") }

        kickprotocol.broadcastAndAwait(MatchmakingMessage(lobby.toKickprotocolLobby()))
            .subscribe()
    }
}