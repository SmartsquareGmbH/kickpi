package de.smartsquare.kickpi.playing

import de.smartsquare.kickpi.BuildConfig.SCORE_TO_FINISH_GAME
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.State.Idle
import de.smartsquare.kickpi.domain.State.Matchmaking
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.message.IdleMessage
import de.smartsquare.kickprotocol.message.PlayingMessage
import io.reactivex.functions.Consumer
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import java.lang.Thread.sleep

class ScoreUseCase(
    private val kickprotocol: Kickprotocol,
    private val lobby: LobbyViewModel,
    private val onGoalCallback: () -> Unit,
    private val gameRepository: GameRepository
) : Consumer<Unit>, AnkoLogger {

    override fun accept(unit: Unit?) {
        if (lobby currentlyIn Idle || lobby currentlyIn Matchmaking) {
            info { "Skipped a gpio edge because no match is in progress." }
            return
        }

        onGoalCallback().also { info { "Goal scored." } }

        if (lobby.scoreLeft.value < SCORE_TO_FINISH_GAME.toInt() &&
            lobby.scoreRight.value < SCORE_TO_FINISH_GAME.toInt()) {
            kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby())).subscribe()
        } else {
            kickprotocol.broadcastAndAwait(IdleMessage()).subscribe()
            gameRepository.save(lobby)
            lobby.reset()
        }
    }
}
