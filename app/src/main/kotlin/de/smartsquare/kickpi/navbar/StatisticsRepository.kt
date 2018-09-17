package de.smartsquare.kickpi.navbar

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface StatisticsRepository {

    @GET("/statistics/topten/duoq")
    fun findTopTenDuoQPlayers(): Observable<List<Player>>
}

