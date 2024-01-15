package dev.zontreck.wmd.networking.packets.c2s;

import dev.zontreck.libzontreck.chat.ChatColor;
import dev.zontreck.libzontreck.chestgui.ChestGUI;
import dev.zontreck.libzontreck.chestgui.ChestGUIButton;
import dev.zontreck.libzontreck.chestgui.ChestGUIIdentifier;
import dev.zontreck.libzontreck.items.ModItems;
import dev.zontreck.libzontreck.lore.LoreContainer;
import dev.zontreck.libzontreck.lore.LoreEntry;
import dev.zontreck.libzontreck.util.ServerUtilities;
import dev.zontreck.libzontreck.vectors.Vector2i;
import dev.zontreck.wmd.WatchMyDurability;
import dev.zontreck.wmd.configs.WMDClientConfig;
import dev.zontreck.wmd.networking.ModMessages;
import dev.zontreck.wmd.networking.packets.s2c.PushClientConfigUpdate;
import dev.zontreck.wmd.networking.packets.s2c.RequestClientConfig;
import dev.zontreck.wmd.networking.packets.s2c.S2CResetConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientConfigResponse {
    public CompoundTag tag;
    public UUID id;

    public ClientConfigResponse(FriendlyByteBuf buf) {
        tag = buf.readAnySizeNbt();
        id = buf.readUUID();
    }

    public ClientConfigResponse(UUID playerID) {
        tag = WMDClientConfig.serialize();
        id = playerID;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeUUID(id);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();

        ctx.enqueueWork(() -> {
            // Open config editor for player
            boolean enableHealth = tag.getBoolean("watchMyHealth");
            boolean enableHunger = tag.getBoolean("watchMyHunger");
            boolean enableDurability = tag.getBoolean("watchDurability");


            ServerPlayer player = ServerUtilities.getPlayerByID(id.toString());

            try {
                ChestGUI prompt = ChestGUI.builder().withGUIId(new ChestGUIIdentifier("wmdsettings")).withPlayer(player.getUUID()).withTitle("WMD Settings");
                ItemStack wtd = new ItemStack(Items.DIAMOND_PICKAXE, 1);
                wtd.setHoverName(Component.literal("Watch Tool Durability"));

                prompt.withButton(new ChestGUIButton(wtd, (stack, container, lore) -> {
                            var wd = !tag.getBoolean("watchDurability");
                            tag.putBoolean("watchDurability", wd);

                            ModMessages.sendToPlayer(new PushClientConfigUpdate(tag), player);


                            lore.miscData.loreData.get(0).text = ChatColor.doColors("!Dark_Green!Status: " + (wd ? "!Dark_Green!Enabled" : "!Dark_Red!Disabled"));
                            lore.commitLore();


                        }, new Vector2i(0, 0))
                                .withInfo(new LoreEntry.Builder().text(ChatColor.doColors("!Dark_Green!Status: " + (enableDurability ? "!Dark_Green!Enabled" : "!Dark_Red!Disabled"))).build())

                );

                ItemStack wmhunger = new ItemStack(Items.APPLE, 1);
                wmhunger.setHoverName(Component.literal("Watch My Hunger"));

                prompt.withButton(new ChestGUIButton(wmhunger, (stack, container, lore) -> {
                            var eh = !tag.getBoolean("watchMyHunger");
                            tag.putBoolean("watchMyHunger", eh);
                            ModMessages.sendToPlayer(new PushClientConfigUpdate(tag), player);

                            lore.miscData.loreData.get(0).text = ChatColor.doColors("!Dark_Green!Status: " + (eh ? "!Dark_Green!Enabled" : "!Dark_Red!Disabled"));
                            lore.commitLore();


                        }, new Vector2i(0, 1))
                                .withInfo(new LoreEntry.Builder().text(ChatColor.doColors("!Dark_Green!Status: " + (enableHunger ? "!Dark_Green!Enabled" : "!Dark_Red!Disabled"))).build())
                );
                ItemStack wmhealth = new ItemStack(Items.PUFFERFISH, 1);
                wmhealth.setHoverName(Component.literal("Watch My Health"));

                prompt.withButton(new ChestGUIButton(wmhealth, (stack, container, lore) -> {
                            var eh = !tag.getBoolean("watchMyHealth");
                            tag.putBoolean("watchMyHealth", eh);
                            ModMessages.sendToPlayer(new PushClientConfigUpdate(tag), player);


                            lore.miscData.loreData.get(0).text = ChatColor.doColors("!Dark_Green!Status: " + (eh ? "!Dark_Green!Enabled" : "!Dark_Red!Disabled"));
                            lore.commitLore();

                        }, new Vector2i(0, 2))
                                .withInfo(new LoreEntry.Builder().text(ChatColor.doColors("!Dark_Green!Status: " + (enableHealth ? "!Dark_Green!Enabled" : "!Dark_Red!Disabled"))).build())
                );

                prompt.withButton(new ChestGUIButton(ModItems.CHESTGUI_RESET.get(), "Reset", (stack, container, lore) -> {
                    ModMessages.sendToPlayer(new S2CResetConfig(), ServerUtilities.getPlayerByID(id.toString()));

                    prompt.close();

                    ModMessages.sendToPlayer(new RequestClientConfig(), player);

                }, new Vector2i(2, 4)));

                prompt.hasReset = false;
                prompt.open();
            } catch (Exception e) {
                WatchMyDurability.LOGGER.error(e.getMessage());
                e.printStackTrace();
            }

        });
    }
}
