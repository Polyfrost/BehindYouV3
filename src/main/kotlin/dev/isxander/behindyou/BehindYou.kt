package dev.isxander.behindyou

import dev.isxander.behindyou.commands.BehindYouCommand
import dev.isxander.behindyou.config.Config
import dev.isxander.behindyou.updater.Updater
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File

@Mod(
    modid = "behindyouv3",
    name = "BehindYouV3",
    version = "3.0.0",
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object BehindYou {
    var toggled = false
    var held = false
    var previousPerspective = mc.gameSettings.thirdPersonView
    var previousFOV = mc.gameSettings.fovSetting

    val holdKeybind = KeyBinding("BehindYou (Hold)", Keyboard.KEY_G, "BehindYouV3")
    val toggleKeybind = KeyBinding("BehindYou (Toggle)", Keyboard.KEY_NONE, "BehindYouV3")

    lateinit var jarFile: File
    val modDir = File(File(mc.mcDataDir, "W-OVERFLOW"), "BehindYouV3")

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    fun onFMLInit(event: FMLInitializationEvent) {
        ClientRegistry.registerKeyBinding(holdKeybind)
        ClientRegistry.registerKeyBinding(toggleKeybind)

        MinecraftForge.EVENT_BUS.register(this)

        BehindYouCommand.register()
        Updater.update()
    }

    @SubscribeEvent
    fun clientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        if (!toggled && !held) {
            previousPerspective = mc.gameSettings.thirdPersonView
            previousFOV = mc.gameSettings.fovSetting
        }

        if (!held && holdKeybind.isKeyDown) toggled = false
        held = holdKeybind.isKeyDown
        if (toggleKeybind.isPressed) toggled = !toggled

        if (mc.gameSettings.keyBindTogglePerspective.isPressed) toggled = false

        if (held || toggled) {
            mc.gameSettings.thirdPersonView = 2
            if (Config.changeFOV) {
                mc.gameSettings.fovSetting = Config.backFOV.toFloat()
            }
        } else {
            mc.gameSettings.thirdPersonView = previousPerspective
            mc.gameSettings.fovSetting = previousFOV
        }
    }

}

val mc: Minecraft
    get() = Minecraft.getMinecraft()