package de.smartsquare.kickpi

import android.app.Application
import android.net.TrafficStats
import com.kirillr.strictmodehelper.StrictModeCompat
import org.koin.android.ext.android.startKoin

@Suppress("MagicNumber")
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        TrafficStats.setThreadStatsTag(1000)
        startKoin(this, modules)

        if (BuildConfig.DEBUG) {
            val threadPolicy = StrictModeCompat.ThreadPolicy.Builder()
                .detectAll()
                .permitDiskReads()
                .permitDiskWrites()
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
