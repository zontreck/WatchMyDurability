package dev.zontreck.wmd.commands.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.zontreck.wmd.networking.ModMessages;
import dev.zontreck.wmd.networking.packets.s2c.RequestClientConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SettingsCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("wmdsettings").executes(c->settingsPrompt(c.getSource())));
    }

    public static int settingsPrompt(CommandSourceStack sender)
    {
        try {
            ModMessages.sendToPlayer(new RequestClientConfig(), sender.getPlayerOrException());
        } catch (CommandSyntaxException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }
}
