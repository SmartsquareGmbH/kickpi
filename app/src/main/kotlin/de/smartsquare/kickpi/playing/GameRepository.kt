package de.smartsquare.kickpi.playing

import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.playing.Game.Team
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

class GameRepository(private val kickchain: KickchainGameRepository) {

    fun save(lobby: LobbyViewModel) {
        lobby.let {
            val leftTeam = lobby.leftTeam.value.let(::Team)
            val rightTeam = lobby.rightTeam.value.let(::Team)
            val score = Game.Score(lobby.scoreLeft.value, lobby.scoreRight.value)

            Game(leftTeam, rightTeam, score)
        }.also { kickchain.save(it).execute() }
    }
}

interface KickchainGameRepository {

    @POST("game")
    fun save(@Body game: Game): Call<ResponseBody>
}

data class Game(val team1: Team, val team2: Team, val score: Score) {

    data class Team(val players: List<String>)

    data class Score(val goals1: Int, val goals2: Int)
}
