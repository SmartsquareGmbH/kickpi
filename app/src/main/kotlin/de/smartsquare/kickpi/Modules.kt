package de.smartsquare.kickpi

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.things.pio.PeripheralManager
import dagger.Module
import dagger.Provides
import de.smartsquare.kickpi.BuildConfig.KICKWAY_URL
import de.smartsquare.kickpi.play.save.GameRepository
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class GPIOModule {

    @Provides
    @Singleton
    fun peripheralManager(): PeripheralManager = PeripheralManager.getInstance()
}

@Module
class ActivityModule(private val context: Context) {

    @Provides
    fun nearbyMessageClient() = Nearby.getMessagesClient(context)

    @Provides
    fun activityContext() = context

    @Provides
    fun eventBus() = EventBus.getDefault()
}

@Module
class HTTPModule {

    private val baseRetrofit = Retrofit.Builder()
        .baseUrl(KICKWAY_URL)
        .addConverterFactory(GsonConverterFactory.create())

    @Provides
    fun joinLobbyAuthorizationService() = baseRetrofit
        .build().create(AuthorizationRepository::class.java)

    @Provides
    fun gameService() = baseRetrofit
        .build().create(GameRepository::class.java)
}
