package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.deftu.omnicore.api.client.options.OmniPerspective;
import net.minecraft.client.CameraType;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CameraType.class)
public class Mixin_KeepThirdPerson {
    @ModifyReturnValue(method = "isFirstPerson", at = @At("RETURN"))
    private boolean keepThirdPerson(boolean isFirstPerson) {
        if (BehindYouConfig.INSTANCE.isEnabled() && BehindYouConfig.Animation.INSTANCE.getEnabled()) {
            return BehindYouClient.isFinished() && isFirstPerson;
        } else {
            return isFirstPerson;
        }
    }

    @ModifyReturnValue(method = "isMirrored", at = @At("RETURN"))
    private boolean keepMirrored(boolean isMirrored) {
        if (!BehindYouConfig.INSTANCE.isEnabled() || !BehindYouConfig.Animation.INSTANCE.getEnabled()) {
            return isMirrored;
        }

        boolean isAnimating = !BehindYouClient.isFinished()
                && BehindYouClient.getPreviousPerspective().isFrontView()
                && OmniPerspective.getCurrentPerspective().isFirstPerson();
        return isAnimating || isMirrored;
    }
}
