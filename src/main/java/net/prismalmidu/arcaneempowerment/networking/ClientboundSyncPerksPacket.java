package net.prismalmidu.arcaneempowerment.networking;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;

import java.util.function.Supplier;

public class ClientboundSyncPerksPacket {
    private final CompoundTag nbt;

    public ClientboundSyncPerksPacket(CompoundTag nbt) {
        this.nbt = nbt;
    }

    public ClientboundSyncPerksPacket(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(this.nbt);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // Process on client memory cache
            Minecraft.getInstance().player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
                perks.loadNBT(this.nbt);
            });
        });
        return true;
    }
}