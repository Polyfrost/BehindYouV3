package dev.isxander.behindyou.config

import dev.isxander.behindyou.BehindYou
import dev.isxander.behindyou.updater.DownloadGui
import dev.isxander.behindyou.updater.Updater
import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

object Config : Vigilant(File(BehindYou.modDir, "behindyouv3.toml"), "BehindYouV3") {

    @Property(
        type = PropertyType.SELECTOR,
        name = "Keybind Handle Mode",
        description = "Set how the keybind is turned on.",
        category = "General",
        options = ["Hold", "Toggle"]
    )
    var keybindMode = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Change FOV When Clicking Keybind",
        description = "Change your FOV when clicking the BehindYou keybind.",
        category = "General"
    )
    var changeFOV = false

    @Property(
        type = PropertyType.NUMBER,
        name = "Change FOV For Backview Keybind",
        description = "Set the FOV when clicking on the behind keybind.",
        category = "General",
        min = 30,
        max = 130
    )
    var backFOV = 100

    @Property(
        type = PropertyType.NUMBER,
        name = "Change FOV For Frontview Keybind",
        description = "Set the FOV when clicking on the front keybind.",
        category = "General",
        min = 30,
        max = 130
    )
    var frontFOV = 100

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
        description = "Update BehindYouV3 by clicking the button.",
        category = "Updater"
    )
    fun update() {
        if (Updater.shouldUpdate) EssentialAPI.getGuiUtil()
            .openScreen(DownloadGui()) else EssentialAPI.getNotifications()
            .push("BehindYouV3", "No update had been detected at startup, and thus the update GUI has not been shown.")
    }

    init {
        initialize()
    }
}