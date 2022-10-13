package dev.zontreck.mcmods;

import net.minecraft.world.entity.player.Player;

public class Health {
    
    public float maximum;
    public float current;

    public int asPercent()
    {
        return (int)Math.round(Math.abs((current * 100 / maximum)));
    }
    public Health lastHealthState;


    public static Health of(Player player){
        Health obj = new Health();
        obj.current = player.getHealth();
        obj.maximum = player.getMaxHealth();

        return obj;
    }

    public boolean shouldGiveAlert()
    {
        if(asPercent()<=50){
            return true;
        }else return false;
    }
    public boolean identical(Health other)
    {
        if(other.current == current && other.maximum == maximum)return true;
        else return false;
    }
}
