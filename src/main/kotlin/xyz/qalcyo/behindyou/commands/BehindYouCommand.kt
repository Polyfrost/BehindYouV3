package xyz.qalcyo.behindyou.commands

import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import xyz.qalcyo.behindyou.BehindYou
import xyz.qalcyo.behindyou.config.BehindYouConfig

object BehindYouCommand : Command(BehindYou.ID) {

    override val commandAliases: Set<Alias> = setOf(Alias("behindyou"))

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(BehindYouConfig.gui())
    }
}