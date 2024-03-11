package dev.isxander.behindyou.mixin;

import dev.isxander.behindyou.BehindYou;
import dev.isxander.behindyou.config.BehindYouConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Unique private float behindyou$prevLevel = 0;

    @Unique
    private boolean behindYouV3$enable() {
        return BehindYouConfig.INSTANCE.enabled;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0))
    private int bypassCheck(GameSettings instance) {
        return behindYouV3$enable() ? 1 : instance.thirdPersonView;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 1))
    private int shaderFix(GameSettings instance) {
        return behindYouV3$enable() ? BehindYou.INSTANCE.getRealPerspective() : instance.thirdPersonView;
    }

    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 2))
    private int rotateFix(GameSettings instance) {
        return behindYouV3$enable() ? BehindYou.INSTANCE.getRealPerspective() : instance.thirdPersonView;
    }

    @ModifyArg(method = "orientCamera", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", ordinal = 2), index = 2)
    private float set(float z) {
        BehindYou.INSTANCE.setDistance(-z);
        if (behindYouV3$enable()) {
            float level = -BehindYou.INSTANCE.level();
            if (behindyou$prevLevel != level) {
                behindyou$prevLevel = level;
                Minecraft.getMinecraft().renderGlobal.setDisplayListEntitiesDirty();
            }
            return level;
        }
        return z;
    }

}
