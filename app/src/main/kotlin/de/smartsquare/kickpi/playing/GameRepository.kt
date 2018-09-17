package de.smartsquare.kickpi.playing

import android.util.Log
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.playing.Game.Team
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

class GameRepository(private val kickchain: KickchainGameRepository) {

    private val TAG = "KICKPI"

    fun save(lobby: LobbyViewModel) {
        lobby.let {
            val leftTeam = lobby.leftTeam.value.let(::Team)
            val rightTeam = lobby.rightTeam.value.let(::Team)
            val score = Game.Score(lobby.scoreLeft.value, lobby.scoreRight.value)

            Game(leftTeam, rightTeam, score)
        }.also { game ->
            kickchain.save(game)
                .subscribeOn(Schedulers.io())
                .retryWhen { request ->
                    request.delay(5, TimeUnit.SECONDS)
                        .also { Log.i(TAG, "Error while storing [$game]. Retrying every 5 seconds.") }
                }
                .subscribe {
                    Log.i(TAG, "Successfully stored $game.")
                }
        }
    }
}

interface KickchainGameRepository {

    @POST("game")
    fun save(@Body game: Game): Completable
}

data class Game(val team1: Team, val team2: Team, val score: Score) {

    data class Team(val players: List<String>)

    data class Score(val goals1: Int, val goals2: Int)
}
