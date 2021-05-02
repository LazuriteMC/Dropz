package dev.lazurite.dropz.mixin.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.config.Config;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import dev.lazurite.rayon.core.impl.util.math.VectorHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin allows the player to throw an item
 * <b><i>harder</i></b> whilst sneaking. Hence, they can
 * <b><i>yeet</i></b> the newly created {@link ItemEntity}.
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"), cancellable = true)
    public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info) {
        ItemEntity entity = info.getReturnValue();

        if (entity != null) {
            // Set the position
            Vec3d lookDirection = getRotationVec(1.0f).normalize();
            Vec3d position = entity.getPos().add(lookDirection.multiply(0.25)).add(0, entity.getStandingEyeHeight(), 0);
            entity.updatePosition(position.getX(), position.getY(), position.getZ());
            ElementRigidBody body = ((PhysicsElement) entity).getRigidBody();

            // Set the rotation
            Quaternion orientation = new Quaternion();
            QuaternionHelper.rotateX(orientation, random.nextInt(180));
            QuaternionHelper.rotateY(orientation, random.nextInt(180));
            QuaternionHelper.rotateZ(orientation, random.nextInt(180));
            body.setPhysicsRotation(orientation);

            // Set the linear and angular velocities
            body.setLinearVelocity(VectorHelper.vec3dToVector3f(getRotationVec(1.0f).multiply(1.5)));
            body.setAngularVelocity(new Vector3f(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5));

            /* Multiply velocity by yeet multiplier if player is sneaking */
            if (isSneaking()) {
                int yeetMultiplier = Config.getInstance().yeetMultiplier.getMultiplier();
                Vector3f yeet = new Vector3f(yeetMultiplier, yeetMultiplier, yeetMultiplier);
                body.setLinearVelocity(body.getLinearVelocity(new Vector3f()).multLocal(yeet));
            }
        }
    }
}