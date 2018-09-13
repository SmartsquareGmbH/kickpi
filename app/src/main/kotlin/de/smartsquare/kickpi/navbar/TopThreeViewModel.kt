package de.smartsquare.kickpi.navbar

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class TopThreeViewModel(statisticsService: StatisticsService) : ViewModel() {
    val firstPlace: MutableLiveData<String> = MutableLiveData()
    val secondPlace: MutableLiveData<String> = MutableLiveData()
    val thirdPlace: MutableLiveData<String> = MutableLiveData()

    init {
        with(statisticsService.findTopThreePlayers()) {
            getOrElse(0) { "" }.also(firstPlace::setValue)
            getOrElse(1) { "" }.also(secondPlace::setValue)
            getOrElse(2) { "" }.also(thirdPlace::setValue)
        }
    }
}