package dev.zontreck.wmd;

import com.mojang.logging.LogUtils;

import dev.zontreck.ariaslib.util.DelayedExecutorService;
import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.wmd.checkers.CheckHealth;
import dev.zontreck.wmd.checkers.CheckHunger;
import dev.zontreck.wmd.checkers.CheckInventory;
import dev.zontreck.wmd.commands.ModCommands;
import dev.zontreck.wmd.configs.WMDClientConfig;
import dev.zontreck.wmd.networking.ModMessages;
import dev.zontreck.wmd.types.Health;
import dev.zontreck.wmd.types.Hunger;
import dev.zontreck.wmd.types.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
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

    public static boolean WMD_SERVER_AVAILABLE =false;
    

    public WatchMyDurability()
    {
        
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(Type.CLIENT, WMDClientConfig.SPEC, "watchmydurability-client.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ModCommands());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        //LOGGER.info("HELLO FROM COMMON SETUP");
        ModMessages.register();
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        //LOGGER.warn("If this is running on a server, it is doing absolutely nothing, please remove me.");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        static Timer time = new Timer();

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            LOGGER.info(": : : CLIENT SETUP : : :");
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

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents
    {
    
        @SubscribeEvent
        public static void onJoin(ClientPlayerNetworkEvent.LoggedInEvent event){
            // Joined
            //LOGGER.info("PLAYER LOGGED IN");
            LOGGER.info(": : : PLAYER LOGGED IN : : :");
            WatchMyDurability.isInGame=true;
            DelayedExecutorService.start();

            DelayedExecutorService.getInstance().scheduleRepeating(CheckInventory.getInstance(), WMDClientConfig.TimerVal.get());
            DelayedExecutorService.getInstance().scheduleRepeating(CheckHealth.getInstance(), WMDClientConfig.TimerVal.get());
            DelayedExecutorService.getInstance().scheduleRepeating(CheckHunger.getInstance(), WMDClientConfig.TimerVal.get());
        }
    
        @SubscribeEvent
        public static void onLeave(ClientPlayerNetworkEvent.LoggedOutEvent event){
            //LOGGER.info("PLAYER LOGGED OUT");
            LOGGER.info(": : : PLAYER LOGGED OUT : : :");
            WatchMyDurability.isInGame=false;
            WatchMyDurability.WMD_SERVER_AVAILABLE=false;
            DelayedExecutorService.stop();
        }

        @SubscribeEvent
        public static void onClone(ClientPlayerNetworkEvent.RespawnEvent event)
        {
            LOGGER.info(": : : : PLAYER RESPAWNED OR MOVED TO A NEW WORLD : : : :");
            
        }
    }
}
