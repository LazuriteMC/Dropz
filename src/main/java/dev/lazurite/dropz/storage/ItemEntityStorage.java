package dev.lazurite.dropz.storage;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import dev.lazurite.dropz.mixin.common.access.ItemEntityAccess;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

/**
 * This interface allows access to the isBlock variable stored
 * in {@link ItemEntityMixin}. It also contains a helper
 * onCollide static method.
 * @see ItemEntityMixin
 * @see Dropz
 */
public interface ItemEntityStorage {
    static void onCollide(EntityRigidBody body1, EntityRigidBody body2) {
        World world = body1.getDynamicsWorld().getWorld();

        if (!world.isClient()) {
            if (body1.getEntity() instanceof ItemEntity && body2.getEntity() instanceof ItemEntity) {
                ItemEntity item1 = (ItemEntity) body1.getEntity();
                ItemEntity item2 = (ItemEntity) body1.getEntity();
                ((ItemEntityAccess) item1).invokeTryMerge(item2);
            }
        }
    }

    boolean isBlock();
}
