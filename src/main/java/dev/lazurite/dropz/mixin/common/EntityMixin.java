package dev.lazurite.dropz.mixin.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Environment(EnvType.CLIENT)
    @Inject(method = "shouldRender(D)Z", at = @At("HEAD"), cancellable = true)
    public void shouldRender(double distance, CallbackInfoReturnable<Boolean> info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }

    @Inject(method = "updateWaterState", at = @At("HEAD"), cancellable = true)
    public void updateWaterState(CallbackInfoReturnable<Boolean> info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }
}
