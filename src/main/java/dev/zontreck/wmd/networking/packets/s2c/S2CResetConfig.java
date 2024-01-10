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

            WMDClientConfig.WMD_PREFIX.set(WMDClientConfig.WMD_PREFIX.getDefault());
            WMDClientConfig.WMD_PREFIX.save();

            WMDClientConfig.EnableHealthAlert.set(WMDClientConfig.EnableHealthAlert.getDefault());
            WMDClientConfig.EnableHealthAlert.save();

            WMDClientConfig.EnableHungerAlert.set(WMDClientConfig.EnableHungerAlert.getDefault());
            WMDClientConfig.EnableHealthAlert.save();

            WMDClientConfig.EnableToolWatcher.set(WMDClientConfig.EnableToolWatcher.getDefault());
            WMDClientConfig.EnableToolWatcher.save();
        });
    }
}
