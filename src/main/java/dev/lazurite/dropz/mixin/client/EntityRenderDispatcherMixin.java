package dev.lazurite.dropz.mixin.client;

import dev.lazurite.rayon.physics.body.EntityRigidBody;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Shadow @Final public GameOptions gameOptions;
    @Shadow private World world;
    @Shadow private boolean renderShadows;

    @Shadow public abstract <T extends Entity> EntityRenderer<? super T> getRenderer(T entity);
    @Shadow protected abstract void renderFire(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity);
    @Shadow public abstract double getSquaredDistanceToCamera(double x, double y, double z);

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public <E extends Entity> void render(E entity, double x, double y, double z, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info) {
        if (entity instanceof ItemEntity) {
            EntityRenderer entityRenderer = this.getRenderer(entity);

            try {
                matrices.push();
                matrices.translate(x, y + EntityRigidBody.get(entity).getBox().getYLength() / 2.0, z);
                entityRenderer.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
                matrices.translate(0, -EntityRigidBody.get(entity).getBox().getYLength() / 2.0, 0);

                if (entity.doesRenderOnFire()) {
                    this.renderFire(matrices, vertexConsumers, entity);
                }

                if (this.gameOptions.entityShadows && this.renderShadows && entityRenderer.shadowRadius > 0.0F && !entity.isInvisible()) {
                    double g = this.getSquaredDistanceToCamera(entity.getX(), entity.getY(), entity.getZ());
                    float h = (float) ((1.0D - g / 256.0D) * (double) entityRenderer.shadowOpacity);
                    if (h > 0.0F) {
                        EntityRenderDispatcher.renderShadow(matrices, vertexConsumers, entity, h, tickDelta, this.world, entityRenderer.shadowRadius);
                    }
                }

                matrices.pop();
                info.cancel();
            } catch (Throwable var24) {
                CrashReport crashReport = CrashReport.create(var24, "Rendering entity in world");
                CrashReportSection crashReportSection = crashReport.addElement("Entity being rendered");
                entity.populateCrashReport(crashReportSection);
                CrashReportSection crashReportSection2 = crashReport.addElement("Renderer details");
                crashReportSection2.add("Assigned renderer", (Object) entityRenderer);
                crashReportSection2.add("Location", (Object) CrashReportSection.createPositionString(x, y, z));
                crashReportSection2.add("Rotation", (Object) yaw);
                crashReportSection2.add("Delta", (Object) tickDelta);
                throw new CrashException(crashReport);
            }
        }
    }
}
