package org.polyfrost.behindyou.client

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.InitializationEvent
import org.polyfrost.polyui.animate.Animation
import org.polyfrost.polyui.animate.Easing
import org.polyfrost.polyui.unit.seconds

object BehindYouClient {
    private val minecraft: Minecraft
        get() = Minecraft.getInstance()

    var fov: Float
        get() = minecraft.options.fov().get().toFloat()
        set(value) {
            minecraft.options.fov().set(value.toInt())
        }

    private var baselineFov = 0f
    private var initialFov = 0f

    @get:JvmStatic var previousPerspective = CameraType.FIRST_PERSON
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
        } else if (minecraft.options.cameraType == CameraType.FIRST_PERSON) {
            baselineFov = fov
        }

        return zAnimation.update(currentTime - zAnimationStartTime).toDouble().coerceAtMost(zIn)
    }

    @JvmStatic
    fun updatePerspective(perspective: CameraType) {
        val currentPerspective = minecraft.options.cameraType
        if (currentPerspective == perspective) return

        val (z, targetFov) = when (perspective) {
            CameraType.THIRD_PERSON_FRONT -> {
                if (currentPerspective.isFirstPerson) {
                    baselineFov = fov
                }

                BehindYouConfig.Distance.back to BehindYouConfig.Fov.back
            }

            CameraType.THIRD_PERSON_BACK -> {
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
        if (BehindYouConfig.Animation.enabled && !currentPerspective.isFirstPerson && !perspective.isFirstPerson) {
            zAnimation.from = 0.3f
            zAnimationStartTime = System.nanoTime()
            zAnimation.reset()
        }

        previousPerspective = currentPerspective
        minecraft.options.setCameraType(perspective)
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
            zAnimation = createAnimation(BehindYouConfig.Animation.speed.seconds, 0.3f, 0f)
            zAnimation.finishNow()
        }

        if (!::fovAnimation.isInitialized) {
            fovAnimation = createAnimation(BehindYouConfig.Animation.speed.seconds, initialFov, initialFov)
            fovAnimation.finishNow()
        }
    }

    private fun createAnimation(duration: Long, from: Float, to: Float): Animation =
        Easing.Quart(Easing.Type.Out, duration, from, to)

    fun modifyAnimations(duration: Long) {
        zAnimation = createAnimation(duration, zAnimation.value, zAnimation.to)
        zAnimation.finishNow()
        fovAnimation = createAnimation(duration, fovAnimation.value, fovAnimation.to)
        fovAnimation.finishNow()
    }
}
