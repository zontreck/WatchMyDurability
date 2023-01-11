package dev.zontreck.mcmods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.logging.log4j.core.config.builder.api.ComponentBuilder;

import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.libzontreck.chat.ChatColorFactory;
import dev.zontreck.libzontreck.chat.ChatColor.ColorOptions;
import dev.zontreck.mcmods.configs.WMDClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CheckInventory  extends TimerTask
{

    @Override
    public void run() {

        try {
                
            if(!WatchMyDurability.isInGame)return;
            //WatchMyDurability.LOGGER.info("TICKING CHECK INVENTORY EVENT");
            // Get the player inventory
            Inventory inv = Minecraft.getInstance().player.getInventory();
            
            checkList("_armor", inv.armor);
            checkList("_items", inv.items);
            checkList("_offhand", inv.offhand);
            

            PushItems("_armor", inv.armor);
            PushItems("_items", inv.items);
            PushItems("_offhand", inv.offhand);
        } catch (Exception e) {
            WatchMyDurability.LOGGER.warn(": : : : [ERROR] : : : :");
            WatchMyDurability.LOGGER.warn("A error in the WatchMyDurability timer code has occurred. This could happen with hub worlds and the server transfers that occur. If this happened in another instance, please report this error to the developer, along with what you were doing.");
        }


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
            Minecraft.getInstance().player.displayClientMessage(new TextComponent(Msg), false);

            SoundEvent sv = SoundEvents.VILLAGER_HURT; // It sounds like a growling stomach
            Soundify(sv);
        }

        WatchMyDurability.LastHealth=current;

    }

    public void PushItems(String type, List<ItemStack> stack)
    {
        // OK
        // Push the items into the registry, replacing the existing entry
        ItemRegistry.purge(type);
        Map<Integer, ItemRegistry.Item> items = new HashMap<Integer, ItemRegistry.Item>();
        Integer slotNum = 0;
        for (ItemStack itemStack : stack) {
            ItemRegistry.Item itx = WatchMyDurability.REGISTRY.GetNewItem(itemStack);

            items.put(slotNum, itx);
            slotNum++;

        }

        ItemRegistry.register(type,items);
    }

    public void Soundify(SoundEvent sound)
    {
        //WatchMyDurability.LOGGER.info("PLAY ALERT SOUND");
        Minecraft.getInstance().player.playSound(sound, 1.0f, 1.0f);
    }

    public void checkList(String type, NonNullList<ItemStack> stacks){
        Integer slotNum = 0;
        //boolean ret=false;
        for (ItemStack is1 : stacks) {
            if(is1.isDamageableItem() && is1.isDamaged() && !ItemRegistry.contains(type, slotNum, WatchMyDurability.REGISTRY.GetNewItem(is1))){

                int percent = 0;
                int max = is1.getMaxDamage();
                int val = is1.getDamageValue();
                int cur = max - val;
                percent = cur * 100 / max;

                //WatchMyDurability.LOGGER.debug("ITEM DURABILITY: "+cur+"; MAXIMUM: "+max+"; PERCENT: "+percent);
                for (Integer entry : WMDClientConfig.alertPercents.get()) {
                    Integer idx = WMDClientConfig.alertPercents.get().indexOf(entry);
                    String entryStr = WMDClientConfig.alertMessages.get().get(idx);

                    if(percent <= entry){
                        String replaced = WatchMyDurability.WMDPrefix + ChatColor.DARK_RED + entryStr.replaceAll("!item!", is1.getDisplayName().getString());
                        WatchMyDurability.LOGGER.info("Enqueue alert for an item. Playing sound for item: "+is1.getDisplayName().getString());
                        
                        SoundEvent theSound = SoundEvents.ITEM_BREAK;
                        Soundify(theSound);
                        
                        
                        Component X = new TextComponent(replaced);
                        Minecraft.getInstance().player.displayClientMessage(X, false);
                        break; // Rule applies, break out of this loop, move to next item.
                    }
                }
            }

            slotNum ++;
        }
        //return ret;
    }

}
