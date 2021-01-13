package dev.lazurite.dropz.mixin.common;

import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import physics.javax.vecmath.Vector3f;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
    @Shadow private int age;
    @Shadow private int pickupDelay;

    @Shadow public abstract ItemStack getStack();
    @Shadow protected abstract boolean canMerge();
    @Shadow protected abstract void tryMerge();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        Vector3f velocity = new Vector3f((float) x * 20, (float) y * 20, (float) z * 20);
        EntityRigidBody.get(this).setLinearVelocity(velocity);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(CallbackInfo info) {
        if (this.getStack().isEmpty()) {
            this.remove();
        } else {
            super.tick();

            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }

            boolean hasMoved = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX()) || MathHelper.floor(this.prevY) != MathHelper.floor(this.getY()) || MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());

            if (this.age % (hasMoved ? 2 : 40) == 0) {
                if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA) && !this.isFireImmune()) {
                    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
                }

                if (!this.world.isClient && this.canMerge()) {
                    this.tryMerge();
                }
            }

            if (this.age != -32768) {
                ++this.age;
            }

            /* After 5 minutes, say goodbye */
            if (!this.world.isClient && this.age >= 6000) {
                this.remove();
            }
        }

        info.cancel();
    }


    @Inject(at = @At("HEAD"), method = "createSpawnPacket", cancellable = true)
    public void createSpawnPacket(CallbackInfoReturnable<Packet<?>> info) {
        info.setReturnValue(RayonSpawnS2CPacket.get((ItemEntity) (Object) this));
    }
}
