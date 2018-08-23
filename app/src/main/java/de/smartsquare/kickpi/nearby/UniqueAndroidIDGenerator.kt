package de.smartsquare.kickpi.nearby

import android.annotation.SuppressLint
import android.app.Activity
import android.provider.Settings

class UniqueAndroidIDGenerator(private val activity : Activity) : IDGenerator {

    @SuppressLint("HardwareIds")
    override fun generate(): String = Settings.Secure.getString(activity.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
}
