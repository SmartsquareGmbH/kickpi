package de.smartsquare.kickpi.play.save

import de.smartsquare.kickpi.play.score.GameFinishedEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class SaveUseCase @Inject constructor(private val gameService: GameService) {

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    fun saveGameOnGameFinishedEvent(gameFinishedEvent: GameFinishedEvent) {
        gameService.save(gameFinishedEvent.lobby)
    }
}
