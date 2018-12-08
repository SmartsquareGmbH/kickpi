package de.smartsquare.kickpi.domain

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import de.smartsquare.kickpi.BuildConfig.SCORE_TO_FINISH_GAME
import de.smartsquare.kickpi.NonNullLiveData
import de.smartsquare.kickpi.domain.Position.LEFT
import de.smartsquare.kickpi.domain.Position.RIGHT
import de.smartsquare.kickpi.domain.State.Idle
import de.smartsquare.kickpi.domain.State.Matchmaking
import de.smartsquare.kickpi.domain.State.Playing

class LobbyViewModel : ViewModel() {

    val name = MutableLiveData<String>()
    val owner = MutableLiveData<String>()
    val state = MutableLiveData<State>().apply { postValue(Idle) }
    val leftTeam = NonNullLiveData<List<String>>(emptyList())
    val rightTeam = NonNullLiveData<List<String>>(emptyList())
    val scoreLeft = NonNullLiveData(0)
    val scoreRight = NonNullLiveData(0)

    fun startMatchmaking(lobbyOwner: String, lobbyName: String) {
        if (lobbyOwner.trim().isEmpty() || lobbyName.trim().isEmpty()) {
            throw IllegalArgumentException("Lobby owner and name should not be blank.")
        }
        if (this currentlyIn Matchmaking || this currentlyIn Playing) {
            throw IllegalStateException("Unable to create lobby in state ${state.value}.")
        }

        this.state.value = Matchmaking
        this.owner.value = lobbyOwner
        this.leftTeam.value = listOf(lobbyOwner)
        this.name.value = lobbyName
    }

    @Suppress("ThrowsCount")
    fun join(position: Position, name: String) {
        if (this currentlyIn Idle || this currentlyIn Playing) {
            throw IllegalStateException("$name tried to join the lobby but it is currently in ${state.value}.")
        }
        if (name.trim().isEmpty()) {
            throw IllegalArgumentException("Player name should not be blank.")
        }

        with(if (position == RIGHT) rightTeam else leftTeam) {
            if (value.size > 1) {
                throw TeamIsFullException("$name tried to join the game but the team was already full.")
            }
            if (rightTeam.value.contains(name).or(leftTeam.value.contains(name))) {
                throw PlayerAlreadyInGameException("$name tried to join the game twice.")
            }

            value += name
        }
    }

    fun leave(name: String) {
        if (this currentlyIn Idle || this currentlyIn Playing) {
            throw IllegalStateException("$name tried to leave lobby but it is currently in ${state.value}.")
        }
        if (name.trim().isEmpty()) {
            throw IllegalArgumentException("Player name should not be blank")
        }

        val allPlayers = leftTeam.value + rightTeam.value
        if (allPlayers == listOf(name)) {
            state.value = Idle
            owner.value = null
        } else if (name.equals(owner.value)) {
            val nextOwner = (leftTeam.value + rightTeam.value - name).first()
            owner.value = nextOwner
        }

        rightTeam.value -= name
        leftTeam.value -= name
    }

    fun startGame(name: String) {
        if (leftTeam.value.isEmpty().or(rightTeam.value.isEmpty())) {
            throw IllegalStateException("A game cannot begin without players.")
        }
        if ((name == owner.value).not()) {
            throw UnauthorizedException("$name tried to start the game but the owner is ${owner.value}.")
        }

        state.value = Playing
    }

    fun score(position: Position) {
        if (this currentlyIn Idle || this currentlyIn Matchmaking) {
            throw IllegalStateException("Unable to score a goal in state ${state.value}.")
        }

        when (position) {
            LEFT -> scoreLeft.value++
            RIGHT -> scoreRight.value++
        }

        if (scoreLeft.value == SCORE_TO_FINISH_GAME.toInt() || scoreRight.value == SCORE_TO_FINISH_GAME.toInt()) {
            this.state.value = Idle
        }
    }

    fun reset() {
        this.scoreRight.value = 0
        this.scoreLeft.value = 0
        this.leftTeam.value = emptyList()
        this.rightTeam.value = emptyList()
        this.owner.value = null
    }

    infix fun currentlyIn(state: State) = this.state.value == state
}
