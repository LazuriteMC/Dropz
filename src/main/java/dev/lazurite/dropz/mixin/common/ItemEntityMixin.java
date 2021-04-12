package dev.lazurite.dropz.mixin.common;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.util.DropType;
import dev.lazurite.dropz.util.storage.ItemEntityStorage;
import dev.lazurite.dropz.Dropz;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.physics.space.body.shape.BoundingBoxShape;
import dev.lazurite.rayon.entity.api.EntityPhysicsElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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
    @Unique private final ElementRigidBody rigidBody = new ElementRigidBody(this);
    @Unique private Item prevItem = Items.AIR;
    @Unique private DropType type = DropType.ITEM;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow public abstract ItemStack getStack();
    @Shadow @Nullable public abstract UUID getThrower();

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void init(EntityType<? extends ItemEntity> type, World world, CallbackInfo info) {
        this.prevItem = getStack().getItem();
        this.type = DropType.get(getStack());
        this.rigidBody.setAngularDamping(0.4f);
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
            this.type = DropType.get(getStack());
            this.prevItem = getStack().getItem();
            CollisionShape shape = new BoundingBoxShape(type.getBox());

            MinecraftSpace.get(asEntity().getEntityWorld()).getThread().execute(() -> {
                getRigidBody().setCollisionShape(shape);
                getRigidBody().setMass(type.getMass());
            });
        }

        doDamage();
        Vector3f location = getRigidBody().getPhysicsLocation(new Vector3f());
        this.updatePosition(location.x, location.y + getBoundingBox().getYLength() * 0.5, location.z);
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isSpaceEmpty(Lnet/minecraft/entity/Entity;)Z"))
    public boolean isSpaceEmpty(World world, Entity entity) {
        return true;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 0))
    public void setVelocityInTick0(ItemEntity itemEntity, Vec3d velocity) { }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 1))
    public void setVelocityInTick1(ItemEntity itemEntity, Vec3d velocity) { }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V", ordinal = 2))
    public void setVelocityInTick2(ItemEntity itemEntity, Vec3d velocity) { }

    @Environment(EnvType.CLIENT)
    @Inject(method = "method_27314", at = @At("HEAD"), cancellable = true)
    public void method_27314(float f, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(0.0f);
    }

    @Inject(method = "canMerge()Z", at = @At("HEAD"), cancellable = true)
    public void canMerge(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }

    @Inject(method = "applyBuoyancy", at = @At("HEAD"), cancellable = true)
    public void applyBuoyancy(CallbackInfo info) {
        info.cancel();
    }

    @Inject(method = "method_24348", at = @At("HEAD"), cancellable = true)
    private void method_24348(CallbackInfo info) {
       info.cancel();
    }

    @Inject(method = "createSpawnPacket", at = @At("HEAD"), cancellable = true)
    public void createSpawnPacket(CallbackInfoReturnable<Packet<?>> info) {
        info.setReturnValue(getSpawnPacket());
    }

    @Override
    public DropType getDropType() {
        return this.type;
    }

    @Override
    public ElementRigidBody getRigidBody() {
        return this.rigidBody;
    }

    @Override
    public void step(MinecraftSpace space) { }

    private void doDamage() {
        /* Momentum */
        float p = getRigidBody().getLinearVelocity(new Vector3f()).length() * getRigidBody().getMass();

        /* Velocity */
        float v = getRigidBody().getLinearVelocity(new Vector3f()).length();

        if (v >= 15) {
            for (Entity entity : asEntity().getEntityWorld().getOtherEntities(((ItemEntity) (Object) this), this.getBoundingBox(), (entity) -> entity instanceof LivingEntity)) {
                if (getThrower() != null) {
                    if (!entity.equals(asEntity().getEntityWorld().getPlayerByUuid(getThrower()))) {
                        entity.damage(DamageSource.GENERIC, p / 20.0f);

                        /* Loses 90% of its speed */
                        MinecraftSpace.get(asEntity().getEntityWorld()).getThread().execute(() ->
                                getRigidBody().applyCentralImpulse(getRigidBody().getLinearVelocity(new Vector3f()).multLocal(0.1f).multLocal(getRigidBody().getMass())));
                    }
                }
            }
        }
    }
}
