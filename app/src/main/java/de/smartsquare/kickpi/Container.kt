package de.smartsquare.kickpi

import android.app.Activity
import dagger.Component

@Component
interface Container {

    fun inject(app: Activity)
}
