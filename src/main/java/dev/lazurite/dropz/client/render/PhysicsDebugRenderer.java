package dev.lazurite.dropz.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.api.physics.server.entity.PhysicsEntity;
import dev.lazurite.api.physics.util.math.VectorHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import shadow.com.bulletphysics.linearmath.DebugDrawModes;
import shadow.com.bulletphysics.linearmath.IDebugDraw;
import shadow.javax.vecmath.Vector3f;

@Environment(EnvType.CLIENT)
public class PhysicsDebugRenderer extends IDebugDraw {
    @Override
    public void drawLine(Vector3f from, Vector3f to, Vector3f color) {
//        Vector3f vec = new Vector3f();
//        vec.set(to);
//        vec.sub(from);
//        System.out.println(vec);

        RenderSystem.disableTexture();
        RenderSystem.depthMask(false);

//        GL30.glPushMatrix();
//        float[] points = {
//                from.x,
//                from.y,
//                from.z,
//                color.x,
//                color.y,
//                color.z,
//
//                to.x,
//                to.y,
//                to.z,
//                color.x,
//                color.y,
//                color.z
//        };
//
//        int[] vbo = {1, 0};
//        GL30.glGenBuffers(vbo);
//
//        int[] vao = {1, 0};
//        GL30.glGenVertexArrays(vao);
//        GL30.glBindVertexArray(vao[1]);
//        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo[1]);
//        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, points, GL30.GL_STATIC_DRAW);
//        GL30.glEnableVertexAttribArray(0);
//        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 6 * 4, 0);
//        GL30.glEnableVertexAttribArray(1);
//        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 6 * 4, 3 * 4);
//        GL30.glBindVertexArray(0);
//
//        GL30.glBindVertexArray(vao[1]);
//        GL30.glDrawArrays(GL30.GL_LINES, 0, 2);
//        GL30.glBindVertexArray(0);
//        GL30.glPopMatrix();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

        Vector3f from2 = new Vector3f();
        from2.set(from);
//        from2.sub(entity.getPhysics().getPosition());
        from2.sub(VectorHelper.vec3dToVector3f(camera.getPos()));

        Vector3f to2 = new Vector3f();
        to2.set(to);
//        to2.sub(entity.getPhysics().getPosition());
        to2.sub(VectorHelper.vec3dToVector3f(camera.getPos()));

//        this.stack.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_X.getDegreesQuaternion(camera.getPitch()));
//        this.stack.multiply(net.minecraft.client.util.math.Vector3f.POSITIVE_Y.getDegreesQuaternion(camera.getYaw() + 180.0F));

//        RenderSystem.rotatef(camera.getYaw(), 0, 1, 0);
//        RenderSystem.rotatef(camera.getPitch(), 1, 0, 0);

        RenderSystem.clear(256, MinecraftClient.IS_SYSTEM_MAC);
        RenderSystem.lineWidth(1.0F);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(from2.x, from2.y, from2.z).color(color.x, color.y, color.z, 0.5F).next();
        bufferBuilder.vertex(to2.x, to2.y, to2.z).color(color.x, color.y, color.z, 0.5F).next();
        Tessellator.getInstance().draw();

//        GL30.glBegin(GL30.GL_MATRIX_MODE);
//        GL30.glRotatef(camera.getYaw(), 0, 1, 0);
//        GL30.glRotatef(camera.getPitch(), 1, 0, 0);
//        GL30.glColor3f(color.x, color.y, color.z);
//        GL30.glVertex3f(from2.x, from2.y, from2.z);
//        GL30.glVertex3f(to2.x, to2.y, to2.z);
//        GL30.glEnd();

        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
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
        return DebugDrawModes.DRAW_WIREFRAME;
    }
}
