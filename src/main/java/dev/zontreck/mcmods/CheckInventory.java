package dev.zontreck.mcmods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class CheckInventory  extends TimerTask
{

    @Override
    public void run() {

        if(!WatchMyDurability.isInGame)return;
        //WatchMyDurability.LOGGER.info("TICKING CHECK INVENTORY EVENT");
        // Get the player inventory
        Inventory inv = WatchMyDurability._player.getInventory();
        
        if(checkList("_armor", inv.armor)) Soundify();
        if(checkList("_items", inv.items)) Soundify();
        if(checkList("_offhand", inv.offhand)) Soundify();
        

        PushItems("_armor", inv.armor);
        PushItems("_items", inv.items);
        PushItems("_offhand", inv.offhand);

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

    public void Soundify()
    {
        //WatchMyDurability.LOGGER.info("PLAY ALERT SOUND");
        WatchMyDurability._player.playSound(SoundEvents.ITEM_BREAK, 1.0f, 1.0f);
    }

    public boolean checkList(String type, NonNullList<ItemStack> stacks){
        Integer slotNum = 0;
        for (ItemStack is1 : stacks) {
            if(is1.isDamageableItem() && is1.isDamaged() && !ItemRegistry.contains(type, slotNum, WatchMyDurability.REGISTRY.GetNewItem(is1))){

                int percent = 0;
                int max = is1.getMaxDamage();
                int val = is1.getDamageValue();
                int cur = max - val;
                percent = cur * 100 / max;

                //WatchMyDurability.LOGGER.debug("ITEM DURABILITY: "+cur+"; MAXIMUM: "+max+"; PERCENT: "+percent);
                if(percent <= 10)
                {
                    Component X = Component.literal(is1.getDisplayName().getString()+" is about to break!");
                    WatchMyDurability._player.displayClientMessage(X, false);
                    // Play Sound
                    return true;
                } else return false;
            }

            slotNum ++;
        }

        return false;
    }

}
