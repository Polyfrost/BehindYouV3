package dev.isxander.behindyou.mixin;

import dev.isxander.behindyou.BehindYou;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RenderGlobal.class)
public class RenderGlobalMixin {

    @Shadow private boolean displayListEntitiesDirty;
    @Unique
    private static int lastPerspective;

    @Unique
    private static boolean changed;

    @ModifyVariable(method = "setupTerrain", at = @At(value = "STORE"), name = "flag")
    private boolean perspective(boolean value) {
        changed = BehindYou.INSTANCE.getRealPerspective() != lastPerspective;
        if (changed) {
            lastPerspective = BehindYou.INSTANCE.getRealPerspective();
            return false;
        }
        return value;
    }

    @Redirect(method = "setupTerrain", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/RenderGlobal;displayListEntitiesDirty:Z", ordinal = 2))
    private boolean set(RenderGlobal instance) {
        return changed || displayListEntitiesDirty;
    }
}
