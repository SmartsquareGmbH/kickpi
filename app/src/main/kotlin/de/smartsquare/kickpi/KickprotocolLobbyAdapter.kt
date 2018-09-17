package de.smartsquare.kickpi

import de.smartsquare.kickpi.domain.LobbyViewModel

typealias KickprotocolLobby = de.smartsquare.kickprotocol.Lobby

fun LobbyViewModel.toKickprotocolLobby() = KickprotocolLobby(
    this.owner.value ?: "",
    this.name.value ?: "",
    this.leftTeam.value,
    this.rightTeam.value,
    this.scoreLeft.value,
    this.scoreRight.value
)
