package de.smartsquare.kickpi.gameserver

import de.smartsquare.kickpi.gameserver.Position.*
import de.smartsquare.kickpi.gameserver.State.Idle
import de.smartsquare.kickpi.gameserver.State.Matchmaking
import de.smartsquare.kickpi.gameserver.State.Playing
import java.security.InvalidParameterException
import java.util.Optional
import javax.swing.text.html.Option

class Lobby {

    private var state = Idle
     var name = Optional.empty<String>()
        private set
    var owner = Optional.empty<String>()
        private set
    val leftTeam = mutableListOf<String>()
    val rightTeam = mutableListOf<String>()
    var scoreLeft = 0
    var scoreRight = 0

    fun startMatchmaking(lobbyOwner: String, lobbyName: String) {
        if (lobbyOwner.trim().isEmpty()) throw IllegalArgumentException("Lobby owner should not be blank.")
        if (lobbyName.trim().isEmpty()) throw IllegalArgumentException("Lobby name should not be blank.")
        if (this currentlyIn Matchmaking || this currentlyIn Playing) throw IllegalStateException("Unable to create lobby in state $state.")

        this.state = Matchmaking
        this.leftTeam.add(lobbyOwner)
        this.owner = Optional.of(lobbyOwner)
        this.name = Optional.of(lobbyName)
    }

    fun join(position: Position, name: String) {
        if (this currentlyIn Idle || this currentlyIn Playing) throw IllegalStateException("$name tried to join the lobby but it is currently in $state.")
        if (name.trim().isEmpty()) throw IllegalArgumentException("Player name should not be blank.")

        with(if (position == RIGHT) rightTeam else leftTeam) {
            if (size > 1) throw TeamIsFullException("$name tried to join the game but the team was already full.")
            if (rightTeam.contains(name).or(leftTeam.contains(name))) throw PlayerAlreadyInGameException("$name tried to join the game twice.")

            add(name)
        }
    }

    fun leave(name: String) {
            if (this currentlyIn Idle || this currentlyIn Playing) throw IllegalStateException("$name tried to leave lobby but it is currently in $state.")
        if (name.trim().isEmpty()) throw IllegalArgumentException("Player name should not be blank")

        val allPlayers = leftTeam + rightTeam
        if (allPlayers == listOf(name)) {
            state = Idle
            owner = Optional.empty()
        } else if (owner.get() == name) {
            val nextOwner = (leftTeam + rightTeam - name).first()
            owner = Optional.of(nextOwner)
        }

        rightTeam.remove(name).also { leftTeam.remove(name) }
    }

    fun startGame(name: String) {
        if (leftTeam.isEmpty().or(rightTeam.isEmpty())) throw IllegalStateException()
        if ((owner.get() == name).not()) throw UnauthorizedException("$name tried to start the game but the owner is ${owner.get()}.")

        state = Playing
    }

    fun score(position: Position) {
        if (this currentlyIn Idle || this currentlyIn Matchmaking) throw IllegalStateException("Unable to score a goal in state $state.")

        when (position) {
            LEFT -> scoreLeft++
            RIGHT -> scoreRight++
        }
    }

    infix fun currentlyIn(state: State) = this.state == state

}