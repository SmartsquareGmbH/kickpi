package de.smartsquare.kickpi.http

import de.smartsquare.kickpi.GoalEvent
import de.smartsquare.kickpi.nearby.UniqueAndroidIDGenerator
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class HTTPManager @Inject constructor(idGenerator: UniqueAndroidIDGenerator) {

    private val httpClient = OkHttpClient.Builder()
            .addInterceptor { chain -> chain.proceed(chain.request().newBuilder().addHeader("raspberry", idGenerator.generate()).build()) }
            .build()

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun onGoalEvent(goalEvent: GoalEvent) {
    }
}
