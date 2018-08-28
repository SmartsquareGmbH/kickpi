package de.smartsquare.kickpi

class UnauthorizedException : RuntimeException()
class DuplicateNameException : RuntimeException()
class TeamAlreadyFullException : RuntimeException()
class MissingOpponentsException : RuntimeException()
class MatchInProgressException : RuntimeException()