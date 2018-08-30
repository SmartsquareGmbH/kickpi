package de.smartsquare.kickpi.matchmaking.join

import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.LobbyModificationEvent

class NewPlayerJoinedEvent(lobby: Lobby) : LobbyModificationEvent(lobby)