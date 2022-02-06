package dev.lazurite.dropz.mixin.common;

import com.jme3.math.Vector3f;
import com.mojang.math.Quaternion;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import dev.lazurite.toolbox.api.math.QuaternionHelper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
public abstract class PlayerEntityMixin {
    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"), cancellable = true)
    public void drop_RETURN(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info) {
        final var player = (Player) (Object) this;
        final var itemEntity = info.getReturnValue();

        if (itemEntity != null) {
            final var lookDirection = player.getViewVector(1.0f).normalize();
            final var position = itemEntity.position().add(lookDirection.scale(0.05)).add(0, itemEntity.getEyeHeight(), 0);
            itemEntity.absMoveTo(position.x(), position.y(), position.z());

            final var body = ((EntityPhysicsElement) itemEntity).getRigidBody();
            final var random = player.getRandom();

            // Set the rotation
            final var rotation = new Quaternion(Quaternion.ONE);
            QuaternionHelper.rotateX(rotation, random.nextInt(180));
            QuaternionHelper.rotateY(rotation, random.nextInt(180));
            QuaternionHelper.rotateZ(rotation, random.nextInt(180));
            body.setPhysicsRotation(Convert.toBullet(rotation));

            // Set the linear and angular velocities
            body.setLinearVelocity(Convert.toBullet(lookDirection.scale(2.0)));
            body.setAngularVelocity(new Vector3f(random.nextInt(8) - 4, random.nextInt(8) - 4, random.nextInt(8) - 4));

            /* Multiply velocity by yeet multiplier if player is sneaking */
            if (player.isShiftKeyDown()) {
                final var yeetMultiplier = 2.0f;
                Vector3f yeet = new Vector3f(yeetMultiplier, yeetMultiplier, yeetMultiplier);
                body.setLinearVelocity(body.getLinearVelocity(new Vector3f()).multLocal(yeet));
            }
        }
    }
}