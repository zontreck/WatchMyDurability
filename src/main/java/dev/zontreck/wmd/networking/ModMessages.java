package dev.zontreck.wmd.networking;

import dev.zontreck.wmd.WatchMyDurability;
import dev.zontreck.wmd.networking.packets.c2s.ClientConfigResponse;
import dev.zontreck.wmd.networking.packets.s2c.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages
{
    private static SimpleChannel channel;
    private static int PACKET_ID = 0;
    private static int id()
    {
        return PACKET_ID++;
    }

    public static void register()
    {
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(WatchMyDurability.MODID, "messages"))
                .networkProtocolVersion(()->"1.0")
                .clientAcceptedVersions(s->true)
                .serverAcceptedVersions(s->true)
                .simpleChannel();
        channel = net;

        net.messageBuilder(WMDServerAvailable.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(WMDServerAvailable::toBytes)
                .decoder(WMDServerAvailable::new)
                .consumer(WMDServerAvailable::handle)
                .add();

        net.messageBuilder(S2CResetConfig.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(S2CResetConfig::toBytes)
                .decoder(S2CResetConfig::new)
                .consumer(S2CResetConfig::handle)
                .add();

        net.messageBuilder(RequestClientConfig.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RequestClientConfig::toBytes)
                .decoder(RequestClientConfig::new)
                .consumer(RequestClientConfig::handle)
                .add();

        net.messageBuilder(ClientConfigResponse.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ClientConfigResponse::toBytes)
                .decoder(ClientConfigResponse::new)
                .consumer(ClientConfigResponse::handle)
                .add();

        net.messageBuilder(PushClientConfigUpdate.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(PushClientConfigUpdate::toBytes)
                .decoder(PushClientConfigUpdate::new)
                .consumer(PushClientConfigUpdate::handle)
                .add();
    }


    public static <MSG> void sendToServer(MSG message){
        channel.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
    {
        channel.send(PacketDistributor.PLAYER.with(()->player), message);
    }

    public static <MSG> void sendToAll(MSG message)
    {
        channel.send(PacketDistributor.ALL.noArg(), message);
    }
}
