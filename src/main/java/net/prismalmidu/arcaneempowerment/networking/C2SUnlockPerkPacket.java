package net.prismalmidu.arcaneempowerment.networking;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.prismalmidu.arcaneempowerment.block.entity.PerkShrineBlockEntity;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;

import java.util.function.Supplier;

public class C2SUnlockPerkPacket {
    private final String perkId;
    private final BlockPos pos;

    // Constructor used by the client to prepare the data
    public C2SUnlockPerkPacket(String perkId, BlockPos pos) {
        this.perkId = perkId;
        this.pos = pos;
    }

    // Constructor used by the server to reconstruct the data out of raw network bytes
    public C2SUnlockPerkPacket(FriendlyByteBuf buf) {
        this.perkId = buf.readUtf();
        this.pos = buf.readBlockPos();
    }

    // Encodes the data variables into network bytes
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.perkId);
        buf.writeBlockPos(this.pos);
    }

    // The logic executed entirely on the server side when the packet arrives
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            BlockEntity be = player.level().getBlockEntity(this.pos);
            if (be instanceof PerkShrineBlockEntity shrine) {

                // 1. Process block entity transaction verification on server side
                shrine.tryUnlockPerkOnServer(player, this.perkId);

                // 2. CRUCIAL FIX: Force-broadcast updated tag parameters to synchronize UI nodes
                player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
                    net.minecraft.nbt.CompoundTag nbt = new net.minecraft.nbt.CompoundTag();
                    perks.saveNBT(nbt);
                    ModMessages.sendToPlayer(new ClientboundSyncPerksPacket(nbt), player);
                });
            }
        });
        return true;
    }
}