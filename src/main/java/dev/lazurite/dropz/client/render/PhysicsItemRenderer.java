package dev.lazurite.dropz.client.render;

import dev.lazurite.api.physics.util.math.QuaternionHelper;
import dev.lazurite.dropz.server.ServerInitializer;
import dev.lazurite.dropz.server.entity.PhysicsItemEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhysicsItemRenderer extends EntityRenderer<PhysicsItemEntity> {
    public PhysicsItemRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(PhysicsItemEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        MinecraftClient client = MinecraftClient.getInstance();

        matrixStack.push();
        ItemStack itemStack = entity.getStack();
        BakedModel bakedModel = client.itemRenderer.getHeldItemModel(itemStack, entity.world, (LivingEntity)null);

        matrixStack.peek().getModel().multiply(QuaternionHelper.quat4fToQuaternion(entity.getPhysics().getOrientation()));

        client.itemRenderer.renderItem(itemStack, ModelTransformation.Mode.GROUND, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
        matrixStack.pop();
    }

    @Override
    public Identifier getTexture(PhysicsItemEntity entity) {
        return null;
    }

    public static void register() {
        EntityRendererRegistry.INSTANCE.register(ServerInitializer.PHYSICS_ITEM_ENTITY, (entityRenderDispatcher, context) -> new PhysicsItemRenderer(entityRenderDispatcher));
    }
}