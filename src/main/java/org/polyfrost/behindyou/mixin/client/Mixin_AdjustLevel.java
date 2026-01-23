package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.deftu.omnicore.api.client.render.OmniRenderTicks;
import net.minecraft.client.Camera;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class Mixin_AdjustLevel {
    @WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float adjustLevel(Camera instance, float zoom, Operation<Float> original) {
        float maxZoom = original.call(instance, zoom);

        if (BehindYouConfig.INSTANCE.isEnabled()) {
            return (float) BehindYouClient.getLevel(maxZoom, OmniRenderTicks.get());
        } else {
            return maxZoom;
        }
    }
}
