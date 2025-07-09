package org.polyfrost.behindyou.mixin.client;

import org.polyfrost.behindyou.client.BehindYouClient;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;

//#if MC >= 1.16.5
//$$ import net.minecraft.client.CameraType;
//#else
import net.minecraft.client.settings.GameSettings;
import org.polyfrost.behindyou.client.PlayerPerspective;
//#endif

@Mixin(EntityRenderer.class)
public class Mixin_EntityRenderer_SetFOVLevel {

    //#if MC >= 1.16.5
    //$$ @Redirect(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"))
    //$$ private boolean behindyouv3$markDetached(CameraType instance) {
    //$$     return !BehindYouClient.isFinished() || instance.isFirstPerson();
    //$$ }
    //#else
    @Redirect(method = "orientCamera", at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;thirdPersonView:I", ordinal = 0))
    private int behindyouv3$checkAndKeepF5(GameSettings instance) {
        return BehindYouClient.isFinished() ? instance.thirdPersonView : PlayerPerspective.BACK.ordinal();
    }

    // Replaced by a separate Mixin in 1.16.5+
    @ModifyVariable(method = "orientCamera", at = @At(value = "STORE", ordinal = 0), ordinal = 3)
    private double behindyouv3$set(double z, float partialTicks) {
        return BehindYouClient.getLevel(z, partialTicks);
    }
    //#endif

}
