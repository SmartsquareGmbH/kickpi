package de.smartsquare.kickpi.matchmaking

import android.util.Log
import de.smartsquare.kickpi.Endpoints
import de.smartsquare.kickpi.KickPiLobby
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.PlayingMessage
import de.smartsquare.kickprotocol.message.StartGameMessage
import io.reactivex.functions.Consumer

class StartGameUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpoints: Endpoints,
    private val lobby: KickPiLobby
) : Consumer<MessageEvent.Message<StartGameMessage>> {

    private val TAG = "Start Game Use Case"

    override fun accept(message: MessageEvent.Message<StartGameMessage>) {
        endpoints.getIfAuthorized(message.endpointId)
            ?.let(lobby::startGame)
            ?.also {
                kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby()))
                    .subscribe()
            }
            ?.also { Log.i(TAG, "Game started by ${endpoints.getIfAuthorized(message.endpointId)}") }
    }
}