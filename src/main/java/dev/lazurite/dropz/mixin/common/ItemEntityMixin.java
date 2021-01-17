package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.access.ItemEntityAccess;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import physics.com.bulletphysics.collision.shapes.CollisionShape;
import physics.javax.vecmath.Quat4f;
import physics.javax.vecmath.Vector3f;

import java.util.Random;
import java.util.UUID;

/**
 * This is basically a rewrite of most of {@link ItemEntity}'s
 * functionality. It changes what happens when it ticks and when
 * it's spawned into the world.
 * @see ItemEntityAccess
 * @see Dropz
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityAccess {
    @Unique private final Box blockBox = new Box(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15);
    @Unique private final Box itemBox = new Box(-0.25, -0.25, -0.05, 0.25, 0.25, 0.05);
    @Unique private Item prevItem;
    @Unique private boolean isBlock;
    @Shadow private int pickupDelay;
    @Shadow private int age;
    @Shadow public abstract ItemStack getStack();
    @Shadow @Nullable public abstract UUID getThrower();

    private ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/World;DDD)V")
    public void init(World world, double x, double y, double z, CallbackInfo info) {
        EntityRigidBody body = EntityRigidBody.get(this);
        Random random = new Random();
        prevItem = getStack().getItem();

        /* Shift down */
        updatePosition(x, y - 0.75f, z);

        /* Set random spin */
        body.setAngularVelocity(new Vector3f(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5));

        /* Set random orientation */
        Quat4f orientation = new Quat4f(0, 1, 0, 0);
        QuaternionHelper.rotateX(orientation, random.nextInt(180));
        QuaternionHelper.rotateY(orientation, random.nextInt(180));
        QuaternionHelper.rotateZ(orientation, random.nextInt(180));
        body.setOrientation(orientation);
    }

    @Unique
    private void genCollisionShape(ItemStack stack) {
        isBlock = Registry.BLOCK.get(Registry.ITEM.getId(stack.getItem())) != Blocks.AIR;

        EntityRigidBody body = EntityRigidBody.get(this);
        CollisionShape shape = new BoundingBoxShape(isBlock ? blockBox : itemBox);
        Vector3f inertia = new Vector3f();

        shape.calculateLocalInertia(body.getMass(), inertia);
        body.setCollisionShape(shape);
        body.setMassProps(isBlock ? 2.0f : 1.0f, inertia);
    }

    @Unique @Override
    public boolean isBlock() {
        return this.isBlock;
    }

    @Override
    public void tick() {
        if (this.getStack().isEmpty()) {
            this.remove();
        } else {
            super.tick();
            EntityRigidBody body = EntityRigidBody.get(this);

            /* Momentum */
            float p = body.getLinearVelocity(new Vector3f()).length() * body.getMass();

            if (p >= 15 && age > 2) {
                for (Entity entity : getEntityWorld().getOtherEntities(this, getBoundingBox().expand(1))) {
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
                this.genCollisionShape(getStack());
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
