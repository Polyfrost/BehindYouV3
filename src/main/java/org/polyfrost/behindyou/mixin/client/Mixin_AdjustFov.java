package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
//? if >= 26 {
import net.minecraft.client.Camera;
//?} else
import net.minecraft.client.renderer.GameRenderer;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if < 26 'Camera' -> 'GameRenderer'
@Mixin(GameRenderer.class)
public class Mixin_AdjustFov {
    //~ if < 26 'calculateFov' -> 'getFov'
    @ModifyExpressionValue(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0))
    private float adjustFov(float original) {
        int fov = Minecraft.getInstance().options.fov().get();
        return original * BehindYouClient.getFov(fov) / fov;
    }
}
