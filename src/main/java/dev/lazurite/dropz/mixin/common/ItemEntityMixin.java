package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.server.entity.PhysicsDropEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin spawns a {@link PhysicsDropEntity} in place
 * of the {@link ItemEntity} that was going to spawn instead.
 * @see ServerWorldMixin
 */
@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(
            at = @At("RETURN"),
            method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"
    )
    public void init(World world, double x, double y, double z, ItemStack stack, CallbackInfo info) {
        PhysicsDropEntity entity = new PhysicsDropEntity(world, stack);
        entity.updatePosition(x, y, z);
        System.out.println(x + ", " + y + ", " + z);
        world.spawnEntity(entity);
    }
}
