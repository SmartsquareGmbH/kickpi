package de.smartsquare.kickpi.ioc

import okhttp3.HttpUrl
import javax.inject.Inject

class Kickchain @Inject constructor() {
    private var scoreLeft: HttpUrl? = null
    private var scoreRight: HttpUrl? = null
    private var spectate: HttpUrl? = null

    val isIngame get() = scoreLeft != null && scoreRight != null && spectate != null

    fun startGame(scoreLeft: HttpUrl, scoreRight: HttpUrl, spectate: HttpUrl) {
        this.scoreLeft = scoreLeft
        this.scoreRight = scoreRight
        this.spectate = spectate
    }

    fun endGame() {
        this.scoreLeft = null
        this.scoreRight = null
        this.spectate = null
    }
}
