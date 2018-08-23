package de.smartsquare.kickpi

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import de.smartsquare.kickpi.gpio.GPIOManager
import de.smartsquare.kickpi.http.HTTPManager
import de.smartsquare.kickpi.ioc.ActivityModule
import de.smartsquare.kickpi.ioc.DaggerContainer
import de.smartsquare.kickpi.nearby.NearbyManager
import kotlinx.android.synthetic.main.activity_main.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape.RECT
import nl.dionsegijn.konfetti.models.Shape.CIRCLE
import nl.dionsegijn.konfetti.models.Size
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

    fun confetto() {
        val confettiContainer = findViewById<KonfettiView>(R.id.viewKonfetti)

        confettiContainer.build()
                .addColors(Color.RED, Color.argb(0, 204, 0, 51), Color.argb(0, 204, 0, 0))
                .setDirection(0.0, 350.0)
                .setSpeed(3f, 8f)
                .setFadeOutEnabled(true)
                .setTimeToLive(10000)
                .addShapes(RECT, CIRCLE)
                .addSizes(Size(10))
                .setPosition(viewKonfetti.width - 0f, viewKonfetti.height - 0f)
                .stream(100, 5000L)
    }
}
