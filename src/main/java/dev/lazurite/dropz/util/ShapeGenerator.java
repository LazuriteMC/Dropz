package dev.lazurite.dropz.util;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.VectorHelper;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;

public class ShapeGenerator {
    public static BoundingBox create(ItemEntity itemEntity) {
        if (itemEntity.getItem().getItem() instanceof BlockItem) {
            return getPatternShape(itemEntity);
        }

        return Convert.toBullet(new AABB(-0.2, -0.2, -0.025, 0.2, 0.2, 0.025));
    }

    public static BoundingBox getPatternShape(ItemEntity itemEntity) {
        final var level = itemEntity.getLevel();
        final var itemStack = itemEntity.getItem();
        final Pattern pattern;

        if (level.isClientSide()) {
            pattern = Disassembler.getItem(itemStack.getItem(), null);
        } else {
            pattern = Transporter.getPatternBuffer().getItem(Item.getId(itemStack.getItem()));
        }

        if (pattern != null) {
            final var min = new Vector3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            final var max = new Vector3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            final var points = pattern.getQuads().stream()
                    .flatMap(quad -> quad.getPoints().stream())
                    .map(Convert::toBullet)
                    .toList();

            for (var point : points) {
                if (point.x <= min.x && point.y <= min.y && point.z <= min.z) {
                    min.set(point);
                }

                if (point.x >= max.x && point.y >= max.y && point.z >= max.z) {
                    max.set(point);
                }
            }

            final var boundingBox = new AABB(
                    VectorHelper.toVec3(Convert.toMinecraft(min)),
                    VectorHelper.toVec3(Convert.toMinecraft(max)));

            // Pattern shape based
            return Convert.toBullet(boundingBox.contract(boundingBox.getXsize() * 0.2f, boundingBox.getYsize() * 0.2f, boundingBox.getZsize() * 0.2f));
        }

        return null;
    }
}