package org.polyfrost.behindyou.client

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.InitializationEvent

object BehindYouClient {
    // How close (in blocks) the camera must get to its target before we treat the animation as done
    // for the purpose of switching INTO first person. The z animation eases out, so it decelerates
    private const val ARRIVAL_EPSILON = 0.5f

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

    @JvmStatic val isFinished: Boolean
        get() {
            setupAnimations()
            if (zAnimation.isFinished) return true
            return kotlin.math.abs(zAnimation.value - zAnimation.to) <= ARRIVAL_EPSILON
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
        val deltaTime = (partialTicks * 50_000_000f).toLong()

        if (BehindYouConfig.Fov.enabled) {
            fov = fovAnimation.update(deltaTime)
        } else if (minecraft.options.cameraType == CameraType.FIRST_PERSON) {
            baselineFov = fov
        }

        return zAnimation.update(deltaTime).toDouble().coerceAtMost(zIn)
    }

    @JvmStatic
    fun updatePerspective(perspective: CameraType) {
        val currentPerspective = minecraft.options.cameraType
        if (currentPerspective == perspective) return

        val (z, targetFov) = when (perspective) {
            CameraType.THIRD_PERSON_FRONT -> {
                if (currentPerspective == CameraType.FIRST_PERSON) {
                    baselineFov = fov
                }

                BehindYouConfig.Distance.back to BehindYouConfig.Fov.back
            }

            CameraType.THIRD_PERSON_BACK -> {
                if (currentPerspective == CameraType.FIRST_PERSON) {
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
        if (BehindYouConfig.Animation.enabled &&
            currentPerspective != CameraType.FIRST_PERSON &&
            perspective != CameraType.FIRST_PERSON
        ) {
            zAnimation.from = 0.3f
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
        zAnimation.reset()

        if (!BehindYouConfig.Fov.enabled) return

        if (!animations) this.fov = fov
        fovAnimation.to = fov
        fovAnimation.from = if (animations) fovAnimation.value else fov
        fovAnimation.reset()
    }

    private fun setupAnimations() {
        if (!::zAnimation.isInitialized) {
            zAnimation = createAnimation(BehindYouConfig.Animation.speed.seconds, 0.1f, 0f)
            zAnimation.finishNow()
        }

        if (!::fovAnimation.isInitialized) {
            fovAnimation = createAnimation(BehindYouConfig.Animation.speed.seconds, initialFov, initialFov)
            fovAnimation.finishNow()
        }
    }

    private fun createAnimation(duration: Long, from: Float, to: Float): Animation =
        Animation(duration, from, to)

    fun modifyAnimations(duration: Long) {
        zAnimation = createAnimation(duration, zAnimation.value, zAnimation.to)
        zAnimation.finishNow()
        fovAnimation = createAnimation(duration, fovAnimation.value, fovAnimation.to)
        fovAnimation.finishNow()
    }
}
