package de.smartsquare.kickpi.idle

import de.smartsquare.kickpi.EndpointStore
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.CreateGameMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class CreateGameUseCase(
    private val kickprotocol: Kickprotocol,
    private val lobby: LobbyViewModel,
    private val endpointStore: EndpointStore
) : Consumer<MessageEvent.Message<CreateGameMessage>>, AnkoLogger {

    override fun accept(message: MessageEvent.Message<CreateGameMessage>) {
        endpointStore.register(message.endpointId, message.message.username)

        lobby.startMatchmaking(lobbyOwner = message.message.username, lobbyName = "Haus Dejavu")

        info { "Game created by ${message.message.username}" }

        kickprotocol.broadcastAndAwait(MatchmakingMessage(lobby.toKickprotocolLobby())).subscribe()
    }
}
