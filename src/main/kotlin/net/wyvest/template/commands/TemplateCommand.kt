package net.wyvest.template.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import net.wyvest.template.ForgeTemplate
import net.wyvest.template.config.TemplateConfig

@Suppress("unused")
object TemplateCommand : Command(ForgeTemplate.ID, true) {

    override val commandAliases = setOf(
        Alias("wyvesttemplate")
    )

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(TemplateConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for " + ForgeTemplate.NAME)
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(TemplateConfig.gui())
    }
}