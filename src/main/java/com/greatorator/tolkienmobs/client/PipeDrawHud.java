package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.TolkienMobsConfig;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.item.custom.PipeItem;
import com.greatorator.tolkienmobs.util.pipeweed.PipeweedStrain;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * A short bar over the hotbar that fills while a pipe is being drawn on, so a smoker can see how
 * far into the draw they are instead of guessing at a hold.
 *
 * <p>The pipe lets go of a bowl after a minimum draw and stops asking at the full one, so the bar
 * carries a notch at that minimum: a release before the fill passes it only warms the leaf, and the
 * fill takes the strain's pale smoke colour once it is past the notch, which is the point where a
 * release will actually smoke. Before that it is the leaf's own flower colour - the same colour the
 * bowl is holding - so the bar says which strain is in the pipe as well as how far it has come.
 *
 * <p>It is drawn just under the item's own name line and above the experience bar, only while the
 * pipe is in use and its bowl is loaded, so it appears exactly when a draw is under way.
 */
public final class PipeDrawHud {
    private static final Identifier LAYER = Identifier.fromNamespaceAndPath(TolkienMobsMain.MODID, "pipe_draw");

    /** The bar's own size, and how far above the bottom edge of the screen it sits. */
    private static final int WIDTH = 60;
    private static final int HEIGHT = 3;
    private static final int ABOVE_BOTTOM = 46;

    /** The unpainted track, and the frame drawn a pixel around bar, track and notch alike. */
    private static final int TRACK = 0x60000000;
    private static final int FRAME = 0xC0000000;
    private static final int NOTCH = 0xFFFFFFFF;

    private PipeDrawHud() {
    }

    public static void register(RegisterGuiLayersEvent event) {
        // Above the item name line puts it in the gap between that line and the experience bar.
        event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, LAYER, PipeDrawHud::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.isUsingItem()) {
            return;
        }
        ItemStack stack = player.getUseItem();
        if (!(stack.getItem() instanceof PipeItem)) {
            return;
        }
        PipeweedStrain bowl = PipeItem.bowlOf(stack);
        if (bowl == null) {
            return;
        }

        int total = Math.max(1, TolkienMobsConfig.pipeDrawTicks());
        int drawn = total - player.getUseItemRemainingTicks();
        float progress = Mth.clamp((float) drawn / total, 0.0F, 1.0F);
        float needed = (float) TolkienMobsConfig.pipeMinDrawTicks() / total;

        int x = (graphics.guiWidth() - WIDTH) / 2;
        int y = graphics.guiHeight() - ABOVE_BOTTOM;

        graphics.fill(x - 1, y - 1, x + WIDTH + 1, y + HEIGHT + 1, FRAME);
        graphics.fill(x, y, x + WIDTH, y + HEIGHT, TRACK);

        int filled = Math.round(WIDTH * progress);
        if (filled > 0) {
            int colour = progress >= needed ? bowl.smoke() : bowl.bloom();
            graphics.fill(x, y, x + filled, y + HEIGHT, ARGB.opaque(colour));
        }

        // The notch is drawn last so it stays visible under the fill.
        int notch = x + Math.round(WIDTH * needed);
        graphics.fill(notch, y - 1, notch + 1, y + HEIGHT + 1, NOTCH);
    }
}
