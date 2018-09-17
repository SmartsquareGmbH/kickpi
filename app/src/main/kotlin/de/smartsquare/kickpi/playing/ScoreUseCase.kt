package de.smartsquare.kickpi.playing

import android.util.Log
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.State
import de.smartsquare.kickpi.domain.State.Matchmaking
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.message.IdleMessage
import de.smartsquare.kickprotocol.message.PlayingMessage
import io.reactivex.functions.Consumer
import org.jetbrains.anko.doAsync
import java.lang.Thread.sleep

class ScoreUseCase(
    private val kickprotocol: Kickprotocol,
    private val lobby: LobbyViewModel,
    private val onGoalCallback: () -> Unit,
    private val gameRepository: GameRepository
) : Consumer<Unit> {

    private val TAG = "KICKPI"

    override fun accept(unit: Unit?) {
        if (lobby currentlyIn State.Idle || lobby currentlyIn Matchmaking) {
            Log.i(TAG, "Skipped a gpio edge because no match is in progress.")
            return
        }

        onGoalCallback().also { Log.i(TAG, "Goal scored.") }

        if (lobby.scoreLeft.value < 10 && lobby.scoreRight.value < 10) {
            kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby())).subscribe()
        } else {
            kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby())).subscribe()

            gameRepository.save(lobby)

            doAsync {
                sleep(5000)
                kickprotocol.broadcastAndAwait(IdleMessage()).subscribe()
            }
        }
    }
}