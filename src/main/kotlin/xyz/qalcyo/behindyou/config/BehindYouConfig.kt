package xyz.qalcyo.behindyou.config

import gg.essential.api.EssentialAPI
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import xyz.qalcyo.behindyou.BehindYou
import xyz.qalcyo.behindyou.BehindYou.NAME
import xyz.qalcyo.behindyou.BehindYou.mc
import xyz.qalcyo.behindyou.gui.DownloadConfirmGui
import xyz.qalcyo.behindyou.utils.Updater
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
        name = "Hold Keybind for Third Person Back",
        description = "Allow the option to turn back to the first person after releasing the behind keybind.",
        category = "General"
    )
    var holdBack = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Hold Keybind for Third Person Front",
        description = "Allow the option to turn back to the first person after releasing the front keybind.",
        category = "General"
    )
    var holdFront = true

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
        type = PropertyType.PARAGRAPH,
        name = "Keybind",
        description = "The ability to edit the keybind is in the Minecraft Controls Menu, in options -> controls.",
        category = "General",
        protectedText = true
    )
    var placeholder = ""

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