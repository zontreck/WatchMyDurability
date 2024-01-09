package dev.zontreck.wmd;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.item.ItemStack;

public class ItemRegistry {
    public class Item {
        public String Name;
        public int PercentDamaged;
        public int Count;
        

        public boolean Compare(Item other)
        {
            if(other.Name.equals(Name) && Count == other.Count){
                if(PercentDamaged != other.PercentDamaged) return false;
                else return true;
            }else return false;
        }
    }

    public class Health {
    }


    public Map<String,Map<Integer, Item>> CachedItems;
    public ItemRegistry()
    {
        CachedItems = new HashMap<String,Map<Integer,Item>>();
    }

    public static void Initialize()
    {
        WatchMyDurability.REGISTRY = new ItemRegistry();
    }

    public static void purge(String type) {
        if(WatchMyDurability.REGISTRY.CachedItems.containsKey(type))
            WatchMyDurability.REGISTRY.CachedItems.remove(type);
    }

    public Item GetNewItem(ItemStack itemStack) {
        Item x = new Item();
        x.Name = itemStack.getDisplayName().getString();
        if(itemStack.isDamageableItem() && itemStack.isDamaged()){
            int max = itemStack.getMaxDamage();
            int val = itemStack.getDamageValue();
            int cur = max-val;
            int percent = cur * 100 /max;
            x.PercentDamaged=percent;
        }

        x.Count = itemStack.getCount();
        //WatchMyDurability.LOGGER.debug("ITEM: "+x.Name + "; "+x.PercentDamaged+"; "+x.Type+"; "+x.Count);
        return x;
    }

    public static void register(String type, Map<Integer, Item> items) {

        WatchMyDurability.REGISTRY.CachedItems.put(type, items);
    }

    public static boolean contains(String type, Integer slot, Item getNewItem) {
        ItemRegistry reg = WatchMyDurability.REGISTRY;
        if(reg.CachedItems.containsKey(type)){
            //WatchMyDurability.LOGGER.debug("Registry contains "+type);
            Map<Integer,Item> items = reg.CachedItems.get(type);
            if(items.containsKey(slot)){
                //WatchMyDurability.LOGGER.debug("ItemRegistry contains slot: "+slot);
                Item x = items.get(slot);
                if(x.Compare(getNewItem)){
                    //WatchMyDurability.LOGGER.debug("Items are identical!");
                    // Items are identical
                    return true;
                }else {
                    //WatchMyDurability.LOGGER.debug("ITEMS ARE NOT IDENTICAL");
                    return false;
                }
            }else return false;

        }

        return false;
    }

}
