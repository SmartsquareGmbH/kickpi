package de.smartsquare.kickpi.play.during

import com.squareup.moshi.JsonClass
import de.smartsquare.kickpi.Lobby

@JsonClass(generateAdapter = true)
data class InGameBroadcast(val lobby: Lobby)