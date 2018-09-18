package de.smartsquare.kickpi.matchmaking

import de.smartsquare.kickpi.EndpointStore
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.Position
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.JoinLobbyMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class JoinLobbyUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpointStore: EndpointStore,
    private val lobby: LobbyViewModel
) : Consumer<MessageEvent.Message<JoinLobbyMessage>>, AnkoLogger {

    override fun accept(message: MessageEvent.Message<JoinLobbyMessage>) {
        endpointStore.register(message.endpointId, message.message.username)

        message.message.position
            .let { Position.valueOf(it.name) }
            .also { lobby.join(position = it, name = message.message.username) }
            .also { info { "${message.message.username} joined the game." } }

        kickprotocol.broadcastAndAwait(MatchmakingMessage(lobby.toKickprotocolLobby())).subscribe()
    }
}
