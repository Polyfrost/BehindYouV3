package dev.isxander.xanderbehindyou

import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard

@Mod(
    modid = BehindYouInfo.ID,
    name = BehindYouInfo.NAME,
    version = BehindYouInfo.VERSION_FULL,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object BehindYou {
    var toggled = false
    var held = false
    var previous = mc.gameSettings.thirdPersonView

    val holdKeybind = KeyBinding("BehindYou (Hold)", Keyboard.KEY_G, "BehindYou")
    val toggleKeybind = KeyBinding("BehindYou (Toggle)", Keyboard.KEY_NONE, "BehindYou")

    @Mod.EventHandler
    fun onFMLInit(event: FMLInitializationEvent) {
        ClientRegistry.registerKeyBinding(holdKeybind)
        ClientRegistry.registerKeyBinding(toggleKeybind)

        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun clientTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        if (!toggled && !held) previous = mc.gameSettings.thirdPersonView

        if (!held && holdKeybind.isKeyDown) toggled = false
        held = holdKeybind.isKeyDown
        if (toggleKeybind.isPressed) toggled = !toggled

        if (mc.gameSettings.keyBindTogglePerspective.isPressed) toggled = false

        if (held || toggled) mc.gameSettings.thirdPersonView = 2
        else mc.gameSettings.thirdPersonView = previous
    }

}

val mc: Minecraft
    get() = Minecraft.getMinecraft()