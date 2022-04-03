package dev.isxander.behindyou

import cc.woverflow.onecore.utils.Updater
import cc.woverflow.onecore.utils.command
import cc.woverflow.onecore.utils.openScreen
import dev.isxander.behindyou.config.Config
import gg.essential.universal.UMinecraft
import gg.essential.universal.utils.MCMinecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import java.io.File


@Mod(
    modid = BehindYou.MODID,
    name = BehindYou.NAME,
    version = BehindYou.VERSION,
    acceptedMinecraftVersions = "[1.8.9]",
    clientSideOnly = true,
    modLanguageAdapter = "gg.essential.api.utils.KotlinAdapter"
)
object BehindYou {
    const val MODID = "@ID@"
    const val NAME = "@NAME@"
    const val VERSION = "@VER@"

    var previousPerspective = UMinecraft.getSettings().thirdPersonView
    var previousFOV = UMinecraft.getSettings().fovSetting

    val backKeybind = KeyBinding("BehindYou (Back)", Keyboard.KEY_NONE, "BehindYouV3")
    var previousBackKey = false
    var backToggled = false

    val frontKeybind = KeyBinding("BehindYou (Front)", Keyboard.KEY_NONE, "BehindYouV3")
    var previousFrontKey = false
    var frontToggled = false

    val modDir = File(File(mc.mcDataDir, "W-OVERFLOW"), "BehindYouV3")

    @Mod.EventHandler
    private fun onFMLPreInitialization(event: FMLPreInitializationEvent) {
        if (!modDir.exists()) modDir.mkdirs()
        Updater.addToUpdater(event.sourceFile, NAME, MODID, VERSION, "W-OVERFLOW/$MODID")
    }

    @Mod.EventHandler
    fun onFMLInit(event: FMLInitializationEvent) {
        ClientRegistry.registerKeyBinding(backKeybind)
        ClientRegistry.registerKeyBinding(frontKeybind)

        MinecraftForge.EVENT_BUS.register(this)

        command("behindyou", aliases = arrayListOf("behindyouv3")) {
            main {
                Config.openScreen()
            }
        }
    }

    @SubscribeEvent
    fun clientTick(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END || mc.currentScreen != null || mc.theWorld == null || mc.thePlayer == null) return

        val backDown = backKeybind.isKeyDown
        val frontDown = frontKeybind.isKeyDown

        if (backDown && frontDown) return

        if (backDown != previousBackKey) {
            previousBackKey = backDown

            if (backDown) {
                if (backToggled) {
                    resetBack()
                } else {
                    if (frontToggled) {
                        resetFront()
                    }
                    enterBack()
                }
            } else if (Config.keybindMode == 0) {
                resetBack()
            }

            mc.renderGlobal.setDisplayListEntitiesDirty()
        } else if (frontDown != previousFrontKey) {
            previousFrontKey = frontDown

            if (frontDown) {
                if (frontToggled) {
                    resetFront()
                } else {
                    if (backToggled) {
                        resetBack()
                    }
                    enterFront()
                }
            } else if (Config.keybindMode == 0) {
                resetFront()
            }

            mc.renderGlobal.setDisplayListEntitiesDirty()
        }
    }

    @SubscribeEvent
    fun guiOpen(event: GuiOpenEvent) {
        if (event.gui != null && Config.keybindMode == 0) {
            resetAll()
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        resetAll()
    }

    fun enterBack() {
        backToggled = true
        previousPerspective = UMinecraft.getSettings().thirdPersonView
        previousFOV = UMinecraft.getSettings().fovSetting
        UMinecraft.getSettings().thirdPersonView = 2
        if (Config.changeFOV) {
            UMinecraft.getSettings().fovSetting = Config.backFOV.toFloat()
        }
    }

    fun enterFront() {
        frontToggled = true
        previousPerspective = UMinecraft.getSettings().thirdPersonView
        previousFOV = UMinecraft.getSettings().fovSetting
        UMinecraft.getSettings().thirdPersonView = 1
        if (Config.changeFOV) {
            UMinecraft.getSettings().fovSetting = Config.frontFOV.toFloat()
        }
    }

    fun resetBack() {
        backToggled = false
        UMinecraft.getSettings().thirdPersonView = previousPerspective
        UMinecraft.getSettings().fovSetting = previousFOV
    }

    fun resetFront() {
        frontToggled = false
        UMinecraft.getSettings().thirdPersonView = previousPerspective
        UMinecraft.getSettings().fovSetting = previousFOV
    }

    fun resetAll() {
        if (frontToggled) {
            resetFront()
        }
        if (backToggled) {
            resetBack()
        }
    }

}

val mc: MCMinecraft
    get() = UMinecraft.getMinecraft()