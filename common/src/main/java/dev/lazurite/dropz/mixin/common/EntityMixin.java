package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.util.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "shouldRenderAtSqrDistance", at = @At("HEAD"), cancellable = true)
    public void shouldRenderAtSqrDistance$HEAD(double d, CallbackInfoReturnable<Boolean> cir) {
        if (Config.dropzEnabled && (Object) this instanceof ItemEntity) {
            cir.setReturnValue(true);
        }
    }

}
