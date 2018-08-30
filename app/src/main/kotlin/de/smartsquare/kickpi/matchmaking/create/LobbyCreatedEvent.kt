package de.smartsquare.kickpi.matchmaking.create

import de.smartsquare.kickpi.Lobby
import de.smartsquare.kickpi.LobbyModificationEvent

class LobbyCreatedEvent(lobby: Lobby) : LobbyModificationEvent(lobby)