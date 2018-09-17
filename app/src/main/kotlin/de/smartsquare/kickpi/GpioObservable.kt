package de.smartsquare.kickpi

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.util.concurrent.atomic.AtomicBoolean

@Suppress("NOTHING_TO_INLINE")
inline fun PeripheralManager.open(name: String): GpioObservable {
    val gpio = this.openGpio(name)
    gpio.setDirection(Gpio.DIRECTION_IN)
    gpio.setEdgeTriggerType(Gpio.EDGE_BOTH)
    gpio.setActiveType(Gpio.ACTIVE_HIGH)
    return GpioObservable(gpio)
}

class GpioObservable(private val gpio: Gpio) : Observable<Unit>() {
    override fun subscribeActual(observer: Observer<in Unit>) {
        val callback = Callback(gpio, observer)

        observer.onSubscribe(callback)
        gpio.registerGpioCallback(callback)
    }

    private class Callback(private val gpio: Gpio, private val observer: Observer<in Unit>) : GpioCallback, Disposable {

        private val unsubscribed = AtomicBoolean()

        override fun isDisposed(): Boolean {
            return unsubscribed.get()
        }

        override fun dispose() {
            if (unsubscribed.compareAndSet(false, true)) {
                gpio.close()
            }
        }

        override fun onGpioEdge(gpio: Gpio?): Boolean {
            if (this.isDisposed) return false

            observer.onNext(Unit)
            return true
        }
    }
}
