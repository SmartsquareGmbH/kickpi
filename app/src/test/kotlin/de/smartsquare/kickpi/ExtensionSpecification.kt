package de.smartsquare.kickpi

import org.amshove.kluent.shouldEqual
import org.greenrobot.eventbus.EventBus
import org.junit.jupiter.api.Test

class ExtensionSpecification {

    @Test fun `remove sticky modified lobby event without event present in bus`() {
        EventBus.getDefault().removeStickyModifiedLobbyEvent() shouldEqual null
    }
}
