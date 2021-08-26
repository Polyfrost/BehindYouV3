package net.wyvest.behindyou.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import net.wyvest.behindyou.BehindYou
import net.wyvest.behindyou.config.BehindYouConfig

@Suppress("unused")
object BehindYouCommand : Command(BehindYou.ID, true) {

    override val commandAliases = setOf(
        Alias("behind")
    )

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(BehindYouConfig.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for " + BehindYou.NAME)
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(BehindYouConfig.gui())
    }
}