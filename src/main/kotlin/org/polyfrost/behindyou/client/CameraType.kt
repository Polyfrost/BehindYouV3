package org.polyfrost.behindyou.client

//? if = 1.8.9 {
/*import net.minecraft.client.Minecraft
import net.minecraft.client.Options

enum class CameraType(val id: Int) {
    FIRST_PERSON(0),
    THIRD_PERSON_BACK(1),
    THIRD_PERSON_FRONT(2);

    companion object {
        @JvmStatic
        fun fromId(id: Int): CameraType = entries[Math.floorMod(id, entries.size)]
    }
}

val Options.cameraType: CameraType
    get() = CameraType.fromId(perspective)

fun Options.setCameraType(perspective: CameraType) {
    this.perspective = perspective.id

    val minecraft = Minecraft.getInstance()
    when (perspective) {
        CameraType.FIRST_PERSON -> minecraft.gameRenderer.updateShader(minecraft.camera)
        CameraType.THIRD_PERSON_BACK -> minecraft.gameRenderer.updateShader(null)
        CameraType.THIRD_PERSON_FRONT -> Unit
    }
    minecraft.worldRenderer.onViewChanged()
}
*///?}
