package de.smartsquare.kickpi.play.save

import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.play.save.Game.Team
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import javax.inject.Inject

class GameService @Inject constructor(private val gameRepository: GameRepository) {

    fun save(lobby: Lobby) {
        lobby.let {
            val leftTeam = lobby.leftTeam.let(::Team)
            val rightTeam = lobby.rightTeam.let(::Team)
            val score = Game.Score(lobby.scoreLeftTeam, lobby.scoreRightTeam)

            Game(leftTeam, rightTeam, score)
        }.also { gameRepository.save(it).execute() }
    }
}

interface GameRepository {

    @POST("game")
    fun save(@Body game: Game): Call<ResponseBody>
}

data class Game(val team1: Team, val team2: Team, val score: Score) {

    data class Team(val players: List<String>)

    data class Score(val goals1: Int, val goals2: Int)
}
