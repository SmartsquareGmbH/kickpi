package de.smartsquare.kickpi.ioc

import android.content.Context
import com.google.android.gms.nearby.Nearby
import com.google.android.things.pio.PeripheralManager
import dagger.Module
import dagger.Provides
import de.smartsquare.kickpi.nearby.UniqueAndroidIDGenerator
import okhttp3.OkHttpClient
import javax.inject.Inject
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
}