package de.smartsquare.kickpi

import android.app.Application
import android.os.Handler
import android.os.StrictMode
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.create.CreateLobbyMessage
import de.smartsquare.kickpi.create.CreateLobbyUseCase
import de.smartsquare.kickpi.idle.BroadcastIdleUseCase
import de.smartsquare.kickpi.join.JoinLobbyMessage
import de.smartsquare.kickpi.join.JoinLobbyUseCase
import de.smartsquare.kickpi.leave.LeaveLobbyUseCase
import de.smartsquare.kickpi.play.broadcast.BroadcastGameUseCase
import de.smartsquare.kickpi.play.save.SaveUseCase
import de.smartsquare.kickpi.play.score.ScoreUseCase
import de.smartsquare.kickpi.play.start.StartGameMessage
import de.smartsquare.kickpi.play.start.StartGameUseCase
import org.greenrobot.eventbus.EventBus
import java.lang.Thread.sleep
import javax.inject.Inject

class MainApplication : Application() {

    @Inject lateinit var broadcastIdleUseCase: BroadcastIdleUseCase
    @Inject lateinit var messagesClient: MessagesClient
    @Inject lateinit var joinlobbyUseCase: JoinLobbyUseCase
    @Inject lateinit var leaveLobbyUseCase: LeaveLobbyUseCase
    @Inject lateinit var startGameUseCase: StartGameUseCase
    @Inject lateinit var createLobbyUseCase: CreateLobbyUseCase
    @Inject lateinit var scoreUseCase: ScoreUseCase
    @Inject lateinit var saveUseCase: SaveUseCase
    @Inject lateinit var broadcastGameUseCase: BroadcastGameUseCase

    @Inject lateinit var eventBus: EventBus

    override fun onCreate() {
        super.onCreate()

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
            .build())

        DaggerContainer.builder()
            .activityModule(ActivityModule(this))
            .build()
            .inject(this)

        eventBus.register(broadcastIdleUseCase)
        eventBus.register(broadcastGameUseCase)
        eventBus.register(saveUseCase)
        eventBus.register(scoreUseCase)

        messagesClient.subscribeOnType(createLobbyUseCase, "CREATE_LOBBY")
        messagesClient.subscribeOnType(joinlobbyUseCase, "JOIN_LOBBY")
        messagesClient.subscribeOnType(leaveLobbyUseCase, "LEAVE_LOBBY")
        messagesClient.subscribeOnType(startGameUseCase, "START_GAME")

        broadcastIdleUseCase.publishIdleMessage()
        Handler(mainLooper).post {
            sleep(5000)

            val createLobbyMessage = CreateLobbyMessage(ownerName = "deen", ownerDeviceId = "1337")
            createLobbyUseCase.onFound(NearbyAdapter.toNearby(createLobbyMessage, "CREATE_LOBBY"))

            sleep(5000)

            listOf(
                JoinLobbyMessage(playerName = "drs", playerDeviceId = "1338", team = JoinLobbyMessage.Team.LEFT),
                JoinLobbyMessage(playerName = "saschar", playerDeviceId = "1339", team = JoinLobbyMessage.Team.RIGHT),
                JoinLobbyMessage(playerName = "ruby", playerDeviceId = "1340", team = JoinLobbyMessage.Team.RIGHT)
            )
                .map { NearbyAdapter.toNearby(it, "JOIN_LOBBY") }
                .forEach {
                    sleep(2000)
                    joinlobbyUseCase.onFound(it)
                }

            sleep(5000)
            val startGameMessage = StartGameMessage("deen", "1337")
            startGameUseCase.onFound(NearbyAdapter.toNearby(startGameMessage, "START_GAME"))
        }
    }
}
