package de.smartsquare.kickpi.nearby

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import javax.inject.Inject

class UniqueAndroidIDGenerator @Inject constructor() {

    @Inject
    lateinit var context: Context

    @SuppressLint("HardwareIds")
    fun generate(): String = Settings.Secure.getString(context.applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
}
