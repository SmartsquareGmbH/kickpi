package de.smartsquare.kickpi

import android.app.Application
import com.google.android.things.pio.PeripheralManager
import com.kirillr.strictmodehelper.StrictModeCompat
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

class MainApplication : Application() {

    private val lobby by inject<KickPiLobby>()
    private val peripheralManager by inject<PeripheralManager>()

    override fun onCreate() {
        super.onCreate()

        startKoin(this, modules)

        if (BuildConfig.DEBUG) {
            val threadPolicy = StrictModeCompat.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()

            val vmPolicy = StrictModeCompat.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()

            StrictModeCompat.setPolicies(threadPolicy, vmPolicy)
        }
    }
}
