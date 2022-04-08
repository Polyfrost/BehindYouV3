package dev.isxander.behindyou

import cc.woverflow.onecore.utils.Updater
import cc.woverflow.onecore.utils.command
import cc.woverflow.onecore.utils.openScreen
import dev.isxander.behindyou.config.Config
import gg.essential.universal.UKeyboard
import gg.essential.universal.UMinecraft
import gg.essential.universal.UScreen
import gg.essential.universal.utils.MCMinecraft
import gg.essential.universal.wrappers.UPlayer
import java.io.File

//#if MODERN==0
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
//#endif

//#if FABRIC==1
//$$ import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
//$$ import net.fabricmc.loader.api.FabricLoader
//$$ import net.fabricmc.loader.api.metadata.ModOrigin
//$$ import java.io.File
//$$ import kotlin.io.path.name
//#endif


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

    //#if MODERN==0
    var previousPerspective = UMinecraft.getSettings().thirdPersonView
    var previousFOV = UMinecraft.getSettings().fovSetting
    //#else
    //$$ var previousPerspective = UMinecraft.getSettings().perspective
    //$$ var previousFOV = UMinecraft.getSettings().fov

    val backKeybind = KeyBinding("BehindYou (Back)", UKeyboard.KEY_NONE, "BehindYouV3")
    var previousBackKey = false
    var backToggled = false

    val frontKeybind = KeyBinding("BehindYou (Front)", UKeyboard.KEY_NONE, "BehindYouV3")
    var previousFrontKey = false
    var frontToggled = false

    val modDir = File(File(mc.mcDataDir, "W-OVERFLOW"), "BehindYouV3")

    //#if MODERN==0
    @Mod.EventHandler
    fun onPreInit(event: FMLPreInitializationEvent)
    //#else
    //$$ fun onPreInit()
    //#endif
    {
        if (!modDir.exists()) modDir.mkdirs()
        //#if FABRIC==1
        //$$ val file = getFileOfMod(MODID)
        //$$ if (file == null) return
        //#endif
        Updater.addToUpdater(
            //#if FABRIC==0
            event.sourceFile
            //#else
            //$$ file
            //#endif
            , NAME, MODID, VERSION, "W-OVERFLOW/$MODID")
    }

    @Mod.EventHandler
    fun onInit(event: FMLInitializationEvent) {
        //#if MODERN==0
        ClientRegistry.registerKeyBinding(backKeybind)
        ClientRegistry.registerKeyBinding(frontKeybind)
        //#endif
        //#if FABRIC==1
        //$$ KeyBindingHelper.registerKeyBinding(backKeybind)
        //$$ KeyBindingHelper.registerKeyBinding(frontKeybind)
        //#endif

        //#if MODERN==0
        MinecraftForge.EVENT_BUS.register(this)
        //#endif
        //#if FABRIC==1
        //$$ ClientTickEvents.END_CLICK_TICK.register { onClickTick() }
        //#endif

        command("behindyou", aliases = arrayListOf("behindyouv3")) {
            main {
                Config.openScreen()
            }
        }
    }

    //#if MODERN==0
    @SubscribeEvent
    fun clientTick(event: TickEvent.RenderTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        onClickTick()
    }
    //#endif

    fun onClickTick() {
        if (UMinecraft.getWorld() == null || UPlayer.hasPlayer()) return

        if (UScreen.currentScreen != null) {
            if (Config.keybindMode == 0) {
                resetAll()
            }
        }
//
        val backDown = backKeybind.
            //#if MODERN==0
        isKeyDown
        //#else
        //$$ isPressed
        //#endif
        val frontDown = frontKeybind.
            //#if MODERN==0
        isKeyDown
        //#else
        //$$ isPressed
        //#endif

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
            setPerspective()

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

            setPerspective()
        }
    }

    private fun setPerspective() {
        //#if MODERN==0
        mc.renderGlobal.setDisplayListEntitiesDirty()
        //#else
        //$$ mc.gameRenderer.onCameraEntitySet(mc.options.getPerspective().isFirstPerson() ? mc.getCameraEntity() : null)
        //#endif
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        resetAll()
    }

    fun enterBack() {
        backToggled = true
        previousPerspective = getPerspective()
        previousFOV = getFOV()
        setPerspective(2)
        if (Config.changeFOV) {
            setFOV(Config.backFOV)
        }
    }

    fun enterFront() {
        frontToggled = true
        previousPerspective = getPerspective()
        previousFOV = getFOV()
        setPerspective(1)
        if (Config.changeFOV) {
            setFOV(Config.frontFOV)
        }
    }

    fun resetBack() {
        backToggled = false
        setPerspective(previousPerspective
            //#if MODERN==1
            //$$ .ordinal()
            //#endif
        )
        setFOV(previousFOV)
    }

    fun resetFront() {
        frontToggled = false
        setPerspective(previousPerspective
        //#if MODERN==1
        //$$ .ordinal()
        //#endif
        )
        setFOV(previousFOV)
    }

    fun resetAll() {
        if (frontToggled) {
            resetFront()
        }
        if (backToggled) {
            resetBack()
        }
    }

    private fun getPerspective() =
        //#if MODERN==0
        UMinecraft.getSettings().thirdPersonView
        //#else
        //$$ UMinecraft.getSettings().perspective

    private fun setPerspective(value: Int) {
        //#if MODERN==0
        UMinecraft.getSettings().thirdPersonView = value
        //#else
        //$$ UMinecraft.getSettings().perspective = Perspective.values()[value]
    }

    private fun getFOV() =
        //#if MODERN==0
        UMinecraft.getSettings().fovSetting
        //#else
        //$$ UMinecraft.getSettings().fov

    private fun setFOV(value: Number) {
        //#if MODERN==0
        UMinecraft.getSettings().fovSetting = value.toFloat()
        //#else
        //$$ UMinecraft.getSettings().fov = value.toDouble()
    }

    //#if FABRIC==1
    //$$ fun getFileOfMod(id: String): File? {
    //$$     FabricLoader.getInstance().getModContainer(id).let {
    //$$         if (it.isPresent) {
    //$$             val container = it.get()
    //$$             return when (container.origin.kind) {
    //$$                 ModOrigin.Kind.PATH -> {
    //$$                     container.origin.paths.firstOrNull { file -> file.name.endsWith(".jar") }?.toFile()
    //$$                 }
    //$$                 else -> {
    //$$                     null
    //$$                 }
    //$$             }
    //$$         }
    //$$     }
    //$$     return null
    //$$ }
    //#endif
}

val mc: MCMinecraft
    get() = UMinecraft.getMinecraft()