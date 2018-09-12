package de.smartsquare.kickpi

import android.app.Application
import android.support.design.widget.Snackbar
import android.widget.Toast
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.android.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin(this, modules)
    }
}
