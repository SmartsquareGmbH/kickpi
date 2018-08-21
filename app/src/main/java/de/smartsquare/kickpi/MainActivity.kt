package de.smartsquare.kickpi

import android.app.Activity
import android.os.Bundle
import com.google.android.things.pio.PeripheralManager

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val peripheralManager = PeripheralManager.getInstance()
    }

}
