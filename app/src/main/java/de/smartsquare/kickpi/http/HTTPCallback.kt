package de.smartsquare.kickpi.http

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class HTTPCallback : Callback {
    override fun onFailure(call: Call?, e: IOException?) {
        //TODO log and maybe display message toast
    }

    override fun onResponse(call: Call?, response: Response?) {
        //TODO alright?
    }
}