package de.smartsquare.kickpi.navbar

import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

class FluentTopTenAccessSpecification {

    @Test
    fun `get the best player from the top ten in a fluent way`() {
        val players = listOf(
            Player("ruby", 12, 20),
            Player("deen", 1, 0)
        )

        players.getTop(1).playerNameOrEmptyString() shouldEqual "ruby"
    }
}
