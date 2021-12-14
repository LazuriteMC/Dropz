package dev.lazurite.dropz.util.storage;

import dev.lazurite.dropz.Dropz;
import dev.lazurite.dropz.config.Config;
import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import dev.lazurite.dropz.mixin.common.access.ItemEntityAccess;
import dev.lazurite.dropz.util.DropType;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

/**
 * This interface allows access to the {@link DropType} enum object
 * being stored in {@link ItemEntityMixin}. It also has a static helper
 * method for handling item collisions.
 * @see ItemEntityMixin
 * @see Dropz
 */
public interface ItemEntityStorage {
    static void onCollide(PhysicsElement body1, PhysicsElement body2, float impulse) {
        if (Config.getInstance().merge && body1 instanceof ItemEntity item1 && body2 instanceof ItemEntity item2) {
            Level level = item1.getLevel();

            if (!level.isClientSide()) {
                MinecraftSpace.get(level).getWorkerThread().execute(() -> ((ItemEntityAccess) item1).invokeTryToMerge(item2));
            }
        }
    }

    DropType getDropType();
}
