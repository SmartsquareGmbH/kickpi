package de.smartsquare.kickpi

import android.app.Application
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Strategy
import com.google.android.gms.nearby.messages.MessagesClient
import de.smartsquare.kickpi.create.CreateLobbyUseCase
import de.smartsquare.kickpi.idle.BroadcastIdleUseCase
import de.smartsquare.kickpi.join.JoinLobbyUseCase
import de.smartsquare.kickpi.leave.LeaveLobbyUseCase
import de.smartsquare.kickpi.play.broadcast.BroadcastGameUseCase
import de.smartsquare.kickpi.play.save.SaveUseCase
import de.smartsquare.kickpi.play.score.ScoreUseCase
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
    @Inject lateinit var saveUseCase: SaveUseCase
    @Inject lateinit var broadcastGameUseCase: BroadcastGameUseCase

    @Inject lateinit var eventBus: EventBus

    override fun onCreate() {
        super.onCreate()


        Nearby.getConnectionsClient(this)
            .startAdvertising(
                "Smartsquare HQ",
                "de.smartsquare.kickpi",
                KickpiConnectionLifecycleCallback(),
                AdvertisingOptions(Strategy.P2P_CLUSTER)
            )

        Nearby.getConnectionsClient(this)
            .startDiscovery(
                "de.smartsquare.kickpi",
                KickpiDiscoveryCallback(),
                DiscoveryOptions(Strategy.P2P_CLUSTER)
            )

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
    }

    class KickpiConnectionLifecycleCallback : ConnectionLifecycleCallback() {
        override fun onConnectionResult(p0: String, p1: ConnectionResolution) {

        }

        override fun onDisconnected(p0: String) {
        }

        override fun onConnectionInitiated(p0: String, p1: ConnectionInfo) {
            //mobile client will sich verbinden
        }

    }

    class KickpiDiscoveryCallback : EndpointDiscoveryCallback() {
        override fun onEndpointFound(p0: String, p1: DiscoveredEndpointInfo) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onEndpointLost(p0: String) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}
