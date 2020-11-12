package dev.lazurite.dropz.mixin;

import dev.lazurite.dropz.server.ServerInitializer;
import dev.lazurite.dropz.server.entity.PhysicsItemEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import shadow.javax.vecmath.Vector3f;

/**
 * Contains mixins mostly relating to {@link Entity} spawning, movement, and positioning.
 * @author Ethan Johnson
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Shadow ClientWorld world;

    /**
     * This mixin is necessary since the game hard codes all of the entity types into
     * this method. This mixin just adds another one.
     * @param packet
     * @param info required by every mixin injection
     * @param x
     * @param y
     * @param z
     * @param type
     */
    @Inject(
            method = "onEntitySpawn(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;getEntityTypeId()Lnet/minecraft/entity/EntityType;"),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo info, double x, double y, double z, EntityType<?> type) {
        if (type == ServerInitializer.PHYSICS_ITEM_ENTITY) {
            PhysicsItemEntity entity = new PhysicsItemEntity(type, world);

            int i = packet.getId();
            entity.updatePositionAndAngles(new Vector3f(
                    (float) x, (float) y, (float) z),
                    (float)(packet.getYaw() * 360) / 256.0F, 0);
            entity.setUuid(packet.getUuid());
            entity.setEntityId(i);

            this.world.addEntity(i, entity);
            info.cancel();
        }
    }
}
