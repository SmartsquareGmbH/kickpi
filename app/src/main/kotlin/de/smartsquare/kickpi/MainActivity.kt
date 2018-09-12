package de.smartsquare.kickpi

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import de.smartsquare.kickpi.gameserver.Lobby
import de.smartsquare.kickpi.gameserver.Position
import de.smartsquare.kickpi.gameserver.State
import de.smartsquare.kickprotocol.Kickprotocol
import org.koin.android.ext.android.inject
import de.smartsquare.kickprotocol.ConnectionEvent.Connected
import de.smartsquare.kickprotocol.message.IdleMessage
import org.koin.core.parameter.parametersOf
import de.smartsquare.kickpi.gameserver.State.Matchmaking
import de.smartsquare.kickpi.gameserver.State.Playing
import de.smartsquare.kickpi.gameserver.State.Idle
import de.smartsquare.kickprotocol.message.MatchmakingMessage
import de.smartsquare.kickprotocol.message.PlayingMessage
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins

class MainActivity : AppCompatActivity() {

    private val kickprotocol: Kickprotocol by inject() { parametersOf(this) }
    private val lobby: KickPiLobby by inject()

    private val endpoints = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        RxJavaPlugins.setErrorHandler { error ->
            if (error is OnErrorNotImplementedException) {
                error.cause?.message
            } else {
                error.message
            }?.let {
                Log.i("Error in Activity", it)
                Snackbar.make(this.findViewById(android.R.id.content), it, 5000).show()
            }
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

                setContentView(R.layout.lobby)

                val firstPlayerLeft: TextView = findViewById(R.id.firstPlayerLeft)
                val secondPlayerLeft: TextView = findViewById(R.id.scndPlayerLeft)
                val firstPlayerRight: TextView = findViewById(R.id.firstPlayerRight)
                val secondPlayerRight: TextView = findViewById(R.id.scndPlayerRight)

                lobby.rightTeam.getOrNull(0)?.also { firstPlayerRight.text = it }
                lobby.rightTeam.getOrNull(1)?.also { secondPlayerRight.text = it }
                lobby.leftTeam.getOrNull(0)?.also { firstPlayerLeft.text = it }
                lobby.leftTeam.getOrNull(1)?.also { secondPlayerLeft.text = it }
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

                val firstPlayerLeft: TextView = findViewById(R.id.firstPlayerLeft)
                val secondPlayerLeft: TextView = findViewById(R.id.scndPlayerLeft)
                val firstPlayerRight: TextView = findViewById(R.id.firstPlayerRight)
                val secondPlayerRight: TextView = findViewById(R.id.scndPlayerRight)

                lobby.rightTeam.getOrNull(0)?.also { firstPlayerRight.text = it }
                lobby.rightTeam.getOrNull(1)?.also { secondPlayerRight.text = it }
                lobby.leftTeam.getOrNull(0)?.also { firstPlayerLeft.text = it }
                lobby.leftTeam.getOrNull(1)?.also { secondPlayerLeft.text = it }
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
                        setContentView(R.layout.score)

                        val scoreLeftTeam: TextView = findViewById(R.id.scoreLeft)
                        val scoreRightTeam: TextView = findViewById(R.id.scoreRight)
                        scoreLeftTeam.text = lobby.scoreLeft.toString()
                        scoreRightTeam.text = lobby.scoreRight.toString()
                    }
            }
    }

    override fun onStop() {
        kickprotocol.stop()
        super.onStop()
    }
}
