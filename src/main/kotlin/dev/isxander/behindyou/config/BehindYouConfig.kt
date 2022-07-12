//#if MODERN==0 || FABRIC==1
package dev.isxander.behindyou.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.DualOption
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator
import dev.isxander.behindyou.BehindYou
import java.io.File

object BehindYouConfig : Config(
    Mod("BehindYouV3", ModType.UTIL_QOL, VigilanceMigrator(File(BehindYou.modDir, "behindyouv3.toml").path)),
    "behindyouv3.json"
) {

    @Info(
        text = "The ability to edit the keybind is in the Minecraft Controls Menu.\n" +
                "You can find the Minecraft controls menu in Options -> Controls.",
        category = "General", type = InfoType.INFO, size = 2
    )
    var ignored = false

    @DualOption(
        name = "Frontview Keybind Handle Mode",
        category = "General",
        left = "Hold", right = "Toggle"
    )
    var frontKeybindMode = 0

    @DualOption(
        name = "Backview Keybind Handle Mode",
        category = "General",
        left = "Hold", right = "Toggle"
    )
    var backKeybindMode = 0

    @Switch(
        name = "Change FOV When Clicking Keybind",
        category = "General"
    )
    var changeFOV = false

    @Slider(
        name = "FOV For Backview Keybind",
        category = "General",
        min = 30F,
        max = 110F
    )
    var backFOV = 100

    @Slider(
        name = "Change FOV For Frontview Keybind",
        category = "General",
        min = 30F,
        max = 110F
    )
    var frontFOV = 100

    init {
        initialize()
    }
}
//#endif
