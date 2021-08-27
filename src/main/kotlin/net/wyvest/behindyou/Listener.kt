package net.wyvest.behindyou

import net.minecraft.client.settings.GameSettings
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.wyvest.behindyou.BehindYou.keybind
import net.wyvest.behindyou.BehindYou.mc
import net.wyvest.behindyou.config.BehindYouConfig

object Listener {

    private var prevState = false

    /**
     * Keybind hold / toggle logic stolen from PerspectiveModv4 under MIT License
     * https://github.com/DJtheRedstoner/PerspectiveModv4/blob/28fb656d8b285f3da2227dd859735392902a31ef/LICENSE
     */
    @SubscribeEvent
    fun onKeyInput(event: TickEvent.RenderTickEvent) {
        if (BehindYouConfig.toggled) {
            val isDown = GameSettings.isKeyDown(keybind)
            if (isDown != prevState && mc.currentScreen == null && mc.theWorld != null && mc.thePlayer != null) {
                prevState = isDown
                if (isDown) {
                    when (mc.gameSettings.thirdPersonView) {
                        0 -> {
                            mc.gameSettings.thirdPersonView = 2
                            mc.entityRenderer.loadEntityShader(null)
                        }
                        1 -> {
                            mc.gameSettings.thirdPersonView = 2
                        }
                        2 -> {
                            mc.gameSettings.thirdPersonView = 0
                            mc.entityRenderer.loadEntityShader(mc.renderViewEntity)
                        }
                    }
                    mc.renderGlobal.setDisplayListEntitiesDirty()
                } else if (BehindYouConfig.hold) {
                    mc.gameSettings.thirdPersonView = 0
                    mc.entityRenderer.loadEntityShader(mc.renderViewEntity)
                    mc.renderGlobal.setDisplayListEntitiesDirty()
                }
            }
        }
    }

}