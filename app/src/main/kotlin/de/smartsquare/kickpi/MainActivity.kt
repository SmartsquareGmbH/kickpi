package de.smartsquare.kickpi

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.viewKonfetti
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape.CIRCLE
import nl.dionsegijn.konfetti.models.Shape.RECT
import nl.dionsegijn.konfetti.models.Size

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
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
