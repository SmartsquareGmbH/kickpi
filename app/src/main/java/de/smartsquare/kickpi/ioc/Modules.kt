package de.smartsquare.kickpi.ioc

import com.google.android.things.pio.PeripheralManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class GPIOModule {

    @Provides
    @Singleton
    fun peripheralManager(): PeripheralManager = PeripheralManager.getInstance()
}

@Module
class HTTPModule {

    @Provides
    @Singleton
    fun kickwayRetrofitClient(): Retrofit = Retrofit.Builder().build()
}
