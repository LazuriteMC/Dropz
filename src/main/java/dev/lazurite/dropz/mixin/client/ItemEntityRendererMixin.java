package dev.lazurite.dropz.mixin.client;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    @Shadow @Final
    private ItemRenderer itemRenderer;

    protected ItemEntityRendererMixin(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Shadow
    protected abstract int getRenderedAmount(ItemStack stack);

    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    public void render(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo info) {
        matrixStack.push();

        ItemStack itemStack = itemEntity.getStack();
        BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, (LivingEntity)null);

        int k = this.getRenderedAmount(itemStack);

        float l = MathHelper.sin(((float)itemEntity.getAge() + g) / 10.0F + itemEntity.hoverHeight) * 0.1F + 0.1F;
        float m = bakedModel.getTransformation().getTransformation(ModelTransformation.Mode.GROUND).scale.getY();
        matrixStack.translate(0.0D, (double)(l + 0.25F * m), 0.0D);

        float o = bakedModel.getTransformation().ground.scale.getX();
        float p = bakedModel.getTransformation().ground.scale.getY();
        float q = bakedModel.getTransformation().ground.scale.getZ();
        float v;
        float w;
        if (!bakedModel.hasDepth()) {
            float r = -0.0F * (float)(k - 1) * 0.5F * o;
            v = -0.0F * (float)(k - 1) * 0.5F * p;
            w = -0.09375F * (float)(k - 1) * 0.5F * q;
            matrixStack.translate((double)r, (double)v, (double)w);
        }

        matrixStack.pop();
        super.render(itemEntity, f, g, matrixStack, vertexConsumerProvider, i);
        info.cancel();
    }
}
