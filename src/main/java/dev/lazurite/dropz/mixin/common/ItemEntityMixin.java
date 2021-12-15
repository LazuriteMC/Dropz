package dev.lazurite.dropz.mixin.common;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.util.DropType;
import dev.lazurite.dropz.util.storage.ItemEntityStorage;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.entity.EntityRigidBody;
import dev.lazurite.rayon.impl.bullet.collision.body.shape.MinecraftShape;
import dev.lazurite.rayon.impl.bullet.collision.space.MinecraftSpace;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * This is basically a rewrite of most of {@link ItemEntity}'s
 * functionality. It changes what happens when it ticks and when
 * it's spawned into the world.
 * @see ItemEntityStorage
 * @see Dropz
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements EntityPhysicsElement, ItemEntityStorage {
    @Unique private final EntityRigidBody rigidBody = new EntityRigidBody(this);
    @Unique private Item prevItem = Items.AIR;
    @Unique private DropType type = DropType.ITEM;

    @Shadow public abstract ItemStack getItem();
    @Shadow @Nullable public abstract UUID getThrower();

    public ItemEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    public void init_RETURN(EntityType<? extends ItemEntity> type, Level level, CallbackInfo info) {
        this.prevItem = getItem().getItem();
        this.type = DropType.get(getItem());
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;tick()V",
                    shift = At.Shift.AFTER
            )
    )
    public void tick_tick(CallbackInfo info) {
        if (!getItem().getItem().equals(prevItem)) {
            this.type = DropType.get(getItem());
            this.prevItem = getItem().getItem();
            CollisionShape shape = MinecraftShape.of(type.getAABB());

            MinecraftSpace.get(this.getLevel()).getWorkerThread().execute(() -> {
                getRigidBody().setCollisionShape(shape);
                getRigidBody().setMass(type.getMass());
            });
        }

        doDamage();
        Vector3f location = getRigidBody().getPhysicsLocation(new Vector3f());
        this.absMoveTo(location.x, location.y + getBoundingBox().getYsize() * 0.5, location.z);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"))
    public boolean tick_noCollision(Level level, Entity entity, AABB aabb) {
        return true;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    public void tick_setDeltaMovement(ItemEntity itemEntity, Vec3 vec3) { }

    @Inject(method = "getSpin", at = @At("HEAD"), cancellable = true)
    public void getSpin_HEAD(float f, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(0.0f);
    }

    @Inject(method = "isMergable", at = @At("HEAD"), cancellable = true)
    public void isMergable_HEAD(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }

    @Inject(method = "setUnderwaterMovement", at = @At("HEAD"), cancellable = true)
    public void setunderwaterMovement_HEAD(CallbackInfo info) {
        info.cancel();
    }

    @Inject(method = "setUnderLavaMovement", at = @At("HEAD"), cancellable = true)
    private void setUnderLavaMovement(CallbackInfo info) {
       info.cancel();
    }

    @Inject(method = "getAddEntityPacket", at = @At("HEAD"), cancellable = true)
    public void getAddEntityPacket_HEAD(CallbackInfoReturnable<Packet<?>> info) {
        //info.setReturnValue(getSpawnPacket()); // TODO
    }

    @Override
    public DropType getDropType() {
        return this.type;
    }

    @Override
    public EntityRigidBody getRigidBody() {
        return this.rigidBody;
    }

    private void doDamage() {
        /* Momentum */
        float p = getRigidBody().getLinearVelocity(new Vector3f()).length() * getRigidBody().getMass();

        /* Velocity */
        float v = getRigidBody().getLinearVelocity(new Vector3f()).length();

        if (v >= 15) {
            for (Entity entity : this.getLevel().getEntities(((ItemEntity) (Object) this), this.getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                if (getThrower() != null) {
                    if (!entity.equals(this.getLevel().getPlayerByUUID(getThrower()))) {
                        entity.hurt(DamageSource.GENERIC, p / 20.0f);

                        /* Loses 90% of its speed */
                        MinecraftSpace.get(this.getLevel()).getWorkerThread().execute(() ->
                                getRigidBody().applyCentralImpulse(getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.1f).multLocal(getRigidBody().getMass())));
                    }
                }
            }
        }
    }
}
