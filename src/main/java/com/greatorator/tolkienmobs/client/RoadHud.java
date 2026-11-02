package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.faction.Waypoint;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The road's needle: an arrow at the top of the screen that points at the ground the page in hand names,
 * and the distance to it.
 * <p>
 * It exists because a page can say "go to Rivendell" to a player who has no idea which way that is. What
 * it does not do is say where Rivendell is: the arrow is a bearing and the distance is rounded to a
 * long walk, so the country still has to be crossed rather than typed into a teleport. It is drawn the
 * way the other HUDs here are - a layer of its own that shows nothing at all until a page gives it
 * somewhere to point - and it clears itself when the page is paid.
 * <p>
 * The arrow is drawn out of eight fixed stamps rather than a rotated triangle: a glyph turned by
 * fractions of a degree needs a matrix and buys nothing a player can see, and eight points of the
 * compass is already finer than a walk across Eriador.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class RoadHud {
    private static final Identifier LAYER = Identifier.fromNamespaceAndPath(MODID, "road");

    /**
     * How far above the bottom edge the plate sits, and how long the arrow's shaft is.
     * <p>
     * The bottom of the screen rather than the top, because the top is where a boss bar is drawn: the
     * needle stood there and a Murder Frog's health covered it completely. This height is above the
     * hotbar, the experience bar, the pipe's draw bar and the courage line, so nothing in the pack or
     * in vanilla reaches it.
     */
    private static final int ABOVE_BOTTOM = 88;
    private static final int PLATE_HEIGHT = 34;
    private static final int PLATE = 30;
    private static final int SHAFT = 9;

    private static final int PARCHMENT = 0xE9D8B4;
    private static final int BACKING = 0x66000000;
    private static final int INK = 0xFFFFD98A;

    /** The eight directions, ahead first, as the step a stamp takes per pixel of shaft. */
    private static final int[][] STAMPS = {
            {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

    private RoadHud() {
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.SELECTED_ITEM_NAME, LAYER, RoadHud::render);
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        LocalPlayer player = Minecraft.getInstance().player;
        Waypoint.Mark mark = Waypoint.client();
        if (player == null || mark == null || Minecraft.getInstance().level == null) return;
        // A stone in another country is not a direction: the needle would point at a bearing that means
        // nothing where the player is standing.
        if (!Minecraft.getInstance().level.dimension().identifier().toString().equals(mark.dimension().identifier().toString())) {
            return;
        }

        double dx = mark.pos().getX() + 0.5D - player.getX();
        double dz = mark.pos().getZ() + 0.5D - player.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        // Where the target lies, and where the player is looking, in the same frame: north is -Z and
        // east is +X, which is what the game's own compass reads.
        double toTarget = Math.atan2(dx, -dz);
        double view = Math.atan2(-Mth.sin(player.getYRot() * Mth.DEG_TO_RAD), -Mth.cos(player.getYRot() * Mth.DEG_TO_RAD));
        int octant = Math.floorMod((int) Math.round((toTarget - view) / (Math.PI / 4.0D)), STAMPS.length);

        int centre = graphics.guiWidth() / 2;
        String name = mark.label().indexOf('.') < 0
                ? mark.label()
                : Component.translatable(mark.label()).getString();
        String line = name + "  ·  "
                + Component.translatable("hud.tolkienmobs.road.blocks",
                String.valueOf(Math.max(1, Math.round(distance)))).getString();
        Font font = Minecraft.getInstance().font;
        // The plate is the point: an arrow and a line of text over open sky reads as a rendering
        // glitch rather than as a compass, and this has to be recognisable at a glance from a walk.
        int width = Math.max(PLATE, font.width(line)) + 12;
        int plateTop = graphics.guiHeight() - ABOVE_BOTTOM;
        graphics.fill(centre - width / 2, plateTop, centre + width / 2, plateTop + PLATE_HEIGHT, BACKING);
        drawArrow(graphics, centre, plateTop + 11, octant);
        graphics.text(font, line, centre - font.width(line) / 2, plateTop + 22, PARCHMENT, true);
    }

    /** One of the eight stamps: a shaft outward from the centre and a two-stroke head at its tip. */
    private static void drawArrow(GuiGraphicsExtractor graphics, int cx, int cy, int octant) {
        int[] step = STAMPS[octant];
        for (int i = 1; i <= SHAFT; i++) {
            int x = cx + step[0] * i;
            int y = cy + step[1] * i;
            graphics.fill(x - 1, y - 1, x + 1, y + 1, INK);
        }
        int tipX = cx + step[0] * SHAFT;
        int tipY = cy + step[1] * SHAFT;
        // The head is the two diagonals either side of the shaft, so the arrow reads as an arrow at
        // every one of the eight stamps rather than only on the four that face a screen edge.
        int[] left = {step[0] - step[1], step[1] + step[0]};
        int[] right = {step[0] + step[1], step[1] - step[0]};
        for (int i = 1; i <= 3; i++) {
            graphics.fill(tipX - left[0] * i - 1, tipY - left[1] * i - 1,
                    tipX - left[0] * i + 1, tipY - left[1] * i + 1, INK);
            graphics.fill(tipX - right[0] * i - 1, tipY - right[1] * i - 1,
                    tipX - right[0] * i + 1, tipY - right[1] * i + 1, INK);
        }
    }
}
