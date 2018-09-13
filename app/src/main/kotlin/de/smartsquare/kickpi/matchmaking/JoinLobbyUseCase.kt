package de.smartsquare.kickpi.matchmaking

import android.util.Log
import de.smartsquare.kickpi.Endpoints
import de.smartsquare.kickpi.KickPiLobby
import de.smartsquare.kickpi.gameserver.Position
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.JoinLobbyMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer

class JoinLobbyUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpoints: Endpoints,
    private val lobby: KickPiLobby
) : Consumer<MessageEvent.Message<JoinLobbyMessage>> {

    private val TAG = "Join Lobby Use Case"

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