package dev.lazurite.dropz.mixin.common;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.util.DropType;
import dev.lazurite.dropz.util.storage.ItemEntityStorage;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.rayon.api.element.PhysicsElement;
import dev.lazurite.rayon.impl.Rayon;
import dev.lazurite.rayon.impl.bullet.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.impl.bullet.world.MinecraftSpace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * This is basically a rewrite of most of {@link ItemEntity}'s
 * functionality. It changes what happens when it ticks and when
 * it's spawned into the world.
 * @see ItemEntityStorage
 * @see Dropz
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements PhysicsElement, ItemEntityStorage {
    @Unique private final ElementRigidBody rigidBody = new ElementRigidBody(this);
    @Unique private Item prevItem = Items.AIR;
    @Unique private DropType type = DropType.ITEM;

    @Shadow private int pickupDelay;
    @Shadow private int age;
    @Shadow public abstract ItemStack getStack();
    @Shadow @Nullable public abstract UUID getThrower();

    private ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void init(EntityType<? extends ItemEntity> type, World world, CallbackInfo info) {
        this.prevItem = getStack().getItem();
        this.type = DropType.get(getStack());
    }

    @Unique
    private void genCollisionShape(ItemStack stack) {
        type = DropType.get(stack);
        CollisionShape shape = new BoundingBoxShape(type.getBox());

        Rayon.THREAD.get(world).execute(space -> {
            getRigidBody().setCollisionShape(shape);
            getRigidBody().setMass(type.getMass());
        });
    }

    @Unique
    private void doDamage() {
        /* Momentum */
        float p = getRigidBody().getLinearVelocity(new Vector3f()).length() * getRigidBody().getMass();
        float v = getRigidBody().getLinearVelocity(new Vector3f()).length();

        if (v >= 15) {
            for (Entity entity : getEntityWorld().getOtherEntities(this, getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                if (getThrower() != null) {
                    if (!entity.equals(getEntityWorld().getPlayerByUuid(getThrower()))) {
                        entity.damage(DamageSource.GENERIC, p / 20.0f);

                        /* Loses 90% of its speed */
                        Rayon.THREAD.get(world).execute(space ->
                            getRigidBody().applyCentralImpulse(getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.1f).multLocal(getRigidBody().getMass())));
                    }
                }
            }
        }
    }

    @Unique @Override
    public DropType getDropType() {
        return this.type;
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/Entity;tick()V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    public void tick(CallbackInfo info) {
        if (!getStack().getItem().equals(prevItem)) {
            genCollisionShape(getStack());
            prevItem = getStack().getItem();
        }

        this.doDamage();

        if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
        }

        if (this.age != -32768) {
            ++this.age;
        }

        if (!this.world.isClient && this.age >= 6000) {
            this.remove();
        }

        info.cancel();
    }

    @Override
    protected void pushOutOfBlocks(double x, double y, double z) { }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return true;
    }

    @Override
    public void setVelocity(double x, double y, double z) {
        Vector3f velocity = new Vector3f((float) x * 20, (float) y * 20, (float) z * 20).multLocal(getRigidBody().getMass());
        Rayon.THREAD.get(world).execute(space -> getRigidBody().applyCentralImpulse(velocity));
    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.rigidBody;
    }

    @Override
    public void step(MinecraftSpace space) { }
}
