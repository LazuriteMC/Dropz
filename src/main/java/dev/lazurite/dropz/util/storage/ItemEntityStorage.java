package dev.lazurite.dropz.util.storage;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import dev.lazurite.dropz.mixin.common.access.ItemEntityAccess;
import dev.lazurite.dropz.util.DropType;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.minecraft.entity.ItemEntity;
import net.minecraft.world.World;

import java.util.concurrent.Executor;

/**
 * This interface allows access to the {@link DropType} enum object
 * being stored in {@link ItemEntityMixin}. It also has a static helper
 * method for handling item collisions.
 * @see ItemEntityMixin
 * @see Dropz
 */
public interface ItemEntityStorage {
    static void onCollide(Executor executor, PhysicsElement body1, PhysicsElement body2, float impulse) {
        if (body1 instanceof ItemEntity && body2 instanceof ItemEntity) {
            ItemEntity item1 = ((ItemEntity) ((EntityPhysicsElement) body1).asEntity());
            ItemEntity item2 = ((ItemEntity) ((EntityPhysicsElement) body2).asEntity());
            World world = item1.getEntityWorld();

            if (!world.isClient()) {
                executor.execute(() -> ((ItemEntityAccess) item1).invokeTryMerge(item2));
            }
        }
    }

    DropType getDropType();
}
