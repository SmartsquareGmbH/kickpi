package de.smartsquare.kickpi.ioc

import dagger.Component
import de.smartsquare.kickpi.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [GPIOModule::class, ActivityModule::class])
interface Container {

    fun inject(app: MainActivity)
}
