package org.polyfrost.behindyou.client.config

import org.polyfrost.behindyou.client.BehindYouClient
import org.polyfrost.behindyou.client.PlayerPerspective
import org.polyfrost.oneconfig.api.config.v1.Config
import org.polyfrost.oneconfig.api.config.v1.annotations.*
import org.polyfrost.oneconfig.api.ui.v1.keybind.KeybindManager
import org.polyfrost.polyui.animate.Animations
import org.polyfrost.polyui.input.KeybindHelper
import org.polyfrost.polyui.unit.seconds

object BehindYouConfig : Config("behindyouv3.json", "/behindyou_dark.svg", "BehindYouV3", Category.QOL) {

    private var frontActive = false

    @Keybind(
        title = "Frontview Keybind"
    )
    var frontKeybind = KeybindHelper.builder().chars('y').does {
        if (frontKeybindMode == 0) frontActive = it
        else { // toggle
            // only listen to key down
            if (it) frontActive = !frontActive
        }

        if (frontActive) {
            PlayerPerspective.currentPerspective = PlayerPerspective.FRONT
        } else {
            BehindYouClient.previous()
        }
    }.build()

    @RadioButton(
        title = "Frontview Keybind Handle Mode",
        options = ["Hold", "Toggle"]
    )
    var frontKeybindMode = 0

    private var backActive = false

    @Keybind(
        title = "Backview Keybind"
    )
    var backKeybind = KeybindHelper.builder().chars('u').does {
        if (backKeybindMode == 0) backActive = it
        else { // toggle
            // only listen to key down
            if (it) backActive = !backActive
        }

        if (backActive) {
            PlayerPerspective.currentPerspective = PlayerPerspective.BACK
        } else {
            BehindYouClient.previous()
        }
    }.build()

    @RadioButton(
        title = "Backview Keybind Handle Mode",
        options = ["Hold", "Toggle"]
    )
    var backKeybindMode = 0

    @Checkbox(
        title = "Camera Animations"
    )
    var useAnims = true

    @Slider(
        title = "Animation Time (s)",
        min = 0.1f, max = 2f
    )
    var animSpeed = 1f

    @Switch(
        title = "Modify FOV",
    )
    var changeFOV = true

    @Slider(
        title = "Backview FOV",
        min = 30F,
        max = 110F
    )
    var backFOV = 100f

    @Slider(
        title = "Backview Distance",
        min = 1f,
        max = 4f
    )
    var backDistance = 4f

    @Slider(
        title = "Frontview FOV",
        min = 30F,
        max = 110F
    )
    var frontFOV = 100f

    @Slider(
        title = "Frontview Distance",
        min = 1f,
        max = 4f
    )
    var frontDistance = 4f

    init {
        addDependency("animSpeed", "useAnims")
        addDependency("backFOV", "changeFOV")
        addDependency("frontFOV", "changeFOV")
        addCallback("animSpeed") { value: Float ->
            BehindYouClient.modifyAnimations(value.seconds, Animations.EaseOutQuart)
            false
        }

        KeybindManager.registerKeybind(frontKeybind)
        KeybindManager.registerKeybind(backKeybind)
    }
}
