package de.smartsquare.kickpi

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.things.pio.PeripheralManager
import dagger.Module
import dagger.Provides
import de.smartsquare.kickpi.create.AuthorizationService
import de.smartsquare.kickpi.play.finish.GameService
import org.greenrobot.eventbus.EventBus
import retrofit2.Retrofit
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

    @Provides
    fun createLobbyAuthorizationService() = Retrofit.Builder()
        .baseUrl("https://mysterious-dusk-56126.herokuapp.com/")
        .build()
        .create(de.smartsquare.kickpi.create.AuthorizationService::class.java)

    @Provides
    fun joinLobbyAuthorizationService() = Retrofit.Builder()
        .baseUrl("https://mysterious-dusk-56126.herokuapp.com/")
        .build()
        .create(de.smartsquare.kickpi.join.AuthorizationService::class.java)

    @Provides
    fun gameService() = Retrofit.Builder()
        .baseUrl("https://mysterious-dusk-56126.herokuapp.com/")
        .build()
        .create(GameService::class.java)
}
