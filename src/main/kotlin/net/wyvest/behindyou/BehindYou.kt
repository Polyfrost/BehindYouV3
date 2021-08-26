package net.wyvest.behindyou

import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.wyvest.behindyou.commands.BehindYouCommand
import net.wyvest.behindyou.config.BehindYouConfig
import net.wyvest.behindyou.utils.Updater
import xyz.matthewtgm.requisite.util.ChatHelper
import xyz.matthewtgm.requisite.util.ForgeHelper
import java.io.File

@Mod(name = BehindYou.NAME, modid = BehindYou.ID, version = BehindYou.VERSION, modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter")
object BehindYou {
    const val NAME = "BehindYouv2"
    const val VERSION = "2.0.0"
    const val ID = "behindyouv2"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    fun sendMessage(message: String) {
        ChatHelper.sendMessage(EnumChatFormatting.DARK_PURPLE.toString() + "[$NAME] ", message)
    }
    lateinit var jarFile: File
    val modDir = File(File(File(mc.mcDataDir, "config"), "Wyvest"), NAME)

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        BehindYouConfig.initialize()
        BehindYouCommand.register()
        Updater.update()
        ForgeHelper.registerEventListener(Listener)
    }
}