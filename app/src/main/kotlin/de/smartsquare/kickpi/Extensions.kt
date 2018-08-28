package de.smartsquare.kickpi

@Suppress("NOTHING_TO_INLINE")
inline fun String.throwIllegalArgumentExceptionIfBlank() {
    if (this.isBlank()) throw IllegalArgumentException()
}