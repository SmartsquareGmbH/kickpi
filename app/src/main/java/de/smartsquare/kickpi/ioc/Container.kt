package de.smartsquare.kickpi.ioc

import dagger.Component
import de.smartsquare.kickpi.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [HTTPModule::class, GPIOModule::class])
interface Container {

    fun inject(app: MainActivity)
}
