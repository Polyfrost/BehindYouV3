package org.polyfrost.behindyou.client

import dev.deftu.omnicore.client.OmniClient

//#if MC >= 1.16.5
//$$ import net.minecraft.client.CameraType
//#endif

enum class PlayerPerspective {

    NORMAL,
    BACK,
    FRONT;

    fun apply() {
        currentPerspective = this
    }

    companion object {

        var rawCurrentPerspective: Int
            get() {
                //#if MC >= 1.16.5
                //$$ return OmniClient.getInstance().options.cameraType.ordinal
                //#else
                return OmniClient.getInstance().gameSettings.thirdPersonView
                //#endif
            }
            set(value) {
                if (value < 0 || value > 2) {
                    throw IllegalArgumentException("Invalid perspective value: $value")
                }

                //#if MC >= 1.16.5
                //$$ OmniClient.getInstance().options.cameraType = CameraType.values().getOrNull(value)
                //#else
                OmniClient.getInstance().gameSettings.thirdPersonView = value
                //#endif
            }

        @Suppress("EnumValuesSoftDeprecate")
        var currentPerspective: PlayerPerspective
            get() = values().getOrElse(rawCurrentPerspective) { NORMAL }
            set(value) {
                rawCurrentPerspective = value.ordinal
            }

        @JvmStatic
        @Suppress("EnumValuesSoftDeprecate")
        fun get(ordinal: Int): PlayerPerspective? {
            return values().getOrNull(ordinal)
        }

    }

}
