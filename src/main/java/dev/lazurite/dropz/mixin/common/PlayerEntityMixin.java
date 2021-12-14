package dev.lazurite.dropz.mixin.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.config.Config;
import dev.lazurite.rayon.api.PhysicsElement;
import dev.lazurite.rayon.impl.bullet.collision.body.ElementRigidBody;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin allows the player to throw an item
 * <b><i>harder</i></b> whilst sneaking. Hence, they can
 * <b><i>yeet</i></b> the newly created {@link ItemEntity}.
 */
@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"), cancellable = true)
    public void drop_RETURN(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info) {
        ItemEntity entity = info.getReturnValue();

        if (entity != null) {
            // Set the position
            Vec3 lookDirection = getViewVector(1.0f).normalize();
            Vec3 position = entity.position().add(lookDirection.scale(0.25)).add(0, entity.getEyeHeight(), 0);
            entity.absMoveTo(position.x(), position.y(), position.z());
            ElementRigidBody body = ((PhysicsElement) entity).getRigidBody();

            // Set the rotation
            Quaternion orientation = new Quaternion();
            QuaternionHelper.rotateX(Convert.toMinecraft(orientation), random.nextInt(180));
            QuaternionHelper.rotateY(Convert.toMinecraft(orientation), random.nextInt(180));
            QuaternionHelper.rotateZ(Convert.toMinecraft(orientation), random.nextInt(180));
            body.setPhysicsRotation(orientation);

            // Set the linear and angular velocities
            body.setLinearVelocity(Convert.toBullet(getViewVector(1.0f).scale(1.5)));
            body.setAngularVelocity(new Vector3f(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5));

            /* Multiply velocity by yeet multiplier if player is sneaking */
            if (isShiftKeyDown()) {
                int yeetMultiplier = Config.getInstance().yeetMultiplier.getMultiplier();
                Vector3f yeet = new Vector3f(yeetMultiplier, yeetMultiplier, yeetMultiplier);
                body.setLinearVelocity(body.getLinearVelocity(new Vector3f()).multLocal(yeet));
            }
        }
    }
}