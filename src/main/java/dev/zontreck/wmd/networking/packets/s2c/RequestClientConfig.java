package dev.zontreck.wmd.networking.packets.s2c;

import dev.zontreck.wmd.networking.ModMessages;
import dev.zontreck.wmd.networking.packets.c2s.ClientConfigResponse;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RequestClientConfig
{
    public RequestClientConfig(FriendlyByteBuf buf)
    {

    }

    public RequestClientConfig(){

    }

    public void toBytes(FriendlyByteBuf buf){

    }

    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(()->{
            ClientConfigResponse reply = new ClientConfigResponse(Minecraft.getInstance().player.getUUID());
            ModMessages.sendToServer(reply);
        });
    }
}
