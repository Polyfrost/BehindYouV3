package dev.isxander.behindyou

import dev.isxander.behindyou.commands.BehindYouCommand
import dev.isxander.behindyou.config.Config
import dev.isxander.behindyou.updater.Updater
import gg.essential.api.EssentialAPI
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File


@Mod(
    modid = BehindYou.MODID,
    name = BehindYou.NAME,
    version = BehindYou.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object BehindYou {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    var previousPerspective = mc.gameSettings.thirdPersonView
    var previousFOV = mc.gameSettings.fovSetting

    val backKeybind = KeyBinding("BehindYou (Back)", Keyboard.KEY_NONE, "BehindYouV3")
    var previousBackKey = false
    var backToggled = false

    val frontKeybind = KeyBinding("BehindYou (Front)", Keyboard.KEY_NONE, "BehindYouV3")
    var previousFrontKey = false
    var frontToggled = false

    lateinit var jarFile: File
    val modDir = File(File(mc.mcDataDir, "W-OVERFLOW"), "BehindYouV3")

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    fun onFMLInit(event: FMLInitializationEvent) {
        ClientRegistry.registerKeyBinding(backKeybind)
        ClientRegistry.registerKeyBinding(frontKeybind)

        MinecraftForge.EVENT_BUS.register(this)

        BehindYouCommand.register()
        Updater.update()

        EssentialAPI.getShutdownHookUtil().register {
            mc.gameSettings.fovSetting = previousFOV
            mc.gameSettings.saveOptions()
        }
    }

    @SubscribeEvent
    fun clientTick(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END || mc.currentScreen != null || mc.theWorld == null || mc.thePlayer == null) return

        val backDown = backKeybind.isKeyDown
        val frontDown = frontKeybind.isKeyDown

        if (backDown && frontDown) return

        if (backDown != previousBackKey) {
            previousBackKey = backDown

            if (backDown) {
                if (backToggled) {
                    resetBack()
                } else {
                    enterBack()
                }
            } else if (Config.keybindMode == 0) {
                resetBack()
            }

            mc.renderGlobal.setDisplayListEntitiesDirty()
        } else if (frontDown != previousFrontKey) {
            previousFrontKey = frontDown

            if (frontDown) {
                if (frontToggled) {
                    resetFront()
                } else {
                    enterFront()
                }
            } else if (Config.keybindMode == 0) {
                resetFront()
            }

            mc.renderGlobal.setDisplayListEntitiesDirty()
        }
    }

    @SubscribeEvent
    fun guiOpen(event: GuiOpenEvent) {
        if (event.gui != null && Config.keybindMode == 0) {
            resetAll()
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        resetAll()
    }

    fun enterBack() {
        backToggled = true
        previousPerspective = mc.gameSettings.thirdPersonView
        previousFOV = mc.gameSettings.fovSetting
        mc.gameSettings.thirdPersonView = 2
        if (Config.changeFOV) {
            mc.gameSettings.fovSetting = Config.backFOV.toFloat()
        }
    }

    fun enterFront() {
        frontToggled = true
        previousPerspective = mc.gameSettings.thirdPersonView
        previousFOV = mc.gameSettings.fovSetting
        mc.gameSettings.thirdPersonView = 1
        if (Config.changeFOV) {
            mc.gameSettings.fovSetting = Config.frontFOV.toFloat()
        }
    }

    fun resetBack() {
        backToggled = false
        mc.gameSettings.thirdPersonView = previousPerspective
        mc.gameSettings.fovSetting = previousFOV
    }

    fun resetFront() {
        frontToggled = false
        mc.gameSettings.thirdPersonView = previousPerspective
        mc.gameSettings.fovSetting = previousFOV
    }

    fun resetAll() {
        if (frontToggled) {
            resetFront()
        }
        if (backToggled) {
            resetBack()
        }
    }

}

val mc: Minecraft
    get() = Minecraft.getMinecraft()