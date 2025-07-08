package aghenon.rendermod;

import aghenon.rendermod.effects.SphereEffectRenderer;
import aghenon.rendermod.effects.TestCubeRenderer;
import aghenon.rendermod.network.payloads.SphereEffectPayload;
import aghenon.rendermod.rendering.ShaderHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class RenderModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public Identifier getFabricId() {
						return Identifier.of("rendermod", "shader_loader");
					}

					@Override
					public void reload(ResourceManager manager) {

						RenderMod.LOGGER.info("Resources reloaded ");

						ShaderHandler.unload();
						ShaderHandler.load(manager);
					}
				}
		);

		SphereEffectRenderer.init();
	}
}