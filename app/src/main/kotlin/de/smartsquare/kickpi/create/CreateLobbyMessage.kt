package de.smartsquare.kickpi.create

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateLobbyMessage(val ownerName: String, val ownerDeviceId: String)