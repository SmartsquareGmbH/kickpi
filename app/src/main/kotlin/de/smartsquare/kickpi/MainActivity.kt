package de.smartsquare.kickpi

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.play.score.GameFinishedEvent
import de.smartsquare.kickpi.play.start.GameStartedEvent
import kotlinx.android.synthetic.main.score.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape.CIRCLE
import nl.dionsegijn.konfetti.models.Shape.RECT
import nl.dionsegijn.konfetti.models.Size

class MainActivity : Activity() {
    private val firstPlayerLeft: TextView = findViewById(R.id.firstPlayerLeft)
    private val scndPlayerLeft: TextView = findViewById(R.id.scndPlayerLeft)
    private val firstPlayerRight: TextView = findViewById(R.id.firstPlayerRight)
    private val scndPlayerRight: TextView = findViewById(R.id.scndPlayerRight)
    private val scoreLeftTeam: TextView = findViewById(R.id.scoreLeft)
    private val scoreRightTeam: TextView = findViewById(R.id.scoreRight)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    fun displayLobbyOnLobbyCreated(lobbyCreatedEvent: LobbyCreatedEvent) {
        setContentView(R.layout.lobby)

        firstPlayerLeft.text = lobbyCreatedEvent.lobby.leftTeam[0]
        scndPlayerLeft.text = lobbyCreatedEvent.lobby.leftTeam[1]
        firstPlayerRight.text = lobbyCreatedEvent.lobby.rightTeam[0]
        scndPlayerRight.text = lobbyCreatedEvent.lobby.rightTeam[1]
    }

    fun displayScoreOnGameStarted(gameStartedEvent: GameStartedEvent) {
        setContentView(R.layout.score)

        scoreLeftTeam.text = gameStartedEvent.lobby.scoreLeftTeam.toString()
        scoreRightTeam.text = gameStartedEvent.lobby.scoreRightTeam.toString()
    }

    fun confettiOnGameFinished(gameFinishedEvent: GameFinishedEvent) {
        val confettiContainer = findViewById<KonfettiView>(R.id.viewKonfetti)

        confettiContainer.build()
                .addColors(Color.RED, Color.argb(0, 204, 0, 51), Color.argb(0, 204, 0, 0))
                .setDirection(0.0, 350.0)
                .setSpeed(3f, 8f)
                .setFadeOutEnabled(true)
                .setTimeToLive(10000)
                .addShapes(RECT, CIRCLE)
                .addSizes(Size(10))
                .setPosition(viewKonfetti.width - 0f, viewKonfetti.height - 0f)
                .stream(100, 5000L)
    }
}
