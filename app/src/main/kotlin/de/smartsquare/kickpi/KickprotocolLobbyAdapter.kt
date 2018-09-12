package de.smartsquare.kickpi

typealias KickPiLobby = de.smartsquare.kickpi.gameserver.Lobby
typealias KickprotocolLobby = de.smartsquare.kickprotocol.Lobby

fun KickPiLobby.toKickprotocolLobby() = KickprotocolLobby(
    this.owner.orElseGet { "" },
    this.name.orElseGet { "" },
    this.leftTeam,
    this.rightTeam,
    this.scoreLeft,
    this.scoreRight
)