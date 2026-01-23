package org.polyfrost.behindyou.client

import dev.deftu.omnicore.api.client.client
import dev.deftu.omnicore.api.client.input.OmniKeys
import dev.deftu.omnicore.api.client.options.OmniPerspective
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.Checkbox
import org.polyfrost.oneconfig.api.config.v1.annotations.Keybind
import org.polyfrost.oneconfig.api.config.v1.annotations.RadioButton
import org.polyfrost.oneconfig.api.config.v1.annotations.Slider
import org.polyfrost.oneconfig.api.config.v1.annotations.Switch
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.input.KeybindHelper
import org.polyfrost.polyui.unit.seconds

object BehindYouConfig : Config(
    "behindyouv3.json",
    "/assets/behindyouv3/behindyou_dark.svg",
    "BehindYouV3",
    Category.QOL
) {
    @Switch(title = "Enable BehindYou")
    var isEnabled = true

    @RadioButton(title = "Front View KeyBind Handle Mode")
    var frontKeybindToggleMode = ToggleKeybind.Hold

    @Keybind(title = "Front View KeyBind")
    var frontKeybind = KeybindHelper.builder().keys(OmniKeys.KEY_Y.code).does { isDown ->
        if (client.screen != null) return@does

        when (frontKeybindToggleMode) {
            ToggleKeybind.Hold -> isFrontViewActive = isDown
            ToggleKeybind.Toggle -> {
                if (!isDown) return@does
                isFrontViewActive = !isFrontViewActive
            }
        }

        if (isFrontViewActive) {
            BehindYouClient.updatePerspective(OmniPerspective.THIRD_PERSON_FRONT)
        } else {
            BehindYouClient.previous()
        }
    }.build()

    @RadioButton(title = "Back View KeyBind Handle Mode")
    var backKeybindToggleMode = ToggleKeybind.Hold

    @Keybind(title = "Back View KeyBind")
    var backKeybind = KeybindHelper.builder().keys(OmniKeys.KEY_U.code).does { isDown ->
        if (client.screen != null) return@does

        when (backKeybindToggleMode) {
            ToggleKeybind.Hold -> isBackViewActive = isDown
            ToggleKeybind.Toggle -> {
                if (!isDown) return@does
                isBackViewActive = !isBackViewActive
            }
        }

        if (isBackViewActive) {
            BehindYouClient.updatePerspective(OmniPerspective.THIRD_PERSON_BACK)
        } else {
            BehindYouClient.previous()
        }
    }.build()

    @Checkbox(title = "Camera Animations")
    var isCameraAnimated = true

    @Slider(title = "Animation Time (secs)", min = 0.1f, max = 2f)
    var animSpeed = 1f

    @Switch(title = "Modify FOV")
    var isFovChanged = true

    @Slider(title = "Back View FOV", min = 30F, max = 110F)
    var backFov = 90f

    @Slider(title = "Back View Distance", min = 1f, max = 4f)
    var backDistance = 4f

    @Slider(title = "Front View FOV", min = 30F, max = 110F)
    var frontFov = 90f

    @Slider(title = "Front View Distance", min = 1f, max = 4f)
    var frontDistance = 4f

    init {
        addDependency("animSpeed", "isCameraAnimated")
        addDependency("backFov", "isFovChanged")
        addDependency("frontFov", "isFovChanged")
        addCallback("animSpeed") { value: Float ->
            BehindYouClient.modifyAnimations(value.seconds, Animations.EaseOutQuart)
            false
        }

        KeybindManager.registerKeybind(frontKeybind)
        KeybindManager.registerKeybind(backKeybind)
    }
}
