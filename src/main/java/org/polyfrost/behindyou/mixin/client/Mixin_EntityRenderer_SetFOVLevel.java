package org.polyfrost.behindyou.mixin.client;

import org.polyfrost.behindyou.client.BehindYouClient;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

//#if MC <= 1.12.2
import dev.deftu.omnicore.api.client.options.OmniPerspective;
import net.minecraft.client.settings.GameSettings;
//#endif

@Mixin(EntityRenderer.class)
public class Mixin_EntityRenderer_SetFOVLevel {
    //#if MC <= 1.12.2
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0))
    private int behindyouv3$checkAndKeepF5(GameSettings instance) {
        return BehindYouClient.isFinished() ? instance.thirdPersonView : OmniPerspective.THIRD_PERSON_BACK.ordinal();
    }

    // Replaced by a separate Mixin in 1.16.5+
    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
    private double behindyouv3$set(double z, float partialTicks) {
        return BehindYouClient.getLevel(z, partialTicks);
    }
    //#endif
}
