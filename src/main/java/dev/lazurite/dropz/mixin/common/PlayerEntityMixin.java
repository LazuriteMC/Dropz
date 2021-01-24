package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.util.storage.PlayerEntityStorage;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.QuaternionHelper;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
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
import physics.javax.vecmath.Quat4f;
import physics.javax.vecmath.Vector3f;

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
            entity.updatePosition(entity.getX(), entity.getY() - 0.75, entity.getZ());

            EntityRigidBody body = EntityRigidBody.get(entity);
            body.setLinearVelocity(VectorHelper.mul(body.getLinearVelocity(new Vector3f()), 1.25f));

            /* Set random spin */
            body.setAngularVelocity(new Vector3f(random.nextInt(10) - 5, random.nextInt(10) - 5, random.nextInt(10) - 5));

            /* Set random orientation */
            Quat4f orientation = new Quat4f(0, 1, 0, 0);
            QuaternionHelper.rotateX(orientation, random.nextInt(180));
            QuaternionHelper.rotateY(orientation, random.nextInt(180));
            QuaternionHelper.rotateZ(orientation, random.nextInt(180));
            body.setOrientation(orientation);

            if (isSneaking()) {
                body.setLinearVelocity(VectorHelper.mul(body.getLinearVelocity(new Vector3f()), yeetMultiplier));
                info.setReturnValue(entity);
            }
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
