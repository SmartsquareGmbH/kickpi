package de.smartsquare.kickpi

import de.smartsquare.kickpi.nearby.StartGameMessage
import okhttp3.HttpUrl

class StartIdleEvent
data class GoalEvent(val scoreURL: HttpUrl)
data class StartGameEvent(val startGameMessage: StartGameMessage)
class EndGameEvent