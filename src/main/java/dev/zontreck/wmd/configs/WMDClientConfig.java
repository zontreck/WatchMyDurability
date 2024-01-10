package dev.zontreck.wmd.configs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.ForgeConfigSpec;

public class WMDClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<List<Integer>> alertPercents;
    public static ForgeConfigSpec.ConfigValue<List<String>> alertMessages;
    public static ForgeConfigSpec.ConfigValue<Integer> TimerVal;
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableHealthAlert;
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableHungerAlert;
    public static ForgeConfigSpec.ConfigValue<Boolean> EnableToolWatcher;


    public static ForgeConfigSpec.ConfigValue<String> WMD_PREFIX;

    static{
        List<Integer> alerts1 = new ArrayList<>();
        alerts1.add(10);

        List<String> alerts2 = new ArrayList<>();
        alerts2.add("!item! is about to break");
        

        BUILDER.push("Alerts");
        BUILDER.comment("Both of the following lists must have the same number of entries. NOTE: Percents do NOT stack. After the first rule is applied, it will move to the next item, so please make the list ascend, and not descend.  Example: 10, 50").define("VERSION", "1.1.1.1");

        alertPercents = BUILDER.comment("The list of alerts you want at what percentages of remaining durability").define("Percents", alerts1);
        alertMessages = BUILDER.comment("The messages you want displayed when a alert is triggered. You must have the same amount of messages as alerts").define("Messages", alerts2);
        TimerVal = BUILDER.comment("How many seconds between timer ticks to check your inventory items?").define("Timer", 5);

        BUILDER.pop();

        BUILDER.push("General");
        EnableHealthAlert = BUILDER.comment("The following was added for a friend. If you need reminders to eat in order to heal, turn the below option on").define("watchMyHealth", false);
        EnableHungerAlert = BUILDER.comment("This is a newer setting to watch your hunger status instead of your hunger to alert when you need to eat").define("watchMyHunger", false);
        EnableToolWatcher = BUILDER.comment("Enable watching tool durability").define("watchDurability", true);
        BUILDER.pop();


        BUILDER.push("Messages");

        WMD_PREFIX = BUILDER.comment("The prefix string for WMD").define("prefix", "!Dark_Gray![!Bold!!Dark_Green!WMD!Reset!!Dark_Gray!]!Reset!");

        SPEC=BUILDER.build();
    }

    public static CompoundTag serialize()
    {
        CompoundTag ret = new CompoundTag();

        ret.putBoolean("watchMyHealth", EnableHealthAlert.get());
        ret.putBoolean("watchMyHunger", EnableHungerAlert.get());
        ret.putBoolean("watchDurability", EnableToolWatcher.get());



        return ret;
    }

    public static void deserialize(CompoundTag tag)
    {
        EnableHealthAlert.set(tag.getBoolean("watchMyHealth"));
        EnableHealthAlert.save();

        EnableHungerAlert.set(tag.getBoolean("watchMyHunger"));
        EnableHungerAlert.save();

        EnableToolWatcher.set(tag.getBoolean("watchDurability"));
        EnableToolWatcher.save();
    }
}
