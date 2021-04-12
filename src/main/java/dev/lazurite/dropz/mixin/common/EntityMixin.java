package dev.lazurite.dropz.mixin.common;

import com.jme3.math.Vector3f;
import dev.lazurite.rayon.core.api.PhysicsElement;
import dev.lazurite.rayon.core.impl.physics.space.MinecraftSpace;
import dev.lazurite.rayon.core.impl.physics.space.body.ElementRigidBody;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow public World world;

    @Shadow public abstract ActionResult interact(PlayerEntity player, Hand hand);

    @Environment(EnvType.CLIENT)
    @Inject(method = "shouldRender(D)Z", at = @At("HEAD"), cancellable = true)
    public void shouldRender(double distance, CallbackInfoReturnable<Boolean> info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void pushOutOfBlocks(double x, double y, double z, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }

    @Inject(method = "setVelocity(DDD)V", at = @At("HEAD"))
    public void setVelocity(double x, double y, double z, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            ElementRigidBody rigidBody = ((PhysicsElement) this).getRigidBody();
            Vector3f velocity = new Vector3f((float) x * 20, (float) y * 20 * 0.5f, (float) z * 20).multLocal(rigidBody.getMass());
            MinecraftSpace.get(world).getThread().execute(() -> rigidBody.applyCentralImpulse(velocity));
        }
    }

    @Inject(method = "updateWaterState", at = @At("HEAD"), cancellable = true)
    public void updateWaterState(CallbackInfoReturnable<Boolean> info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move(MovementType type, Vec3d movement, CallbackInfo info) {
        if (((Entity) (Object) this) instanceof ItemEntity) {
            info.cancel();
        }
    }
}
