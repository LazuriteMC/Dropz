package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.util.ShapeGenerator;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.EntityRigidBody;
import dev.lazurite.transporter.api.pattern.Pattern;
import dev.lazurite.transporter.impl.Transporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements EntityPhysicsElement {
    @Unique private final EntityRigidBody rigidBody = new EntityRigidBody(this);
    @Unique private Item prevItem = Items.AIR;
    @Unique private Pattern prevPattern;

    @Shadow public abstract ItemStack getItem();

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    public void init_RETURN(EntityType<? extends ItemEntity> type, Level level, CallbackInfo info) {
        this.prevItem = getItem().getItem();
        this.rigidBody.setMass(5f);
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
        final var thisEntity = (ItemEntity) (Object) this;

        if (!thisEntity.level.isClientSide) {
            final var pattern = Transporter.getPatternBuffer().getItem(Item.getId(getItem().getItem()));

            if (pattern != prevPattern) {
                this.prevPattern = pattern;
                rigidBody.setCollisionShape(ShapeGenerator.create((ItemEntity) (Object) this));
            }
        }

        if (!getItem().getItem().equals(prevItem)) {
            this.prevItem = getItem().getItem();

            // Item has changed, create a new shape
            rigidBody.setCollisionShape(ShapeGenerator.create((ItemEntity) (Object) this));
        }
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;noCollision(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Z"))
    public boolean tick_noCollision(Level level, Entity entity, AABB aabb) {
        return true;
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    public void tick_setDeltaMovement(ItemEntity itemEntity, Vec3 vec3) {

    }

    @Inject(method = "getSpin", at = @At("HEAD"), cancellable = true)
    public void getSpin_HEAD(float f, CallbackInfoReturnable<Float> info) {
        info.setReturnValue(0.0f);
    }

    @Inject(method = "isMergable", at = @At("HEAD"), cancellable = true)
    public void isMergable_HEAD(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(false);
    }

    @Inject(method = "setUnderwaterMovement", at = @At("HEAD"), cancellable = true)
    public void setUnderwaterMovement_HEAD(CallbackInfo info) {
        info.cancel();
    }

    @Inject(method = "setUnderLavaMovement", at = @At("HEAD"), cancellable = true)
    private void setUnderLavaMovement(CallbackInfo info) {
       info.cancel();
    }

    @Override
    public EntityRigidBody getRigidBody() {
        return this.rigidBody;
    }

    // TODO monka
//    private void doDamage() {
//        final var p = getRigidBody().getLinearVelocity(new Vector3f()).length() * getRigidBody().getMass();
//        final var v = getRigidBody().getLinearVelocity(new Vector3f()).length();
//        final var level = thisEntity.getLevel();
//        final var box = thisEntity.getBoundingBox();
//
//        if (v >= 15) {
//            for (Entity entity : level.getEntities(((ItemEntity) (Object) this), box, (entity) -> entity instanceof LivingEntity)) {
//                if (getThrower() != null) {
//                    if (!entity.equals(level.getPlayerByUUID(getThrower()))) {
//                        entity.hurt(DamageSource.GENERIC, p / 20.0f);
//
//                        /* Loses 90% of its speed */
//                        final var linearVelocity = rigidBody.getLinearVelocity(new Vector3f());
//                        rigidBody.applyCentralImpulse(linearVelocity.multLocal(0.1f * rigidBody.getMass()));
//                    }
//                }
//            }
//        }
//    }
}
