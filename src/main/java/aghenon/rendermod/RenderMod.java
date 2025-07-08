package aghenon.rendermod;

import aghenon.rendermod.items.ModItems;
import aghenon.rendermod.network.ModPackets;
import aghenon.rendermod.network.payloads.SphereEffectPayload;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderMod implements ModInitializer {
	public static final String MOD_ID = "rendermod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");

		ModPackets.Initialize();

		PayloadTypeRegistry.playS2C().register(SphereEffectPayload.ID, SphereEffectPayload.CODEC);

		ModItems.initialize();

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT)
				.register((itemGroup) -> itemGroup.add(ModItems.SHEESH));
	}
}