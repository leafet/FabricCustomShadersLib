package aghenon.rendermod.effects;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class TestCubeRenderer {
    private static final List<CubeInstance> ACTIVE_CUBES = new ArrayList<>();
    private static final Vec3d DEBUG_POS = new Vec3d(0, 100, 0); // Фиксированные координаты

    public static void init() {
        // Для теста добавляем куб автоматически
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if(ACTIVE_CUBES.isEmpty()) {
                ACTIVE_CUBES.add(new CubeInstance(DEBUG_POS));
            }
        });

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();

            matrices.push();
            // Конвертируем мировые координаты в координаты камеры
            matrices.translate(
                    DEBUG_POS.x - camera.getPos().x,
                    DEBUG_POS.y - camera.getPos().y,
                    DEBUG_POS.z - camera.getPos().z
            );

            renderDebugCube(matrices);

            matrices.pop();
        });
    }

    private static void renderDebugCube(MatrixStack matrices) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float size = 1.0f; // Размер куба
        float alpha = 0.5f;

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        // Нижняя грань
        addLine(buffer, matrix, -size, -size, -size, size, -size, -size, alpha);
        addLine(buffer, matrix, size, -size, -size, size, -size, size, alpha);
        addLine(buffer, matrix, size, -size, size, -size, -size, size, alpha);
        addLine(buffer, matrix, -size, -size, size, -size, -size, -size, alpha);

        // Верхняя грань
        addLine(buffer, matrix, -size, size, -size, size, size, -size, alpha);
        addLine(buffer, matrix, size, size, -size, size, size, size, alpha);
        addLine(buffer, matrix, size, size, size, -size, size, size, alpha);
        addLine(buffer, matrix, -size, size, size, -size, size, -size, alpha);

        // Вертикальные рёбра
        addLine(buffer, matrix, -size, -size, -size, -size, size, -size, alpha);
        addLine(buffer, matrix, size, -size, -size, size, size, -size, alpha);
        addLine(buffer, matrix, size, -size, size, size, size, size, alpha);
        addLine(buffer, matrix, -size, -size, size, -size, size, size, alpha);

        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void addLine(BufferBuilder buffer, Matrix4f matrix,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float alpha) {
        buffer.vertex(matrix, x1, y1, z1)
                .color(0f, 1f, 1f, alpha);

        buffer.vertex(matrix, x2, y2, z2)
                .color(0f, 1f, 1f, alpha);

    }

    private static class CubeInstance {
        final Vec3d position;

        CubeInstance(Vec3d pos) {
            this.position = pos;
        }
    }
}
