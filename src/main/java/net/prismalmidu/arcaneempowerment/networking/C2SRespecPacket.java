package net.prismalmidu.arcaneempowerment.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.network.NetworkEvent;
import net.prismalmidu.arcaneempowerment.capability.ModPerkRegistry;
import net.prismalmidu.arcaneempowerment.capability.PerkModifier;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;

import java.util.function.Supplier;

public class C2SRespecPacket {

    public C2SRespecPacket() {}
    public C2SRespecPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
                // 1. Strip mod attributes from the entity data map FIRST before erasing registry records
                for (PerkModifier modifier : ModPerkRegistry.REGISTRY.values()) {
                    modifier.targetAttribute().ifPresent(attribute -> {
                        AttributeInstance instance = player.getAttribute(attribute);
                        if (instance != null && instance.getModifier(modifier.uuid()) != null) {
                            instance.removeModifier(modifier.uuid()); // Strips and syncs visually
                        }
                    });
                }

                // 2. Erase perk ownership arrays and reclaim available cash
                perks.respec();

                // 3. Dispatch an explicit payload package down to refresh the screen layout data
                CompoundTag nbt = new CompoundTag();
                perks.saveNBT(nbt);
                ModMessages.sendToPlayer(new ClientboundSyncPerksPacket(nbt), player);
            });
        });
        return true;
    }
}