package org.polyfrost.behindyou.client

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import org.polyfrost.oneconfig.api.event.v1.eventHandler
import org.polyfrost.oneconfig.api.event.v1.events.WorldEvent

object BehindYouClient {
    // blocks from target at which the eased out z animation counts as arrived for first person switching
    private const val ARRIVAL_EPSILON = 0.5f

    private const val VANILLA_DISTANCE = 4f

    private val minecraft: Minecraft
        get() = Minecraft.getInstance()

    private val fov: Float
        get() = minecraft.options.fov().get().toFloat()

    @get:JvmStatic var previousPerspective = CameraType.FIRST_PERSON
        private set

    private lateinit var zAnimation: Animation
    private lateinit var fovAnimation: Animation
    private var managedPerspective: CameraType? = null

    @JvmStatic val isManagingPerspective: Boolean
        get() {
            val managed = managedPerspective ?: return false
            if (minecraft.options.cameraType != managed) {
                managedPerspective = null
                return false
            }

            if (managed == CameraType.FIRST_PERSON &&
                isFinished &&
                (!BehindYouConfig.Fov.enabled || fovAnimation.isFinished)
            ) {
                managedPerspective = null
                return false
            }

            return true
        }

    @JvmStatic val isFinished: Boolean
        get() {
            setupAnimations()
            if (zAnimation.isFinished) return true
            return kotlin.math.abs(zAnimation.value - zAnimation.to) <= ARRIVAL_EPSILON
        }

    fun initialize() {
        ConfigMigrator.migrate()
        BehindYouConfig.preload()

        eventHandler<WorldEvent.Load> {
            ConfigMigrator.notifyPending()
        }
    }

    fun stopManagingPerspective() {
        managedPerspective = null
    }

    @JvmStatic
    fun getLevel(zIn: Double): Double {
        if (!isManagingPerspective) return zIn

        setupAnimations()
        return zAnimation.update().toDouble().coerceAtMost(zIn)
    }

    @JvmStatic
    fun getFov(fovIn: Float): Float {
        if (!BehindYouConfig.isEnabled || !BehindYouConfig.Fov.enabled || !isManagingPerspective) return fovIn

        setupAnimations()
        if (minecraft.options.cameraType == CameraType.FIRST_PERSON &&
            fovAnimation.isFinished &&
            fovAnimation.to != fovIn
        ) {
            fovAnimation.from = fovIn
            fovAnimation.to = fovIn
            fovAnimation.finishNow()
        }

        return fovAnimation.update()
    }

    @JvmStatic
    fun updatePerspective(perspective: CameraType) {
        val currentPerspective = minecraft.options.cameraType
        if (currentPerspective == perspective) return
        if (!isManagingPerspective) resetAnimationsToVanilla(currentPerspective)

        val (z, targetFov) = thirdPersonTargets(perspective) ?: (0.3f to fov)
        val animate = shouldAnimate(currentPerspective, perspective)

        setTargetLevel(z, targetFov, animate)
        if (animate && currentPerspective != CameraType.FIRST_PERSON && perspective != CameraType.FIRST_PERSON) {
            zAnimation.from = 0.3f
            zAnimation.reset()
        }

        previousPerspective = currentPerspective
        managedPerspective = perspective
        minecraft.options.setCameraType(perspective)
    }

    private fun shouldAnimate(from: CameraType, to: CameraType): Boolean {
        if (!BehindYouConfig.Animation.enabled) return false

        val isReturning = (to == CameraType.FIRST_PERSON)
        val mode = when (if (isReturning) from else to) {
            CameraType.THIRD_PERSON_FRONT -> BehindYouConfig.Animation.front
            CameraType.THIRD_PERSON_BACK -> BehindYouConfig.Animation.back
            else -> return false
        }

        return if (isReturning) mode.animateReturn else mode.animateEnter
    }

    private fun resetAnimationsToVanilla(perspective: CameraType) {
        setupAnimations()

        zAnimation.to = if (perspective == CameraType.FIRST_PERSON) 0f else VANILLA_DISTANCE
        zAnimation.finishNow()
        fovAnimation.to = fov
        fovAnimation.finishNow()
    }

    private fun setTargetLevel(z: Float, fov: Float, animate: Boolean) {
        setupAnimations()

        zAnimation.to = z
        if (animate) {
            zAnimation.from = zAnimation.value
            zAnimation.reset()
        } else {
            zAnimation.finishNow()
        }

        if (!BehindYouConfig.Fov.enabled) return

        fovAnimation.to = fov
        if (animate) {
            fovAnimation.from = fovAnimation.value
            fovAnimation.reset()
        } else {
            fovAnimation.finishNow()
        }
    }

    private fun setupAnimations() {
        if (::zAnimation.isInitialized && ::fovAnimation.isInitialized) return

        val targets = thirdPersonTargets(minecraft.options.cameraType)

        if (!::zAnimation.isInitialized) {
            val distance = targets?.first ?: 0f
            zAnimation = createAnimation(BehindYouConfig.Animation.speed.seconds, distance, distance)
            zAnimation.finishNow()
        }

        if (!::fovAnimation.isInitialized) {
            val targetFov = targets?.second ?: fov
            fovAnimation = createAnimation(BehindYouConfig.Animation.speed.seconds, targetFov, targetFov)
            fovAnimation.finishNow()
        }
    }

    private fun thirdPersonTargets(perspective: CameraType): Pair<Float, Float>? = when (perspective) {
        CameraType.THIRD_PERSON_FRONT -> BehindYouConfig.Distance.front to BehindYouConfig.Fov.front
        CameraType.THIRD_PERSON_BACK -> BehindYouConfig.Distance.back to BehindYouConfig.Fov.back
        else -> null
    }

    private fun createAnimation(duration: Long, from: Float, to: Float): Animation =
        Animation(duration, from, to)

    fun syncActivePerspective(
        perspective: CameraType = minecraft.options.cameraType,
        distance: Float? = null,
        targetFov: Float? = null,
    ) {
        if (!isManagingPerspective || minecraft.options.cameraType != perspective) return
        val targets = thirdPersonTargets(perspective) ?: return

        setupAnimations()
        zAnimation.to = distance ?: targets.first
        zAnimation.finishNow()
        fovAnimation.to = targetFov ?: targets.second
        fovAnimation.finishNow()
    }

    fun modifyAnimations(duration: Long) {
        // animations that do not exist yet read the new duration from config in setupAnimations
        if (!::zAnimation.isInitialized || !::fovAnimation.isInitialized) return

        zAnimation = createAnimation(duration, zAnimation.value, zAnimation.to)
        zAnimation.finishNow()
        fovAnimation = createAnimation(duration, fovAnimation.value, fovAnimation.to)
        fovAnimation.finishNow()
    }
}
