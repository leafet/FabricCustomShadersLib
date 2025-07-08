package aghenon.rendermod.effects;

import aghenon.rendermod.RenderMod;
import aghenon.rendermod.network.payloads.SphereEffectPayload;
import aghenon.rendermod.rendering.ShaderHandler;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SphereEffectRenderer {

    private static final List<SphereInstance> ACTIVE_SPHERES = new ArrayList<>();
    private static int treshold = 80;

    public static void init(){

        ClientPlayNetworking.registerGlobalReceiver(SphereEffectPayload.ID, ((sphereEffectPayload, context) -> {
            context.client().execute(() -> {
                ACTIVE_SPHERES.add(new SphereInstance(sphereEffectPayload.pos()));
                treshold = 1000;
            });
        }));

        ClientTickEvents.END_CLIENT_TICK.register(client ->{
            ACTIVE_SPHERES.removeIf(sphereInstance -> sphereInstance.age ++ >= treshold);
        });

        WorldRenderEvents.END.register(context -> {
            Camera camera = context.camera();

            for (SphereInstance sphere : ACTIVE_SPHERES){
                // Create model matrix for sphere position
                Matrix4f modelMatrix = new Matrix4f().translation(
                        (float) sphere.position.x,
                        (float) sphere.position.y,
                        (float) sphere.position.z
                );

                // Get camera rotation and invert it for view matrix
                Matrix4f viewMatrix = camera.getRotation().get(new Matrix4f());
                viewMatrix.invert(); // Invert the rotation matrix
                // Apply camera translation
                viewMatrix.translate(
                        (float) -camera.getPos().x,
                        (float) -camera.getPos().y,
                        (float) -camera.getPos().z
                );

                // Combine: view * model
                Matrix4f modelViewMatrix = new Matrix4f(viewMatrix).mul(modelMatrix);

                renderSphere(modelViewMatrix, modelMatrix, sphere, context, camera.getPos().toVector3f());
            }
        });
    }

    private static float easeOutCirc(float x) {
        x = 1 - x;
        return x >= 1.0f ? 1.0f : 1.0f - (float) Math.pow(2, -10 * x);
    }

    private static void renderSphere(Matrix4f modelViewMat, Matrix4f modelMat, SphereInstance sphere, WorldRenderContext context, Vector3f cameraPos) {

        float scale = 10.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest(); // Enable depth testing so spheres are blocked by blocks
        RenderSystem.disableCull();
        RenderSystem.depthMask(false); // Enable depth writing

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(
                VertexFormat.DrawMode.TRIANGLE_STRIP,
                VertexFormats.POSITION_COLOR
        );

        ShaderProgram shader = ShaderHandler.MY_SPHERE_SHADER;
        if (shader == null) {
            return;
        }

        shader.bind();

        int projLoc = GL20.glGetUniformLocation(shader.getGlRef(), "ProjectionMat");
        int modelViewLoc = GL20.glGetUniformLocation(shader.getGlRef(), "ModelViewMat");
        int modelLoc = GL20.glGetUniformLocation(shader.getGlRef(), "ModelMat");
        int playerPosLoc = GL20.glGetUniformLocation(shader.getGlRef(), "u_playerPos");

        Matrix4f projectionMatrix = context.projectionMatrix();

        float[] projArray = new float[16];
        float[] mvArray = new float[16];
        float[] modelArray = new float[16];

        projectionMatrix.get(projArray);
        modelViewMat.get(mvArray);
        modelMat.get(modelArray);

        GL20.glUniformMatrix4fv(projLoc, false, projArray);
        GL20.glUniformMatrix4fv(modelViewLoc, false, mvArray);
        GL20.glUniformMatrix4fv(modelLoc, false, modelArray);
        GL20.glUniform3f(playerPosLoc, cameraPos.x, cameraPos.y, cameraPos.z);

        buildSphere(buffer, scale, 0.5f);

        BufferRenderer.draw(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();

    }

    private static float[] makeProjMatrix(Matrix4f projM, Matrix4f viewModelM){
        float[] res = new float[16];
        projM.mul(viewModelM).get(res);
        return res;
    }

    private static void buildSphere(BufferBuilder buffer, float scale, float alpha) {
        int stacks = 64;
        int sectors = 64;
        Color color = new Color(1f, 1f, 1f, alpha);

        for (int i = 0; i < stacks; i++) {
            double phi1 = Math.PI * i / stacks;
            double phi2 = Math.PI * (i + 1) / stacks;
            for (int j = 0; j <= sectors; j++) {
                double theta = 2.0 * Math.PI * j / sectors;
                // Вершины в локальных координатах (без масштаба и смещения)
                float x1 = (float)(Math.sin(phi1) * Math.cos(theta));
                float y1 = (float)(Math.cos(phi1));
                float z1 = (float)(Math.sin(phi1) * Math.sin(theta));
                float x2 = (float)(Math.sin(phi2) * Math.cos(theta));
                float y2 = (float)(Math.cos(phi2));
                float z2 = (float)(Math.sin(phi2) * Math.sin(theta));

                buffer.vertex(x1 * scale, y1 * scale, z1 * scale).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
                buffer.vertex(x2 * scale, y2 * scale, z2 * scale).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            }
        }
    }

    private static class SphereInstance{
        final Vec3d position;
        int age;
        SphereInstance(Vec3d pos){
            this.position = pos;
            age = 0;
        }
    }
}

