package dev.lazurite.dropz.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.dropz.util.ItemStorage;
import dev.lazurite.rayon.api.EntityPhysicsElement;
import dev.lazurite.rayon.impl.bullet.math.Convert;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Modifies the renderer for {@link ItemEntity} by removing
 * features such as hovering and rotating automatically.
 */
@Environment(EnvType.CLIENT)
@Mixin(ItemEntityRenderer.class)
public abstract class ItemEntityRendererMixin extends EntityRenderer<ItemEntity> {
    @Shadow @Final private ItemRenderer itemRenderer;

    protected ItemEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/world/entity/item/ItemEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", cancellable = true)
    public void render_HEAD(ItemEntity itemEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        final var itemStack = itemEntity.getItem();
        this.shadowRadius = 0.0f;

        final var element = (EntityPhysicsElement) itemEntity;
        final var rotation = Convert.toMinecraft(element.getPhysicsRotation(new com.jme3.math.Quaternion(), g));
        final var box = ((ItemStorage) element).getBox();
        final var bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.level, null, i);
        final var translation = bakedModel.getTransforms().getTransform(ItemDisplayContext.GROUND).translation;

        poseStack.pushPose();
        poseStack.mulPose(rotation);
        poseStack.translate(-translation.x(), -translation.y(), -translation.z());

        if (itemStack.getItem() instanceof BlockItem) {
            if (box != null) {
                poseStack.translate(0.0, 0.125f - box.getYExtent() * 0.5f, 0.0);
            }
        }

        this.itemRenderer.render(itemStack, ItemDisplayContext.GROUND, false, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY, bakedModel);
        poseStack.popPose();
        ci.cancel();
    }
}