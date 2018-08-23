package de.smartsquare.kickpi

import de.smartsquare.kickpi.nearby.StartGameMessage

class StartIdleEvent
data class StartGameEvent(val startGameMessage: StartGameMessage)
class EndGameEvent