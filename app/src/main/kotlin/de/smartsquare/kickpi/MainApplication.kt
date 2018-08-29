package de.smartsquare.kickpi

import android.app.Application
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.create.CreateLobbyUseCase
import de.smartsquare.kickpi.idle.BroadcastIdleUseCase
import de.smartsquare.kickpi.join.JoinLobbyUseCase
import de.smartsquare.kickpi.leave.LeaveLobbyUseCase
import de.smartsquare.kickpi.play.score.ScoreUseCase
import de.smartsquare.kickpi.play.start.GameStartedEvent
import de.smartsquare.kickpi.play.start.StartGameUseCase
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainApplication : Application() {

    @Inject lateinit var broadcastIdleUseCase: BroadcastIdleUseCase
    @Inject lateinit var messagesClient: MessagesClient
    @Inject lateinit var joinlobbyUseCase: JoinLobbyUseCase
    @Inject lateinit var leaveLobbyUseCase: LeaveLobbyUseCase
    @Inject lateinit var startGameUseCase: StartGameUseCase
    @Inject lateinit var createLobbyUseCase: CreateLobbyUseCase
    @Inject lateinit var scoreUseCase: ScoreUseCase

    @Inject lateinit var eventBus: EventBus

    override fun onCreate() {
        super.onCreate()

        DaggerContainer.builder()
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)

        eventBus.register(broadcastIdleUseCase)
        scoreUseCase.registerGPIOCallbacksForBothGoalsOnGameStartedEvent(GameStartedEvent(Lobby("deen")))

        messagesClient.subscribeOnType(createLobbyUseCase, "CREATE_LOBBY")
        messagesClient.subscribeOnType(joinlobbyUseCase, "JOIN_LOBBY")
        messagesClient.subscribeOnType(leaveLobbyUseCase, "LEAVE_LOBBY")
        messagesClient.subscribeOnType(startGameUseCase, "START_GAME")

        broadcastIdleUseCase.publishIdleMessage()
    }
}
