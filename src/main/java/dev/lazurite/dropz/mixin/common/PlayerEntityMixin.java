package dev.lazurite.dropz.mixin.common;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import dev.lazurite.dropz.util.storage.PlayerEntityStorage;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.PhysicsThread;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import dev.lazurite.rayon.core.impl.util.math.QuaternionHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin allows the player to throw an item
 * <b><i>harder</i></b> whilst sneaking. Hence, they can
 * <b><i>yeet</i></b> the newly created {@link ItemEntity}.
 * @see PlayerEntityStorage
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements PlayerEntityStorage {
    @Unique private float yeetMultiplier = 4.0f;

    private PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"), cancellable = true)
    public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info) {
        ItemEntity entity = info.getReturnValue();

        if (entity != null) {
            entity.updatePosition(entity.getX(), entity.getY() - 0.5, entity.getZ());

            ElementRigidBody body = ((PhysicsElement) entity).getRigidBody();
            Quaternion orientation = new Quaternion();
            QuaternionHelper.rotateX(orientation, random.nextInt(180));
            QuaternionHelper.rotateY(orientation, random.nextInt(180));
            QuaternionHelper.rotateZ(orientation, random.nextInt(180));

            PhysicsThread.get(world).execute(() -> {
                /* Set initial velocity */
                body.setLinearVelocity(body.getLinearVelocity(new Vector3f()).multLocal(1.25f));

                /* Set random spin */
                body.setAngularVelocity(new Vector3f(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5));

                /* Set random orientation */
                body.setPhysicsRotation(orientation);

                /* Multiply velocity by yeet multiplier if player is sneaking */
                if (isSneaking()) {
                    Vector3f yeet = new Vector3f(yeetMultiplier,  yeetMultiplier, yeetMultiplier);
                    body.setLinearVelocity(body.getLinearVelocity(new Vector3f()).multLocal(yeet));
                }
            });
        }
    }

    @Override
    public void setYeetMultiplier(float yeetMultiplier) {
        this.yeetMultiplier = yeetMultiplier;
    }

    @Override
    public float getYeetMultiplier() {
        return this.yeetMultiplier;
    }
}
