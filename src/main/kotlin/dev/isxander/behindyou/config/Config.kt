//#if MODERN==0 || FABRIC==1
package dev.isxander.behindyou.config

import dev.isxander.behindyou.BehindYou
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyData
import gg.essential.vigilance.data.PropertyType
import gg.essential.vigilance.data.SortingBehavior
import java.io.File

object Config : Vigilant(File(BehindYou.modDir, "behindyouv3.toml"), "BehindYouV3", sortingBehavior = object : SortingBehavior() {
    override fun getPropertyComparator(): Comparator<in PropertyData> = Comparator { o1, o2 ->
        if (o1.attributesExt.name == "!!Keybind!!") return@Comparator -1
        if (o2.attributesExt.name == "!!Keybind!!") return@Comparator 1
        else return@Comparator compareValuesBy(o1, o2) {
            it.attributesExt.subcategory
        }
    }
}) {

    @Property(
        type = PropertyType.BUTTON,
        name = "!!Keybind!!",
        description = "§l§o§nThe ability to edit the keybind is in the Minecraft Controls Menu.\n" +
                "§rYou can find the Minecraft controls menu in Options -> Controls.",
        category = "General",
        placeholder = "Hide on restart"
    )
    fun warning() {
        disableWarning = true
        markDirty()
        writeData()
    }

    @Property(type = PropertyType.SWITCH, name = "Disable Keybind Warning", category = "General", hidden = true)
    var disableWarning = false

    @Property(
        type = PropertyType.SELECTOR,
        name = "Frontview Keybind Handle Mode",
        description = "Set how the keybind is turned on for frontview.",
        category = "General",
        options = ["Hold", "Toggle"]
    )
    var frontKeybindMode = 0

    @Property(
        type = PropertyType.SELECTOR,
        name = "Backview Keybind Handle Mode",
        description = "Set how the keybind is turned on for backview.",
        category = "General",
        options = ["Hold", "Toggle"]
    )
    var backKeybindMode = 0

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

    init {
        initialize()
        hidePropertyIf("warning") { disableWarning }
    }
}
//#endif