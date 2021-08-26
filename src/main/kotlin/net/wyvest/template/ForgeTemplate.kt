package net.wyvest.template

import net.minecraft.client.Minecraft
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.wyvest.template.commands.TemplateCommand
import net.wyvest.template.config.TemplateConfig
import net.wyvest.template.utils.Updater
import xyz.matthewtgm.requisite.util.ChatHelper
import java.io.File

@Mod(name = ForgeTemplate.NAME, modid = ForgeTemplate.ID, version = ForgeTemplate.VERSION, modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter")
object ForgeTemplate {
    const val NAME = "ForgeTemplate"
    const val VERSION = "1.0.0"
    const val ID = "forgetemplate"
    val mc: Minecraft
        get() = Minecraft.getMinecraft()
    fun sendMessage(message: String?) {
        ChatHelper.sendMessage(EnumChatFormatting.DARK_PURPLE.toString() + "[$NAME] ", message)
    }
    lateinit var jarFile: File
    private val modDir = File(File(File(mc.mcDataDir, "config"), "Wyvest"), NAME)

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        jarFile = event.sourceFile
    }

    @Mod.EventHandler
    fun onFMLInitialization(event: FMLInitializationEvent) {
        TemplateConfig.initialize()
        TemplateCommand.register()
        Updater.update()
    }
}