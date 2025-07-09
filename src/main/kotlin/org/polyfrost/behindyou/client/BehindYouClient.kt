package org.polyfrost.behindyou.client

import dev.deftu.omnicore.client.OmniClient
import dev.deftu.omnicore.common.OmniLoader
import org.polyfrost.behindyou.client.config.BehindYouConfig
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.InitializationEvent
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.unit.seconds

object BehindYouClient {

    private val isPatcher by lazy {
        OmniLoader.isModLoaded("patcher")
    }

    var fov: Float
        get() {
            return OmniClient.getInstance().gameSettings.fovSetting
                //#if MC >= 1.19.2
                //$$ .value.toFloat()
                //#elseif MC >= 1.16.5
                //$$ .toFloat()
                //#endif
        }
        set(value) {
            //#if MC >= 1.19.2
            //$$ OmniClient.getInstance().options.fov.setValue(value.toInt())
            //#else
            OmniClient.getInstance().gameSettings.fovSetting = value
                //#if MC >= 1.16.5
                //$$ .toDouble()
                //#endif
            //#endif
        }
    private var initialFov = 0f

    private var previousPerspective = PlayerPerspective.NORMAL

    private var zAnimation: Animation = Animations.EaseOutQuart.create(BehindYouConfig.animSpeed.seconds, 0f, 0f)
    private var fovAnimation: Animation = Animations.EaseOutQuart.create(BehindYouConfig.animSpeed.seconds, initialFov, initialFov)

    @JvmStatic val isFinished get() = zAnimation.isFinished

    fun initialize() {
        BehindYouConfig.preload()

        eventHandler<InitializationEvent> { initialFov = fov }
    }

    @JvmStatic
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

    @JvmStatic
    fun updatePerspective(perspective: PlayerPerspective) {
        val currentPerspective = PlayerPerspective.currentPerspective
        val mc = OmniClient.getInstance()
        val z: Float
        val fov: Float
        val config = BehindYouConfig

        when (perspective) {
            PlayerPerspective.BACK -> {
                if (currentPerspective == PlayerPerspective.NORMAL) initialFov = this.fov
                z = config.backDistance
                fov = config.backFOV
            }

            PlayerPerspective.FRONT -> {
                if (currentPerspective == PlayerPerspective.NORMAL) initialFov = this.fov
                z = config.frontDistance
                fov = config.frontFOV
            }

            else -> {
                //#if MC <= 1.12.2
                z = if (isPatcher && club.sk1er.patcher.config.PatcherConfig.parallaxFix) -0.05f else 0.1f
                //#else
                //$$ z = 0.1f
                //#endif
                fov = initialFov
            }
        }

        setTargetLevel(z, fov)
        mc.renderGlobal.setDisplayListEntitiesDirty()
        val prev = currentPerspective
        if (prev != perspective) previousPerspective = prev
        perspective.apply()
    }

    fun previous() {
        updatePerspective(previousPerspective)
    }

    // partial ticks are a fraction (0..1) of a tick, which is 50ms.
    private fun Float.toNanoseconds() = (this * 50_000_000f).toLong()

}