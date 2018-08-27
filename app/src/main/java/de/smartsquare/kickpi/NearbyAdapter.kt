package de.smartsquare.kickpi

import com.google.android.gms.nearby.messages.Message
import com.squareup.moshi.Moshi

class NearbyAdapter {

    companion object {

        fun <T : Any?> fromNearby(message: Message, target: Class<T>): T {
            return Moshi.Builder().build().adapter(target)
                    .fromJson(message.content.toString(Charsets.UTF_8))
                    ?: throw IllegalArgumentException("Invalid message: $message")
        }

        fun toNearby(message: Any, type: String): Message {
            val json = Moshi.Builder().build().adapter(message.javaClass).toJson(message)

            return Message(json.toByteArray(), type)
        }
    }
}