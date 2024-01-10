package dev.zontreck.wmd.commands;

import dev.zontreck.wmd.commands.impl.SettingsCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModCommands
{
    @SubscribeEvent
    public void register(final RegisterCommandsEvent event)
    {
        SettingsCommand.register(event.getDispatcher());
    }
}
