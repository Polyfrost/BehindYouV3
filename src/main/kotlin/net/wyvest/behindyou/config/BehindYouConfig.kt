package net.wyvest.behindyou.config

import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import net.wyvest.behindyou.BehindYou
import net.wyvest.behindyou.BehindYou.NAME
import net.wyvest.behindyou.BehindYou.mc
import net.wyvest.behindyou.gui.DownloadConfirmGui
import net.wyvest.behindyou.utils.Updater
import java.io.File

object BehindYouConfig : Vigilant(File(BehindYou.modDir, "${BehindYou.ID}.toml"), NAME) {

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle Mod",
        description = "Toggle the mod.",
        category = "General"
    )
    var toggled = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "General"
    )
    var showUpdateNotification = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update $NAME by clicking the button.",
        category = "General"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadConfirmGui(mc.currentScreen)) else EssentialAPI.getNotifications()
            .push(NAME, "No update had been detected at startup, and thus the update GUI has not been shown.")
    }
}