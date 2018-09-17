package de.smartsquare.kickpi

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView


import android.widget.TextView
import com.google.android.things.pio.PeripheralManager
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import de.smartsquare.kickpi.BuildConfig.LEFT_GOAL_GPIO
import de.smartsquare.kickpi.BuildConfig.RIGHT_GOAL_GPIO
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.gameserver.Position
import de.smartsquare.kickpi.gameserver.State
import de.smartsquare.kickpi.idle.ConnectUseCase
import de.smartsquare.kickpi.idle.CreateGameUseCase
import de.smartsquare.kickpi.idle.LobbyFragment
import de.smartsquare.kickpi.matchmaking.JoinLobbyUseCase
import de.smartsquare.kickpi.matchmaking.LeaveLobbyUseCase
import de.smartsquare.kickpi.matchmaking.MatchmakingFragment
import de.smartsquare.kickpi.matchmaking.StartGameUseCase
import de.smartsquare.kickpi.navbar.TopThreeViewModel
import de.smartsquare.kickpi.playing.GameRepository
import de.smartsquare.kickpi.playing.ScoreFragment
import de.smartsquare.kickpi.playing.ScoreUseCase
import de.smartsquare.kickprotocol.ConnectionEvent.Connected
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.filterMessages
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import org.koin.android.ext.android.inject
import org.koin.android.scope.ext.android.bindScope
import org.koin.android.scope.ext.android.getOrCreateScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit.SECONDS

class MainActivity : AppCompatActivity() {

    private inline fun View.showIfContentAvailable() =
        Observer<String> {
            if (it.isNullOrBlank().not()) {
                this.visibility = VISIBLE
            }
        }

    private val kickprotocol: Kickprotocol by inject() { parametersOf(this) }
    private val gameRepository: GameRepository by inject() { parametersOf(this) }
    private val peripheralManager by inject<PeripheralManager>()
    private val endpoints by inject<Endpoints>()

    private val goldPlayer by bindView<TextView>(R.id.goldPlayer)
    private val goldIcon by bindView<ImageView>(R.id.goldIcon)
    private val silverPlayer by bindView<TextView>(R.id.silverPlayer)
    private val silverIcon by bindView<ImageView>(R.id.silverIcon)
    private val bronzePlayer by bindView<TextView>(R.id.bronzePlayer)
    private val bronzeIcon by bindView<ImageView>(R.id.bronzeIcon)

    private val topThreeViewModel by viewModel<TopThreeViewModel>()
    private val lobbyViewModel by viewModel<LobbyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindScope(getOrCreateScope("activity"))

        initializeTopThree()

        val lobbyFragment = LobbyFragment()
        val lobbyFragmentTransaction = supportFragmentManager.beginTransaction()
        lobbyFragmentTransaction.add(R.id.fragmentcontainer, lobbyFragment)
        lobbyFragmentTransaction.commit()

        RxJavaPlugins.setErrorHandler {
            Log.i("Error in Activity", it.cause?.message)
            it.cause?.message?.let { it1 -> Snackbar.make(this.findViewById(android.R.id.content), it1, 5000).show() }
        }

        mapOf(
            LEFT_GOAL_GPIO to { lobbyViewModel.score(Position.LEFT) },
            RIGHT_GOAL_GPIO to { lobbyViewModel.score(Position.RIGHT) }
        ).forEach { gpio, callback ->
            peripheralManager.open(gpio)
                .throttleFirst(5, SECONDS, Schedulers.computation())
                .autoDisposable(this.scope())
                .subscribe(ScoreUseCase(kickprotocol, lobbyViewModel, callback, gameRepository))
        }

        kickprotocol.advertise("Smartsquare HQ Kicker")
            .autoDisposable(this.scope())
            .subscribe()
        kickprotocol.connectionEvents
            .filter { it is Connected }
            .autoDisposable(this.scope())
            .subscribe(ConnectUseCase(kickprotocol, lobbyViewModel, this))

        kickprotocol.createGameMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(CreateGameUseCase(kickprotocol, lobbyViewModel, endpoints))
        lobbyViewModel.state.observe(this, Observer {
            val fragment = when (it) {
                State.Matchmaking -> MatchmakingFragment()
                State.Playing -> ScoreFragment()
                else -> LobbyFragment()
            }
            with(supportFragmentManager.beginTransaction()) {
                replace(R.id.fragmentcontainer, fragment).also { commitNow() }
            }
        })

        kickprotocol.joinLobbyMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(JoinLobbyUseCase(kickprotocol, endpoints, lobbyViewModel))

        kickprotocol.startGameMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(StartGameUseCase(kickprotocol, endpoints, lobbyViewModel))
        kickprotocol.leaveLobbyMessageEvents
            .filterMessages()
            .autoDisposable(this.scope())
            .subscribe(LeaveLobbyUseCase(kickprotocol, endpoints, lobbyViewModel))

    }

    private fun initializeTopThree() {
        topThreeViewModel.firstPlace.observe(this, Observer(goldPlayer::setText))
        topThreeViewModel.firstPlace.observe(this, goldIcon.showIfContentAvailable())
        topThreeViewModel.secondPlace.observe(this, Observer(silverPlayer::setText))
        topThreeViewModel.secondPlace.observe(this, silverIcon.showIfContentAvailable())
        topThreeViewModel.thirdPlace.observe(this, Observer(bronzePlayer::setText))
        topThreeViewModel.thirdPlace.observe(this, bronzeIcon.showIfContentAvailable())
    }

    override fun onStop() {
        kickprotocol.stop()
        super.onStop()
    }
}
