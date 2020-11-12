package dev.lazurite.dropz.server.entity;

import dev.lazurite.api.physics.network.tracker.EntityTrackerRegistry;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.dropz.server.ServerInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PhysicsItemEntity extends PhysicsEntity {
    public static final EntityTrackerRegistry.Entry<ItemStack> ITEM_STACK = EntityTrackerRegistry.register("item_stack", ServerInitializer.ITEM_STACK_TYPE, new ItemStack(Items.AIR), PhysicsItemEntity.class);

    public PhysicsItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PhysicsItemEntity(World world, ItemStack stack) {
        this(ServerInitializer.PHYSICS_ITEM_ENTITY, world);
        this.setValue(ITEM_STACK, stack);
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
