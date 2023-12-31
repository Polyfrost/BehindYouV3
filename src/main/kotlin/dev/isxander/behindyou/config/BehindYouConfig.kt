package dev.isxander.behindyou.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import dev.isxander.behindyou.BehindYou
import java.io.File

object BehindYouConfig : Config(
    Mod("BehindYouV3", ModType.UTIL_QOL, "/behindyou_dark.svg", VigilanceMigrator(File(BehindYou.oldModDir, "behindyouv3.toml").path)),
    "behindyouv3.json"
) {

    @KeyBind(
        name = "Frontview Keybind"
    )
    var frontKeybind = OnePlaceholderKeyBind(UKeyboard.KEY_NONE)

    @DualOption(
        name = "Frontview Keybind Handle Mode",
        left = "Hold", right = "Toggle"
    )
    var frontKeybindMode = false

    @KeyBind(
        name = "Backview Keybind"
    )
    var backKeybind = OnePlaceholderKeyBind(UKeyboard.KEY_NONE)

    @DualOption(
        name = "Backview Keybind Handle Mode",
        left = "Hold", right = "Toggle"
    )
    var backKeybindMode = false

    @DualOption(
        name = "Back To",
        left = "Previous", right = "First",
        size = 2
    )
    var backToFirst = true

    @Checkbox(
        name = "Zoom Animations"
    )
    var animation = false

    @Slider(
        name = "Animation Speed",
        min = 0.1f, max = 2f
    )
    var speed = 1f

    @Switch(
        name = "Change FOV When Clicking Keybind",
    )
    var changeFOV = false

    @Slider(
        name = "FOV For Backview Keybind",
        min = 30F,
        max = 110F
    )
    var backFOV = 100

    @Slider(
        name = "Change FOV For Frontview Keybind",
        min = 30F,
        max = 110F
    )
    var frontFOV = 100

    init {
        initialize()
    }
}
