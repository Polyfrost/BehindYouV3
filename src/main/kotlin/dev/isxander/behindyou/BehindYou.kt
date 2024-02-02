package dev.isxander.behindyou

import cc.polyfrost.oneconfig.gui.animations.*
import cc.polyfrost.oneconfig.libs.universal.*
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.commands.annotations.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import club.sk1er.patcher.config.PatcherConfig
import dev.isxander.behindyou.config.*
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File
import kotlin.math.abs

@Mod(
    modid = BehindYou.MODID,
    name = BehindYou.NAME,
    version = BehindYou.VERSION,
    clientSideOnly = true,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)

object BehindYou {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    var previousPerspective = 0
    var vPerspective = 0
    var previousFOV = 0f
    var realPerspective = 0

    var previousBackKey = false
    var backToggled = false
    var previousFrontKey = false
    var frontToggled = false

    val oldModDir = File(File("./W-OVERFLOW"), "BehindYouV3")

    var end = 0f
    var distance = 0f
    var animation: Animation = DummyAnimation(0f)
    private var isPatcher = false

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        isPatcher = Loader.isModLoaded("patcher")
        MinecraftForge.EVENT_BUS.register(this)
        CommandManager.INSTANCE.registerCommand(BehindYouCommand())
    }

    @SubscribeEvent
    fun tick(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        onTick()
    }

    fun level(): Float {
        animation = if (realPerspective == 0) {
            if (mc.gameSettings.thirdPersonView != 0) {
                mc.gameSettings.thirdPersonView = 0
            }
            DummyAnimation(if (isPatcher && PatcherConfig.parallaxFix) -0.05f else 0.1f)
        } else {
            if (end != 0.3f) end = distance
            if (animation.get() > distance) {
                DummyAnimation(distance)
            } else
                EaseOutQuart(if (BehindYouConfig.animation) 200 * abs(animation.get() - end) / BehindYouConfig.speed else 0f, animation.get(), end, false)
        }
        if (animation.isFinished && animation.end == 0.3f) {
            mc.gameSettings.thirdPersonView = 0
            realPerspective = 0
        }
        return animation.get()
    }

    private fun onTick() {
        if (UScreen.currentScreen != null || mc.theWorld == null || !UPlayer.hasPlayer()) {
            if (!BehindYouConfig.frontKeybindMode || !BehindYouConfig.backKeybindMode) {
                resetAll()
            }
            return
        }
        if (BehindYouConfig.backToFirst) previousPerspective = 0

        val backDown = BehindYouConfig.backKeybind.keyBinds.any { UKeyboard.isKeyDown(it) }
        val frontDown = BehindYouConfig.frontKeybind.keyBinds.any { UKeyboard.isKeyDown(it) }

        if (backDown && frontDown) return

        if (backDown != previousBackKey) {
            previousBackKey = backDown

            if (backDown) {
                if (backToggled) {
                    resetBack()
                } else {
                    if (frontToggled) {
                        resetFront()
                    }
                    if (vPerspective != 2) enterBack() else resetBack()
                }
            } else if (!BehindYouConfig.backKeybindMode) {
                resetBack()
            }

        } else if (frontDown != previousFrontKey) {
            previousFrontKey = frontDown

            if (frontDown) {
                if (frontToggled) {
                    resetFront()
                } else {
                    if (backToggled) {
                        resetBack()
                    }
                    if (vPerspective != 1) enterFront() else resetFront()
                }
            } else if (!BehindYouConfig.frontKeybindMode) {
                resetFront()
            }

        }
    }

    fun enterBack() {
        backToggled = true
        previousFOV = getFOV()
        setPerspective(2)
        if (BehindYouConfig.changeFOV) {
            setFOV(BehindYouConfig.backFOV)
        }
    }

    fun enterFront() {
        frontToggled = true
        previousFOV = getFOV()
        setPerspective(1)
        if (BehindYouConfig.changeFOV) {
            setFOV(BehindYouConfig.frontFOV)
        }
    }

    fun resetBack() {
        backToggled = false
        setPerspective(
            previousPerspective
        )
        setFOV(previousFOV)
    }

    fun resetFront() {
        frontToggled = false
        setPerspective(previousPerspective
        )
        setFOV(previousFOV)
    }

    fun resetAll() {
        if (frontToggled) {
            resetFront()
        }
        if (backToggled) {
            resetBack()
        }
    }

    fun setPerspective(value: Int) {
        if (vPerspective == value) return
        previousPerspective = vPerspective
        vPerspective = value

        if (value == 0) {
            end = 0.3f
            if (!BehindYouConfig.animation) {
                mc.gameSettings.thirdPersonView = 0
                realPerspective = 0
            }
        } else {
            end = distance
            mc.gameSettings.thirdPersonView = value
            realPerspective = value
            animation = DummyAnimation(0.3f)
        }
    }

    private fun getFOV() = mc.gameSettings.fovSetting

    private fun setFOV(value: Number) {
        mc.gameSettings.fovSetting = value.toFloat()
    }

    @Command(value = "behindyou", description = "Open the BehindYou config GUI.")
    class BehindYouCommand {
        @Main
        fun main() {
            BehindYouConfig.openGui()
        }
    }
}