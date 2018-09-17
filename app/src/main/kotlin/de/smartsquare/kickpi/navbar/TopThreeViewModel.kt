package de.smartsquare.kickpi.navbar

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit.SECONDS

class TopThreeViewModel(statisticsRepository: StatisticsRepository) : ViewModel() {

    val firstPlace: MutableLiveData<String> = MutableLiveData()
    val secondPlace: MutableLiveData<String> = MutableLiveData()
    val thirdPlace: MutableLiveData<String> = MutableLiveData()

    init {
        statisticsRepository.findTopTenDuoQPlayers()
            .subscribeOn(Schedulers.io())
            .retryWhen { it.delay(5, SECONDS) }
            .subscribe { players ->
                players.getTop(1).playerNameOrEmptyString().also(firstPlace::postValue)
                players.getTop(2).playerNameOrEmptyString().also(secondPlace::postValue)
                players.getTop(3).playerNameOrEmptyString().also(thirdPlace::postValue)
            }
    }
}
