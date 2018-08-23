package de.smartsquare.kickpi.nearby

import com.google.android.gms.nearby.messages.Message
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
class StartGameMessage(val kickwayURL : String) {

    companion object {

        fun fromNearbyMessage(message: Message): StartGameMessage {
            return Moshi.Builder().build().adapter(StartGameMessage::class.java)
                    .fromJson(message.content.toString(Charsets.UTF_8))
                    ?: throw IllegalArgumentException("Invalid message: $message")
        }
    }
}