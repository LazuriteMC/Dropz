package dev.lazurite.dropz.server.entity;

import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.helper.ShapeHelper;
import dev.lazurite.api.physics.network.tracker.EntityTrackerRegistry;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.api.physics.util.math.QuaternionHelper;
import dev.lazurite.dropz.server.ServerInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import shadow.com.bulletphysics.collision.shapes.CompoundShape;
import shadow.com.bulletphysics.collision.shapes.ConvexHullShape;
import shadow.com.bulletphysics.linearmath.Transform;
import shadow.com.bulletphysics.util.ObjectArrayList;
import shadow.javax.vecmath.Quat4f;
import shadow.javax.vecmath.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Environment(EnvType.CLIENT)
public class PhysicsItemEntity extends PhysicsEntity {
    public static final EntityTrackerRegistry.Entry<ItemStack> ITEM_STACK = EntityTrackerRegistry.register("item_stack", ServerInitializer.ITEM_STACK_TYPE, new ItemStack(Items.AIR), PhysicsItemEntity.class);

    public PhysicsItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PhysicsItemEntity(World world, ItemStack stack) {
        this(ServerInitializer.PHYSICS_ITEM_ENTITY, world);
        this.setValue(ITEM_STACK, stack);
        this.setValue(SIZE, 8);
        this.setValue(MASS, 1.0f);
        this.setValue(DRAG_COEFFICIENT, 0.005f);

        if (world.isClient()) {
            ShapeHelper.shape = buildShape(stack);
            ((ClientPhysicsHandler) physics).createRigidBody();
        }
    }

    public ConvexHullShape createHull(BakedQuad quad) {
        ObjectArrayList<Vector3f> points = new ObjectArrayList<>();
        int[] v = quad.getVertexData();

        for (int i = 0; i < v.length; i += 8) {
            points.add(new Vector3f(
                    Float.intBitsToFloat(v[i]),
                    Float.intBitsToFloat(v[i+1]),
                    Float.intBitsToFloat(v[i+2])
            ));
        }

        return new ConvexHullShape(points);
    }

    public CompoundShape buildShape(ItemStack stack) {
        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getHeldItemModel(stack, world, null);
        Transform transform = new Transform();
        List<BakedQuad> quads = new ArrayList<BakedQuad>() {{
            addAll(model.getQuads(null, Direction.DOWN, new Random()));
            addAll(model.getQuads(null, Direction.UP, new Random()));
            addAll(model.getQuads(null, Direction.NORTH, new Random()));
            addAll(model.getQuads(null, Direction.SOUTH, new Random()));
            addAll(model.getQuads(null, Direction.WEST, new Random()));
            addAll(model.getQuads(null, Direction.EAST, new Random()));
        }};

        CompoundShape compoundShape = new CompoundShape();
        for (BakedQuad quad : quads)
            compoundShape.addChildShape(transform, createHull(quad));
        return compoundShape;
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
            QuaternionHelper.rotateY(orientation, 0.5f);
            physics.setOrientation(orientation);
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
