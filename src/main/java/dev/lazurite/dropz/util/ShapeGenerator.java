package dev.lazurite.dropz.util;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.VectorHelper;
import dev.lazurite.transporter.api.Disassembler;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

public class ShapeGenerator {
    public static CollisionShape create(ItemEntity itemEntity) {
        final var level = itemEntity.getLevel();
        final var itemStack = itemEntity.getItem();
        CollisionShape shape = null;

        if (itemStack.getItem() instanceof BlockItem blockItem && !blockItem.getBlock().isPossibleToRespawnInThis()) {
            final var blockState = blockItem.getBlock().defaultBlockState();
            final var blockPos = new BlockPos(0, 0, 0);
            final var voxelShape = blockState.getCollisionShape(level, blockPos);

            // Pattern Shape
            if (Block.isShapeFullBlock(voxelShape)) {
                shape = getPatternShape(itemEntity);

            // Voxel Shape
            } else {
                final var boundingBox = voxelShape.isEmpty() ?
                        new AABB(-0.5, -0.5, -0.5, 0.5, 0.5, 0.5) :
                        voxelShape.bounds();

                shape = MinecraftShape.convex(new BoundingBox(
                        new Vector3f(),
                        (float) boundingBox.getXsize() * 0.25f,
                        (float) boundingBox.getYsize() * 0.25f,
                        (float) boundingBox.getZsize() * 0.25f));
            }
        }
        
        if (shape == null) {
            final var boundingBox = new AABB(-1.0, -1.0, -0.125, 1.0, 1.0, 0.125);

            shape = MinecraftShape.convex(new BoundingBox(
                    new Vector3f(),
                    (float) boundingBox.getXsize() * 0.25f,
                    (float) boundingBox.getYsize() * 0.25f,
                    (float) boundingBox.getZsize() * 0.25f));
        }

        return shape;
    }

    public static CollisionShape getPatternShape(ItemEntity itemEntity) {
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
                if (point.x < min.x || point.y < min.y || point.z < min.z) {
                    min.set(point);
                }

                if (point.x > max.x || point.y > max.y || point.z > max.z) {
                    max.set(point);
                }
            }

            final var boundingBox = new AABB(
                    VectorHelper.toVec3(Convert.toMinecraft(min)),
                    VectorHelper.toVec3(Convert.toMinecraft(max)));

            // Pattern shape based
            return MinecraftShape.convex(boundingBox);
        }

        return null;
    }
}
