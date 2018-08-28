package de.smartsquare.kickpi

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.google.android.gms.nearby.messages.MessageFilter
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.android.gms.nearby.messages.SubscribeOptions
import de.smartsquare.kickpi.create.CreateLobbyUseCase
import de.smartsquare.kickpi.idle.IdleUseCase
import de.smartsquare.kickpi.join.JoinLobbyUseCase
import kotlinx.android.synthetic.main.activity_main.viewKonfetti
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape.CIRCLE
import nl.dionsegijn.konfetti.models.Shape.RECT
import nl.dionsegijn.konfetti.models.Size
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class MainActivity : Activity() {

    @Inject lateinit var idleUseCase: IdleUseCase
    @Inject lateinit var messagesClient: MessagesClient
    @Inject lateinit var joinlobbyUseCase: JoinLobbyUseCase
    @Inject lateinit var createLobbyUseCase: CreateLobbyUseCase

    @Inject lateinit var eventBus: EventBus

    @Suppress("NOTHING_TO_INLINE")
    private inline fun MessagesClient.subscribeOnType(listener: MessageListener, type: String) {
        MessageFilter.Builder().includeNamespacedType("de.smartsquare.kickpi", type).build()
            .let { SubscribeOptions.Builder().setFilter(it).build() }
            .also {
                this.subscribe(listener, it)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerContainer.builder().activityModule(ActivityModule(this)).build().inject(this)

        eventBus.register(idleUseCase)

        messagesClient.subscribeOnType(joinlobbyUseCase, "JOIN_LOBBY")
        messagesClient.subscribeOnType(createLobbyUseCase, "CREATE_LOBBY")

        idleUseCase.publishIdleMessage()
    }

    fun confetto() {
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
