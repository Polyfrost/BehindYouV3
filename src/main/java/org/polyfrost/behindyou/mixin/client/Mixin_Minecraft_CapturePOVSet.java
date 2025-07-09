package org.polyfrost.behindyou.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.objectweb.asm.Opcodes;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.PlayerPerspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class Mixin_Minecraft_CapturePOVSet {

    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.PUTFIELD))
    private void behindyouv3$capturePovSetF5(GameSettings instance, int value) {
        PlayerPerspective perspective = PlayerPerspective.get(value);
        if (perspective != null) {
            BehindYouClient.updatePerspective(perspective);
        }
    }

    @Redirect(method = "runGameLoop", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", opcode = Opcodes.PUTFIELD))
    private void behindyouv3$capturePovSetStuckInBlock(GameSettings instance, int value) {
        PlayerPerspective perspective = PlayerPerspective.get(value);
        if (perspective != null) {
            BehindYouClient.updatePerspective(perspective);
        }
    }

}
