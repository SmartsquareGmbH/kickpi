package de.smartsquare.kickpi.nearby

import com.google.android.gms.nearby.messages.Message
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi

@JsonClass(generateAdapter = true)
data class IdleMessage(val name: String, val id: String) {

    val moshi = Moshi.Builder().build()

    fun toNearbyMessage(): Message {
        val thisAsJson = moshi.adapter(this.javaClass).toJson(this)

        return Message(thisAsJson.toByteArray(), MessageType.IDLE.name)
    }
}
