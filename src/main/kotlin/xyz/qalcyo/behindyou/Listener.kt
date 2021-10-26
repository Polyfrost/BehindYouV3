package xyz.qalcyo.behindyou

import net.minecraft.client.settings.GameSettings
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import xyz.qalcyo.behindyou.BehindYou.keybind
import xyz.qalcyo.behindyou.BehindYou.mc
import xyz.qalcyo.behindyou.config.BehindYouConfig

object Listener {

    private var prevState = false
    private var prevFOV = -1F

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
                            if (BehindYouConfig.changeFOV) {
                                prevFOV = mc.gameSettings.fovSetting
                                mc.gameSettings.fovSetting = BehindYouConfig.fovNumber.toFloat()
                            }
                        }
                        1 -> {
                            mc.gameSettings.thirdPersonView = 2
                            if (BehindYouConfig.changeFOV) {
                                prevFOV = mc.gameSettings.fovSetting
                                mc.gameSettings.fovSetting = BehindYouConfig.fovNumber.toFloat()
                            }
                        }
                        2 -> {
                            mc.gameSettings.thirdPersonView = 0
                            mc.entityRenderer.loadEntityShader(mc.renderViewEntity)
                            if (prevFOV != -1F) {
                                mc.gameSettings.fovSetting = prevFOV
                            }
                        }
                    }
                    mc.renderGlobal.setDisplayListEntitiesDirty()
                } else if (BehindYouConfig.hold) {
                    mc.gameSettings.thirdPersonView = 0
                    mc.entityRenderer.loadEntityShader(mc.renderViewEntity)
                    if (prevFOV != -1F) {
                        mc.gameSettings.fovSetting = prevFOV
                    }
                    mc.renderGlobal.setDisplayListEntitiesDirty()
                }
            }
        }
    }

}