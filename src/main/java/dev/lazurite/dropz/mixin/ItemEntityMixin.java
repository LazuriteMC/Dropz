package dev.lazurite.dropz.mixin;

import dev.lazurite.dropz.server.entity.PhysicsItemEntity;
import dev.lazurite.dropz.util.ItemEntityTracker;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadow.javax.vecmath.Vector3f;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V")
    public void init(World world, double x, double y, double z, ItemStack stack, CallbackInfo info) {
        ItemEntity thisEntity = (ItemEntity) (Object) this;
        PhysicsItemEntity entity = new PhysicsItemEntity(world, stack);
        ItemEntityTracker.add(entity, thisEntity);
        entity.updatePositionAndAngles(new Vector3f((float) x, (float) y, (float) z), 0, 0);
        world.spawnEntity(entity);
    }
}
