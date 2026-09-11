package net.prismalmidu.arcaneempowerment.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;
import net.prismalmidu.arcaneempowerment.capability.PlayerPerksProvider;
import net.prismalmidu.arcaneempowerment.networking.ClientboundSyncPerksPacket;
import net.prismalmidu.arcaneempowerment.networking.ModMessages;

@Mod.EventBusSubscriber(modid = ArcaneEmpowerment.MOD_ID)
public class ModCapabilityEvents {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerPerksProvider.PLAYER_PERKS).isPresent()) {
                // Attaches the capability to the player under your mod id wrapper
                event.addCapability(new ResourceLocation(ArcaneEmpowerment.MOD_ID, "properties"), new PlayerPerksProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Keep data alive across player death/respawn
            event.getOriginal().getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(oldStore -> {
                event.getEntity().getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }

        // CRUCIAL: Sync down to client right after cloning the capability profiles!
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayerPerks(player);
        }
    }

    // =========================================================================
    // NEW SYNCHRONIZATION HOOKS FOR WORLD LOADING AND DIMENSION CHANGES
    // =========================================================================

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayerPerks(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            syncPlayerPerks(player);
        }
    }

    // Central helper method to build the NBT map data and fire it across the network line
    private static void syncPlayerPerks(ServerPlayer player) {
        player.getCapability(PlayerPerksProvider.PLAYER_PERKS).ifPresent(perks -> {
            CompoundTag nbt = new CompoundTag();
            perks.saveNBT(nbt);
            ModMessages.sendToPlayer(new ClientboundSyncPerksPacket(nbt), player);
        });
    }
}