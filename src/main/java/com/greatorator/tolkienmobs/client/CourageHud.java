package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.faction.Courage;
import com.greatorator.tolkienmobs.faction.CourageLedger;
import com.greatorator.tolkienmobs.init.TolkienMobEffects;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The one moment courage is worth saying out loud: the terror is on you, and it does not take your legs.
 * <p>
 * It is drawn the way the pipe's draw bar is - a layer of its own, above the item name line, that shows
 * nothing at all until its moment arrives - rather than as a permanent meter. Courage is a number that
 * changes a handful of times in a campaign, so a meter would be furniture; the moment a scream fails to
 * pin you is exactly when a player wants to be told why. A player who is still
 * {@link Courage#pinned()} by it gets nothing, because the paralysis is its own answer.
 * <p>
 * It holds for a couple of seconds and then fades, so a fight does not become a caption.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class CourageHud {
    private static final Identifier LAYER = Identifier.fromNamespaceAndPath(MODID, "courage");

    /** How long the line stays at full strength, and how long it takes to go once it starts to. */
    private static final long HOLD_MILLIS = 1600L;
    private static final long FADE_MILLIS = 700L;
    /** How far above the bottom edge of the screen the line sits: clear of the item name and the bar. */
    private static final int ABOVE_BOTTOM = 62;

    /** The parchment the book is written on, and the ink, so the HUD matches the ledger. */
    private static final int PARCHMENT = 0xE9D8B4;
    private static final int BACKING = 0x66000000;

    /** When the line was first drawn for the terror now in hand, or -1 when there is none. */
    private static long shownAt = -1L;

    private CourageHud() {
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, LAYER, CourageHud::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.hasEffect(TolkienMobEffects.PARALYSING_FEAR)) {
            shownAt = -1L;
            return;
        }
        Courage stage = CourageLedger.clientStage();
        if (!stage.shrugsOffParalysis()) {
            shownAt = -1L;
            return;
        }

        long now = Util.getMillis();
        if (shownAt < 0L) shownAt = now;
        long held = now - shownAt;
        if (held > HOLD_MILLIS + FADE_MILLIS) return;

        int alpha = held <= HOLD_MILLIS ? 255
                : (int) (255L * (HOLD_MILLIS + FADE_MILLIS - held) / FADE_MILLIS);
        String line = Component.translatable("hud.tolkienmobs.courage.steadfast").getString();
        int width = Minecraft.getInstance().font.width(line);
        int x = (graphics.guiWidth() - width) / 2;
        int y = graphics.guiHeight() - ABOVE_BOTTOM;

        graphics.fill(x - 4, y - 3, x + width + 4, y + 11, (alpha << 24) | (BACKING & 0x00FFFFFF));
        graphics.text(Minecraft.getInstance().font, line, x, y, (alpha << 24) | PARCHMENT, false);
    }
}
