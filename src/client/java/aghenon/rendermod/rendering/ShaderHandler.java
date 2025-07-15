package aghenon.rendermod.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.CompiledShader;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ShaderHandler {
    public static ShaderProgram MY_SPHERE_SHADER;

    public static void load(ResourceManager manager) {
        try {
            Identifier vertexId = Identifier.of("rendermod", "shaders/program/my_sphere.vsh");
            Identifier fragmentId = Identifier.of("rendermod", "shaders/program/my_sphere.fsh");

            String vertexSource = loadShaderSource(manager, vertexId);
            String fragmentSource = loadShaderSource(manager, fragmentId);

            CompiledShader vertexShader = CompiledShader.compile(vertexId, CompiledShader.Type.VERTEX, vertexSource);
            CompiledShader fragmentShader = CompiledShader.compile(fragmentId, CompiledShader.Type.FRAGMENT, fragmentSource);

            MY_SPHERE_SHADER = ShaderProgram.create(vertexShader, fragmentShader, VertexFormats.POSITION_COLOR);

        } catch (Exception e) {
            RenderSystem.recordRenderCall(() -> {
                throw new RuntimeException("Failed to load shaders", e);
            });
        }
    }

    public static void unload() {
        if (MY_SPHERE_SHADER != null) {
            MY_SPHERE_SHADER.close();
            MY_SPHERE_SHADER = null;
        }
    }

    public static void use() {
        if (MY_SPHERE_SHADER != null) {
            MY_SPHERE_SHADER.bind();
        }
    }

    public static String loadShaderSource(ResourceManager resourceManager, Identifier id) throws IOException {
        Resource resource = resourceManager.getResource(id).orElseThrow(() ->
                new FileNotFoundException("Shader not found: " + id));
        try (InputStream input = resource.getInputStream()) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
