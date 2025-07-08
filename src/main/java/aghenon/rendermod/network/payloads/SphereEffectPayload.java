package aghenon.rendermod.network.payloads;

import aghenon.rendermod.network.ModPackets;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.Vec3d;

public record SphereEffectPayload(Vec3d pos) implements CustomPayload {
    public static final CustomPayload.Id<SphereEffectPayload> ID = new CustomPayload.Id<>(ModPackets.SPHERE_EFFECT_PACKET_ID);

    public static final PacketCodec<RegistryByteBuf, SphereEffectPayload> CODEC = PacketCodec
            .tuple(Vec3d.PACKET_CODEC, SphereEffectPayload::pos, SphereEffectPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
