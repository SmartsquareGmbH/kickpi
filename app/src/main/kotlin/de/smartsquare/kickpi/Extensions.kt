@file:Suppress("NOTHING_TO_INLINE")

package de.smartsquare.kickpi

import de.smartsquare.kickpi.create.LobbyCreatedEvent
import de.smartsquare.kickpi.join.NewPlayerJoinedEvent
import de.smartsquare.kickpi.leave.PlayerLeavedEvent
import org.greenrobot.eventbus.EventBus

inline fun String.throwIllegalArgumentExceptionIfBlank() {
    if (this.isBlank()) throw IllegalArgumentException()
}

inline fun EventBus.removeStickyModifiedLobbyEvent() =
    listOf(
        NewPlayerJoinedEvent::class.java,
        LobbyCreatedEvent::class.java,
        PlayerLeavedEvent::class.java
    ).map { this.removeStickyEvent(it) }
        .findLast { it != null }
        ?.lobby

inline fun EventBus.getLastModifiedLobby() =
    listOf(
        NewPlayerJoinedEvent::class.java,
        LobbyCreatedEvent::class.java,
        PlayerLeavedEvent::class.java
    ).map { this.getStickyEvent(it) }
        .findLast { it != null }
        ?.lobby