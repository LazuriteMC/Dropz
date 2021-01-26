package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.util.DropType;
import dev.lazurite.dropz.util.storage.ItemEntityStorage;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import physics.com.bulletphysics.collision.shapes.CollisionShape;
import physics.javax.vecmath.Vector3f;

import java.util.UUID;

/**
 * This is basically a rewrite of most of {@link ItemEntity}'s
 * functionality. It changes what happens when it ticks and when
 * it's spawned into the world.
 * @see ItemEntityStorage
 * @see Dropz
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityStorage {
    @Unique private Item prevItem = Items.AIR;
    @Unique private DropType type = DropType.ITEM;

    @Shadow private int pickupDelay;
    @Shadow private int age;
    @Shadow public abstract ItemStack getStack();
    @Shadow @Nullable public abstract UUID getThrower();

    private ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/World;DDD)V")
    public void init(World world, double x, double y, double z, CallbackInfo info) {
        prevItem = getStack().getItem();
        type = DropType.get(getStack());
    }

    @Unique
    private void genCollisionShape(ItemStack stack) {
        type = DropType.get(stack);
        Vector3f inertia = new Vector3f();
        EntityRigidBody body = EntityRigidBody.get(this);
        CollisionShape shape = new BoundingBoxShape(type.getBox());
        shape.calculateLocalInertia(body.getMass(), inertia);
        body.setCollisionShape(shape);
        body.setMassProps(type.getMass(), inertia);
    }

    @Unique @Override
    public DropType getDropType() {
        return this.type;
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(CallbackInfo info) {
        if (this.getStack().isEmpty()) {
            this.remove();
        } else {
            super.tick();
            EntityRigidBody body = EntityRigidBody.get(this);

            /* Momentum */
            float p = body.getLinearVelocity(new Vector3f()).length() * body.getMass();
            float v = body.getLinearVelocity(new Vector3f()).length();

            if (v >= 15) {
                for (Entity entity : getEntityWorld().getOtherEntities(this, getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                    if (getThrower() != null) {
                        if (!entity.equals(getEntityWorld().getPlayerByUuid(getThrower()))) {
                            entity.damage(DamageSource.GENERIC, p / 20.0f);

                            /* Loses 90% of its speed */
                            body.setLinearVelocity(VectorHelper.mul(body.getLinearVelocity(new Vector3f()), 0.1f));
                        }
                    }
                }
            }

            if (!getStack().getItem().equals(prevItem)) {
                genCollisionShape(getStack());
                prevItem = getStack().getItem();
            }

            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }

            boolean hasMoved = MathHelper.floor(this.prevX) != MathHelper.floor(this.getX()) || MathHelper.floor(this.prevY) != MathHelper.floor(this.getY()) || MathHelper.floor(this.prevZ) != MathHelper.floor(this.getZ());

            if (this.age % (hasMoved ? 2 : 40) == 0) {
                if (this.world.getFluidState(this.getBlockPos()).isIn(FluidTags.LAVA) && !this.isFireImmune()) {
                    this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
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

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        Vector3f velocity = new Vector3f((float) x * 20, (float) y * 20 - 1, (float) z * 20);
        EntityRigidBody.get(this).setLinearVelocity(velocity);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return RayonSpawnS2CPacket.get(this);
    }
}
