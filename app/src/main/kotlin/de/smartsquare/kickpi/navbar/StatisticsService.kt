package de.smartsquare.kickpi.navbar

import retrofit2.Call
import retrofit2.http.GET

//TODO: return error if timeout
class StatisticsService(private val kickwayStatisticsRepository: KickwayStatisticsRepository) {

    fun findTopThreePlayers() = (kickwayStatisticsRepository.findTopTenSoloQPlayers()
        .execute().body() ?: emptyList())
        .map(Player::name)
        .take(3)
}

interface KickwayStatisticsRepository {

    @GET("/statistics/topten/duoq") //TODO: Overall top3 endpoint
    fun findTopTenSoloQPlayers(): Call<List<Player>>
}

data class Player(
    val name: String,
    val totalWins: Int,
    val totalGoals: Int
)
