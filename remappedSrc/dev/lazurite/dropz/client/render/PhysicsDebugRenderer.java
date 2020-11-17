package dev.lazurite.dropz.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import shadow.com.bulletphysics.linearmath.IDebugDraw;
import shadow.javax.vecmath.Vector3f;

@Environment(EnvType.CLIENT)
public class PhysicsDebugRenderer extends IDebugDraw {
    @Override
    public void drawLine(Vector3f x, Vector3f y, Vector3f z) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0F);
        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);

        Tessellator tessellator;
        BufferBuilder bufferBuilder;

        tessellator = Tessellator.getInstance();
        bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(x.x, x.y, x.z).color(1.0F, 0.0F, 0.0F, 0.5F).next();
        bufferBuilder.vertex(y.x, y.y, y.z).color(1.0F, 0.0F, 0.0F, 0.5F).next();
        bufferBuilder.vertex(z.x, z.y, z.z).color(1.0F, 0.0F, 0.0F, 0.5F).next();
        tessellator.draw();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Override
    public void drawContactPoint(Vector3f vector3f, Vector3f vector3f1, float v, int i, Vector3f vector3f2) {

    }

    @Override
    public void reportErrorWarning(String s) {

    }

    @Override
    public void draw3dText(Vector3f vector3f, String s) {

    }

    @Override
    public void setDebugMode(int i) {

    }

    @Override
    public int getDebugMode() {
        return 0;
    }
}
