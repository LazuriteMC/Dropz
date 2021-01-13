package dev.lazurite.dropz.server.entity;

import dev.lazurite.dropz.server.ServerInitializer;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PhysicsDropEntity extends Entity {
    private static final TrackedData<ItemStack> STACK = DataTracker.registerData(PhysicsDropEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);

    public PhysicsDropEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public PhysicsDropEntity(World world, ItemStack stack) {
        this(ServerInitializer.PHYSICS_ITEM_ENTITY, world);
        this.getDataTracker().set(STACK, stack);
    }

    public ItemStack getStack() {
        return this.getDataTracker().get(STACK);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {

    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {

    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(STACK, new ItemStack(Items.AIR));
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return RayonSpawnS2CPacket.get(this);
    }
}
