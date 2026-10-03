package org.polyfrost.behindyou.mixin.client;

//? if > 1.8.9 {
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
//?} else
//import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.CameraType;
//? if > 1.8.9 {
import net.minecraft.client.Minecraft;
//?} else {
/*import net.minecraft.client.gui.GameGui;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.FishingBobberRenderer;
import net.minecraft.client.render.world.WorldRenderer;
import net.minecraft.client.renderer.GameRenderer;
*///?}
import org.polyfrost.behindyou.client.BehindYouClient;
import org.polyfrost.behindyou.client.BehindYouConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if > 1.8.9 {
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
//?} else {
/*@Mixin({GameRenderer.class, WorldRenderer.class, EntityRenderDispatcher.class, FishingBobberRenderer.class, GameGui.class})
public class Mixin_KeepThirdPerson {
    @ModifyExpressionValue(
            method = {"transformCamera", "renderItemInHand", "render", "renderEntities", "getLookVector", "prepare"},
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;perspective:I")
    )
    private int keepThirdPerson(int perspective) {
        if (perspective != CameraType.FIRST_PERSON.getId()
                || !BehindYouConfig.INSTANCE.isEnabled()
                || !BehindYouConfig.Animation.INSTANCE.getEnabled()
                || !BehindYouClient.isManagingPerspective()
                || BehindYouClient.isFinished()) {
            return perspective;
        }

        return BehindYouClient.getPreviousPerspective().getId();
    }
}
*///?}
