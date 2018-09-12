package de.smartsquare.kickpi

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import de.smartsquare.kickpi.gameserver.Position
import de.smartsquare.kickprotocol.Kickprotocol
import org.koin.android.ext.android.inject
import de.smartsquare.kickprotocol.ConnectionEvent.Connected
import de.smartsquare.kickprotocol.message.IdleMessage
import org.koin.core.parameter.parametersOf
import de.smartsquare.kickpi.gameserver.State.Matchmaking
import de.smartsquare.kickpi.gameserver.State.Idle
import de.smartsquare.kickpi.idle.LobbyFragment
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import de.smartsquare.kickprotocol.message.PlayingMessage
import io.reactivex.plugins.RxJavaPlugins

class MainActivity : AppCompatActivity() {

    private val kickprotocol: Kickprotocol by inject() { parametersOf(this) }
    private val lobby: KickPiLobby by inject()

    private val endpoints = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val lobbyFragment = LobbyFragment()
        val lobbyFragmentTransaction = supportFragmentManager.beginTransaction()
        lobbyFragmentTransaction.add(R.id.fragmentcontainer, lobbyFragment)
        lobbyFragmentTransaction.commit()

        RxJavaPlugins.setErrorHandler {
            Log.i("Error in Activity", it.cause?.message)
            it.cause?.message?.let { it1 -> Snackbar.make(this.findViewById(android.R.id.content), it1, 5000).show() }
        }

        kickprotocol.advertise("Smartsquare HQ Kicker")
            .autoDisposable(this.scope())
            .subscribe()

        kickprotocol.connectionEvents
            .filter { it is Connected }
            .retry()
            .autoDisposable(this.scope())
            .subscribe {
                val message = when {
                    lobby currentlyIn Idle -> IdleMessage()
                    lobby currentlyIn Matchmaking -> MatchmakingMessage(lobby.toKickprotocolLobby())
                    else -> PlayingMessage(lobby.toKickprotocolLobby())
                }

                kickprotocol.sendAndAwait(it.endpointId, message)
                    .autoDisposable(this.scope())
                    .subscribe()
            }


        kickprotocol.createGameMessageEvents
            .retry()
            .autoDisposable(this.scope())
            .subscribe { kickprotocolMessage ->
                endpoints.put(kickprotocolMessage.endpointId, kickprotocolMessage.message.username)
                lobby.startMatchmaking(lobbyOwner = kickprotocolMessage.message.username, lobbyName = "Haus Dejavu")

                kickprotocol.broadcastAndAwait(MatchmakingMessage(lobby.toKickprotocolLobby()))
                    .subscribe()


            }

        kickprotocol.joinLobbyMessageEvents
            .retry()
            .autoDisposable(this.scope())
            .subscribe { kickprotocolMessageWithEndpoint ->
                endpoints.put(kickprotocolMessageWithEndpoint.endpointId, kickprotocolMessageWithEndpoint.message.username)

                kickprotocolMessageWithEndpoint.message.position
                    .let { Position.valueOf(it.name) }
                    .also { lobby.join(position = it, name = kickprotocolMessageWithEndpoint.message.username) }

                kickprotocol.broadcastAndAwait(MatchmakingMessage(lobby.toKickprotocolLobby()))
                    .subscribe()
            }

        kickprotocol.startGameMessageEvents
            .retry()
            .autoDisposable(this.scope())
            .subscribe { kickprotocolMessageWithEndpoint ->
                endpoints[kickprotocolMessageWithEndpoint.endpointId]
                    ?.let { lobby.startGame(it) }
                    ?.also {
                        kickprotocol.broadcastAndAwait(PlayingMessage(lobby.toKickprotocolLobby()))
                            .subscribe()
                    }
                    ?.also {
                        setContentView(R.layout.fragment_score)

                        val scoreLeftTeam: TextView = findViewById(R.id.scoreLeft)
                        val scoreRightTeam: TextView = findViewById(R.id.scoreRight)
                        scoreLeftTeam.text = lobby.scoreLeft.toString()
                        scoreRightTeam.text = lobby.scoreRight.toString()
                    }
            }

        kickprotocol.leaveLobbyMessageEvents
            .retry()
            .autoDisposable(this.scope())
            .subscribe { kickprotocolMessageWithEndpoint ->
                endpoints[kickprotocolMessageWithEndpoint.endpointId]
                    ?.let { lobby.leave(it) }
                    ?.let {
                        when {
                            lobby currentlyIn Idle -> IdleMessage()
                            else -> MatchmakingMessage(lobby.toKickprotocolLobby())
                        }
                    }
                    ?.also {
                        kickprotocol.broadcastAndAwait(it).subscribe()
                    }
                    ?.also {
                        val firstPlayerLeft: TextView = findViewById(R.id.firstPlayerLeft)
                        val firstPlayerRight: TextView = findViewById(R.id.firstPlayerRight)

                        lobby.rightTeam.getOrNull(0)?.also { firstPlayerRight.text = it }
                        lobby.leftTeam.getOrNull(0)?.also { firstPlayerLeft.text = it }
                    }
            }
    }

    override fun onStop() {
        kickprotocol.stop()
        super.onStop()
    }
}
