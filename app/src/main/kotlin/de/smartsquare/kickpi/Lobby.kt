package de.smartsquare.kickpi

data class Lobby(
    val owner: String,
    val leftTeam: List<String>,
    val scoreLeftTeam: Int,
    val rightTeam: List<String>,
    val scoreRightTeam: Int
) {
    constructor(owner: String) : this(owner, listOf(owner), 0, emptyList(), 0)
    constructor(owner: String, leftTeamExceptOwner: List<String>, rightTeam: List<String>) : this(owner, leftTeamExceptOwner + owner, 0, rightTeam, 0)

    fun isLastPlayer(playerName: String) = this.leftTeam.equals(listOf(playerName)).and(this.rightTeam.isEmpty())
        .or(this.rightTeam.equals(listOf(playerName)).and(this.leftTeam.isEmpty()))

    fun contains(playerName: String) = this.leftTeam.contains(playerName).or(this.rightTeam.contains(playerName))
}
