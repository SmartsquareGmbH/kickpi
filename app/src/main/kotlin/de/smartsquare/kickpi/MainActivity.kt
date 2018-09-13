package de.smartsquare.kickpi

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.android.things.pio.PeripheralManager
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import de.smartsquare.kickpi.BuildConfig.LEFT_GOAL_GPIO
import de.smartsquare.kickpi.BuildConfig.RIGHT_GOAL_GPIO
import de.smartsquare.kickpi.idle.ConnectUseCase
import de.smartsquare.kickpi.idle.CreateGameUseCase
import de.smartsquare.kickpi.idle.LobbyFragment
import de.smartsquare.kickpi.matchmaking.JoinLobbyUseCase
import de.smartsquare.kickpi.matchmaking.LeaveLobbyUseCase
import de.smartsquare.kickpi.matchmaking.MatchmakingFragment
import de.smartsquare.kickpi.matchmaking.StartGameUseCase
import de.smartsquare.kickpi.navbar.TopThreeViewModel
import de.smartsquare.kickpi.playing.ScoreUseCase
import de.smartsquare.kickprotocol.ConnectionEvent.Connected
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.filterMessages
import io.reactivex.plugins.RxJavaPlugins
import kotterknife.bindView
import org.koin.android.ext.android.inject
import org.koin.android.scope.ext.android.bindScope
import org.koin.android.scope.ext.android.getOrCreateScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit.SECONDS

class MainActivity : AppCompatActivity() {

    private val kickprotocol: Kickprotocol by inject() { parametersOf(this) }

    private val createGameUseCase by inject<CreateGameUseCase> { parametersOf(this) }
    private val connectUseCase by inject<ConnectUseCase> { parametersOf(this) }
    private val joinLobbyUseCase by inject<JoinLobbyUseCase> { parametersOf(this) }
    private val startGameUseCase by inject<StartGameUseCase> { parametersOf(this) }
    private val leaveLobbyUseCase by inject<LeaveLobbyUseCase> { parametersOf(this) }

    private val peripheralManager by inject<PeripheralManager>()
    private val lobby by inject<KickPiLobby>()

    private val goldPlayer by bindView<TextView>(R.id.goldPlayer)
    private val silverPlayer by bindView<TextView>(R.id.silverPlayer)
    private val bronzePlayer by bindView<TextView>(R.id.bronzePlayer)

    private val firstPlayerLeft: TextView by bindView<TextView>(R.id.firstPlayerOfLeftTeam)
    private val secondPlayerLeft: TextView by bindView<TextView>(R.id.secondPlayerOfLeftTeam)
    private val firstPlayerRight: TextView by bindView<TextView>(R.id.firstPlayerOfLeftTeam)
    private val secondPlayerRight: TextView by bindView<TextView>(R.id.firstPlayerOfRightTeam)
    private val connectionCount: TextView by bindView<TextView>(R.id.connectionCount)

    private val topThreeViewModel by viewModel<TopThreeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        topThreeViewModel.firstPlace.observe(this, Observer {
            goldPlayer.text = it
        })
        topThreeViewModel.secondPlace.observe(this, Observer {
            silverPlayer.text = it
        })
        topThreeViewModel.thirdPlace.observe(this, Observer {
            bronzePlayer.text = it
        })

        bindScope(getOrCreateScope("activity"))

        val lobbyFragment = LobbyFragment()
        val lobbyFragmentTransaction = supportFragmentManager.beginTransaction()
        lobbyFragmentTransaction.add(R.id.fragmentcontainer, lobbyFragment)
        lobbyFragmentTransaction.commit()

        RxJavaPlugins.setErrorHandler {
            Log.i("Error in Activity", it.cause?.message)
            it.cause?.message?.let { it1 -> Snackbar.make(this.findViewById(android.R.id.content), it1, 5000).show() }
        }

        mapOf(
            LEFT_GOAL_GPIO to { lobby.scoreLeft++ },
            RIGHT_GOAL_GPIO to { lobby.scoreRight++ }
        ).forEach { gpio, callback ->
            peripheralManager.open(gpio)
                .debounce(5000, SECONDS)
                .autoDisposable(this.scope())
                .subscribe(ScoreUseCase(kickprotocol, lobby, callback))
        }

        kickprotocol.advertise("Smartsquare HQ Kicker")
            .autoDisposable(this.scope())
            .subscribe()
        kickprotocol.connectionEvents
            .filter { it is Connected }
            .autoDisposable(this.scope())
            .subscribe(connectUseCase)

        kickprotocol.createGameMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(createGameUseCase)
        createGameUseCase.onGameCreation {
            with(supportFragmentManager.beginTransaction()) {
                replace(R.id.fragmentcontainer, MatchmakingFragment()).also { commit() }
            }
        }

        kickprotocol.joinLobbyMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(joinLobbyUseCase)

        kickprotocol.startGameMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(startGameUseCase)
        kickprotocol.leaveLobbyMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(leaveLobbyUseCase)
    }

    override fun onStop() {
        kickprotocol.stop()
        super.onStop()
    }
}
