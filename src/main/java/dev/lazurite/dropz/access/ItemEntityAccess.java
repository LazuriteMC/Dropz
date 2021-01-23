package dev.lazurite.dropz.access;

import dev.lazurite.dropz.mixin.common.ItemEntityMixin;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.minecraft.entity.ItemEntity;

/**
 * This interface is what allows access to the
 * following methods in {@link ItemEntityMixin}.
 */
public interface ItemEntityAccess {
    static void onCollision(EntityRigidBody body1, EntityRigidBody body2) {
        if (!body1.getEntity().getEntityWorld().isClient()) {
            if (body1.getEntity() instanceof ItemEntity && body2.getEntity() instanceof ItemEntity) {
                ((ItemEntityAccess) body1.getEntity()).tryMerge(((ItemEntity) body2.getEntity()));
            }
        }
    }

    boolean isBlock();
    void tryMerge(ItemEntity other);
}
