package net.wyvest.template.config

import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import net.wyvest.template.ForgeTemplate
import net.wyvest.template.ForgeTemplate.NAME
import net.wyvest.template.ForgeTemplate.mc
import net.wyvest.template.gui.DownloadConfirmGui
import net.wyvest.template.utils.Updater
import java.io.File

object TemplateConfig : Vigilant(File("config/Wyvest/$NAME/${ForgeTemplate.ID}.toml"), NAME) {
    @Property(
        type = PropertyType.SWITCH,
        name = "Show Update Notification",
        description = "Show a notification when you start Minecraft informing you of new updates.",
        category = "Updater"
    )
    var showUpdateNotification = true

    @Property(
        type = PropertyType.BUTTON,
        name = "Update Now",
        description = "Update $NAME by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadConfirmGui(mc.currentScreen)) else EssentialAPI.getNotifications()
            .push(NAME, "No update had been detected at startup, and thus the update GUI has not been shown.")
    }
}