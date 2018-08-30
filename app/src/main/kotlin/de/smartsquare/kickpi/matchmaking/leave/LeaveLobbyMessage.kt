package de.smartsquare.kickpi.matchmaking.leave

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveLobbyMessage(val playerName: String, val playerDeviceId: String)
