package de.smartsquare.kickpi

data class Lobby(
    val owner: String,
    val leftTeam: List<String>,
    val scoreLeftTeam: Int,
    val rightTeam: List<String>,
    val scoreRightTeam: Int
) {
    constructor(owner: String) : this(owner, listOf(owner), 0, emptyList(), 0)
}
