package de.smartsquare.kickpi.leave

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaveLobbyMessage(val playerName: String, val playersDeviceId: String)
