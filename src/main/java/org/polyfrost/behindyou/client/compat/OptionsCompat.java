package org.polyfrost.behindyou.client.compat;

//? if = 1.8.9 {
/*import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public interface OptionsCompat {
    default Supplier<Integer> fov() {
        return () -> Math.round(self().fov);
    }

    default CameraType getCameraType() {
        return CameraType.fromId(self().perspective);
    }

    default void setCameraType(CameraType cameraType) {
        self().perspective = cameraType.getId();

        Minecraft minecraft = Minecraft.getInstance();
        switch (cameraType) {
            case FIRST_PERSON:
                minecraft.gameRenderer.updateShader(minecraft.getCamera());
                break;
            case THIRD_PERSON_BACK:
                minecraft.gameRenderer.updateShader(null);
                break;
            default:
                break;
        }
        minecraft.worldRenderer.onViewChanged();
    }

    private Options self() {
        return (Options) (Object) this;
    }
}
*///?}
