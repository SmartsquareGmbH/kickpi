package de.smartsquare.kickpi.matchmaking

import de.smartsquare.kickpi.EndpointStore
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.State
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.MessageEvent
import de.smartsquare.kickprotocol.message.IdleMessage
import de.smartsquare.kickprotocol.message.LeaveLobbyMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import io.reactivex.functions.Consumer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class LeaveLobbyUseCase(
    private val kickprotocol: Kickprotocol,
    private val endpointStore: EndpointStore,
    private val lobby: LobbyViewModel
) : Consumer<MessageEvent.Message<LeaveLobbyMessage>>, AnkoLogger {

    override fun accept(message: MessageEvent.Message<LeaveLobbyMessage>) {
        endpointStore.getIfAuthorized(message.endpointId)
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
            ?.also { info { "${endpointStore.getIfAuthorized(message.endpointId)} left the game." } }
    }
}
