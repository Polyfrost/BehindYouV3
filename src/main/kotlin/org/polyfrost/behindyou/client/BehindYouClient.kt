package org.polyfrost.behindyou.client

import dev.deftu.omnicore.api.client.client
import dev.deftu.omnicore.api.client.options.OmniPerspective
import dev.deftu.omnicore.api.client.options.OmniVideoSettings
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.InitializationEvent
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.unit.seconds

object BehindYouClient {
    var fov: Float
        get() = OmniVideoSettings.fov.toFloat()
        set(value) {
            client.options.fov().set(value.toInt())
        }

    private var baselineFov = 0f
    private var initialFov = 0f
    private var isFovActive = false

    @get:JvmStatic
    var previousPerspective = OmniPerspective.FIRST_PERSON
        private set

    private lateinit var zAnimation: Animation
    private lateinit var fovAnimation: Animation

    @JvmStatic val isFinished: Boolean
        get() {
            setupAnimations()
            return ::zAnimation.isInitialized && zAnimation.isFinished
        }

    fun initialize() {
        BehindYouConfig.preload()

        eventHandler<InitializationEvent> {
            baselineFov = fov
            initialFov = fov
        }
    }

    @JvmStatic
    fun getLevel(zIn: Double, partialTicks: Float): Double {
        setupAnimations()
        val deltaTime = partialTicks.toNanoseconds()
        if (BehindYouConfig.isFovChanged && isFovActive) {
            fov = fovAnimation.update(deltaTime)
        } else {
            if (OmniPerspective.currentPerspective == OmniPerspective.FIRST_PERSON) {
                baselineFov = fov
            }
        }

        return zAnimation.update(deltaTime).toDouble().coerceAtMost(zIn)
    }

    fun modifyAnimations(duration: Long, curve: Animations) {
        zAnimation = curve.create(duration, zAnimation.value, zAnimation.to)
        zAnimation.finishNow()
        fovAnimation = curve.create(duration, fovAnimation.value, fovAnimation.to)
        fovAnimation.finishNow()
    }

    private fun setTargetLevel(z: Float, fov: Float) {
        setupAnimations()

        val animations = BehindYouConfig.isCameraAnimated
        zAnimation.to = z
        zAnimation.from = if (animations) zAnimation.value else z
        zAnimation.reset()
        if (!BehindYouConfig.isFovChanged) return
        fovAnimation.to = fov
        fovAnimation.from = if (animations) fovAnimation.value else fov
        fovAnimation.reset()
    }

    @JvmStatic
    fun updatePerspective(perspective: OmniPerspective) {
        val currentPerspective = OmniPerspective.currentPerspective
        val (z, targetFov) = when (perspective) {
            OmniPerspective.THIRD_PERSON_FRONT -> {
                if (currentPerspective == OmniPerspective.FIRST_PERSON) {
                    baselineFov = fov
                }

                BehindYouConfig.backDistance to BehindYouConfig.backFov
            }

            OmniPerspective.THIRD_PERSON_BACK -> {
                if (currentPerspective == OmniPerspective.FIRST_PERSON) {
                    baselineFov = fov
                }

                BehindYouConfig.frontDistance to BehindYouConfig.frontFov
            }

            else -> {
                val zReset = 0.3f
                zReset to baselineFov
            }
        }

        setTargetLevel(z, targetFov)
        if (perspective == OmniPerspective.FIRST_PERSON) {
            if (BehindYouConfig.isFovChanged) {
                fov = baselineFov
            }

            isFovActive = false
            fovAnimation.to = baselineFov
            fovAnimation.from = baselineFov
            fovAnimation.reset()
        } else {
            isFovActive = BehindYouConfig.isFovChanged
            if (isFovActive) {
                fovAnimation.from = fov
                fovAnimation.to = targetFov
                fovAnimation.reset()
            }
        }

        if (currentPerspective != perspective) {
            previousPerspective = currentPerspective
        }

        perspective.apply()
    }

    private fun setupAnimations() {
        if (!::zAnimation.isInitialized) {
            zAnimation = Animations.EaseOutQuart.create(BehindYouConfig.animSpeed.seconds, 0f, 0f)
            zAnimation.finishNow()
        }

        if (!::fovAnimation.isInitialized) {
            fovAnimation = Animations.EaseOutQuart.create(BehindYouConfig.animSpeed.seconds, initialFov, initialFov)
            fovAnimation.finishNow()
        }
    }

    fun previous() {
        updatePerspective(previousPerspective)
    }

    // partial ticks are a fraction (0..1) of a tick, which is 50ms.
    private fun Float.toNanoseconds(): Long {
        return (this * 50_000_000f).toLong()
    }
}