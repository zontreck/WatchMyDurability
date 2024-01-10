package dev.zontreck.wmd.events;

import dev.zontreck.libzontreck.LibZontreck;
import dev.zontreck.wmd.WatchMyDurability;
import dev.zontreck.wmd.networking.packets.s2c.WMDServerAvailable;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler
{
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event)
    {
        WMDServerAvailable avail = new WMDServerAvailable();
        avail.send(event.getEntity().getUUID());
    }
}
