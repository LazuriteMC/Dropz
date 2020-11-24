package dev.lazurite.dropz.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin class mainly deals with spawning of the
 * {@link dev.lazurite.dropz.server.entity.PhysicsItemEntity}.
 * @author Ethan Johnson
 */
@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    /**
     * This mixin prevents regular {@link net.minecraft.entity.ItemEntity} objects from being spawned
     * into the {@link ServerWorld} (and subsequently into the {@link net.minecraft.client.world.ClientWorld}).
     * @param entity the entity being spawned
     * @param info required by every mixin injection
     */
    @Inject(at = @At("HEAD"), method = "spawnEntity", cancellable = true)
    public void spawnEntity(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity.getType().equals(EntityType.ITEM)) {
            info.setReturnValue(false);
        }
    }
}
