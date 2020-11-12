package dev.lazurite.dropz.entity;

import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class PhysicsItemEntity extends PhysicsEntity {
    public PhysicsItemEntity(World world) {
        super(EntityType.ITEM, world);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
