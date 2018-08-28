package de.smartsquare.kickpi.play.broadcast

import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.NearbyAdapter
import de.smartsquare.kickpi.play.score.GoalScoredEvent
import de.smartsquare.kickpi.play.start.GameStartedEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import javax.inject.Inject

class BroadcastGameUseCase @Inject constructor(private val messagesClient: MessagesClient) {

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    fun broadcastGameOnStart(gameStartedEvent: GameStartedEvent) {
        val lobby = gameStartedEvent.lobby
        val broadcast = InGameBroadcast(lobby)
        val nearbyBroadcast = NearbyAdapter.toNearby(broadcast, "IN_GAME")

        messagesClient.publish(nearbyBroadcast)
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND, sticky = true)
    fun broadcastGameOnGoalScored(goalScoredEvent: GoalScoredEvent) {
        val lobby = goalScoredEvent.lobby
        val broadcast = InGameBroadcast(lobby)
        val nearbyBroadcast = NearbyAdapter.toNearby(broadcast, "IN_GAME")

        messagesClient.publish(nearbyBroadcast)
    }
}
