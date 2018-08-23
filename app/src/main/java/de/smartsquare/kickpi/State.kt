package de.smartsquare.kickpi

enum class State {
    IDLE, IN_GAME;

    companion object Game {
        var state: State = IDLE
        var kickwayURL : String? = null

        fun startGame(kickwayURL: String) {
            state = IN_GAME
            this.kickwayURL = kickwayURL
        }

        fun endGame() {
            state = IDLE
            this.kickwayURL = null
        }
    }
}
