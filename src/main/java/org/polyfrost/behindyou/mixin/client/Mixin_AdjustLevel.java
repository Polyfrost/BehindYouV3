package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Camera;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class Mixin_AdjustLevel {
    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float adjustLevel(Camera instance, float zoom, Operation<Float> original, @Local(argsOnly = true) float partialTick) {
        float maxZoom = original.call(instance, zoom);

        if (BehindYouConfig.INSTANCE.isEnabled()) {
            return (float) BehindYouClient.getLevel(maxZoom, partialTick);
        } else {
            return maxZoom;
        }
    }
}
