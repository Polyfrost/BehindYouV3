package org.polyfrost.behindyou

import net.fabricmc.api.ClientModInitializer
import org.polyfrost.behindyou.client.BehindYouClient

class BehindYouEntrypoint : ClientModInitializer {
    override
    fun onInitializeClient() {
        BehindYouClient.initialize()
    }
}
