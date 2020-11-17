package dev.lazurite.dropz.mixin;

import dev.lazurite.api.physics.client.PhysicsWorld;
import dev.lazurite.dropz.client.ClientInitializer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadow.com.bulletphysics.linearmath.Transform;
import shadow.javax.vecmath.Vector3f;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
//        PhysicsWorld world = PhysicsWorld.getInstance();
//        world.getDynamicsWorld().setDebugDrawer(ClientInitializer.debugRenderer);
//        world.getDynamicsWorld().debugDrawWorld();
//        world.getRigidBodies().forEach(body -> {
//            world.getDynamicsWorld().debugDrawObject(body.getWorldTransform(new Transform()), body.getCollisionShape(), new Vector3f(1.0f, 0, 1.0f));
//        });
    }
}
