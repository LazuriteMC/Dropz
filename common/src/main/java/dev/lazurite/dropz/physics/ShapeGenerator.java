package dev.lazurite.dropz.physics;

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
        var item = itemEntity.getItem().getItem();

        if (item instanceof BlockItem) {
            return getRoughPatternShape(itemEntity);
        }

        return Convert.toBullet(new AABB(-0.2, -0.2, -0.0125, 0.2, 0.2, 0.0125));
    }

    public static BoundingBox getRoughPatternShape(ItemEntity itemEntity) {
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

            // Find min and max points in the pattern
            for (var point : points) {
                if (point.x <= min.x) min.x = point.x;
                if (point.y <= min.y) min.y = point.y;
                if (point.z <= min.z) min.z = point.z;
                if (point.x >= max.x) max.x = point.x;
                if (point.y >= max.y) max.y = point.y;
                if (point.z >= max.z) max.z = point.z;
            }

            final var boundingBox = Convert.toBullet(new AABB(
                    VectorHelper.toVec3(Convert.toMinecraft(min)),
                    VectorHelper.toVec3(Convert.toMinecraft(max))));

            boundingBox.setXExtent(0.9f * boundingBox.getXExtent());
            boundingBox.setYExtent(0.9f * boundingBox.getYExtent());
            boundingBox.setZExtent(0.9f * boundingBox.getZExtent());

            return boundingBox;
        }

        return null;
    }

}