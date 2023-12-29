//#if MODERN==0 || FABRIC==1
package dev.isxander.behindyou

//#if MODERN==0
import cc.polyfrost.oneconfig.gui.animations.Animation
import cc.polyfrost.oneconfig.gui.animations.DummyAnimation
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad
import cc.polyfrost.oneconfig.libs.universal.UChat
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import dev.isxander.behindyou.config.BehindYouConfig
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File

//#endif

//#if FABRIC==1
//$$ import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
//$$ import net.fabricmc.loader.api.FabricLoader
//$$ import net.minecraft.client.option.Perspective
//$$ import net.minecraft.client.option.KeyBinding
//$$ import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
//$$ import net.minecraft.client.util.InputUtil
//$$ import net.fabricmc.loader.api.metadata.ModOrigin
//$$ import kotlin.io.path.name
//#endif

//#if MODERN==0
@Mod(
    modid = BehindYou.MODID,
    name = BehindYou.NAME,
    version = BehindYou.VERSION,
    clientSideOnly = true,
    modLanguageAdapter = "cc.polyfrost.oneconfig.utils.KotlinLanguageAdapter"
)
//#endif
object BehindYou {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    //#if MODERN==0
    var previousPerspective = 0
    var previousFOV = 0f
    //#else
    //$$ var previousPerspective = Perspective.FIRST_PERSON
    //$$ var previousFOV = 0.0
    //#endif

    var previousBackKey = false
    var backToggled = false
    var previousFrontKey = false
    var frontToggled = false

    val oldModDir = File(File("./W-OVERFLOW"), "BehindYouV3")

    var end = 0f
    var distance = 0f
    var animation: Animation = DummyAnimation(0f)

    //#if MODERN==0
    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent)
    //#else
    //$$ fun onInit()
    //#endif
    {
        //#if MODERN==1
        //$$ onPreInit()
        //#endif
        //#if FABRIC==1
        //$$ KeyBindingHelper.registerKeyBinding(backKeybind)
        //$$ KeyBindingHelper.registerKeyBinding(frontKeybind)
        //#endif

        //#if MODERN==0
        MinecraftForge.EVENT_BUS.register(this)
        //#endif
        //#if FABRIC==1
        //$$ ClientTickEvents.END_CLIENT_TICK.register { onTick() }
        //#endif
        CommandManager.INSTANCE.registerCommand(BehindYouCommand())
    }

    //#if MODERN==0
    @SubscribeEvent
    fun tick(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        onTick()
    }
    //#endif

    fun level(): Float {
        animation = if (getPerspective() == 0) {
            DummyAnimation(0.3f)
        }else {
            if (end != 0.1f) end = distance
            EaseInOutQuad(200, animation.get(), end, false)
        }
        if (animation.get() < 0.3f) {
            //#if MODERN==0
            UMinecraft.getSettings().thirdPersonView = 0
            //#else
            //$$ UMinecraft.getSettings().perspective = Perspective.values()[0]
            //#endif
        }
        return if (animation.get() < 0.3f) 0.1f else animation.get()
    }



    fun onTick() {
        if (UScreen.currentScreen != null || UMinecraft.getWorld() == null || !UPlayer.hasPlayer()) {
            if (!BehindYouConfig.frontKeybindMode || !BehindYouConfig.backKeybindMode) {
                resetAll()
            }
            return
        }

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
                    enterBack()
                }
            } else if (!BehindYouConfig.backKeybindMode) {
                resetBack()
            }
            setPerspective()

        } else if (frontDown != previousFrontKey) {
            previousFrontKey = frontDown

            if (frontDown) {
                if (frontToggled) {
                    resetFront()
                } else {
                    if (backToggled) {
                        resetBack()
                    }
                    enterFront()
                }
            } else if (!BehindYouConfig.frontKeybindMode) {
                resetFront()
            }

            setPerspective()
        }
    }

    private fun setPerspective() {
        //#if MODERN==0
        UMinecraft.getMinecraft().renderGlobal.setDisplayListEntitiesDirty()
        //#else
        //$$ mc.gameRenderer.onCameraEntitySet(if (mc.options.getPerspective().isFirstPerson()) mc.getCameraEntity() else null)
        //#endif
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
            //#if MODERN==1
            //$$ .ordinal
            //#endif
        )
        setFOV(previousFOV)
    }

    fun resetFront() {
        frontToggled = false
        setPerspective(previousPerspective
        //#if MODERN==1
        //$$ .ordinal
        //#endif
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

    fun getPerspective() =
        //#if MODERN==0
        UMinecraft.getSettings().thirdPersonView
        //#else
        //$$ UMinecraft.getSettings().perspective
        //#endif

    fun setPerspective(value: Int) {
        //#if MODERN==0
        previousPerspective = getPerspective()
        //#else
        //$$ previousPerspective = getPerspective()
        //#endif
        if (value == 0) {
            end = 0.1f
        } else {
            end = distance
            //#if MODERN==0
            UMinecraft.getSettings().thirdPersonView = value
            //#else
            //$$ UMinecraft.getSettings().perspective = Perspective.values()[value]
            //#endif
        }
    }

    private fun getFOV() =
        //#if MODERN==0
        UMinecraft.getSettings().fovSetting
        //#else
        //$$ UMinecraft.getSettings().fov
        //#endif

    private fun setFOV(value: Number) {
        //#if MODERN==0
        UMinecraft.getSettings().fovSetting = value.toFloat()
        //#else
        //$$ UMinecraft.getSettings().fov = value.toDouble()
        //#endif
    }

    @Command(value = "behindyou", description = "Open the BehindYou config GUI.")
    class BehindYouCommand {
        @Main
        fun main() {
            BehindYouConfig.openGui()
        }
    }
}
//#endif