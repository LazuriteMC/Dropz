package dev.lazurite.dropz.server.entity;

import dev.lazurite.api.physics.client.handler.ClientPhysicsHandler;
import dev.lazurite.api.physics.client.helper.ShapeHelper;
import dev.lazurite.api.physics.network.tracker.EntityTrackerRegistry;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.dropz.server.ServerInitializer;
import dev.lazurite.dropz.util.ItemShapeHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;
import shadow.javax.vecmath.Quat4f;

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
                    ((ClientPhysicsHandler) getPhysics()).setCollisionShape(ItemShapeHelper.getBlockShape(getStack()));
                } else {
                    ((ClientPhysicsHandler) getPhysics()).setCollisionShape(ItemShapeHelper.getItemShape(getStack()));
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
