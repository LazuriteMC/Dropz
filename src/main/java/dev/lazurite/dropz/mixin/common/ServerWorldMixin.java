package dev.lazurite.dropz.mixin.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin prevents regular {@link ItemEntity}
 * objects from being spawned into the {@link World}.
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(at = @At("HEAD"), method = "spawnEntity", cancellable = true)
    public void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity.getType().equals(EntityType.ITEM)) {
            info.setReturnValue(false);
        }
    }
}
