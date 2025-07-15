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
            ACTIVE_SPHERES.add(new SphereInstance(sphereEffectPayload.pos()));
            treshold = 1000;
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

                Matrix4f viewMatrix = camera.getRotation().get(new Matrix4f());
                viewMatrix.invert();

                viewMatrix.translate(
                        (float) -camera.getPos().x,
                        (float) -camera.getPos().y,
                        (float) -camera.getPos().z
                );

                // Combine: view * model
                Matrix4f modelViewMatrix = new Matrix4f(viewMatrix).mul(modelMatrix);

                Vector3f cameraPos = camera.getPos().toVector3f();

                renderSphere(modelViewMatrix, modelMatrix, sphere, context, cameraPos, viewMatrix);
            }
        });
    }

    private static void renderSphere(Matrix4f modelViewMat, Matrix4f modelMat, SphereInstance sphere, WorldRenderContext context, Vector3f cam, Matrix4f viewMatrix) {

        float scale = 10.0f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest(); // Enable depth testing so spheres are blocked by blocks
        RenderSystem.disableCull();
        RenderSystem.depthMask(false); // Enable depth writing

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(
                VertexFormat.DrawMode.QUADS,
                VertexFormats.POSITION
        );

        buffer.vertex(-1.0f, -1.0f, 0.0f);
        buffer.vertex(1.0f, -1.0f, 0.0f);
        buffer.vertex(1.0f, 1.0f, 0.0f);
        buffer.vertex(-1.0f, 1.0f, 0.0f);

        ShaderProgram shader = ShaderHandler.MY_SPHERE_SHADER;
        if (shader == null) {
            return;
        }

        shader.bind();

        Vector3f center = sphere.position.toVector3f();

        int camPosLoc = GL20.glGetUniformLocation(shader.getGlRef(), "u_cameraPos");
        int sphereCenterLoc = GL20.glGetUniformLocation(shader.getGlRef(), "u_sphereCenter");
        int radiusLoc = GL20.glGetUniformLocation(shader.getGlRef(), "u_sphereRadius");
        int invProjLoc = GL20.glGetUniformLocation(shader.getGlRef(), "u_inverseProjectionMatrix");
        int invViewLoc = GL20.glGetUniformLocation(shader.getGlRef(), "u_inverseViewMatrix");

        GL20.glUniform3f(camPosLoc, cam.x, cam.y, cam.z);
        GL20.glUniform3f(sphereCenterLoc, center.x, center.y, center.z);
        GL20.glUniform1f(radiusLoc, scale);

        Matrix4f invProj = new Matrix4f(context.projectionMatrix()).invert();
        Matrix4f invView = new Matrix4f(viewMatrix).invert();

        float[] invProjArray = new float[16];
        float[] invViewArray = new float[16];
        invProj.get(invProjArray);
        invView.get(invViewArray);

        GL20.glUniformMatrix4fv(invProjLoc, false, invProjArray);
        GL20.glUniformMatrix4fv(invViewLoc, false, invViewArray);

        BufferRenderer.draw(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();

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

