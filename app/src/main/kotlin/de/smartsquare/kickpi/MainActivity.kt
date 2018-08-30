package de.smartsquare.kickpi

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import de.smartsquare.kickpi.join.NewPlayerJoinedEvent
import de.smartsquare.kickpi.play.score.GameFinishedEvent
import de.smartsquare.kickpi.play.start.GameStartedEvent
import kotlinx.android.synthetic.main.score.*
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape.CIRCLE
import nl.dionsegijn.konfetti.models.Shape.RECT
import nl.dionsegijn.konfetti.models.Size
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this) // TODO: register the activity in the application?

        setContentView(R.layout.activity_main)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayLobbyOnLobbyCreated(lobbyCreatedEvent: NewPlayerJoinedEvent) {
        setContentView(R.layout.lobby)

        val firstPlayerLeft: TextView = findViewById(R.id.firstPlayerLeft)
        val secondPlayerLeft: TextView = findViewById(R.id.scndPlayerLeft)
        val firstPlayerRight: TextView = findViewById(R.id.firstPlayerRight)
        val secondPlayerRight: TextView = findViewById(R.id.scndPlayerRight)

        lobbyCreatedEvent.lobby.rightTeam.getOrNull(0)?.also { firstPlayerRight.text = it }
        lobbyCreatedEvent.lobby.rightTeam.getOrNull(1)?.also { secondPlayerRight.text = it }
        lobbyCreatedEvent.lobby.leftTeam.getOrNull(1)?.also { firstPlayerLeft.text = it }
        lobbyCreatedEvent.lobby.leftTeam.getOrNull(1)?.also { secondPlayerLeft.text = it }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun displayScoreOnGameStarted(gameStartedEvent: GameStartedEvent) {
        setContentView(R.layout.score)

        val scoreLeftTeam: TextView = findViewById(R.id.scoreLeft)
        val scoreRightTeam: TextView = findViewById(R.id.scoreRight)
        scoreLeftTeam.text = gameStartedEvent.lobby.scoreLeftTeam.toString()
        scoreRightTeam.text = gameStartedEvent.lobby.scoreRightTeam.toString()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun confettiOnGameFinished(gameFinishedEvent: GameFinishedEvent) {
        val confettiContainer = findViewById<KonfettiView>(R.id.viewKonfetti)

        //TODO: display confetti in right or left upper corner
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
