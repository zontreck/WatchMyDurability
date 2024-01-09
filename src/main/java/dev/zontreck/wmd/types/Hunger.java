package dev.zontreck.wmd.types;

import dev.zontreck.wmd.WatchMyDurability;
import net.minecraft.world.entity.player.Player;

public class Hunger
{
    public boolean needsToEat;

    public static Hunger of(Player player)
    {
        Hunger hunger = new Hunger();
        hunger.needsToEat = player.getFoodData().needsFood();
        return hunger;
    }

    public boolean shouldGiveAlert()
    {
        if(needsToEat && !WatchMyDurability.LastHunger.needsToEat) return true;
        else return false;
    }

    public boolean identical()
    {
        if(needsToEat == WatchMyDurability.LastHunger.needsToEat) return true;
        return false;
    }
}
