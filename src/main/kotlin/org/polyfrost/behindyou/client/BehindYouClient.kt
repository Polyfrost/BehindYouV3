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

    @get:JvmStatic var previousPerspective = OmniPerspective.FIRST_PERSON
        private set

    private lateinit var zAnimation: Animation
    private lateinit var fovAnimation: Animation
    private var zAnimationStartTime = 0L
    private var fovAnimationStartTime = 0L

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
    fun getLevel(zIn: Double): Double {
        setupAnimations()
        val currentTime = System.nanoTime()

        if (BehindYouConfig.Fov.enabled) {
            fov = fovAnimation.update(currentTime - fovAnimationStartTime)
        } else if (OmniPerspective.currentPerspective == OmniPerspective.FIRST_PERSON) {
            baselineFov = fov
        }

        return zAnimation.update(currentTime - zAnimationStartTime).toDouble().coerceAtMost(zIn)
    }

    @JvmStatic
    fun updatePerspective(perspective: OmniPerspective) {
        val currentPerspective = OmniPerspective.currentPerspective
        if (currentPerspective == perspective) return

        val (z, targetFov) = when (perspective) {
            OmniPerspective.THIRD_PERSON_FRONT -> {
                if (currentPerspective.isFirstPerson) {
                    baselineFov = fov
                }

                BehindYouConfig.Distance.back to BehindYouConfig.Fov.back
            }

            OmniPerspective.THIRD_PERSON_BACK -> {
                if (currentPerspective.isFirstPerson) {
                    baselineFov = fov
                }

                BehindYouConfig.Distance.front to BehindYouConfig.Fov.front
            }

            else -> {
                val zReset = 0.3f
                zReset to baselineFov
            }
        }

        setTargetLevel(z, targetFov)
        if (BehindYouConfig.Animation.enabled && currentPerspective.isThirdPerson && perspective.isThirdPerson) {
            zAnimation.from = 0.3f
            zAnimationStartTime = System.nanoTime()
            zAnimation.reset()
        }

        previousPerspective = currentPerspective
        perspective.apply()
    }

    private fun setTargetLevel(z: Float, fov: Float) {
        setupAnimations()
        val animations = BehindYouConfig.Animation.enabled

        zAnimation.to = z
        zAnimation.from = if (animations) zAnimation.value else z
        zAnimationStartTime = System.nanoTime()
        zAnimation.reset()

        if (!BehindYouConfig.Fov.enabled) return

        if (!animations) this.fov = fov
        fovAnimation.to = fov
        fovAnimation.from = if (animations) fovAnimation.value else fov
        fovAnimationStartTime = System.nanoTime()
        fovAnimation.reset()
    }

    private fun setupAnimations() {
        if (!::zAnimation.isInitialized) {
            zAnimation = Animations.EaseOutQuart.create(BehindYouConfig.Animation.speed.seconds, 0.3f, 0f)
            zAnimation.finishNow()
        }

        if (!::fovAnimation.isInitialized) {
            fovAnimation = Animations.EaseOutQuart.create(BehindYouConfig.Animation.speed.seconds, initialFov, initialFov)
            fovAnimation.finishNow()
        }
    }

    fun modifyAnimations(duration: Long, curve: Animations) {
        zAnimation = curve.create(duration, zAnimation.value, zAnimation.to)
        zAnimation.finishNow()
        fovAnimation = curve.create(duration, fovAnimation.value, fovAnimation.to)
        fovAnimation.finishNow()
    }
}