package de.smartsquare.kickpi.matchmaking.join

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JoinLobbyMessage(val playerName: String, val playerDeviceId: String, val team: Team) {
    enum class Team {
        LEFT, RIGHT
    }
}
