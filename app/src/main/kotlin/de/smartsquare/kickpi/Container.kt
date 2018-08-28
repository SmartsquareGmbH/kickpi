package de.smartsquare.kickpi

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [GPIOModule::class, ActivityModule::class, HTTPModule::class])
interface Container {

    fun inject(app: MainApplication)
}
