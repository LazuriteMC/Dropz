package dev.lazurite.dropz.mixin.common;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "shouldRenderAtSqrDistance", at = @At("HEAD"), cancellable = true)
    public void shouldRenderAtSqrDistance_HEAD(double distance, CallbackInfoReturnable<Boolean> info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "moveTowardsClosestSpace", at = @At("HEAD"), cancellable = true)
    public void moveTowardsClosestSpace_HEAD(double x, double y, double z, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }

    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At("HEAD"), cancellable = true)
    public void updateInWaterStateAndDoFluidPushing_HEAD(CallbackInfoReturnable<Boolean> info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move_HEAD(MoverType moverType, Vec3 vec3, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }
}
