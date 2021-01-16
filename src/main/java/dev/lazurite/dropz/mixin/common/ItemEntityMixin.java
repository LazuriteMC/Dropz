package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.util.ItemEntityAccess;
import dev.lazurite.rayon.api.packet.RayonSpawnS2CPacket;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.shape.BoundingBoxShape;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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
import physics.javax.vecmath.Vector3f;

import java.util.Objects;
import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ItemEntityAccess {
    @Shadow private int pickupDelay;
    @Shadow private int age;

    @Unique private final Box blockBox = new Box(-0.15, -0.15, -0.15, 0.15, 0.15, 0.15);
    @Unique private final Box itemBox = new Box(-0.25, -0.25, -0.05, 0.25, 0.25, 0.05);

    @Unique private Item prevItem;
    @Unique private boolean isBlock;

    @Shadow public abstract ItemStack getStack();

    @Shadow @Nullable public abstract UUID getOwner();

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void init(EntityType<?> entityType, World world, CallbackInfo info) {
        prevItem = getStack().getItem();
    }

    @Unique
    public void genCollisionShape(ItemStack stack) {
        isBlock = Registry.BLOCK.get(Registry.ITEM.getId(stack.getItem())) != Blocks.AIR;

        EntityRigidBody body = EntityRigidBody.get(this);
        CollisionShape shape = new BoundingBoxShape(isBlock ? blockBox : itemBox);
        Vector3f inertia = new Vector3f();

        shape.calculateLocalInertia(body.getMass(), inertia);
        body.setCollisionShape(shape);
        body.setMassProps(body.getMass(), inertia);
    }

    @Override
    public void tick() {
        if (this.getStack().isEmpty()) {
            this.remove();
        } else {
            super.tick();

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

    @Unique @Override
    public void merge(ItemEntity itemEntity) {
        if (Objects.equals(getOwner(), itemEntity.getOwner()) && ItemEntity.canMerge(getStack(), itemEntity.getStack())) {
            if (itemEntity.getStack().getCount() < getStack().getCount()) {
                ItemEntity.merge(itemEntity, itemEntity.getStack(), (ItemEntity) (Object) this, getStack());
            } else {
                ItemEntity.merge((ItemEntity) (Object) this, getStack(), itemEntity, itemEntity.getStack());
            }
        }
    }

    @Unique @Override
    public boolean isBlock() {
        return this.isBlock;
    }

    @Override
    public boolean isInvisible() {
        return false;
    }

    @Override
    public void move(MovementType type, Vec3d movement) {
        // nothin
    }

    @Override
    public void addVelocity(double x, double y, double z) {
//         nothin
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        Vector3f velocity = new Vector3f((float) x * 20, (float) y * 20, (float) z * 20);
        EntityRigidBody.get(this).setLinearVelocity(velocity);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return RayonSpawnS2CPacket.get(this);
    }
}
