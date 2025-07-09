package org.polyfrost.behindyou.mixin.client;

import dev.deftu.omnicore.client.render.OmniGameRendering;
import net.minecraft.client.Camera;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class Mixin_Camera_AdjustLevel {

    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"))
    private double behindyouv3$adjustLevel(Camera instance, double zoom) {
        return BehindYouClient.getLevel(zoom, OmniGameRendering.getTickDelta(true));
    }

}
