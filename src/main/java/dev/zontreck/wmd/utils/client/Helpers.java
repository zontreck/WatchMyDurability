package dev.zontreck.wmd.utils.client;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class Helpers
{

    public static void Soundify(SoundEvent sound)
    {
        //WatchMyDurability.LOGGER.info("PLAY ALERT SOUND");
        Minecraft.getInstance().player.playSound(sound, 1.0f, 1.0f);
    }
}
