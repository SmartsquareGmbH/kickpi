package de.smartsquare.kickpi.play.finish

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface GameService {

    @POST("game")
    fun store(@Body game: Game): Call<ResponseBody>
}

data class Game(val team1: Team, val team2: Team, val score: Score) {

    data class Team(val players: List<String>)

    data class Score(val goals1: Int, val goals2: Int)
}
