package org.polyfrost.behindyou

import dev.deftu.omnicore.common.OmniLoader
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.Mod
import org.polyfrost.behindyou.config.BehindYouConfig
import org.polyfrost.oneconfig.api.platform.v1.Platform
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.unit.seconds

@Mod(
    modid = "@MOD_ID@",
    name = "@MOD_NAME@",
    version = "@MOD_VERSION@",
    clientSideOnly = true,
    modLanguageAdapter = "org.polyfrost.oneconfig.utils.v1.forge.KotlinLanguageAdapter"
)
object BehindYou {
    const val NORMAL = 0
    const val BACK = 1
    const val FRONT = 2

    private val isPatcher by lazy {
        OmniLoader.isModLoaded("patcher")
    }

    var fov: Float
        get() = Minecraft.getMinecraft().gameSettings.fovSetting
        set(value) {
            Minecraft.getMinecraft().gameSettings.fovSetting = value
        }
    private var initialFov = fov

    /**
     * 0: normal
     * 1: back
     * 2: front
     */
    var perspective: Int
        get() = Minecraft.getMinecraft().gameSettings.thirdPersonView
        set(value) {
            val mc = Minecraft.getMinecraft()
            val z: Float
            val fov: Float
            val config = BehindYouConfig
            when (value) {
                BACK -> {
                    if (perspective == NORMAL) initialFov = this.fov
                    z = config.backDistance
                    fov = config.backFOV
                }

                FRONT -> {
                    if (perspective == NORMAL) initialFov = this.fov
                    z = config.frontDistance
                    fov = config.frontFOV
                }

                else -> {
                    z = if (isPatcher && club.sk1er.patcher.config.PatcherConfig.parallaxFix) -0.05f else 0.1f
                    fov = initialFov
                }
            }

            setTargetLevel(z, fov)
            mc.renderGlobal.setDisplayListEntitiesDirty()
            val prev = perspective
            if (prev != value) previousPerspective = prev
            mc.gameSettings.thirdPersonView = value
        }
    private var previousPerspective = NORMAL

    private var zAnimation: Animation = Animations.EaseOutQuart.create(BehindYouConfig.animSpeed.seconds, 0f, 0f)
    private var fovAnimation: Animation = Animations.EaseOutQuart.create(BehindYouConfig.animSpeed.seconds, initialFov, initialFov)
    val isFinished get() = zAnimation.isFinished


    fun getLevel(zIn: Double, partialTicks: Float): Double {
        if (!BehindYouConfig.enabled) return zIn
        val deltaTime = partialTicks.toNanoseconds()
        if (BehindYouConfig.changeFOV) fov = fovAnimation.update(deltaTime)
        return zAnimation.update(deltaTime).toDouble()
    }

    fun modifyAnimations(duration: Long, curve: Animations) {
        zAnimation = curve.create(duration, zAnimation.value, zAnimation.to)
        fovAnimation = curve.create(duration, fovAnimation.value, fovAnimation.to)
    }

    private fun setTargetLevel(z: Float, fov: Float) {
        val animations = BehindYouConfig.useAnims
        zAnimation.to = z
        zAnimation.from = if (animations) zAnimation.value else z
        zAnimation.reset()
        if (!BehindYouConfig.changeFOV) return
        fovAnimation.to = fov
        fovAnimation.from = if (animations) fovAnimation.value else fov
        fovAnimation.reset()
    }

    fun previous() {
        perspective = previousPerspective
    }

    // partial ticks are a fraction (0..1) of a tick, which is 50ms.
    private fun Float.toNanoseconds() = (this * 50_000_000f).toLong()
}