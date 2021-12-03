package xyz.qalcyo.behindyou

import gg.essential.universal.ChatColor
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import xyz.qalcyo.behindyou.commands.BehindYouCommand
import xyz.qalcyo.behindyou.config.BehindYouConfig
import xyz.qalcyo.behindyou.utils.Updater
import org.lwjgl.input.Keyboard
import java.io.File


@Mod(
    name = BehindYou.NAME,
    modid = BehindYou.ID,
    version = BehindYou.VERSION,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object BehindYou {
    const val NAME = "BehindYouV2"
    const val VERSION = "2.2.0-beta2"
    const val ID = "behindyouv2"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()

    fun sendMessage(message: String) {
        if (mc.thePlayer == null)
            return
        val text = ChatComponentText(EnumChatFormatting.DARK_PURPLE.toString() + "[$NAME] " + ChatColor.RESET.toString() + " " + message)
        Minecraft.getMinecraft().thePlayer.addChatMessage(text)
    }

    lateinit var jarFile: File
    val modDir = File(File(mc.mcDataDir, "Qalcyo"), NAME)
    val behindKeybind = KeyBinding("Behind Keybind", Keyboard.KEY_R, "Behind You")
    val frontKeybind = KeyBinding("Front Keybind", Keyboard.KEY_NONE, "Behind You")

    @Mod.EventHandler
    fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        BehindYouConfig.initialize()
        BehindYouCommand.register()
        ClientRegistry.registerKeyBinding(frontKeybind)
        ClientRegistry.registerKeyBinding(behindKeybind)
        EVENT_BUS.register(Listener)
        Updater.update()
    }
}