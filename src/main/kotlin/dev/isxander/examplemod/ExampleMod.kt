package dev.isxander.examplemod

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = ExampleModInfo.ID,
    name = ExampleModInfo.NAME,
    version = ExampleModInfo.VERSION_FULL,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object ExampleMod {

    @Mod.EventHandler
    fun onFMLInit(event: FMLInitializationEvent) {

    }

}