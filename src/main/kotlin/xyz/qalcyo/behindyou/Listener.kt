package xyz.qalcyo.behindyou

import net.minecraft.client.settings.GameSettings
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import xyz.qalcyo.behindyou.BehindYou.behindKeybind
import xyz.qalcyo.behindyou.BehindYou.frontKeybind
import xyz.qalcyo.behindyou.BehindYou.mc
import xyz.qalcyo.behindyou.config.BehindYouConfig

object Listener {

    private var prevState = false
    private var prevFOV = -1F
    private var prevStateFront = false
    private var prevFOVFront = -1F

    /**
     * Keybind hold / toggle logic stolen from PerspectiveModv4 under MIT License
     * https://github.com/DJtheRedstoner/PerspectiveModv4/blob/master/LICENSE
     */
    @SubscribeEvent
    fun onKeyInput(event: TickEvent.RenderTickEvent) {
        if (BehindYouConfig.toggled) {
            val isBehindDown = GameSettings.isKeyDown(behindKeybind)
            if (isBehindDown != prevState && mc.currentScreen == null && mc.theWorld != null && mc.thePlayer != null) {
                prevState = isBehindDown
                if (isBehindDown) {
                    when (mc.gameSettings.thirdPersonView) {
                        0 -> {
                            mc.gameSettings.thirdPersonView = 2
                            mc.entityRenderer.loadEntityShader(null)
                            if (BehindYouConfig.changeFOV) {
                                prevFOV = mc.gameSettings.fovSetting
                                mc.gameSettings.fovSetting = BehindYouConfig.backFOV.toFloat()
                            }
                        }
                        1 -> {
                            mc.gameSettings.thirdPersonView = 2
                            if (BehindYouConfig.changeFOV) {
                                prevFOV = mc.gameSettings.fovSetting
                                mc.gameSettings.fovSetting = BehindYouConfig.backFOV.toFloat()
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
                } else if (BehindYouConfig.holdBack) {
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

    /**
     * Keybind hold / toggle logic stolen from PerspectiveModv4 under MIT License
     * https://github.com/DJtheRedstoner/PerspectiveModv4/blob/master/LICENSE
     */
    @SubscribeEvent
    fun onKeyInput2(event: TickEvent.RenderTickEvent) {
        if (BehindYouConfig.toggled) {
            val isFrontDown = GameSettings.isKeyDown(frontKeybind)
            if (isFrontDown != prevStateFront && mc.currentScreen == null && mc.theWorld != null && mc.thePlayer != null) {
                prevStateFront = isFrontDown
                if (isFrontDown) {
                    when (mc.gameSettings.thirdPersonView) {
                        0 -> {
                            mc.gameSettings.thirdPersonView = 1
                            mc.entityRenderer.loadEntityShader(null)
                            if (BehindYouConfig.changeFOV) {
                                prevFOVFront = mc.gameSettings.fovSetting
                                mc.gameSettings.fovSetting = BehindYouConfig.frontFOV.toFloat()
                            }
                        }
                        1 -> {
                            mc.gameSettings.thirdPersonView = 0
                            mc.entityRenderer.loadEntityShader(mc.renderViewEntity)
                            if (prevFOVFront != -1F) {
                                mc.gameSettings.fovSetting = prevFOVFront
                            }
                        }
                        2 -> {
                            mc.gameSettings.thirdPersonView = 1
                            if (BehindYouConfig.changeFOV) {
                                prevFOVFront = mc.gameSettings.fovSetting
                                mc.gameSettings.fovSetting = BehindYouConfig.frontFOV.toFloat()
                            }
                        }
                    }
                    mc.renderGlobal.setDisplayListEntitiesDirty()
                } else if (BehindYouConfig.holdFront) {
                    mc.gameSettings.thirdPersonView = 0
                    mc.entityRenderer.loadEntityShader(mc.renderViewEntity)
                    if (prevFOVFront != -1F) {
                        mc.gameSettings.fovSetting = prevFOVFront
                    }
                    mc.renderGlobal.setDisplayListEntitiesDirty()
                }
            }
        }
    }

}