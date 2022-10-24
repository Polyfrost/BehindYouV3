//#if MODERN==0 || FABRIC==1
package dev.isxander.behindyou.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.DualOption
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import cc.polyfrost.oneconfig.libs.universal.UKeyboard
import dev.isxander.behindyou.BehindYou
import java.io.File

object BehindYouConfig : Config(
    Mod("BehindYouV3", ModType.UTIL_QOL, VigilanceMigrator(File(BehindYou.modDir, "behindyouv3.toml").path)),
    "behindyouv3.json"
) {

    @KeyBind(
        name = "Frontview Keybind"
    )
    var frontKeybind = OnePlaceholderKeyBind(UKeyboard.KEY_NONE)

    @KeyBind(
        name = "Backview Keybind"
    )
    var backKeybind = OnePlaceholderKeyBind(UKeyboard.KEY_NONE)

    @DualOption(
        name = "Frontview Keybind Handle Mode",
        left = "Hold", right = "Toggle"
    )
    var frontKeybindMode = false

    //todo WHY IS DUAL OPTION A BOOLEAN

    @DualOption(
        name = "Backview Keybind Handle Mode",
        left = "Hold", right = "Toggle"
    )
    var backKeybindMode = false

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
//#endif
