package de.smartsquare.kickpi

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class NonNullLiveData<T>(private val defaultValue: T) : MutableLiveData<T>() {

    override fun getValue(): T = super.getValue() ?: defaultValue

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, Observer<T> {
            observer.onChanged(it ?: defaultValue)
        })
    }
}