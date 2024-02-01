package dev.zontreck.wmd.checkers;

import dev.zontreck.ariaslib.terminal.Task;
import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.libzontreck.util.ChatHelpers;
import dev.zontreck.wmd.types.Hunger;
import dev.zontreck.wmd.WatchMyDurability;
import dev.zontreck.wmd.configs.WMDClientConfig;
import dev.zontreck.wmd.utils.client.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class CheckHunger extends Task
{
    private static CheckHunger inst = new CheckHunger();
    public static CheckHunger getInstance()
    {
        return inst;
    }
    public CheckHunger()
    {
        super("CheckHunger", true);
    }

    @Override
    public void run() {
        if(!WMDClientConfig.EnableHungerAlert.get()) return;


        Hunger current = Hunger.of(Minecraft.getInstance().player);
        if(WatchMyDurability.LastHunger == null)WatchMyDurability.LastHunger = new Hunger();

        if(current.identical()) return;
        if(current.shouldGiveAlert())
        {

            String Msg = ChatColor.doColors("!Dark_Red!!bold!You need to eat!");
            Component chat = ChatHelpers.macro(Msg);
            Minecraft.getInstance().player.displayClientMessage(chat, false);

            SoundEvent sv = SoundEvents.WARDEN_ROAR; // It sounds like a growling stomach
            Helpers.Soundify(sv);
        }

        WatchMyDurability.LastHunger = current;


    }
}
