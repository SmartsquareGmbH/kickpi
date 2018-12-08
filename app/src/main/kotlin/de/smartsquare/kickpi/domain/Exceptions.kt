package de.smartsquare.kickpi.domain

abstract class GameServerException(override val message: String) : RuntimeException()
class TeamIsFullException(override val message: String) : GameServerException(message)
class UnauthorizedException(override val message: String) : GameServerException(message)
class PlayerAlreadyInGameException(override val message: String) : GameServerException(message)
