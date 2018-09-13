package de.smartsquare.kickpi.idle

import android.util.Log
import de.smartsquare.kickpi.Endpoints
import de.smartsquare.kickpi.KickPiLobby
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.CreateGameMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer

class CreateGameUseCase(
    private val kickprotocol: Kickprotocol,
    private val lobby: KickPiLobby,
    private val endpoints: Endpoints
) : Consumer<MessageEvent.Message<CreateGameMessage>> {

    private val TAG = "Create Game Use Case"
    private var onGameCreationCallback: () -> Unit = {}

    override fun accept(message: MessageEvent.Message<CreateGameMessage>) {
        endpoints.register(message.endpointId, message.message.username)
        lobby.startMatchmaking(lobbyOwner = message.message.username, lobbyName = "Haus Dejavu") //TODO: name generator
        Log.i(TAG, "Game created by ${message.message.username}")
        kickprotocol.broadcastAndAwait(MatchmakingMessage(lobby.toKickprotocolLobby()))
            .subscribe()
        onGameCreationCallback()
    }

    fun onGameCreation(callback: () -> Unit) {
        this.onGameCreationCallback = callback
    }
}