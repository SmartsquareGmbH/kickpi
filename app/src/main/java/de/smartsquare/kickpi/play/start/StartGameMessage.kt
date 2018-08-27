package de.smartsquare.kickpi.play.start

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class StartGameMessage(val ownersDeviceId: String)