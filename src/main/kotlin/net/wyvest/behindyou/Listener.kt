package net.wyvest.behindyou

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.behindyou.config.BehindYouConfig

object Listener {

    @SubscribeEvent
    fun listen(e : TickEvent.ClientTickEvent) {
        if (e.phase == TickEvent.Phase.END) {
            if (BehindYouConfig.toggled) {
                if (BehindYou.mc.gameSettings.thirdPersonView == 1) {
                    ++BehindYou.mc.gameSettings.thirdPersonView
                }
            }
        }
    }

}