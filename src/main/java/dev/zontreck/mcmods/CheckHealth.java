package dev.zontreck.mcmods;

import dev.zontreck.ariaslib.terminal.Task;
import dev.zontreck.ariaslib.util.DelayedExecutorService;
import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.mcmods.configs.WMDClientConfig;
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
            Component chat = Component.literal(Msg);
            Minecraft.getInstance().player.displayClientMessage(chat, false);

            SoundEvent sv = SoundEvents.WARDEN_ROAR; // It sounds like a growling stomach
            WatchMyDurability.Soundify(sv);
        }

        WatchMyDurability.LastHealth=current;
    }
}
