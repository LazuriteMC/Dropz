package dev.lazurite.dropz.mixin;

import dev.lazurite.api.physics.client.PhysicsWorld;
import dev.lazurite.dropz.client.ClientInitializer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
//    @Inject(
//            method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/util/math/MatrixStack;multiply(Lnet/minecraft/util/math/Quaternion;)V",
//                    ordinal = 2
//            )
//    )
    @Inject(method = "renderWorld", at = @At("TAIL"))
    public void renderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        PhysicsWorld world = PhysicsWorld.getInstance();
        world.getDynamicsWorld().setDebugDrawer(ClientInitializer.debugRenderer);
        world.getDynamicsWorld().debugDrawWorld();
    }
}
