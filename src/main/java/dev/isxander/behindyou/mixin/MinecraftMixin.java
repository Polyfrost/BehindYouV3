package dev.isxander.behindyou.mixin;

import dev.isxander.behindyou.BehindYou;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0, opcode = Opcodes.PUTFIELD))
    private void override(GameSettings instance, int value) {
        int perspective = value;
        if (perspective > 2) perspective = 0;
        BehindYou.INSTANCE.setPerspective(perspective);
    }
}
