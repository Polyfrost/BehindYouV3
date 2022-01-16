package dev.isxander.behindyou.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import dev.isxander.behindyou.config.Config

@Suppress("unused")
object BehindYouCommand : Command("behindyou", true) {

    override val commandAliases = setOf(
        Alias("behindyouv3")
    )

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(Config.gui())
    }

    @SubCommand("config", description = "Opens the config GUI for BehindYouV3")
    fun config() {
        EssentialAPI.getGuiUtil().openScreen(Config.gui())
    }
}