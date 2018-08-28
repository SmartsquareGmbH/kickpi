package de.smartsquare.kickpi.play.broadcast

import com.squareup.moshi.JsonClass
import de.smartsquare.kickpi.Lobby

@JsonClass(generateAdapter = true)
data class InGameBroadcast(val lobby: Lobby)