package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.FactionEvents;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import com.greatorator.tolkienmobs.faction.Quest;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import com.greatorator.tolkienmobs.network.manager.QuestAbandonManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * The book: an open ledger of leather and parchment, two leaves at a time.
 * <p>
 * It is one screen with two bookmarks rather than two screens. The quest book opens it on
 * {@link Tab#QUESTS}, and the key the player already knows opens it on {@link Tab#ALLIANCES}, so
 * whichever question a player has, the answer is one click away from the other.
 * <p>
 * The alliances bookmark is a ledger on the left leaf and the work in hand on the right. The quests
 * bookmark spreads the work across both leaves, each entry naming the giver, the place the work was
 * taken and what it is for. Every line is measured before it is drawn - trimmed with an ellipsis, or
 * wrapped onto the leaf - because a page that spells a long item name off its own edge is not a page.
 * <p>
 * Every line of type is drawn without the drop shadow vanilla gives text. A shadow is drawn for the
 * dark of the world behind a nameplate; on parchment it is a smudge under a dark ink on a light page,
 * and it is what made the book hard to read. The inks carry the contrast instead.
 * <p>
 * Both leaves are drawn from caches the server last sent - the standings and the quest book - so this
 * screen never asks the server for anything except to set a quest aside, which it cannot do itself.
 */
public class FactionLedgerScreen extends Screen {
    /** The two bookmarks of the book. */
    public enum Tab {
        ALLIANCES("screen.tolkienmobs.faction_ledger.tab.alliances"),
        QUESTS("screen.tolkienmobs.faction_ledger.tab.quests");

        private final String key;

        Tab(String key) {
            this.key = key;
        }
    }

    /** The book's largest size in GUI pixels; it shrinks to fit a small window rather than overflow. */
    private static final int MAX_BOOK_W = 340;
    private static final int MAX_BOOK_H = 206;
    /** The leather showing around the leaves, and the gap the stitching runs down. */
    private static final int COVER = 10;
    private static final int SPINE = 12;
    private static final int RIBBON_W = 88;
    private static final int RIBBON_H = 12;
    /** The height of one line of the book's type. */
    private static final int LINE = 9;
    /** How long a first click on an abandon button stays armed before it lets go again. */
    private static final long ARM_MILLIS = 4000L;

    private static final int LEATHER = 0xFF4A2F1B;
    private static final int LEATHER_DARK = 0xFF241408;
    private static final int LEATHER_LIGHT = 0xFF6B4526;
    private static final int PAGE = 0xFFE9D8B4;
    private static final int PAGE_LIGHT = 0xFFF4E7C8;
    private static final int PAGE_EDGE = 0xFFC6AC83;
    private static final int PAGE_SHADE = 0xFFDDC9A3;
    private static final int INK = 0xFF35240F;
    private static final int INK_DIM = 0xFF6B5535;
    private static final int INK_GOLD = 0xFF7A4A16;
    private static final int INK_GREEN = 0xFF33602A;
    private static final int INK_BLUE = 0xFF3F6B7C;
    private static final int INK_RED = 0xFF8C2F24;
    private static final int TRACK = 0xFFC6AC83;
    private static final int RIBBON_ON = 0xFF7A2E28;
    private static final int RIBBON_OFF = 0xFF3A2413;
    private static final int RIBBON_INK_ON = 0xFFF0E2C4;
    private static final int RIBBON_INK_OFF = 0xFFB9A88B;

    /** No shadow: the book is ink on parchment, not a nameplate over the world. */
    private static final boolean FLAT = false;

    private Tab tab;

    private int bookLeft;
    private int bookTop;
    private int bookW;
    private int bookH;
    private int pageW;
    private int pageH;
    private int leftPageX;
    private int rightPageX;

    /** Where each abandon button was drawn this frame, so a click can be matched to a quest. */
    private final List<AbandonSpot> abandonSpots = new ArrayList<>();
    /** The entry a first click has armed, and when, so setting work aside takes two clicks. */
    private int armedSlot = -1;
    private long armedAt;

    public FactionLedgerScreen() {
        this(Tab.ALLIANCES);
    }

    public FactionLedgerScreen(Tab tab) {
        super(Component.translatable("screen.tolkienmobs.faction_ledger"));
        this.tab = tab;
    }

    // ---- the book itself ----

    /** Measures the book against the window, so a small GUI gets a smaller book rather than a clipped one. */
    private void layout() {
        this.bookW = Math.min(MAX_BOOK_W, this.width - 16);
        this.bookH = Math.min(MAX_BOOK_H, this.height - 24);
        this.bookLeft = (this.width - this.bookW) / 2;
        this.bookTop = (this.height - this.bookH) / 2;
        this.pageW = (this.bookW - 2 * COVER - SPINE) / 2;
        this.pageH = this.bookH - 2 * COVER;
        this.leftPageX = this.bookLeft + COVER;
        this.rightPageX = this.leftPageX + this.pageW + SPINE;
    }

    private int pageTop() {
        return this.bookTop + COVER;
    }

    private int pageBottom() {
        return this.bookTop + this.bookH - COVER;
    }

    /** Where a leaf's written area starts, under its heading and the rule beneath it. */
    private int bodyTop() {
        return pageTop() + 26;
    }

    private int bodyBottom() {
        return pageBottom() - 8;
    }

    private int innerX(int pageX) {
        return pageX + 9;
    }

    private int innerW() {
        return this.pageW - 18;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        layout();
        abandonSpots.clear();
        if (armedSlot >= 0 && Util.getMillis() - armedAt > ARM_MILLIS) {
            armedSlot = -1;
        }
        drawBook(graphics);
        drawRibbons(graphics);
        if (this.tab == Tab.ALLIANCES) {
            drawStandings(graphics);
            drawCarried(graphics);
        } else {
            drawQuestBook(graphics);
        }
    }

    private void drawBook(GuiGraphicsExtractor graphics) {
        graphics.fill(bookLeft, bookTop, bookLeft + bookW, bookTop + bookH, LEATHER);
        graphics.outline(bookLeft, bookTop, bookW, bookH, LEATHER_DARK);
        graphics.outline(bookLeft + 1, bookTop + 1, bookW - 2, bookH - 2, LEATHER_LIGHT);

        drawLeaf(graphics, leftPageX, true);
        drawLeaf(graphics, rightPageX, false);

        // The stitching down the middle, and the shadow the leaves cast on each other there.
        int spineX = leftPageX + pageW + SPINE / 2;
        graphics.fill(spineX - 1, pageTop() + 2, spineX + 1, pageBottom() - 2, LEATHER_DARK);
    }

    private void drawLeaf(GuiGraphicsExtractor graphics, int x, boolean left) {
        graphics.fill(x, pageTop(), x + pageW, pageBottom(), PAGE);
        graphics.outline(x, pageTop(), pageW, pageH, PAGE_EDGE);
        // The gutter is in shadow, and the outer edge of the leaf catches the light.
        int shadeX = left ? x + pageW - 4 : x;
        graphics.fill(shadeX, pageTop() + 1, shadeX + 4, pageBottom() - 1, PAGE_SHADE);
        int lightX = left ? x + 1 : x + pageW - 2;
        graphics.fill(lightX, pageTop() + 1, lightX + 1, pageBottom() - 1, PAGE_LIGHT);
    }

    private void drawPageTitle(GuiGraphicsExtractor graphics, int pageX, Component title) {
        String line = title.getString();
        // Centred by hand: the centred helper has no way to say "no shadow", and a shadowed heading over
        // flat body text is the one line of the page that still looks wrong.
        graphics.text(this.font, line, pageX + (pageW - this.font.width(line)) / 2, pageTop() + 8, INK_GOLD, FLAT);
        graphics.fill(pageX + 12, pageTop() + 20, pageX + pageW - 12, pageTop() + 21, PAGE_EDGE);
    }

    // ---- the bookmark ribbons ----

    private int ribbonY() {
        return bookTop - (RIBBON_H - 2);
    }

    private void drawRibbons(GuiGraphicsExtractor graphics) {
        Tab[] tabs = Tab.values();
        int y = ribbonY();
        for (int i = 0; i < tabs.length; i++) {
            int x = leftPageX + i * (RIBBON_W + 4);
            boolean on = tabs[i] == this.tab;
            graphics.fill(x, y, x + RIBBON_W, y + RIBBON_H + 1, on ? RIBBON_ON : RIBBON_OFF);
            graphics.fill(x, y, x + RIBBON_W, y + 1, on ? 0xFF9A4038 : 0xFF513322);
            String label = Component.translatable(tabs[i].key).getString();
            graphics.text(this.font, label, x + (RIBBON_W - this.font.width(label)) / 2, y + 3,
                    on ? RIBBON_INK_ON : RIBBON_INK_OFF, FLAT);
        }
    }

    // ---- the left leaf: where the player stands ----

    private void drawStandings(GuiGraphicsExtractor graphics) {
        drawPageTitle(graphics, leftPageX, Component.translatable("screen.tolkienmobs.faction_ledger.tab.alliances"));

        int x = innerX(leftPageX);
        int w = innerW();
        int top = bodyTop();
        int rows = Alliance.values().length;
        int rowH = Math.min(42, Math.max(26, (bodyBottom() - top - 14) / Math.max(1, rows)));
        for (int i = 0; i < rows; i++) {
            drawStanding(graphics, x, top + i * rowH, w, Alliance.values()[i]);
        }
        drawCentred(graphics, leftPageX, Component.translatable("screen.tolkienmobs.faction_ledger.hint"),
                INK_DIM, pageBottom() - 13);
    }

    private void drawStanding(GuiGraphicsExtractor graphics, int x, int y, int w, Alliance alliance) {
        int standing = FactionStandings.clientStanding(alliance);
        FactionRank rank = FactionRank.of(standing);

        // The alliance colours are 0xRRGGBB, and every colour argument here is ARGB - text is skipped
        // outright when its alpha is zero - so both inks are made opaque or a row draws nothing at all.
        int nameInk = ARGB.opaque(alliance.colour());
        int rankInk = ARGB.opaque(rank.colour());

        graphics.item(new ItemStack(alliance.token()), x, y + 1);
        int textX = x + 21;
        int textW = w - 21;
        String points = String.valueOf(standing);
        int pointsW = this.font.width(points);
        graphics.text(this.font, fit(alliance.displayName().getString(), textW - pointsW - 4), textX, y + 2,
                nameInk, FLAT);
        graphics.text(this.font, points, x + w - pointsW, y + 2, rankInk, FLAT);
        graphics.text(this.font, fit(FactionEvents.rankName(rank).getString(), textW), textX, y + 12, rankInk, FLAT);

        int barY = y + 23;
        graphics.fill(textX, barY, textX + textW, barY + 4, TRACK);
        int filled = Math.round(textW * rank.progress(standing));
        if (filled > 0) {
            graphics.fill(textX, barY, textX + filled, barY + 4, rankInk);
        }
    }

    // ---- the right leaf on the ledger bookmark: what is in hand ----

    private void drawCarried(GuiGraphicsExtractor graphics) {
        drawPageTitle(graphics, rightPageX, Component.translatable("screen.tolkienmobs.quests.carried"));

        List<Quest> quests = QuestLedger.clientQuests();
        int x = innerX(rightPageX);
        int w = innerW();
        if (quests.isEmpty()) {
            drawWrapped(graphics, x, bodyTop() + 6, w,
                    Component.translatable("screen.tolkienmobs.quests.empty"), INK_DIM, 3);
            drawCentred(graphics, rightPageX, Component.translatable("screen.tolkienmobs.quests.hint"),
                    INK_DIM, pageBottom() - 13);
            return;
        }
        int rowH = Math.min(30, Math.max(18, (bodyBottom() - bodyTop() - 12) / Math.max(1, quests.size())));
        for (int i = 0; i < quests.size(); i++) {
            drawCarriedRow(graphics, x, bodyTop() + i * rowH, w, quests.get(i));
        }
        drawCentred(graphics, rightPageX, Component.translatable("screen.tolkienmobs.quests.hint"),
                INK_DIM, pageBottom() - 13);
    }

    private void drawCarriedRow(GuiGraphicsExtractor graphics, int x, int y, int w, Quest quest) {
        graphics.item(iconOf(quest), x, y + 1);
        int textX = x + 21;
        int textW = w - 21;
        String counter = quest.progress() + " / " + quest.goal();
        int counterW = this.font.width(counter);
        graphics.text(this.font, fit(quest.objective().getString(), textW - counterW - 4), textX, y + 2,
                quest.complete() ? INK_GREEN : INK, FLAT);
        graphics.text(this.font, counter, x + w - counterW, y + 2, INK_DIM, FLAT);

        int barY = y + 13;
        graphics.fill(textX, barY, textX + textW, barY + 3, TRACK);
        int filled = quest.goal() <= 0 ? 0 : Math.round(textW * (quest.progress() / (float) quest.goal()));
        if (filled > 0) {
            graphics.fill(textX, barY, textX + filled, barY + 3, quest.complete() ? INK_GREEN : INK_BLUE);
        }
    }

    // ---- both leaves on the quest bookmark ----

    private void drawQuestBook(GuiGraphicsExtractor graphics) {
        drawPageTitle(graphics, leftPageX, Component.translatable("screen.tolkienmobs.quests.page"));
        drawPageTitle(graphics, rightPageX, Component.translatable("screen.tolkienmobs.quests.page2"));

        List<Quest> quests = QuestLedger.clientQuests();
        if (quests.isEmpty()) {
            drawWrapped(graphics, innerX(leftPageX), bodyTop() + 8, innerW(),
                    Component.translatable("screen.tolkienmobs.quests.empty"), INK_DIM, 4);
            drawCentred(graphics, leftPageX, Component.translatable("screen.tolkienmobs.quests.hint"),
                    INK_DIM, pageBottom() - 13);
            return;
        }
        // The work is split down the middle of the book, so a full book is read left then right. The
        // offset is what lets a click name the quest it landed on, whichever leaf drew it.
        int half = (quests.size() + 1) / 2;
        drawQuestColumn(graphics, leftPageX, quests.subList(0, half), 0);
        drawQuestColumn(graphics, rightPageX, quests.subList(half, quests.size()), half);
        drawCentred(graphics, leftPageX, Component.translatable("screen.tolkienmobs.quests.hint"),
                INK_DIM, pageBottom() - 13);
    }

    private void drawQuestColumn(GuiGraphicsExtractor graphics, int pageX, List<Quest> quests, int first) {
        if (quests.isEmpty()) return;
        int x = innerX(pageX);
        int w = innerW();
        int top = bodyTop();
        // A line at the foot of the leaf is kept for the hand-in reminder, and what is left is split
        // between at most two entries.
        int blockH = Math.min(84, Math.max(62, (bodyBottom() - top - 14) / Math.max(1, quests.size())));
        for (int i = 0; i < quests.size() && i < 2; i++) {
            drawQuestEntry(graphics, x, top + i * blockH, w, blockH, quests.get(i), first + i);
        }
    }

    private void drawQuestEntry(GuiGraphicsExtractor graphics, int x, int y, int w, int h, Quest quest, int slot) {
        graphics.item(iconOf(quest), x, y);
        graphics.text(this.font, fit(quest.objective().getString(), w - 21), x + 21, y + 2, INK, FLAT);

        // The abandon button closes the giver's line. It is small on purpose, so the first click only
        // arms it and the button itself says what the second click will do.
        boolean armed = slot == armedSlot;
        String control = armed
                ? Component.translatable("screen.tolkienmobs.quests.abandon.confirm").getString()
                : "x";
        int controlW = this.font.width(control);
        int controlX = x + w - controlW;
        graphics.text(this.font, control, controlX, y + 13, armed ? INK_RED : INK_DIM, FLAT);
        abandonSpots.add(new AbandonSpot(slot, controlX - 2, y + 11, controlW + 4, LINE + 2));

        // The details run the width of the leaf, under the picture rather than beside it.
        graphics.text(this.font, fit(quest.giverName().getString(), w - controlW - 6), x, y + 13, INK_DIM, FLAT);
        graphics.text(this.font, fit(quest.takenAt().getString(), w), x, y + 23, INK_DIM, FLAT);

        int summaryTop = y + 34;
        int lines = Math.max(1, Math.min(3, (h - 44) / LINE));
        for (String line : wrap(quest.summary().getString(), w, lines)) {
            graphics.text(this.font, line, x, summaryTop, INK_DIM, FLAT);
            summaryTop += LINE;
        }

        int foot = y + h - 10;
        if (quest.complete()) {
            graphics.text(this.font, fit(Component.translatable("quest.tolkienmobs.complete").getString(), w),
                    x, foot - 1, INK_GREEN, FLAT);
            return;
        }
        String counter = quest.progress() + " / " + quest.goal();
        int counterW = this.font.width(counter);
        int barW = Math.max(0, w - counterW - 6);
        graphics.fill(x, foot, x + barW, foot + 4, TRACK);
        int filled = quest.goal() <= 0 ? 0 : Math.round(barW * (quest.progress() / (float) quest.goal()));
        if (filled > 0) {
            graphics.fill(x, foot, x + filled, foot + 4, INK_BLUE);
        }
        graphics.text(this.font, counter, x + w - counterW, foot - 2, INK_DIM, FLAT);
    }

    /** The picture a quest's entry leads with: the goods it wants, the country it serves, or a blade. */
    private ItemStack iconOf(Quest quest) {
        return switch (quest.kind()) {
            case FETCH -> quest.item() == null ? new ItemStack(Items.PAPER) : new ItemStack(quest.item());
            case FACTION -> new ItemStack(Alliance.byId(quest.target()).token());
            case HUNT -> new ItemStack(Items.IRON_SWORD);
        };
    }

    // ---- type that keeps to its page ----

    /** The longest prefix of the text that fits, with an ellipsis when something had to go. */
    private String fit(String text, int width) {
        if (width <= 0) return "";
        if (this.font.width(text) <= width) return text;
        String ellipsis = "...";
        int end = text.length();
        while (end > 0 && this.font.width(text.substring(0, end) + ellipsis) > width) {
            end--;
        }
        return text.substring(0, end).stripTrailing() + ellipsis;
    }

    /** Wraps a sentence onto a leaf, fitting what is left of the last line rather than running off it. */
    private List<String> wrap(String text, int width, int maxLines) {
        List<String> lines = new ArrayList<>();
        String remaining = text.trim();
        while (!remaining.isEmpty() && lines.size() < maxLines) {
            if (lines.size() == maxLines - 1) {
                lines.add(fit(remaining, width));
                break;
            }
            int cut = remaining.length();
            while (cut > 0 && this.font.width(remaining.substring(0, cut)) > width) {
                int space = remaining.lastIndexOf(' ', cut - 1);
                cut = space > 0 ? space : Math.max(0, cut - 1);
            }
            if (cut <= 0) {
                lines.add(fit(remaining, width));
                break;
            }
            lines.add(remaining.substring(0, cut).trim());
            remaining = remaining.substring(cut).trim();
        }
        return lines;
    }

    private void drawWrapped(GuiGraphicsExtractor graphics, int x, int y, int width, Component text, int ink,
                             int maxLines) {
        int lineY = y;
        for (String line : wrap(text.getString(), width, maxLines)) {
            graphics.text(this.font, line, x, lineY, ink, FLAT);
            lineY += LINE;
        }
    }

    private void drawCentred(GuiGraphicsExtractor graphics, int pageX, Component text, int ink, int y) {
        String line = fit(text.getString(), pageW - 16);
        graphics.text(this.font, line, pageX + (pageW - this.font.width(line)) / 2, y, ink, FLAT);
    }

    // ---- input ----

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            layout();
            Tab[] tabs = Tab.values();
            int y = ribbonY();
            for (int i = 0; i < tabs.length; i++) {
                int x = leftPageX + i * (RIBBON_W + 4);
                if (event.x() >= x && event.x() < x + RIBBON_W && event.y() >= y && event.y() < y + RIBBON_H + 1) {
                    this.tab = tabs[i];
                    // A bookmark is a change of page, so a button armed on the last one is let go.
                    this.armedSlot = -1;
                    return true;
                }
            }
            for (AbandonSpot spot : abandonSpots) {
                if (event.x() < spot.x() || event.x() >= spot.x() + spot.w()) continue;
                if (event.y() < spot.y() || event.y() >= spot.y() + spot.h()) continue;
                if (armedSlot == spot.slot() && Util.getMillis() - armedAt <= ARM_MILLIS) {
                    // The server owns the book: this only names a slot in it, and the sync that comes
                    // back is what redraws the page.
                    ClientPacketDistributor.sendToServer(new QuestAbandonManager(spot.slot()));
                    armedSlot = -1;
                } else {
                    armedSlot = spot.slot();
                    armedAt = Util.getMillis();
                }
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** An abandon button as it was drawn: the slot it would set aside, in screen coordinates. */
    private record AbandonSpot(int slot, int x, int y, int w, int h) {
    }
}
