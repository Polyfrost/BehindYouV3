package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.OptionInstance;
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
    @WrapOperation(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;", ordinal = 0))
    private Object adjustFov(OptionInstance<Integer> instance, Operation<Object> original) {
        int fov = (Integer) original.call(instance);
        return (int) BehindYouClient.getFov(fov);
    }
}
