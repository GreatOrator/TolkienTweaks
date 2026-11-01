package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.Courage;
import com.greatorator.tolkienmobs.faction.CourageLedger;
import com.greatorator.tolkienmobs.faction.FactionEvents;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import com.greatorator.tolkienmobs.faction.Quest;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.network.manager.QuestAbandonManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ARGB;
import net.minecraft.util.Util;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;
import com.greatorator.tolkienmobs.faction.Campaign;

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
    private static final int MAX_BOOK_W = 392;
    private static final int MAX_BOOK_H = 216;
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

    /** Which carried front page the story leaf is open at, counted among the front's pages only. */
    private int storyTurn;
    /** Where the leaf's page arrows were drawn this frame, so a click can turn a page. */
    private final List<TurnSpot> turnSpots = new ArrayList<>();

    public FactionLedgerScreen() {
        this(Tab.ALLIANCES);
    }

    public FactionLedgerScreen(Tab tab) {
        super(Component.translatable("screen.tolkienmobs.faction_ledger"));
        this.tab = tab;
    }

    /** Whether the book has already made its opening sound, so resizing the window does not repeat it. */
    private boolean announced;

    @Override
    protected void init() {
        super.init();
        if (this.announced) return;
        this.announced = true;
        playSound(TolkienSounds.LEDGER_OPEN.get(), 1.0F);
    }

    @Override
    public void removed() {
        playSound(TolkienSounds.LEDGER_CLOSE.get(), 0.95F);
        super.removed();
    }

    /**
     * Plays one of the book's own sounds on this client. A screen cannot ask the server for one - a page has
     * no place in the world to be heard from - so the client that is looking at the page plays it, at a
     * volume that sits under the world rather than over it.
     */
    private static void playSound(SoundEvent sound, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, 0.55F));
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
        turnSpots.clear();
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
        // The courage row is the last row of the leaf rather than pinned to its foot: it is the only
        // line here about the player rather than a country, and it reads as one more entry when it sits
        // at the spacing the countries above it are drawn at.
        int rowH = Math.min(40, Math.max(26,
                (bodyBottom() - top - 6 - hintLines() * LINE) / Math.max(1, rows + 1)));
        int courageTop = top + rows * rowH;
        for (int i = 0; i < rows; i++) {
            drawStanding(graphics, x, top + i * rowH, w, Alliance.values()[i]);
        }
        drawCourage(graphics, x, courageTop, w);
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

    /**
     * The one row on this leaf about the player rather than a country: how much of the dark they can
     * stand, and how far into the next rung they are. It is drawn with the horn that answers fear, in
     * the book that carries the work, because courage is earned out of the two together.
     */
    private void drawCourage(GuiGraphicsExtractor graphics, int x, int y, int w) {
        int deeds = CourageLedger.clientCourage();
        Courage stage = Courage.of(deeds);
        int ink = ARGB.opaque(stage.colour());

        graphics.item(new ItemStack(TolkienItems.WAR_HORN.get()), x, y + 1);
        int textX = x + 21;
        int textW = w - 21;
        String count = String.valueOf(deeds);
        int countW = this.font.width(count);
        graphics.text(this.font, fit(Component.translatable("screen.tolkienmobs.faction_ledger.courage").getString(), textW - countW - 4),
                textX, y + 2, INK_DIM, FLAT);
        graphics.text(this.font, count, x + w - countW, y + 2, ink, FLAT);
        graphics.text(this.font, fit(stage.displayName().getString(), textW), textX, y + 12, ink, FLAT);

        int barY = y + 23;
        graphics.fill(textX, barY, textX + textW, barY + 4, TRACK);
        int filled = Math.round(textW * stage.progress(deeds));
        if (filled > 0) {
            graphics.fill(textX, barY, textX + filled, barY + 4, ink);
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
        int rowH = Math.min(34, Math.max(20,
                (bodyBottom() - bodyTop() - 6 - hintLines() * LINE) / Math.max(1, quests.size())));
        for (int i = 0; i < quests.size(); i++) {
            drawCarriedRow(graphics, x, bodyTop() + i * rowH, w, quests.get(i));
        }
        drawHint(graphics, rightPageX);
    }

    private void drawCarriedRow(GuiGraphicsExtractor graphics, int x, int y, int w, Quest quest) {
        graphics.item(iconOf(quest), x, y + 1);
        int textX = x + 21;
        String counter = quest.progress() + " / " + quest.goal();
        int counterW = this.font.width(counter);
        int textW = w - 21 - counterW - 4;
        int after = wrapped(graphics, textX, y + 2, textW, quest.objective().getString(),
                quest.complete() ? INK_GREEN : INK, 2);
        graphics.text(this.font, counter, x + w - counterW, y + 2, INK_DIM, FLAT);

        int barY = Math.max(y + 13, after + 1);
        graphics.fill(textX, barY, textX + w - 21, barY + 3, TRACK);
        int filled = quest.goal() <= 0 ? 0 : Math.round((w - 21) * (quest.progress() / (float) quest.goal()));
        if (filled > 0) {
            graphics.fill(textX, barY, textX + filled, barY + 3, quest.complete() ? INK_GREEN : INK_BLUE);
        }
    }

    // ---- both leaves on the quest bookmark ----

    private void drawQuestBook(GuiGraphicsExtractor graphics) {
        drawPageTitle(graphics, leftPageX, Component.translatable("screen.tolkienmobs.quests.bounties"));
        drawPageTitle(graphics, rightPageX, Component.translatable("screen.tolkienmobs.quests.story"));

        List<Quest> quests = QuestLedger.clientQuests();
        drawBounties(graphics, quests);
        drawStory(graphics, storiesOf(quests));
        drawHint(graphics, leftPageX);
    }

    /**
     * The bounties leaf: the ordinary errands, in the order they were taken, and no more of them than
     * the leaf has room to spell out. The limit is the server's too - {@link QuestLedger#MAX_BOUNTIES}
     * is what a player may hold - so this page can be trusted to fit whatever it is handed.
     */
    private void drawBounties(GuiGraphicsExtractor graphics, List<Quest> quests) {
        int x = innerX(leftPageX);
        int w = innerW();
        // The book's order is the player's order, and a story page sits somewhere in it, so the slots
        // are the book's own rather than the count of errands drawn.
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < quests.size(); i++) {
            if (!quests.get(i).story()) slots.add(i);
        }
        if (slots.isEmpty()) {
            // A player carrying nothing but the fronts' pages is not a player with nothing: the leaf
            // says where the pages they do hold are, rather than sending them out to look for work they
            // are already doing.
            drawWrapped(graphics, x, bodyTop() + 8, w,
                    Component.translatable(quests.isEmpty()
                            ? "screen.tolkienmobs.quests.bounties.empty"
                            : "screen.tolkienmobs.quests.bounties.front"), INK_DIM, 4);
            return;
        }
        // The foot of the leaf says what to do with finished work, so it is measured before the rows
        // are sized rather than drawn over their last line.
        int room = bodyBottom() - bodyTop() - 4 - hintLines() * LINE;
        int rows = Math.max(1, Math.min(slots.size(), QuestLedger.MAX_BOUNTIES));
        int rowH = Math.min(40, Math.max(24, room / rows));
        for (int i = 0; i < rows; i++) {
            drawBounty(graphics, x, bodyTop() + i * rowH, w, rowH, quests.get(slots.get(i)), slots.get(i));
        }
    }

    private void drawBounty(GuiGraphicsExtractor graphics, int x, int y, int w, int h, Quest quest, int slot) {
        graphics.item(iconOf(quest), x, y);
        // The abandon button closes the giver's line. It is small on purpose, so the first click only
        // arms it and the button itself says what the second click will do.
        boolean armed = slot == armedSlot;
        String control = armed
                ? Component.translatable("screen.tolkienmobs.quests.abandon.confirm").getString()
                : "x";
        int controlW = this.font.width(control);
        int controlX = x + w - controlW;
        int textX = x + 21;
        // The ask is a sentence, and one leaf is not wide enough for a sentence: it takes a second line
        // where the row has the height for one, rather than ending in an ellipsis that leaves the work
        // unnamed. Nobody can act on "Reach somewhe...".
        int after = wrapped(graphics, textX, y + 2, w - controlW - 25, quest.objective().getString(),
                quest.complete() ? INK_GREEN : INK, h >= 34 ? 2 : 1);
        graphics.text(this.font, control, controlX, y + 2, armed ? INK_RED : INK_DIM, FLAT);
        abandonSpots.add(new AbandonSpot(slot, controlX - 2, y + 1, controlW + 4, LINE + 2));

        String counter = quest.progress() + " / " + quest.goal();
        int counterW = this.font.width(counter);
        int lineY = Math.max(y + 11, after);
        graphics.text(this.font, fit(quest.giverName().getString(), w - 21 - counterW - 4), textX, lineY,
                INK_DIM, FLAT);
        graphics.text(this.font, counter, x + w - counterW, lineY, INK_DIM, FLAT);

        // The bar closes the row and holds the whole width, since the count has moved up beside the name.
        int foot = y + h - 4;
        int barW = w - 21;
        graphics.fill(textX, foot, textX + barW, foot + 3, TRACK);
        int filled = quest.goal() <= 0 ? 0 : Math.round(barW * (quest.progress() / (float) quest.goal()));
        if (filled > 0) {
            graphics.fill(textX, foot, textX + filled, foot + 3, quest.complete() ? INK_GREEN : INK_BLUE);
        }
    }

    /**
     * The story leaf: one campaign step at a time, with the room the errands used to take given over to
     * its prose. A player can carry a page from each of the three fronts, so the leaf turns between them
     * and says which of how many is open - it used to draw whichever page came first in the book and stop
     * there, which left the other two in hand and unreadable.
     */
    private void drawStory(GuiGraphicsExtractor graphics, List<Quest> stories) {
        int x = innerX(rightPageX);
        int w = innerW();
        if (stories.isEmpty()) {
            // A finished campaign is the one thing an empty leaf must not be left saying, so the leaf
            // says it is over instead.
            boolean won = Campaign.clientRider() > 0;
            drawWrapped(graphics, x, bodyTop() + 8, w,
                    Component.translatable(won
                            ? "screen.tolkienmobs.quests.campaign.done"
                            : "screen.tolkienmobs.quests.story.empty"),
                    won ? INK_GOLD : INK_DIM, 5);
            return;
        }
        this.storyTurn = Math.max(0, Math.min(this.storyTurn, stories.size() - 1));
        Quest story = stories.get(this.storyTurn);
        drawTurns(graphics, this.storyTurn, stories.size());
        // What the step is called and where it happens, where the page says: a page of the campaign is a
        // place and a name before it is a count, and the leaf says so above the counting.
        var front = story.employerAlliance();
        Component title = front == null ? Campaign.clientRiderTitle() : Campaign.clientTitle(front);
        Component place = front == null ? Campaign.clientRiderPlace() : Campaign.clientPlace(front);
        boolean named = !title.getString().isEmpty();

        graphics.item(iconOf(story), x, bodyTop());
        int y = bodyTop();
        if (named) {
            graphics.text(this.font, fit(title.getString(), w - 21), x + 21, y, INK_GOLD, FLAT);
            y += 11;
            // The objective is what the step asks for, and it is a sentence: "Slay 12 soldiers of
            // Haradwaith" does not fit the width of a leaf, so it wraps onto a second line instead of
            // ending in "Slay 12 soldiers of H..." - a page that says what it is only half-read is the
            // same page it was before it had a name.
            y = wrapped(graphics, x + 21, y, w - 21, story.objective().getString(), INK, 2) + 4;
        } else {
            y = wrapped(graphics, x, y + 2, w, story.objective().getString(), INK, 2) + 4;
        }

        y = wrapped(graphics, x, y, w, story.summary().getString(), INK_DIM, 6) + 2;
        if (!place.getString().isEmpty()) {
            // Where the step happens is a sentence too, and it is written down because it can be read:
            // "the Green Way, where the scout's ditch runs" takes two lines rather than one and a half.
            y = wrapped(graphics, x, y, w, place.getString(), INK_DIM, 2) + 2;
        }
        // A page that has to be carried somewhere else says so above the name of whoever handed
        // it over, because the walk back is part of the work and not a footnote to it.
        Component to = story.takenTo();
        if (!to.getString().isEmpty()) {
            y = wrapped(graphics, x, y, w, to.getString(), INK_RED, 1) + 2;
        }
        graphics.text(this.font, fit(story.giverName().getString(), w), x, y, INK_DIM, FLAT);
        y += LINE;
        graphics.text(this.font, fit(story.takenAt().getString(), w), x, y, INK_DIM, FLAT);
        y += LINE + 2;
        graphics.text(this.font, fit(story.rewardLine().getString(), w), x, y, INK_GOLD, FLAT);

        int foot = bodyBottom() - 8;
        if (story.complete()) {
            // The page that ends a front has the front's last words on it, and they are drawn only
            // once the work is done: it is the one thing on the leaf a player cannot read early.
            Component closing = front == null ? Component.empty() : Campaign.clientClosing(front);
            if (!closing.getString().isEmpty()) {
                wrapped(graphics, x, foot - 12, w, closing.getString(), INK_GOLD, 1);
            }
            graphics.text(this.font, fit(Component.translatable("quest.tolkienmobs.complete").getString(), w),
                    x, foot - 1, INK_GREEN, FLAT);
            return;
        }
        String counter = story.progress() + " / " + story.goal();
        int counterW = this.font.width(counter);
        int barW = Math.max(0, w - counterW - 6);
        graphics.fill(x, foot, x + barW, foot + 4, TRACK);
        int filled = story.goal() <= 0 ? 0 : Math.round(barW * (story.progress() / (float) story.goal()));
        if (filled > 0) {
            graphics.fill(x, foot, x + filled, foot + 4, INK_BLUE);
        }
        graphics.text(this.font, counter, x + w - counterW, foot - 2, INK_DIM, FLAT);
    }

    /** The carried pages of the fronts, in the order the book keeps them. */
    private static List<Quest> storiesOf(List<Quest> quests) {
        List<Quest> stories = new ArrayList<>();
        for (Quest quest : quests) {
            if (quest.story()) stories.add(quest);
        }
        return stories;
    }

    /**
     * The page arrows and the count between them, written into the story leaf's heading row. A player can
     * hold a page from every front at once, and until now the leaf had no way to say so: it drew the first
     * one and stopped. The ends dim rather than vanish, so the leaf always says how many pages there are.
     */
    private void drawTurns(GuiGraphicsExtractor graphics, int turn, int count) {
        if (count < 2) return;
        String counter = (turn + 1) + " / " + count;
        int counterW = this.font.width(counter);
        int arrowW = this.font.width("<");
        int right = rightPageX + pageW - 14;
        int y = pageTop() + 8;
        int backX = right - counterW - arrowW * 2 - 8;
        boolean back = turn > 0;
        boolean forth = turn < count - 1;
        graphics.text(this.font, "<", backX, y, back ? INK : PAGE_EDGE, FLAT);
        graphics.text(this.font, counter, backX + arrowW + 4, y, INK_DIM, FLAT);
        graphics.text(this.font, ">", right - arrowW, y, forth ? INK : PAGE_EDGE, FLAT);
        turnSpots.add(new TurnSpot(-1, backX - 3, y - 2, arrowW + 6, LINE + 3));
        turnSpots.add(new TurnSpot(1, right - arrowW - 3, y - 2, arrowW + 6, LINE + 3));
    }

    /** Draws prose onto a leaf and answers the y it finished on, so what follows sits under it. */
    private int wrapped(GuiGraphicsExtractor graphics, int x, int y, int width, String text, int ink, int maxLines) {
        int lineY = y;
        for (String line : wrap(text, width, maxLines)) {
            graphics.text(this.font, line, x, lineY, ink, FLAT);
            lineY += LINE;
        }
        return lineY;
    }

    /** The picture a quest's entry leads with: the goods it wants, the country it serves, or a blade. */
    private ItemStack iconOf(Quest quest) {
        return switch (quest.kind()) {
            case FETCH -> quest.item() == null ? new ItemStack(Items.PAPER) : new ItemStack(quest.item());
            case FACTION -> new ItemStack(Alliance.byId(quest.target()).token());
            case HUNT -> new ItemStack(Items.IRON_SWORD);
            case LOCATION -> new ItemStack(Items.COMPASS);
            case DIALOGUE -> new ItemStack(Items.WRITABLE_BOOK);
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

    /**
     * The line at the foot of a leaf telling the player what to do with finished work. It is wrapped
     * rather than trimmed: it used to be cut to "Hand a finished quest to..." on every leaf, which is a
     * sentence that stops before it says anything.
     */
    private void drawHint(GuiGraphicsExtractor graphics, int pageX) {
        List<String> lines = wrap(hint().getString(), pageW - 16, 2);
        int y = pageBottom() - 6 - lines.size() * LINE;
        for (String line : lines) {
            graphics.text(this.font, line, pageX + (pageW - this.font.width(line)) / 2, y, INK_DIM, FLAT);
            y += LINE;
        }
    }

    /** How many lines the foot hint needs on this leaf, so the rows above it can keep out of the way. */
    private int hintLines() {
        return wrap(hint().getString(), pageW - 16, 2).size();
    }

    private Component hint() {
        return Component.translatable("screen.tolkienmobs.quests.hint");
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
                    playSound(TolkienSounds.LEDGER_PAGE.get(), 0.92F);
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
            for (TurnSpot spot : turnSpots) {
                if (event.x() < spot.x() || event.x() >= spot.x() + spot.w()) continue;
                if (event.y() < spot.y() || event.y() >= spot.y() + spot.h()) continue;
                this.storyTurn = Math.max(0, this.storyTurn + spot.dir());
                playSound(TolkienSounds.LEDGER_PAGE.get(), spot.dir() > 0 ? 1.02F : 0.98F);
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

    /** A page arrow on the story leaf: which way it turns, and where it was drawn. */
    private record TurnSpot(int dir, int x, int y, int w, int h) {
    }
}
