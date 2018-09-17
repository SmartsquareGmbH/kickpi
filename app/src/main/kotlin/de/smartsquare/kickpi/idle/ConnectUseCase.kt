package de.smartsquare.kickpi.idle

import android.arch.lifecycle.LifecycleOwner
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.gameserver.State
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.ConnectionEvent
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.message.IdleMessage
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import de.smartsquare.kickprotocol.message.PlayingMessage
import io.reactivex.functions.Consumer
import org.koin.standalone.KoinComponent

class ConnectUseCase(
    private val kickprotocol: Kickprotocol,
    private val lobby: LobbyViewModel,
    private val lifecycleOwner: LifecycleOwner
) : Consumer<ConnectionEvent>, KoinComponent {

    override fun accept(connectionEvent: ConnectionEvent) {
        val message = when {
            lobby currentlyIn State.Idle -> IdleMessage()
            lobby currentlyIn State.Matchmaking -> MatchmakingMessage(lobby.toKickprotocolLobby())
            else -> PlayingMessage(lobby.toKickprotocolLobby())
        }

        kickprotocol.sendAndAwait(connectionEvent.endpointId, message)
            .autoDisposable(lifecycleOwner.scope())
            .subscribe()
    }
}