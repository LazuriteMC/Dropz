package dev.lazurite.dropz.server.entity;

import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.helper.ShapeHelper;
import dev.lazurite.api.physics.network.tracker.EntityTrackerRegistry;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.dropz.server.ServerInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import shadow.com.bulletphysics.collision.shapes.*;
import shadow.com.bulletphysics.util.ObjectArrayList;
import shadow.javax.vecmath.Quat4f;
import shadow.javax.vecmath.Vector3f;

import java.util.*;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public class PhysicsItemEntity extends PhysicsEntity {
    public static final EntityTrackerRegistry.Entry<ItemStack> ITEM_STACK = EntityTrackerRegistry.register("item_stack", ServerInitializer.ITEM_STACK_TYPE, new ItemStack(Items.AIR), PhysicsItemEntity.class);

    private boolean isStackSetOnClient = false;

    public PhysicsItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PhysicsItemEntity(World world, ItemStack stack) {
        this(ServerInitializer.PHYSICS_ITEM_ENTITY, world);
        this.setValue(ITEM_STACK, stack);
        this.setValue(SIZE, 8);
        this.setValue(MASS, 1.0f);
        this.setValue(DRAG_COEFFICIENT, 0.005f);
    }

    public CollisionShape getItemShape(ItemStack stack) {
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(getStack());
        Vector3f extents = new Vector3f(16, 16, 1);
        extents.scale(0.03125f);
        return new BoxShape(extents);
    }

    public CollisionShape getBlockShape(BlockState state) {
        BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModel(state);
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();

        if (model instanceof MultipartBakedModel) {
            MultipartBakedModel multi = (MultipartBakedModel) model;

            for (Pair<Predicate<BlockState>, BakedModel> component : multi.components) {
                BakedModel baked = component.getRight();

                if (baked instanceof BasicBakedModel) {
                    BasicBakedModel basic = (BasicBakedModel) baked;

                    for (Direction d : Direction.values()) {
                        points.addAll(getPoints(basic.faceQuads.get(d)));
                        System.out.println(points);
                    }
                }
            }
        }

        ObjectArrayList<Vector3f> buffer = new ObjectArrayList<>();
        for (int i = 0; i < points.size(); i+=4) {
            buffer.add(points.get(i));
            buffer.add(points.get(i+1));
            buffer.add(points.get(i+2));
            buffer.add(points.get(i+3));
            buffer.add(points.get(i));
        }

        return new ConvexHullShape(buffer);
    }

    public ObjectArrayList<Vector3f> getPoints(BakedQuad quad) {
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
        int[] v = quad.getVertexData();

        for (int i = 0; i < v.length; i += 8) { // loop 4 times through 32 byte queue
            Vector3f point = new Vector3f(
                    Float.intBitsToFloat(v[i]),
                    Float.intBitsToFloat(v[i+1]),
                    Float.intBitsToFloat(v[i+2])
            );

            points.add(point);
        }

        return points;
    }

    public ObjectArrayList<Vector3f> getPoints(List<BakedQuad> quads) {
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();

        for (BakedQuad quad : quads) {
            points.addAll(getPoints(quad));
        }

        return points;
    }

    @Override
    public void tick() {
        super.tick();

        if (!getEntityWorld().isClient()) {
            if (getValue(PLAYER_ID).equals(PLAYER_ID.getFallback())) {
                PlayerEntity closest = getEntityWorld().getClosestPlayer(this, 100);

                if (closest != null) {
                    setValue(PLAYER_ID, closest.getEntityId());
                }
            }
        } else {
            Quat4f orientation = physics.getOrientation();
            physics.setOrientation(orientation);

            if (!isStackSetOnClient && !getStack().getItem().equals(Items.AIR)) {

                if (getStack().getItem() instanceof BlockItem) {
                    Block block = Block.getBlockFromItem(getStack().getItem());
                    ShapeHelper.shape = getBlockShape(block.getDefaultState());
                } else {
                    ShapeHelper.shape = getItemShape(getStack());
                }

                ((ClientPhysicsHandler) physics).createRigidBody();
                isStackSetOnClient = true;
            }
        }
    }

    public ItemStack getStack() {
        return this.getValue(ITEM_STACK);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}
