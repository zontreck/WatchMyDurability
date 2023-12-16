/*
 *
 *    DISCLAIMER: This code was taken from Mantle, and will be modified to fit the needs of this mod, such as adding more heat options. This code is subject to Mantle's license of MIT.
 *  Despite this code being taken from, and modified/updated to be modern, all textures are my own creation
 *  This disclaimer is here to give credit where credit is due. The author(s) of mantle have done a absolutely fantastic job. And if Mantle gets updated this shall be removed along with future plans of extra hearts and color options.
 *
 *
 */

package dev.zontreck.mcmods.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.zontreck.mcmods.WatchMyDurability;
import dev.zontreck.mcmods.configs.WMDClientConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class HeartsRenderer {
    private static final ResourceLocation ICON_HEARTS = new ResourceLocation(WatchMyDurability.MODID,
            "textures/gui/hearts.png");
    private static final ResourceLocation ICON_VANILLA = Gui.GUI_ICONS_LOCATION;

    private final Minecraft mc = Minecraft.getInstance();

    private int playerHealth = 0;
    private int lastPlayerHealth = 0;
    private long healthUpdateCounter = 0;
    private long lastSystemTime = 0;
    private final Random rand = new Random();

    private int regen;

    /**
     * Draws a texture to the screen
     *
     * @param matrixStack Matrix stack instance
     * @param x           X position
     * @param y           Y position
     * @param textureX    Texture X
     * @param textureY    Texture Y
     * @param width       Width to draw
     * @param height      Height to draw
     */
    private void blit(GuiGraphics matrixStack, int x, int y, int textureX, int textureY, int width, int height) {
        matrixStack.blit(ICON_HEARTS, x, y, textureX, textureY, width, height);
    }

    private void renderHearts(GuiGraphics pGuiGraphics, Player pPlayer, int pX, int pY, int pHeight, int pOffsetHeartIndex, float pMaxHealth, int pCurrentHealth, int pDisplayHealth, int pAbsorptionAmount, boolean pRenderHighlight) {
        Random random = new Random();
        HeartType hearttype = HeartType.forPlayer(pPlayer);
        int offsetX = 9 * (pPlayer.level().getLevelData().isHardcore() ? 5 : 0);
        int maxHearts = Mth.ceil((double)pMaxHealth / 2.0);
        int absorbHearts = Mth.ceil((double)pAbsorptionAmount / 2.0);
        int maxHealth = maxHearts * 2;

        for(int i1 = maxHearts + absorbHearts - 1; i1 >= 0; --i1) {
            int j1 = i1 / 10;
            int k1 = i1 % 10;
            int x = pX + k1 * 8;
            int y = pY - j1; //* pHeight;
            int row = y * pHeight;


            if (pCurrentHealth + pAbsorptionAmount <= 4) {
                y += random.nextInt(2);
            }

            if (i1 < maxHearts && i1 == pOffsetHeartIndex) {
                y -= 2;
            }

            this.renderHeart(pGuiGraphics, HeartType.CONTAINER, x, y, offsetX, pRenderHighlight, false, 0);
            int j2 = i1 * 2;
            boolean flag = i1 >= maxHearts;
            if (flag) {
                int k2 = j2 - maxHealth;
                if (k2 < pAbsorptionAmount) {
                    boolean halfHeart = k2 + 1 == pAbsorptionAmount;
                    this.renderHeart(pGuiGraphics, hearttype == HeartType.WITHERED ? hearttype : HeartType.ABSORBING, x, y, offsetX, false, halfHeart, row);
                }
            }

            boolean flag3;
            if (pRenderHighlight && j2 < pDisplayHealth) {
                flag3 = j2 + 1 == pDisplayHealth;
                this.renderHeart(pGuiGraphics, hearttype, x, y, offsetX, true, flag3, row);
            }

            if (j2 < pCurrentHealth) {
                flag3 = j2 + 1 == pCurrentHealth;
                this.renderHeart(pGuiGraphics, hearttype, x, y, offsetX, false, flag3, row);
            }
        }

    }


    private void renderHeart(GuiGraphics pGuiGraphics, HeartType pHeartType, int pX, int pY, int pYOffset, boolean pRenderHighlight, boolean pHalfHeart, int row) {
        pGuiGraphics.blit(ICON_HEARTS, pX, pY, pHeartType.getX(row, pHalfHeart, pRenderHighlight), pHeartType.getY(), 9, 9);
    }
    /* HUD */
    /**
     * Event listener
     *
     * @param event Event instance
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public void renderHealthbar(RenderGuiOverlayEvent.Pre event) {
        NamedGuiOverlay ActualOverlay = GuiOverlayManager.findOverlay(new ResourceLocation("minecraft:player_health"));

        if (ActualOverlay == null) {
            if (GuiOverlayManager.getOverlays() == null) {
                WatchMyDurability.LOGGER.info("Overlays non existent?!");
            }
            for (NamedGuiOverlay overlay : GuiOverlayManager.getOverlays()) {
                // Next print
                // LibZontreck.LOGGER.info("GUI OVERLAY: "+overlay.id().getPath());

                if (overlay.id().getPath().equals("player_health")) {
                    ActualOverlay = overlay;
                    break;
                }
            }
        }
        if (event.isCanceled() || !WMDClientConfig.EnableExtraHearts.get() || event.getOverlay() != ActualOverlay) {
            return;
        }
        // ensure its visible
        if (!(mc.gui instanceof ForgeGui gui) || mc.options.hideGui || !gui.shouldDrawSurvivalElements()) {
            return;
        }
        // extra setup stuff from us
        int X = this.mc.getWindow().getGuiScaledWidth() / 2 - 91;
        int Y = this.mc.getWindow().getGuiScaledHeight() / 2 + 91;
        int updateCounter = this.mc.gui.getGuiTicks();
        long healthBlinkTime = this.mc.gui.healthBlinkTime;
        int height = Math.max(10 - (Y - 2), 3);
        int offset = -1;
        Player player = Minecraft.getInstance().player;
        int lastHealth = Minecraft.getInstance().gui.lastHealth;
        int displayHealth = Minecraft.getInstance().gui.displayHealth;
        boolean flag = healthBlinkTime > (long)updateCounter && (healthBlinkTime - (long)updateCounter) / 3L % 2L == 1L;

        float maxHealth = Math.max((float)player.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(displayHealth, lastHealth));
        int absorb = Mth.ceil(player.getAbsorptionAmount());

        if (player.hasEffect(MobEffects.REGENERATION)) {
            offset = updateCounter % Mth.ceil(maxHealth + 5.0F);
        }

        renderHearts(event.getGuiGraphics(), player, X, Y, height, offset, maxHealth, lastHealth, Minecraft.getInstance().gui.displayHealth, absorb, flag);
    }

    @OnlyIn(Dist.CLIENT)
    static enum HeartType {
        CONTAINER(0, false),
        NORMAL(1, true),
        POISONED(2, true),
        WITHERED(3, true),
        ABSORBING(5, false),
        FROZEN(4, false);

        private final int index;
        private final boolean canBlink;

        private HeartType(int pIndex, boolean pCanBlink) {
            this.index = pIndex;
            this.canBlink = pCanBlink;
        }

        public int getX(int rowNum, boolean halfHeart, boolean renderHighlight)
        {
            int heart = rowNum + (halfHeart ? 1 : 0) * 9;

            return heart;
        }

        public int getY()
        {
            switch(this)
            {
                case CONTAINER -> {
                    return 144;
                }
                case POISONED -> {
                    return 9;
                }
                case WITHERED -> {
                    return 18;
                }
                case FROZEN -> {
                    return 27;
                }
                case ABSORBING -> {
                    return 80;
                }
                default -> {
                    // Normal and other unknowns
                    return 0;
                }
            }
        }

        static HeartsRenderer.HeartType forPlayer(Player pPlayer) {
            HeartsRenderer.HeartType hearttype;
            if (pPlayer.hasEffect(MobEffects.POISON)) {
                hearttype = POISONED;
            } else if (pPlayer.hasEffect(MobEffects.WITHER)) {
                hearttype = WITHERED;
            } else if (pPlayer.isFullyFrozen()) {
                hearttype = FROZEN;
            } else {
                hearttype = NORMAL;
            }

            return hearttype;
        }
    }
}
