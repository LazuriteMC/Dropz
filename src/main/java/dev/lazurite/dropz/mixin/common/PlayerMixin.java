package dev.lazurite.dropz.mixin.common;

import com.jme3.math.Vector3f;
import com.mojang.math.Quaternion;
import dev.lazurite.dropz.util.Config;
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
public abstract class PlayerMixin {
    @Inject(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("RETURN"),
            cancellable = true
    )
    public void drop_RETURN(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> info) {
        final var itemEntity = info.getReturnValue();
        if (itemEntity == null) return;

        final var body = ((EntityPhysicsElement) itemEntity).getRigidBody();
        final var player = (Player) (Object) this;
        final var random = player.getRandom();
        final var lookDirection = player.getViewVector(1.0f).normalize();

        final var position = itemEntity.position().add(lookDirection.scale(0.05)).add(0, itemEntity.getEyeHeight(), 0);
        itemEntity.absMoveTo(position.x(), position.y(), position.z());

        // Set up the rotation
        final var rotation = new Quaternion(Quaternion.ONE);
        QuaternionHelper.rotateX(rotation, random.nextInt(180));
        QuaternionHelper.rotateY(rotation, random.nextInt(180));
        QuaternionHelper.rotateZ(rotation, random.nextInt(180));
        body.setPhysicsRotation(Convert.toBullet(rotation));

        // Set up the angular velocity
        body.setAngularVelocity(new Vector3f(
                random.nextInt(8) - 4,
                random.nextInt(8) - 4,
                random.nextInt(8) - 4
        ));

        // Set up the linear velocity
        final var linearVelocity = Convert.toBullet(lookDirection.scale(2.0));

        if (player.isShiftKeyDown()) {
            linearVelocity.multLocal(6.0f).multLocal(Config.getInstance().yeetMultiplier);
        }

        body.setLinearVelocity(linearVelocity);
    }
}