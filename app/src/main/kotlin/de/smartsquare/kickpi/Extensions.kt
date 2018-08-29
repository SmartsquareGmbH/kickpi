@file:Suppress("NOTHING_TO_INLINE")

package de.smartsquare.kickpi

import com.google.android.gms.nearby.messages.MessageFilter
import com.google.android.gms.nearby.messages.MessageListener
import com.google.android.gms.nearby.messages.MessagesClient
import com.google.android.gms.nearby.messages.SubscribeOptions
import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.join.NewPlayerJoinedEvent
import de.smartsquare.kickpi.leave.PlayerLeavedEvent
import de.smartsquare.kickpi.play.score.GameFinishedEvent
import de.smartsquare.kickpi.play.score.GoalScoredEvent
import de.smartsquare.kickpi.play.start.GameStartedEvent
import org.greenrobot.eventbus.EventBus

inline fun String.throwIllegalArgumentExceptionIfBlank() {
    if (this.isBlank()) throw IllegalArgumentException()
}

inline fun EventBus.removeStickyModifiedLobbyEvent() =
    listOf(
        NewPlayerJoinedEvent::class.java,
        LobbyCreatedEvent::class.java,
        PlayerLeavedEvent::class.java
    )
        .mapNotNull { this.removeStickyEvent(it) }
        .lastOrNull()
        ?.lobby

inline fun EventBus.getLastModifiedLobby() =
    listOf(
        NewPlayerJoinedEvent::class.java,
        LobbyCreatedEvent::class.java,
        PlayerLeavedEvent::class.java
    ).mapNotNull { this.getStickyEvent(it) }
        .map { it.lobby }
        .lastOrNull()

inline fun EventBus.isGameInProgress() =
    listOf(
        NewPlayerJoinedEvent::class.java,
        LobbyCreatedEvent::class.java,
        PlayerLeavedEvent::class.java,
        GameStartedEvent::class.java,
        GoalScoredEvent::class.java
    ).map { this.getStickyEvent(it) }
        .any { it != null }

inline fun EventBus.removeStickyModifiedGameEvent() =
    listOf(
        GameStartedEvent::class.java,
        GoalScoredEvent::class.java,
        GameFinishedEvent::class.java
    ).mapNotNull { this.removeStickyEvent(it) }
        .map { it.lobby }
        .lastOrNull()

inline fun MessagesClient.subscribeOnType(listener: MessageListener, type: String) {
    MessageFilter.Builder().includeNamespacedType("de.smartsquare.kickpi", type).build()
        .let { SubscribeOptions.Builder().setFilter(it).build() }
        .also {
            this.subscribe(listener, it)
        }
}
