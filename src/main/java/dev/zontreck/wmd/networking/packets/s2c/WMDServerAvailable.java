package dev.zontreck.wmd.networking.packets.s2c;

import dev.zontreck.libzontreck.util.ServerUtilities;
import dev.zontreck.wmd.WatchMyDurability;
import dev.zontreck.wmd.networking.ModMessages;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class WMDServerAvailable
{
    public WMDServerAvailable(FriendlyByteBuf buf)
    {

    }

    public WMDServerAvailable()
    {

    }

    public void toBytes(FriendlyByteBuf buf)
    {

    }

    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(()->{
            WatchMyDurability.WMD_SERVER_AVAILABLE =true;
        });
    }

    public void send(UUID ID)
    {
        ModMessages.sendToPlayer(this, ServerUtilities.getPlayerByID(ID.toString()));
    }
}
