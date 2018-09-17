package de.smartsquare.kickpi

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.android.things.pio.PeripheralManager
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import de.smartsquare.kickpi.BuildConfig.LEFT_GOAL_GPIO
import de.smartsquare.kickpi.BuildConfig.RIGHT_GOAL_GPIO
import de.smartsquare.kickpi.domain.LobbyViewModel
import de.smartsquare.kickpi.domain.Position
import de.smartsquare.kickpi.domain.State
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
import de.smartsquare.kickprotocol.ConnectionEvent.Disconnected
import de.smartsquare.kickprotocol.Kickprotocol
import de.smartsquare.kickprotocol.filterMessages
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import kotterknife.bindView
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.koin.android.ext.android.inject
import org.koin.android.scope.ext.android.bindScope
import org.koin.android.scope.ext.android.getOrCreateScope
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.concurrent.TimeUnit.SECONDS

class MainActivity : AppCompatActivity(), AnkoLogger {

    companion object {
        private const val SNACKBAR_DURATION_IN_MS = 5000
        private const val SKIP_NEW_GOAL_DURATION = 5L
    }

    private val kickprotocol: Kickprotocol by inject() { parametersOf(this) }
    private val gameRepository: GameRepository by inject() { parametersOf(this) }
    private val peripheralManager by inject<PeripheralManager>()
    private val endpoints by inject<EndpointStore>()
    private val authorizationRepository by inject<AuthorizationRepository>()

    private val root by bindView<ViewGroup>(android.R.id.content)
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

        observeViewModel()
        subscribeToRxJavaErrors()
        subscribeToKickprotocol()
        subscribeTo(LEFT_GOAL_GPIO) { lobbyViewModel.score(Position.LEFT) }
        subscribeTo(RIGHT_GOAL_GPIO) { lobbyViewModel.score(Position.RIGHT) }
        showLobbyFragment()
    }

    private fun observeViewModel() {
        topThreeViewModel.firstPlace.observe(this, Observer(goldPlayer::setText))
        topThreeViewModel.firstPlace.observe(this, goldIcon.showIfContentAvailable())
        topThreeViewModel.secondPlace.observe(this, Observer(silverPlayer::setText))
        topThreeViewModel.secondPlace.observe(this, silverIcon.showIfContentAvailable())
        topThreeViewModel.thirdPlace.observe(this, Observer(bronzePlayer::setText))
        topThreeViewModel.thirdPlace.observe(this, bronzeIcon.showIfContentAvailable())

        lobbyViewModel.state.observe(this, Observer { state ->
            val fragment = when (state) {
                State.Matchmaking -> MatchmakingFragment()
                State.Playing -> ScoreFragment()
                else -> LobbyFragment()
            }
            with(supportFragmentManager.beginTransaction()) {
                replace(R.id.fragmentcontainer, fragment).also { commitNow() }
            }
        })
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun View.showIfContentAvailable() =
        Observer<String> {
            if (it.isNullOrBlank().not()) {
                this.visibility = VISIBLE
            }
        }

    private fun subscribeToRxJavaErrors() {
        RxJavaPlugins.setErrorHandler { wrappedError ->
            wrappedError.cause?.message
                ?.also { info { it } }
                ?.also { Snackbar.make(root, it, SNACKBAR_DURATION_IN_MS).show() }
        }
    }

    private fun subscribeToKickprotocol() {
        kickprotocol.advertise("Smartsquare HQ Kicker")
            .subscribeOn(Schedulers.io())
            .doOnError { error { it } }
            .doOnComplete { info { "Advertising started" } }
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe()

        kickprotocol.connectionEvents
            .subscribeOn(Schedulers.io())
            .filter { it is Connected }
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe(ConnectUseCase(kickprotocol, lobbyViewModel, this))

        kickprotocol.connectionEvents
            .subscribeOn(Schedulers.io())
            .filter { it is Disconnected }
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe { Snackbar.make(root, "${it.endpointId} disconnected", SNACKBAR_DURATION_IN_MS).show() }

        kickprotocol.createGameMessageEvents
            .subscribeOn(Schedulers.io())
            .filterMessages()
            .filterAuthenticatedCreateGameMessages(authorizationRepository)
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe(CreateGameUseCase(kickprotocol, lobbyViewModel, endpoints))

        kickprotocol.joinLobbyMessageEvents
            .subscribeOn(Schedulers.io())
            .filterMessages()
            .filterAuthenticatedJoinLobbyMessages(authorizationRepository)
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe(JoinLobbyUseCase(kickprotocol, endpoints, lobbyViewModel))

        kickprotocol.startGameMessageEvents
            .subscribeOn(Schedulers.io())
            .filterMessages()
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe(StartGameUseCase(kickprotocol, endpoints, lobbyViewModel))

        kickprotocol.leaveLobbyMessageEvents
            .subscribeOn(Schedulers.io())
            .filterMessages()
            .observeOn(AndroidSchedulers.mainThread())
            .autoDisposable(this.scope())
            .subscribe(LeaveLobbyUseCase(kickprotocol, endpoints, lobbyViewModel))
    }

    private fun subscribeTo(gpio: String, callback: () -> Unit) {
        peripheralManager.open(gpio)
            .throttleFirst(SKIP_NEW_GOAL_DURATION, SECONDS, Schedulers.computation())
            .autoDisposable(this.scope())
            .subscribe(ScoreUseCase(kickprotocol, lobbyViewModel, callback, gameRepository))
    }

    private fun showLobbyFragment() {
        with(supportFragmentManager.beginTransaction()) {
            add(R.id.fragmentcontainer, LobbyFragment()).also { commitNow() }
        }
    }

    override fun onStop() {
        kickprotocol.stop()
        super.onStop()
    }
}
