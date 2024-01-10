package dev.zontreck.wmd.networking.packets.s2c;

import dev.zontreck.wmd.configs.WMDClientConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PushClientConfigUpdate
{
    public CompoundTag tag;
    public PushClientConfigUpdate(FriendlyByteBuf buf)
    {
        tag = buf.readAnySizeNbt();
    }

    public PushClientConfigUpdate(CompoundTag tag)
    {
        this.tag=tag;
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeNbt(tag);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(()->{
            WMDClientConfig.deserialize(tag);
        });
    }
}
