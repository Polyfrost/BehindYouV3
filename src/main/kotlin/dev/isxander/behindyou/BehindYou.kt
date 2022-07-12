//#if MODERN==0 || FABRIC==1
package dev.isxander.behindyou

//#if MODERN==0
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import cc.polyfrost.oneconfig.libs.universal.UMinecraft
import cc.polyfrost.oneconfig.libs.universal.UScreen
import cc.polyfrost.oneconfig.libs.universal.wrappers.UPlayer
import cc.polyfrost.oneconfig.utils.commands.CommandManager
import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import dev.isxander.behindyou.config.BehindYouConfig
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
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
    clientSideOnly = true
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

    val backKeybind =
        //#if MODERN==0
        KeyBinding("BehindYou (Back)", UKeyboard.KEY_NONE, "BehindYouV3")
        //#else
        //$$ KeyBinding("BehindYou (Back)", InputUtil.Type.KEYSYM, UKeyboard.KEY_NONE, "BehindYouV3")
        //#endif
    var previousBackKey = false
    var backToggled = false

    val frontKeybind =
        //#if MODERN==0
        KeyBinding("BehindYou (Front)", UKeyboard.KEY_NONE, "BehindYouV3")
        //#else
        //$$ KeyBinding("BehindYou (Front)", InputUtil.Type.KEYSYM, UKeyboard.KEY_NONE, "BehindYouV3")
        //#endif
    var previousFrontKey = false
    var frontToggled = false

    val modDir = File(File("./W-OVERFLOW"), "BehindYouV3")

    //#if MODERN==0
    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent)
    //#else
    //$$ fun onPreInit()
    //#endif
    {
        if (!modDir.exists()) modDir.mkdirs()
        //#if FABRIC==1
        //$$ val file = getFileOfMod(MODID)
        //$$ if (file == null) return
        //#endif
    }

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
        //#if MODERN==0
        ClientRegistry.registerKeyBinding(backKeybind)
        ClientRegistry.registerKeyBinding(frontKeybind)
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
        CommandManager.INSTANCE.registerCommand(BehindYouCommand.Companion::class.java)
    }

    //#if MODERN==0
    @SubscribeEvent
    fun tick(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        onTick()
    }
    //#endif

    fun onTick() {
        if (UScreen.currentScreen != null || UMinecraft.getWorld() == null || !UPlayer.hasPlayer()) {
            if (BehindYouConfig.frontKeybindMode == 0 || BehindYouConfig.backKeybindMode == 0) {
                resetAll()
            }
            return
        }
//
        val backDown = backKeybind.
            //#if MODERN==0
            isKeyDown
            //#else
            //$$ isPressed
            //#endif
        val frontDown = frontKeybind.
            //#if MODERN==0
            isKeyDown
            //#else
            //$$ isPressed
            //#endif

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
            } else if (BehindYouConfig.backKeybindMode == 0) {
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
            } else if (BehindYouConfig.frontKeybindMode == 0) {
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
        previousPerspective = getPerspective()
        previousFOV = getFOV()
        setPerspective(2)
        if (BehindYouConfig.changeFOV) {
            setFOV(BehindYouConfig.backFOV)
        }
    }

    fun enterFront() {
        frontToggled = true
        previousPerspective = getPerspective()
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

    private fun getPerspective() =
        //#if MODERN==0
        UMinecraft.getSettings().thirdPersonView
        //#else
        //$$ UMinecraft.getSettings().perspective
        //#endif

    private fun setPerspective(value: Int) {
        //#if MODERN==0
        UMinecraft.getSettings().thirdPersonView = value
        //#else
        //$$ UMinecraft.getSettings().perspective = Perspective.values()[value]
        //#endif
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
        companion object {
            @Main
            fun main() {
                BehindYouConfig.openGui()
            }
        }
    }
}
//#endif