package de.smartsquare.kickpi

typealias KickPiLobby = de.smartsquare.kickpi.gameserver.Lobby
typealias KickprotocolLobby = de.smartsquare.kickprotocol.Lobby

fun KickPiLobby.toKickprotocolLobby() = KickprotocolLobby(
    this.owner.get(),
    this.name.get(),
    this.leftTeam,
    this.rightTeam,
    this.scoreLeft,
    this.scoreRight
)