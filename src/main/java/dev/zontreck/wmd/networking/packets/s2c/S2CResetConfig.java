package dev.zontreck.wmd.networking.packets.s2c;

import dev.zontreck.wmd.configs.WMDClientConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CResetConfig
{
    public S2CResetConfig(FriendlyByteBuf buf)
    {

    }

    public S2CResetConfig()
    {

    }

    public void toBytes(FriendlyByteBuf buf)
    {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(()->
        {

            WMDClientConfig.WMD_PREFIX.set("!Dark_Gray![!Bold!!Dark_Green!WMD!Reset!!Dark_Gray!]!Reset!");
            WMDClientConfig.WMD_PREFIX.save();

            WMDClientConfig.EnableHealthAlert.set(false);
            WMDClientConfig.EnableHealthAlert.save();

            WMDClientConfig.EnableHungerAlert.set(false);
            WMDClientConfig.EnableHealthAlert.save();

            WMDClientConfig.EnableToolWatcher.set(true);
            WMDClientConfig.EnableToolWatcher.save();
        });
    }
}
