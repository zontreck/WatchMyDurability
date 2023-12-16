package dev.zontreck.mcmods;

import com.mojang.logging.LogUtils;

import dev.zontreck.ariaslib.util.DelayedExecutorService;
import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.mcmods.configs.WMDClientConfig;
import dev.zontreck.mcmods.gui.HeartsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;

import java.util.Timer;

import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(WatchMyDurability.MODID)
public class WatchMyDurability
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "watchmydurability";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    
    /// DO NOT USE FROM ANY THIRD PARTY PACKAGES
    public static User CurrentUser = null; // This is initialized by the client
    public static boolean isInGame = false; // This locks the timer thread
    public static ItemRegistry REGISTRY;
    public static Health LastHealth;
    public static Hunger LastHunger;
    public static String WMDPrefix;

    

    public WatchMyDurability()
    {
        WatchMyDurability.WMDPrefix = ChatColor.doColors("!Dark_Gray![!Bold!!Dark_Green!WMD!Reset!!Dark_Gray!]!reset!");
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(Type.CLIENT, WMDClientConfig.SPEC, "watchmydurability-client.toml");
        
        MinecraftForge.EVENT_BUS.register(new HeartsRenderer());
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        //LOGGER.info("HELLO FROM COMMON SETUP");
    }


    public static void Soundify(SoundEvent sound)
    {
        //WatchMyDurability.LOGGER.info("PLAY ALERT SOUND");
        Minecraft.getInstance().player.playSound(sound, 1.0f, 1.0f);
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        //LOGGER.warn("If this is running on a server, it is doing absolutely nothing, please remove me.");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents
    {
        static Timer time = new Timer();

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info(WMDPrefix+": : : CLIENT SETUP : : :");
            // Some client setup code
            //LOGGER.info("HELLO FROM CLIENT SETUP");
            //LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
            WatchMyDurability.CurrentUser = Minecraft.getInstance().getUser();
            DelayedExecutorService.setup();

            
            //time.schedule(new CheckInventory(),
                //WMDClientConfig.TimerVal.get()*1000,
                //WMDClientConfig.TimerVal.get()*1000);

            ItemRegistry.Initialize();
            
        }

    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ClientEvents
    {
    
        @SubscribeEvent
        public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event){
            // Joined
            //LOGGER.info("PLAYER LOGGED IN");
            LOGGER.info(WMDPrefix+": : : PLAYER LOGGED IN : : :");
            WatchMyDurability.isInGame=true;
            DelayedExecutorService.start();

            DelayedExecutorService.getInstance().scheduleRepeating(CheckInventory.getInstance(), WMDClientConfig.TimerVal.get());
            DelayedExecutorService.getInstance().scheduleRepeating(CheckHealth.getInstance(), WMDClientConfig.TimerVal.get());
            DelayedExecutorService.getInstance().scheduleRepeating(CheckHunger.getInstance(), WMDClientConfig.TimerVal.get());
        }
    
        @SubscribeEvent
        public static void onLeave(ClientPlayerNetworkEvent.LoggingOut event){
            //LOGGER.info("PLAYER LOGGED OUT");
            LOGGER.info(WMDPrefix+": : : PLAYER LOGGED OUT : : :");
            WatchMyDurability.isInGame=false;
            DelayedExecutorService.stop();
        }

        @SubscribeEvent
        public static void onClone(ClientPlayerNetworkEvent.Clone event)
        {
            LOGGER.info(WMDPrefix+": : : : PLAYER RESPAWNED OR MOVED TO A NEW WORLD : : : :");
            
        }
    }
}
