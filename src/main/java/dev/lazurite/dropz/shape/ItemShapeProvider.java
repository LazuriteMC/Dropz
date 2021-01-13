package dev.lazurite.dropz.shape;

import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import physics.com.bulletphysics.collision.shapes.CollisionShape;

public interface ItemShapeProvider {
    static CollisionShape get(Entity entity) {
        ItemEntity itemEntity = (ItemEntity) entity;
        System.out.println(itemEntity.getStack().getItem());
        return new BoundingBoxShape(entity.getBoundingBox());
    }
}
