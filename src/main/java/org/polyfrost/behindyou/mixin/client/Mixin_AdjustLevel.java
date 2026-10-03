package org.polyfrost.behindyou.mixin.client;

//? if > 1.8.9 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Camera;
//?} else {
/*import net.minecraft.client.renderer.GameRenderer;
*///?}
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if = 1.8.9
//import org.spongepowered.asm.mixin.injection.ModifyVariable;

//? if > 1.8.9 {
@Mixin(Camera.class)
public class Mixin_AdjustLevel {
    //? if >=26.1 {
    @WrapOperation(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    //?} else
    //@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float adjustLevel(Camera instance, float zoom, Operation<Float> original) {
        float maxZoom = original.call(instance, zoom);

        if (BehindYouConfig.INSTANCE.isEnabled()) {
            return (float) BehindYouClient.getLevel(maxZoom);
        } else {
            return maxZoom;
        }
    }
}
//?} else {
/*@Mixin(GameRenderer.class)
public class Mixin_AdjustLevel {
    @ModifyVariable(method = "transformCamera", at = @At("STORE"), ordinal = 3)
    private double adjustLevel(double maxZoom) {
        if (BehindYouConfig.INSTANCE.isEnabled()) {
            return BehindYouClient.getLevel(maxZoom);
        } else {
            return maxZoom;
        }
    }
}
*///?}
