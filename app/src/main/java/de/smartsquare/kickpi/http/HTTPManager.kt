package de.smartsquare.kickpi.http

import android.util.Log
import de.smartsquare.kickpi.GoalEvent
import de.smartsquare.kickpi.nearby.UniqueAndroidIDGenerator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HTTPManager @Inject constructor(idGenerator: UniqueAndroidIDGenerator) {

    private val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain -> chain.proceed(chain.request().newBuilder().addHeader("raspberry", idGenerator.generate()).build()) }
            .build()

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onGoalEvent(goalEvent: GoalEvent) {
        Log.i("HTTP Manager", "Telling the kickway about the goal on ${goalEvent.scoreURL}")
        Request.Builder()
                .url(goalEvent.scoreURL)
                .patch(RequestBody.create(null, byteArrayOf()))
                .build()
                .let { httpClient.newCall(it) }
                .also { it.execute() }
    }
}
