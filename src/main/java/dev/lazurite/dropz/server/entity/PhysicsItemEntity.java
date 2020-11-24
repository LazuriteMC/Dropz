package dev.lazurite.dropz.server.entity;

import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.helper.ShapeHelper;
import dev.lazurite.api.physics.network.tracker.EntityTrackerRegistry;
import dev.lazurite.api.physics.network.tracker.generic.GenericTypeRegistry;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.dropz.client.BakedModelShape;
import dev.lazurite.dropz.server.ServerInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import shadow.com.bulletphysics.collision.shapes.*;
import shadow.javax.vecmath.Quat4f;
import shadow.javax.vecmath.Vector3f;

import java.util.Random;

@Environment(EnvType.CLIENT)
public class PhysicsItemEntity extends PhysicsEntity {
    public static final EntityTrackerRegistry.Entry<ItemStack> ITEM_STACK = EntityTrackerRegistry.register("item_stack", ServerInitializer.ITEM_STACK_TYPE, new ItemStack(Items.AIR), PhysicsItemEntity.class);
    public static final EntityTrackerRegistry.Entry<Integer> OLD_ID = EntityTrackerRegistry.register("old_id", GenericTypeRegistry.INTEGER_TYPE, -1, PhysicsItemEntity.class);

    private boolean isStackSetOnClient = false;

    public PhysicsItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PhysicsItemEntity(World world, ItemStack stack, int oldId) {
        this(ServerInitializer.PHYSICS_ITEM_ENTITY, world);
        this.setValue(ITEM_STACK, stack);
        this.setValue(SIZE, 8);
        this.setValue(MASS, 1.0f);
        this.setValue(DRAG_COEFFICIENT, 0.005f);
        this.setValue(OLD_ID, oldId);
    }

    public CollisionShape getItemShape(ItemStack stack) {
//        BakedModel model = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(getStack());
        Vector3f extents = new Vector3f(0.25f, 0.25f, 1.0f / 24.0f);
        BoxShape box = new BoxShape(extents);
        box.setMargin(0.04f);
        return box;
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
                if (getStack().getItem().getClass() == BlockItem.class) {
                    Block block = Block.getBlockFromItem(getStack().getItem());
                    BlockState state = block.getDefaultState();
                    BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModel(state);
                    ShapeHelper.shape = new BakedModelShape(model, null);
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
