package de.smartsquare.kickpi.playing

import android.util.Log
import de.smartsquare.kickpi.KickPiLobby
import de.smartsquare.kickpi.gameserver.State
import de.smartsquare.kickpi.gameserver.State.Matchmaking
import de.smartsquare.kickpi.toKickprotocolLobby
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.message.PlayingMessage
import io.reactivex.functions.Consumer

class ScoreUseCase(
    private val kickprotocol: Kickprotocol,
    private val lobby: KickPiLobby,
    private val onGoalCallback: () -> Int
) : Consumer<Unit> {

    private val TAG = "GPIO Callback"

    override fun accept(unit: Unit?) {
        if (lobby currentlyIn State.Idle || lobby currentlyIn Matchmaking) {
            Log.i(TAG, "Skipped a gpio edge because no match is in progress.")
        }

        onGoalCallback().also { Log.i(TAG, "Goal scored.") }

        kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby())).subscribe()
    }
}