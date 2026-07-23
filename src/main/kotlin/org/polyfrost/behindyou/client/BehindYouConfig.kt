package org.polyfrost.behindyou.client

import net.minecraft.client.CameraType
import net.minecraft.client.Minecraft
import org.lwjgl.glfw.GLFW
import org.polyfrost.behindyou.BehindYouConstants
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Accordion
import org.polyfrost.oneconfig.api.config.v1.annotations.Include
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindHelper
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager

object BehindYouConfig : Config(
    "${BehindYouConstants.ID}.json",
    "/assets/behindyouv3/behindyou_dark.svg",
    BehindYouConstants.NAME,
    Category.QOL
) {
    private val minecraft: Minecraft
        get() = Minecraft.getInstance()

    @Switch(title = "Enable BehindYou", description = "Master switch to enable/disable the mod")
    var isEnabled = true

    @Accordion(title = "Keybinds", subcategory = "Keybind Settings", index = 0)
    object Keybinds {
        @Keybind(title = "Back View Keybind")
        var backKeybind = KeybindHelper.builder().key(GLFW.GLFW_KEY_UNKNOWN).action { isDown ->
            if (!isEnabled || backKeybindHandleMode == KeybindHandleMode.Toggle && !isDown) return@action false

            val perspective = when {
                backKeybindHandleMode == KeybindHandleMode.Hold && !isDown -> CameraType.FIRST_PERSON
                minecraft.options.cameraType == CameraType.THIRD_PERSON_BACK -> CameraType.FIRST_PERSON
                else -> CameraType.THIRD_PERSON_BACK
            }
            BehindYouClient.updatePerspective(perspective)
            true
        }.build()

        @RadioButton(title = "Back View Handle Mode")
        var backKeybindHandleMode = KeybindHandleMode.Hold

        @Keybind(title = "Front View Keybind")
        var frontKeybind = KeybindHelper.builder().key(GLFW.GLFW_KEY_UNKNOWN).action { isDown ->
            if (!isEnabled || frontKeybindHandleMode == KeybindHandleMode.Toggle && !isDown) return@action false

            val perspective = when {
                frontKeybindHandleMode == KeybindHandleMode.Hold && !isDown -> CameraType.FIRST_PERSON
                minecraft.options.cameraType == CameraType.THIRD_PERSON_FRONT -> CameraType.FIRST_PERSON
                else -> CameraType.THIRD_PERSON_FRONT
            }
            BehindYouClient.updatePerspective(perspective)
            true
        }.build()

        @RadioButton(title = "Front View Handle Mode")
        var frontKeybindHandleMode = KeybindHandleMode.Hold
    }

    @Accordion(title = "Animation Settings", description = "Animate the camera between perspectives", subcategory = "Camera Settings", index = 1)
    object Animation {
        @Include
        var enabled = true

        @Slider(title = "Animation Time (secs)", min = 0.1f, max = 2f, step = 0.1f)
        var speed = 1f
    }

    @Accordion(title = "FOV Settings", description = "Modify your field of view", subcategory = "Camera Settings", index = 2)
    object Fov {
        @Include
        var enabled = true

        @Slider(title = "Back View FOV", min = 30F, max = 110F)
        var back = 90f

        @Slider(title = "Front View FOV", min = 30F, max = 110F)
        var front = 90f
    }

    @Accordion(title = "Distance Settings", description = "Modify your camera distance", subcategory = "Camera Settings", index = 3)
    object Distance {
        @Slider(title = "Back View Distance", min = 1f, max = 4f, step = 0.1f)
        var back = 4f

        @Slider(title = "Front View Distance", min = 1f, max = 4f, step = 0.1f)
        var front = 4f
    }

    init {
        addDependency("Animation.speed", "Animation.enabled")
        addCallback("Animation.speed") { value: Float ->
            BehindYouClient.modifyAnimations(value.seconds)
            false
        }

        addDependency("Fov.back", "Fov.enabled")
        addDependency("Fov.front", "Fov.enabled")

        KeybindManager.register(Keybinds.backKeybind)
        KeybindManager.register(Keybinds.frontKeybind)
    }
}
