package org.polyfrost.behindyou.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CameraType.class)
public class Mixin_KeepThirdPerson {
    @ModifyReturnValue(method = "isFirstPerson", at = @At("RETURN"))
    private boolean keepThirdPerson(boolean isFirstPerson) {
        if ((Object) this == Minecraft.getInstance().options.getCameraType()
                && BehindYouConfig.INSTANCE.isEnabled()
                && BehindYouConfig.Animation.INSTANCE.getEnabled()
                && BehindYouClient.isManagingPerspective()) {
            return BehindYouClient.isFinished() && isFirstPerson;
        } else {
            return isFirstPerson;
        }
    }

    @ModifyReturnValue(method = "isMirrored", at = @At("RETURN"))
    private boolean keepMirrored(boolean isMirrored) {
        if ((Object) this != Minecraft.getInstance().options.getCameraType()
                || !BehindYouConfig.INSTANCE.isEnabled()
                || !BehindYouConfig.Animation.INSTANCE.getEnabled()
                || !BehindYouClient.isManagingPerspective()) {
            return isMirrored;
        }

        boolean isAnimating = !BehindYouClient.isFinished()
                && BehindYouClient.getPreviousPerspective() == CameraType.THIRD_PERSON_FRONT
                && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON;
        return isAnimating || isMirrored;
    }
}
