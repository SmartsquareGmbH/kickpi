package de.smartsquare.kickpi.play.save

import de.smartsquare.kickpi.Lobby
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class GameServiceSpecification {

    private val gameRepository = mockk<GameRepository>(relaxed = true)
    private val gameService = GameService(gameRepository)

    @Test fun `save game on finish`() {
        val lobby = Lobby(owner = "deen",
            leftTeam = listOf("deen", "saschar"), scoreLeftTeam = 2,
            rightTeam = listOf("ruby", "drs"), scoreRightTeam = 10
        )

        gameService.save(lobby)

        verify {
            gameRepository.save(Game(
                team1 = Game.Team(listOf("deen", "saschar")),
                team2 = Game.Team(listOf("ruby", "drs")),
                score = Game.Score(2, 10)
            ))
        }
    }
}