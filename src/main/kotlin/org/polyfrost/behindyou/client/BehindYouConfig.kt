package org.polyfrost.behindyou.client

import dev.deftu.omnicore.api.client.client
import dev.deftu.omnicore.api.client.input.OmniKeys
import dev.deftu.omnicore.api.client.options.OmniPerspective
import org.polyfrost.behindyou.BehindYouConstants
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
    "${BehindYouConstants.ID}.json",
    "/assets/behindyouv3/behindyou_dark.svg",
    BehindYouConstants.NAME,
    Category.QOL
) {
    @Switch(title = "Enable BehindYou")
    var isEnabled = true

    @RadioButton(title = "Front View KeyBind Handle Mode")
    var frontKeybindHandleMode = KeybindHandleMode.Hold

    @Keybind(title = "Front View KeyBind")
    var frontKeybind = KeybindHelper.builder().keys(OmniKeys.KEY_Y.code).does { isDown ->
        if (!isEnabled || client.screen != null) return@does
        if (frontKeybindHandleMode == KeybindHandleMode.Toggle && !isDown) return@does

        val perspective = when {
            frontKeybindHandleMode == KeybindHandleMode.Hold && !isDown -> OmniPerspective.FIRST_PERSON
            OmniPerspective.currentPerspective == OmniPerspective.THIRD_PERSON_BACK -> OmniPerspective.FIRST_PERSON
            else -> OmniPerspective.THIRD_PERSON_BACK
        }
        BehindYouClient.updatePerspective(perspective)
    }.build()

    @RadioButton(title = "Back View KeyBind Handle Mode")
    var backKeybindHandleMode = KeybindHandleMode.Hold

    @Keybind(title = "Back View KeyBind")
    var backKeybind = KeybindHelper.builder().keys(OmniKeys.KEY_U.code).does { isDown ->
        if (!isEnabled || client.screen != null) return@does
        if (backKeybindHandleMode == KeybindHandleMode.Toggle && !isDown) return@does

        val perspective = when {
            backKeybindHandleMode == KeybindHandleMode.Hold && !isDown -> OmniPerspective.FIRST_PERSON
            OmniPerspective.currentPerspective == OmniPerspective.THIRD_PERSON_FRONT -> OmniPerspective.FIRST_PERSON
            else -> OmniPerspective.THIRD_PERSON_FRONT
        }
        BehindYouClient.updatePerspective(perspective)
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
