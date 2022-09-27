package dev.zontreck.mcmods.configs;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.ForgeConfigSpec;

public class WMDClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.ConfigValue<List<Integer>> alertPercents;
    public static ForgeConfigSpec.ConfigValue<List<String>> alertMessages;
    public static ForgeConfigSpec.ConfigValue<Integer> TimerVal;
    static{
        List<Integer> alerts1 = new ArrayList<>();
        alerts1.add(10);

        List<String> alerts2 = new ArrayList<>();
        alerts2.add("!item! is about to break");
        

        BUILDER.push("Alerts");
        BUILDER.comment("Both of the following lists must have the same number of entries. NOTE: Percents do NOT stack. After the first rule is applied, it will move to the next item, so please make the list ascend, and not descend.  Example: 10, 50").define("VERSION", "1.0.0");

        alertPercents = BUILDER.comment("The list of alerts you want at what percentages of remaining durability").define("Percents", alerts1);
        alertMessages = BUILDER.comment("The messages you want displayed when a alert is triggered. You must have the same amount of messages as alerts").define("Messages", alerts2);
        TimerVal = BUILDER.comment("How many seconds between timer ticks to check your inventory items?").define("Timer", 5);

        BUILDER.pop();
        SPEC=BUILDER.build();
    }
}