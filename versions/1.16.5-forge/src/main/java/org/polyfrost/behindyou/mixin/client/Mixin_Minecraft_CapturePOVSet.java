package org.polyfrost.behindyou.mixin.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.PlayerPerspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class Mixin_Minecraft_CapturePOVSet {

    @Redirect(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;setCameraType(Lnet/minecraft/client/CameraType;)V"))
    private void capturePOVSetF5(Options instance, CameraType arg) {
        PlayerPerspective perspective = PlayerPerspective.get(arg.ordinal());
        if (perspective != null) {
            BehindYouClient.updatePerspective(perspective);
        }
    }

}
