package de.smartsquare.kickpi.ioc

import okhttp3.HttpUrl
import javax.inject.Inject

class Context @Inject constructor() {
    var scoreLeft: HttpUrl? = null
    var scoreRight: HttpUrl? = null
    var spectate: HttpUrl? = null
}
