package de.smartsquare.kickpi.navbar

import io.reactivex.Single
import retrofit2.http.GET

interface StatisticsRepository {

    @GET("/statistics/topten/duoq")
    fun findTopTenDuoQPlayers(): Single<List<Player>>
}
