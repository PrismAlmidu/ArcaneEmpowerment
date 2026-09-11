package net.prismalmidu.arcaneempowerment.networking;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.prismalmidu.arcaneempowerment.ArcaneEmpowerment;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(ArcaneEmpowerment.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        // 1. Client-to-Server Packets (PLAY_TO_SERVER)
        net.messageBuilder(C2SUnlockPerkPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SUnlockPerkPacket::new)
                .encoder(C2SUnlockPerkPacket::toBytes)
                .consumerMainThread(C2SUnlockPerkPacket::handle)
                .add();

        // =========================================================================
        // REGISTRATION FOR THE NEW RESPEC PACKET
        // =========================================================================
        net.messageBuilder(C2SRespecPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SRespecPacket::new)
                .encoder(C2SRespecPacket::toBytes)
                .consumerMainThread(C2SRespecPacket::handle)
                .add();
        // =========================================================================

        // 2. Server-to-Client Packets (PLAY_TO_CLIENT)
        net.messageBuilder(ClientboundSyncPerksPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ClientboundSyncPerksPacket::new)
                .encoder(ClientboundSyncPerksPacket::toBytes)
                .consumerMainThread(ClientboundSyncPerksPacket::handle)
                .add();
    }

    // Helper method to send a packet from the screen directly up to the server
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    // Helper method to send data packets from the server back down to a single player client
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}