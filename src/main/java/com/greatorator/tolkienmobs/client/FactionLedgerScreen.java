package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.FactionEvents;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;

/**
 * The ledger of alliances: where a player stands with each of the three, drawn from the totals the
 * server last sent. It is a screen of its own rather than a tab, so a player can open it from their
 * key at any time and read it without leaving the world.
 * <p>
 * Each row is a token, the alliance, the rank earned, the point total, and a bar toward the next rank.
 * A hostile standing draws its bar shrinking toward zero, which is the one case where a longer bar is
 * the worse news.
 */
public class FactionLedgerScreen extends Screen {
    private static final int WIDTH = 176;
    private static final int HEIGHT = 166;
    private static final int ROW_HEIGHT = 46;
    private static final int ROW_TOP = 22;

    private static final int PANEL = 0xF012161B;
    private static final int EDGE = 0xFF3A4450;
    private static final int ROW = 0xFF1B222A;
    private static final int TRACK = 0xFF0B0E11;
    private static final int TITLE = 0xFFE8D9A0;
    private static final int HINT = 0xFF8A9299;

    public FactionLedgerScreen() {
        super(Component.translatable("screen.tolkienmobs.faction_ledger"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int left = (this.width - WIDTH) / 2;
        int top = (this.height - HEIGHT) / 2;

        graphics.fill(left, top, left + WIDTH, top + HEIGHT, PANEL);
        graphics.outline(left, top, WIDTH, HEIGHT, EDGE);
        graphics.centeredText(this.font, this.title, left + WIDTH / 2, top + 6, TITLE);

        for (Alliance alliance : Alliance.values()) {
            drawRow(graphics, left, top + ROW_TOP + alliance.ordinal() * ROW_HEIGHT, alliance);
        }

        graphics.centeredText(this.font, Component.translatable("screen.tolkienmobs.faction_ledger.hint"),
                left + WIDTH / 2, top + HEIGHT - 14, HINT);
    }

    private void drawRow(GuiGraphicsExtractor graphics, int left, int rowTop, Alliance alliance) {
        int standing = FactionStandings.clientStanding(alliance);
        FactionRank rank = FactionRank.of(standing);

        graphics.fill(left + 6, rowTop, left + WIDTH - 6, rowTop + ROW_HEIGHT - 4, ROW);
        graphics.item(new ItemStack(alliance.token()), left + 10, rowTop + 6);

        // The alliance colours are 0xRRGGBB, and every colour argument here is ARGB - text is skipped
        // outright when its alpha is zero - so both inks are made opaque or a row draws nothing at all.
        int allianceInk = ARGB.opaque(alliance.colour());
        int rankInk = ARGB.opaque(rank.colour());
        graphics.text(this.font, alliance.displayName(), left + 32, rowTop + 6, allianceInk);
        graphics.text(this.font, FactionEvents.rankName(rank), left + 32, rowTop + 18, rankInk);

        // The total sits on the right of the row, next to the rank it bought.
        String points = String.valueOf(standing);
        graphics.text(this.font, points, left + WIDTH - 12 - this.font.width(points), rowTop + 6, rankInk);

        int barX = left + 32;
        int barY = rowTop + 30;
        int barWidth = WIDTH - 46;
        graphics.fill(barX, barY, barX + barWidth, barY + 5, TRACK);
        int filled = Math.round(barWidth * rank.progress(standing));
        if (filled > 0) {
            graphics.fill(barX, barY, barX + filled, barY + 5, rankInk);
        }
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
