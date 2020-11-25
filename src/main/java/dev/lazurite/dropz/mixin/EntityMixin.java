package dev.lazurite.dropz.mixin;

import dev.lazurite.api.physics.util.math.VectorHelper;
import dev.lazurite.dropz.server.entity.PhysicsItemEntity;
import dev.lazurite.dropz.util.ItemEntityTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "setVelocity(Lnet/minecraft/util/math/Vec3d;)V")
    public void setVelocity(Vec3d velocity, CallbackInfo info) {
        if (!((Entity) (Object) this).getEntityWorld().isClient()) {
            if (((Entity) (Object) this).getClass() == ItemEntity.class) {
                int id = ((Entity) (Object) this).getEntityId();
                PhysicsItemEntity physicsItem = ItemEntityTracker.get(id);

                if (physicsItem != null) {
                    physicsItem.getPhysics().setLinearVelocity(VectorHelper.vec3dToVector3f(velocity.multiply(1)));
                    System.out.println(physicsItem.getPhysics().getLinearVelocity());
                }
            }
        }
    }
}
