package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class Mixin_CapturePOVSet {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    private void capturePOVSetF5(Options instance, CameraType arg, Operation<Void> original) {
        if (BehindYouConfig.INSTANCE.isEnabled() && BehindYouConfig.Keybinds.INSTANCE.getEnableF5()) {
            BehindYouClient.updatePerspective(arg);
        } else {
            original.call(instance, arg);
        }
    }
}
