package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import com.google.android.things.pio.PeripheralManager
import de.smartsquare.kickpi.gpio.GPIOManager
import de.smartsquare.kickpi.http.HTTPManager
import de.smartsquare.kickpi.ioc.ActivityModule
import de.smartsquare.kickpi.ioc.DaggerContainer
import de.smartsquare.kickpi.nearby.NearbyManager
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : Activity() {


    @Inject
    lateinit var gpioManager: GPIOManager

    @Inject
    lateinit var nearbyManager: NearbyManager

    @Inject
    lateinit var httpManager: HTTPManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerContainer.builder().activityModule(ActivityModule(this)).build().inject(this)

        EventBus.getDefault().register(gpioManager)
        EventBus.getDefault().register(nearbyManager)
        EventBus.getDefault().register(httpManager)
        EventBus.getDefault().post(StartIdleEvent())
    }
}
