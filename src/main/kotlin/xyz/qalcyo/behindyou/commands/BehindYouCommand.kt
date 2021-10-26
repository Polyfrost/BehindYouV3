package xyz.qalcyo.behindyou.commands

import xyz.qalcyo.behindyou.BehindYou
import xyz.qalcyo.behindyou.config.BehindYouConfig
import xyz.qalcyo.requisite.Requisite
import xyz.qalcyo.requisite.core.commands.annotations.Command

@Command(name = BehindYou.ID, aliases = ["behindyou"], generateTabCompletions = true)
object BehindYouCommand {

    @Command.Default
    fun handle() {
        Requisite.getInstance().guiHelper.open(BehindYouConfig.gui())
    }
}