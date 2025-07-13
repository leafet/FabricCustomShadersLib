package aghenon.rendermod.items.custom;

import aghenon.rendermod.network.ModPackets;
import aghenon.rendermod.network.payloads.SphereEffectPayload;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class Sheesh extends Item {

    private enum State{
        Idle,
        Binding
    }

    private State state = State.Idle;
    private String allowedUser = "";

    public Sheesh(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(
                Text.literal("Current state is: " + state.toString() + " ")
                .styled(style -> style.withColor(Formatting.GOLD))
                        .append(Text.literal("HELP-ME").styled(style -> style.withObfuscated(true)))
                );
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {

        if (!world.isClient()){

           Vec3d pos = user.raycast(1000, 0.1F, false).getPos();

           for (ServerPlayerEntity player : PlayerLookup.world((ServerWorld) world)){
               sendSphereEffect(player, pos);
           }

        }

        return super.use(world, user, hand);
    }

    private void sendSphereEffect(ServerPlayerEntity player, Vec3d pos){

        ServerPlayNetworking.send(
                player,
                new SphereEffectPayload(pos)
        );
    }

    private void bindItem(PlayerEntity user){

    }
}
