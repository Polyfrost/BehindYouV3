package org.polyfrost.behindyou.mixin.client;

import dev.deftu.omnicore.api.client.render.OmniRenderTicks;
import net.minecraft.client.Camera;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class Mixin_AdjustLevel {
    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(F)F"))
    private float adjustLevel(Camera instance, float zoom) {
        return (float) BehindYouClient.getLevel(zoom, OmniRenderTicks.get());
    }
}
