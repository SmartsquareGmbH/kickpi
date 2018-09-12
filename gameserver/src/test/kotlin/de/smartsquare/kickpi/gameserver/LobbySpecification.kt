package de.smartsquare.kickpi.gameserver

import org.junit.jupiter.api.Test
import de.smartsquare.kickpi.gameserver.State.Matchmaking
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.BeforeEach
import java.util.Optional
import kotlin.test.assertFailsWith
import de.smartsquare.kickpi.gameserver.Position.LEFT
import de.smartsquare.kickpi.gameserver.Position.RIGHT
import de.smartsquare.kickpi.gameserver.State.Idle
import de.smartsquare.kickpi.gameserver.State.Playing
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldNotBeInstanceOf

class LobbySpecification {

    lateinit var lobby: Lobby

    @BeforeEach
    fun refreshLobby() {
        this.lobby = Lobby()
    }

    @Test
    fun `lobby is in matchmaking after start matchmaking`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby currentlyIn Matchmaking shouldBe true
    }

    @Test
    fun `lobby name is present after starting a matchmaking`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.name shouldEqual Optional.of("Smartsquare HQ Tournament")
    }

    @Test
    fun `lobby owner is present after starting a matchmaking`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.owner shouldEqual Optional.of("deen")
    }

    @Test
    fun `lobby name cannot be empty`() {
        assertFailsWith<IllegalArgumentException> {
            lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "")
        }
    }

    @Test
    fun `lobby name cannot be blank`() {
        assertFailsWith<IllegalArgumentException> {
            lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = " ")
        }
    }

    @Test
    fun `starting a matchmaking is forbidden if currently in matchmaking`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<IllegalStateException>(message = "Unable to create lobby in state Matchmaking") {
            lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")
        }
    }

    @Test
    fun `lobby owner exists in the left team by default`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.leftTeam shouldEqual listOf("deen")
    }

    @Test
    fun `joining the left team`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(LEFT, "skonair")

        lobby.leftTeam shouldContain "skonair"
    }

    @Test
    fun `joining the right team`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(RIGHT, "skonair")

        lobby.rightTeam shouldContain "skonair"
    }

    @Test
    fun `joining the left team when already full`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(LEFT, "skonair")

        assertFailsWith<TeamIsFullException> {
            lobby.join(LEFT, "drs")
        }
    }

    @Test
    fun `joining the right team when already full`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(RIGHT, "skonair")
        lobby.join(RIGHT, "drs")

        assertFailsWith<TeamIsFullException> {
            lobby.join(RIGHT, "saschar")
        }
    }

    @Test
    fun `joining the left team twice`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<PlayerAlreadyInGameException> {
            lobby.join(LEFT, "deen")
        }
    }

    @Test
    fun `joining with empty name`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<IllegalArgumentException> {
            lobby.join(LEFT, "")
        }
    }

    @Test
    fun `joining with blank name`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<IllegalArgumentException> {
            lobby.join(LEFT, " ")
        }
    }

    @Test
    fun `joining the left and the right team with the same name`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<PlayerAlreadyInGameException> {
            lobby.join(RIGHT, "deen")
        }
    }

    @Test
    fun `leave lobby with blank name`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<IllegalArgumentException> {
            lobby.leave(" ")
        }
    }

    @Test
    fun `leave lobby with empty name`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<IllegalArgumentException> {
            lobby.leave("")
        }
    }

    @Test
    fun `leave lobby`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(RIGHT, "drs")
        lobby.leave("drs")

        lobby.rightTeam.size shouldEqual 0
    }

    @Test
    fun `leave lobby as owner`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(RIGHT, "drs")
        lobby.leave("deen")

        lobby.leftTeam.size shouldEqual 0
        lobby.owner shouldEqual Optional.of("drs")
    }

    @Test
    fun `leave lobby as last man standing`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.leave("deen")

        lobby currentlyIn Idle
        lobby.owner shouldEqual Optional.empty()
    }

    @Test
    fun `start game`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")
        lobby.join(RIGHT, "drs")

        lobby.startGame("deen")

        lobby currentlyIn Playing shouldEqual true
    }

    @Test
    fun `start game without opponent`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        assertFailsWith<IllegalStateException> {
            lobby.startGame("deen")
        }
    }

    @Test
    fun `start game not as lobby owner`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")

        lobby.join(RIGHT, "drs")

        assertFailsWith<UnauthorizedException> {
            lobby.startGame("drs")
        }
    }

    @Test
    fun `score left team`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")
        lobby.join(RIGHT, "drs")
        lobby.startGame("deen")

        lobby.score(LEFT)

        lobby.scoreLeft shouldEqual 1
    }

    @Test
    fun `score right team`() {
        lobby.startMatchmaking(lobbyOwner = "deen", lobbyName = "Smartsquare HQ Tournament")
        lobby.join(RIGHT, "drs")
        lobby.startGame("deen")

        lobby.score(RIGHT)

        lobby.scoreRight shouldEqual 1
    }



}