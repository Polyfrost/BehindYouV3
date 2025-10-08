package org.polyfrost.behindyou.mixin.client;

import dev.deftu.omnicore.api.client.OmniClient;
import dev.deftu.omnicore.api.client.render.OmniRenderTicks;
import net.minecraft.client.render.Camera;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Camera.class)
public class Mixin_Camera_AdjustLevel {
    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;clipToSpace(F)F"))
    private float behindyouv3$adjustLevel(Camera instance, float zoom) {
        return (float) BehindYouClient.getLevel(zoom, OmniRenderTicks.get());
    }
}
