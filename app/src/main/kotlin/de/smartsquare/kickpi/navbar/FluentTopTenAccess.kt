@file:Suppress("NOTHING_TO_INLINE")

package de.smartsquare.kickpi.navbar

/**
 * @param place 1-10
 * @return the name of the player at the n - 1 position or a empty string
 */
inline fun List<Player>.getTop(place: Int) = Mediator(place, this.map(Player::name))

class Mediator(private val place: Int, private val players: List<String>) {
    fun playerNameOrEmptyString() = players
        .getOrElse(place - 1) { "" }
}
