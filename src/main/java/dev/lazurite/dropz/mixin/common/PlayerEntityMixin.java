package dev.lazurite.dropz.mixin.common;

import dev.lazurite.dropz.util.PlayerEntityAccess;
import dev.lazurite.rayon.physics.body.EntityRigidBody;
import dev.lazurite.rayon.physics.helper.math.VectorHelper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import physics.javax.vecmath.Vector3f;

/**
 * This mixin allows the player to throw an item
 * <b><i>harder</i></b> whilst sneaking. Hence, they can
 * <b><i>yeet</i></b> the newly created {@link ItemEntity}.
 * @see PlayerEntityAccess
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin implements PlayerEntityAccess {
    @Unique private float yeetMultiplier = 3.0f;

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"), cancellable = true)
    public void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info) {
        if (((PlayerEntity) (Object) this).isSneaking()) {
            ItemEntity entity = info.getReturnValue();

            if (entity != null) {
                EntityRigidBody body = EntityRigidBody.get(entity);
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