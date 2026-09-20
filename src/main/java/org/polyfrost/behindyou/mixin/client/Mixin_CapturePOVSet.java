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
    //? if > 1.8.9 {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    //?} else
    //@WrapOperation(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;perspective:I", opcode = org.objectweb.asm.Opcodes.PUTFIELD, ordinal = 0))
    //~ if = 1.8.9 'CameraType arg' -> 'int arg'
    private void capturePOVSetF5(Options instance, CameraType arg, Operation<Void> original) {
        if (BehindYouConfig.INSTANCE.isEnabled() && BehindYouConfig.Keybinds.INSTANCE.getEnableF5()) {
            //~ if = 1.8.9 '(arg)' -> '(CameraType.fromId(arg))'
            BehindYouClient.updatePerspective(arg);
        } else {
            original.call(instance, arg);
        }
    }
}
