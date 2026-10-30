package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.FactionEvents;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import com.greatorator.tolkienmobs.faction.Quest;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

/**
 * The book: where a player stands with each alliance, and what work they are carrying.
 * <p>
 * It is one screen with two pages rather than two screens. The quest book opens it on
 * {@link Tab#QUESTS}, and the key the player already knows opens it on {@link Tab#ALLIANCES}, so
 * whichever question a player has, the answer is one click away from the other.
 * <p>
 * Both pages are drawn from caches the server last sent - the standings and the quest book - so this
 * screen never asks the server for anything and cannot show another player's numbers.
 */
public class FactionLedgerScreen extends Screen {
    /** The two pages of the book. */
    public enum Tab {
        ALLIANCES("screen.tolkienmobs.faction_ledger.tab.alliances"),
        QUESTS("screen.tolkienmobs.faction_ledger.tab.quests");

        private final String key;

        Tab(String key) {
            this.key = key;
        }
    }

    private static final int WIDTH = 176;
    private static final int HEIGHT = 186;
    private static final int CONTENT_TOP = 30;
    private static final int ROW_HEIGHT = 46;
    private static final int QUEST_HEIGHT = 34;
    private static final int TAB_WIDTH = 82;
    private static final int TAB_HEIGHT = 14;

    private static final int PANEL = 0xF012161B;
    private static final int EDGE = 0xFF3A4450;
    private static final int ROW = 0xFF1B222A;
    private static final int TRACK = 0xFF0B0E11;
    private static final int TITLE = 0xFFE8D9A0;
    private static final int HINT = 0xFF8A9299;
    private static final int TAB_ON = 0xFF2A333D;
    private static final int TAB_OFF = 0xFF161C22;

    private Tab tab;

    public FactionLedgerScreen() {
        this(Tab.ALLIANCES);
    }

    public FactionLedgerScreen(Tab tab) {
        super(Component.translatable("screen.tolkienmobs.faction_ledger"));
        this.tab = tab;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int left = (this.width - WIDTH) / 2;
        int top = (this.height - HEIGHT) / 2;

        graphics.fill(left, top, left + WIDTH, top + HEIGHT, PANEL);
        graphics.outline(left, top, WIDTH, HEIGHT, EDGE);
        graphics.centeredText(this.font, this.title, left + WIDTH / 2, top + 6, TITLE);

        drawTabs(graphics, left, top);
        if (this.tab == Tab.ALLIANCES) {
            drawAlliances(graphics, left, top);
        } else {
            drawQuests(graphics, left, top);
        }
    }

    private void drawTabs(GuiGraphicsExtractor graphics, int left, int top) {
        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            Tab entry = tabs[i];
            boolean on = entry == this.tab;
            int x = left + 6 + i * (TAB_WIDTH + 4);
            int y = top + 16;
            graphics.fill(x, y, x + TAB_WIDTH, y + TAB_HEIGHT, on ? TAB_ON : TAB_OFF);
            graphics.centeredText(this.font, Component.translatable(entry.key),
                    x + TAB_WIDTH / 2, y + 3, on ? TITLE : HINT);
        }
    }

    // ---- the alliances page ----

    private void drawAlliances(GuiGraphicsExtractor graphics, int left, int top) {
        for (Alliance alliance : Alliance.values()) {
            drawAllianceRow(graphics, left, top + CONTENT_TOP + alliance.ordinal() * ROW_HEIGHT, alliance);
        }
        graphics.centeredText(this.font, Component.translatable("screen.tolkienmobs.faction_ledger.hint"),
                left + WIDTH / 2, top + HEIGHT - 14, HINT);
    }

    private void drawAllianceRow(GuiGraphicsExtractor graphics, int left, int rowTop, Alliance alliance) {
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

    // ---- the quests page ----

    private void drawQuests(GuiGraphicsExtractor graphics, int left, int top) {
        List<Quest> quests = QuestLedger.clientQuests();
        if (quests.isEmpty()) {
            graphics.centeredText(this.font, Component.translatable("screen.tolkienmobs.quests.empty"),
                    left + WIDTH / 2, top + CONTENT_TOP + 30, HINT);
            graphics.centeredText(this.font, Component.translatable("screen.tolkienmobs.quests.hint"),
                    left + WIDTH / 2, top + HEIGHT - 14, HINT);
            return;
        }
        for (int i = 0; i < quests.size(); i++) {
            drawQuestRow(graphics, left, top + CONTENT_TOP + i * QUEST_HEIGHT, quests.get(i));
        }
        graphics.centeredText(this.font, Component.translatable("screen.tolkienmobs.quests.hint"),
                left + WIDTH / 2, top + HEIGHT - 14, HINT);
    }

    private void drawQuestRow(GuiGraphicsExtractor graphics, int left, int rowTop, Quest quest) {
        graphics.fill(left + 6, rowTop, left + WIDTH - 6, rowTop + QUEST_HEIGHT - 2, ROW);
        graphics.item(iconOf(quest), left + 9, rowTop + 8);

        int ink = quest.complete() ? 0xFF62B45B : 0xFFD8D8D8;
        graphics.text(this.font, quest.objective(), left + 28, rowTop + 4, ink);
        graphics.text(this.font, quest.rewardLine(), left + 28, rowTop + 15, ARGB.opaque(0xD9A441));

        // A finished quest says so instead of showing a bar that cannot move.
        if (quest.complete()) {
            graphics.text(this.font, Component.translatable("quest.tolkienmobs.complete")
                    .withStyle(ChatFormatting.GREEN), left + 28, rowTop + 25, ARGB.opaque(0x62B45B));
            return;
        }
        int barX = left + 28;
        int barY = rowTop + 26;
        int barWidth = WIDTH - 46;
        graphics.fill(barX, barY, barX + barWidth, barY + 4, TRACK);
        int filled = quest.goal() <= 0 ? 0 : Math.round(barWidth * (quest.progress() / (float) quest.goal()));
        if (filled > 0) {
            graphics.fill(barX, barY, barX + filled, barY + 4, ARGB.opaque(0x4FB8C9));
        }
        String counter = quest.progress() + " / " + quest.goal();
        graphics.text(this.font, counter, left + WIDTH - 12 - this.font.width(counter), rowTop + 15, HINT);
    }

    /** The picture a quest's row leads with: the goods it wants, the country it serves, or a blade. */
    private ItemStack iconOf(Quest quest) {
        return switch (quest.kind()) {
            case FETCH -> quest.item() == null ? new ItemStack(Items.PAPER) : new ItemStack(quest.item());
            case FACTION -> new ItemStack(Alliance.byId(quest.target()).token());
            case HUNT -> new ItemStack(Items.IRON_SWORD);
        };
    }

    // ---- input ----

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            int left = (this.width - WIDTH) / 2;
            int top = (this.height - HEIGHT) / 2;
            Tab[] tabs = Tab.values();
            for (int i = 0; i < tabs.length; i++) {
                int x = left + 6 + i * (TAB_WIDTH + 4);
                int y = top + 16;
                if (event.x() >= x && event.x() < x + TAB_WIDTH && event.y() >= y && event.y() < y + TAB_HEIGHT) {
                    this.tab = tabs[i];
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
