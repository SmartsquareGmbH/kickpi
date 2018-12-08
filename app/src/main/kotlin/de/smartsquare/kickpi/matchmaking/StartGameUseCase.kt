package de.smartsquare.kickpi.matchmaking

import de.smartsquare.kickpi.EndpointStore
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.PlayingMessage
import de.smartsquare.kickprotocol.message.StartGameMessage
import io.reactivex.functions.Consumer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class StartGameUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpointStore: EndpointStore,
    private val lobby: LobbyViewModel
) : Consumer<MessageEvent.Message<StartGameMessage>>, AnkoLogger {

    override fun accept(message: MessageEvent.Message<StartGameMessage>) {
        endpointStore.getIfAuthorized(message.endpointId)
            ?.let(lobby::startGame)
            ?.also {
                kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby()))
                    .subscribe()
            }
            ?.also { info { "Game started by ${endpointStore.getIfAuthorized(message.endpointId)}" } }
    }
}
