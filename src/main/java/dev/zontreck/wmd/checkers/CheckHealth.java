package dev.zontreck.wmd.checkers;

import dev.zontreck.ariaslib.terminal.Task;
import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.libzontreck.util.ChatHelpers;
import dev.zontreck.wmd.types.Health;
import dev.zontreck.wmd.WatchMyDurability;
import dev.zontreck.wmd.configs.WMDClientConfig;
import dev.zontreck.wmd.utils.client.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public class CheckHealth extends Task
{
    private static CheckHealth inst = new CheckHealth();
    public static CheckHealth getInstance()
    {
        return inst;
    }
    public CheckHealth() {
        super("CheckHealth", true);
    }

    @Override
    public void run() {


        // Hijack this timer so we dont need to register yet another
        if(!WMDClientConfig.EnableHealthAlert.get())return;


        Health current = Health.of(Minecraft.getInstance().player);
        if(WatchMyDurability.LastHealth == null)WatchMyDurability.LastHealth = current;
        else{
            if(current.identical(WatchMyDurability.LastHealth))return;
        }

        // Good to proceed
        if(current.shouldGiveAlert())
        {
            String Msg = ChatColor.doColors("!Dark_Red!!bold!You need to eat!");
            Component chat = ChatHelpers.macro(Msg);
            Minecraft.getInstance().player.displayClientMessage(chat, false);

            SoundEvent sv = SoundEvents.ENDER_DRAGON_GROWL;
            Helpers.Soundify(sv);
        }

        WatchMyDurability.LastHealth=current;
    }
}
