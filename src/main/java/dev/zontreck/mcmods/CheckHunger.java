package dev.zontreck.mcmods;

import dev.zontreck.ariaslib.terminal.Task;
import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.mcmods.configs.WMDClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class CheckHunger extends Task
{
    public CheckHunger()
    {
        super("CheckHunger", true);
    }

    @Override
    public void run() {
        if(!WMDClientConfig.EnableHungerAlert.get()) return;

        Hunger current = Hunger.of(Minecraft.getInstance().player);

        if(current.identical()) return;
        if(current.shouldGiveAlert())
        {

            String Msg = ChatColor.doColors("!Dark_Red!!bold!You need to eat!");
            Component chat = Component.literal(Msg);
            Minecraft.getInstance().player.displayClientMessage(chat, false);

            SoundEvent sv = SoundEvents.WARDEN_ROAR; // It sounds like a growling stomach
            WatchMyDurability.Soundify(sv);
        }

        WatchMyDurability.LastHunger = current;


    }
}
