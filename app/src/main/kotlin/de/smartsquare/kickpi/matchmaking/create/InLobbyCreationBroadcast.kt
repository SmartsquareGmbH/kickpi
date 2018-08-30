package de.smartsquare.kickpi.matchmaking.create

import com.squareup.moshi.JsonClass
import de.smartsquare.kickpi.Lobby

@JsonClass(generateAdapter = true)
data class InLobbyCreationBroadcast(val lobby: Lobby)