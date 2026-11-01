package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.init.TolkienSounds;
import com.greatorator.tolkienmobs.network.manager.ConversationDoneManager;
import com.greatorator.tolkienmobs.network.manager.ConversationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.ArrayList;
import java.util.List;

/**
 * One page of the campaign, spoken rather than summarised.
 * <p>
 * The book says what a page asks of a player. It has never said what is said <em>to</em> them, and the
 * campaign script is written as conversation - a speaker, a handful of sentences, and two answers the
 * player can give back. This is the surface that reads it: a page of parchment with the speaker's name on
 * it, the words wrapped onto the leaf, and the replies as rows a player can click.
 * <p>
 * It is the same page the book is, in the same leather and the same ink, because it is the same object:
 * the herald hands over a page and the page is read before it is put in the ledger. The second half of the
 * conversation follows the answer that was given, and it is spoken rather than written down on the leaf,
 * which is why the leaf waits for a reply instead of printing all of it at once.
 * <p>
 * The two answers are voice rather than branch: the page's business is the same whichever is said, and the
 * speaker answers both in the same breath. A page that branches can hang its keys off a reply instead, and
 * this screen does not care which.
 * <p>
 * Two other ways it opens are the same leaf with less on it. A page already taken is opened again to be
 * read, and it opens <em>answered</em>: the last thing the speaker said and the row that puts the page away,
 * because a player who has read it once is not being asked the same question twice. A herald who will not
 * hand his country's work over says so here too, on a leaf headed with his answer and holding one paragraph
 * and the same close row - a refusal is a thing said, and a chat line that scrolls away is not saying it.
 */
public class ConversationScreen extends Screen {
    /** The leaf's largest size in GUI pixels; it shrinks to fit a small window rather than overflow. */
    private static final int MAX_W = 300;
    private static final int MAX_H = 190;
    /** The leather showing around the leaf. */
    private static final int COVER = 9;
    /** The height of one line of type. */
    private static final int LINE = 9;
    /** The gap between two replies, and the indent the reply text sits at. */
    private static final int REPLY_GAP = 6;
    private static final int REPLY_INDENT = 8;
    /** How many answers a page can offer. The script never asks for more than two. */
    private static final int REPLIES = 2;

    private static final int LEATHER = 0xFF4A2F1B;
    private static final int LEATHER_DARK = 0xFF241408;
    private static final int PAGE = 0xFFE9D8B4;
    private static final int PAGE_EDGE = 0xFFC6AC83;
    private static final int INK = 0xFF35240F;
    private static final int INK_DIM = 0xFF6B5535;
    private static final int INK_GOLD = 0xFF7A4A16;
    private static final int INK_BLUE = 0xFF3F6B7C;
    private static final int INK_RED = 0xFF8C2F24;
    /** The wash under a row the mouse is over, faint enough to read the ink through. */
    private static final int HOVER = 0x2A7A4A16;

    /** No shadow: this is ink on parchment, not a nameplate over the world. */
    private static final boolean FLAT = false;

    /** The key prefix a page's words hang off, as {@code campaign.tolkienmobs.page.muster}. */
    private final String page;
    /** How the leaf was opened: a page handed over, a page re-read, or a refusal. */
    private final ConversationManager.Mode mode;
    /** The key the leaf's own words hang off, which is the page's or the refusal's. */
    private final String words;
    /** The leaf a refusal is written on, shared by the three heralds, who are one office. */
    private static final String REFUSED = "campaign.tolkienmobs.talk.refused";
    /** Who is speaking, as the body that handed the page is named in the world. */
    private final String speaker;

    /** Which reply was given, or -1 while the leaf is still waiting for one. */
    private int chosen;

    private int bookX;
    private int bookY;
    private int bookW;
    private int bookH;

    /** Where each reply was drawn this frame, so a click can be matched to it. */
    private final List<Spot> replySpots = new ArrayList<>();
    /** Where the close row was drawn, once the page has been answered. */
    private Spot closeSpot;

    public ConversationScreen(String speaker, String page, ConversationManager.Mode mode) {
        super(Component.translatable(mode == ConversationManager.Mode.REFUSED ? REFUSED : page));
        this.speaker = speaker;
        this.page = page;
        this.mode = mode;
        this.words = mode == ConversationManager.Mode.REFUSED ? REFUSED : page;
        // A page opened to be read again is already at its last line; a fresh one and a refusal both
        // start at the top, and only the fresh one is waiting for an answer.
        this.chosen = mode == ConversationManager.Mode.ANSWERED ? 0 : -1;
    }

    // ---- the leaf ----

    /** Measures the leaf against the window, so a small GUI gets a smaller page rather than a clipped one. */
    private void layout() {
        this.bookW = Math.min(MAX_W, this.width - 16);
        this.bookH = Math.min(MAX_H, this.height - 24);
        this.bookX = (this.width - this.bookW) / 2;
        this.bookY = (this.height - this.bookH) / 2;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        layout();
        replySpots.clear();
        closeSpot = null;

        graphics.fill(bookX, bookY, bookX + bookW, bookY + bookH, LEATHER);
        graphics.outline(bookX, bookY, bookW, bookH, LEATHER_DARK);
        int pageX = bookX + COVER;
        int pageY = bookY + COVER;
        int pageW = bookW - COVER * 2;
        int pageH = bookH - COVER * 2;
        graphics.fill(pageX, pageY, pageX + pageW, pageY + pageH, PAGE);
        graphics.outline(pageX, pageY, pageW, pageH, PAGE_EDGE);

        int x = pageX + 12;
        int w = pageW - 24;
        // What the step is called, centred, and the rule under it: the page is the same page the book
        // holds, so it is headed the same way.
        String title = fit(Component.translatable(this.words).getString(), w);
        graphics.text(this.font, title, x + (w - this.font.width(title)) / 2, pageY + 9, INK_GOLD, FLAT);
        graphics.fill(pageX + 12, pageY + 20, pageX + pageW - 12, pageY + 21, PAGE_EDGE);

        int y = pageY + 26;
        graphics.text(this.font, fit(speaker, w), x, y, INK_RED, FLAT);
        y += LINE + 5;

        int foot = pageY + pageH - 12;
        // A refusal has nothing to answer to: the herald has already said what he has to say, so the
        // leaf shows his answer and the row that puts it away.
        boolean answered = this.mode == ConversationManager.Mode.REFUSED || this.chosen >= 0;
        if (!answered) {
            // The opening waits for an answer, so the replies are laid out first and the words are
            // wrapped into what is left above them.
            int replyTop = foot - REPLIES * (LINE + REPLY_GAP);
            int room = Math.max(1, (replyTop - y) / LINE);
            int first = Math.max(1, room / 2);
            y = wrapped(graphics, x, y, w, key(".say.1"), INK, first) + 4;
            wrapped(graphics, x, y, w, key(".say.2"), INK, Math.max(1, room - first));
            for (int i = 0; i < REPLIES; i++) {
                drawReply(graphics, x, replyTop + i * (LINE + REPLY_GAP), w, i, mouseX, mouseY);
            }
            return;
        }

        // What he says next goes on the leaf the answer was given on, and the row under it puts the page
        // away: the conversation is over once it says so, not when the player guesses to press Escape.
        wrapped(graphics, x, y, w, key(".said"), INK, Math.max(1, (foot - y) / LINE));
        drawClose(graphics, x, foot, w, mouseX, mouseY);
    }

    /** The text of one of this leaf's keys. */
    private String key(String suffix) {
        return Component.translatable(this.words + suffix).getString();
    }

    private void drawReply(GuiGraphicsExtractor graphics, int x, int y, int w, int index, int mouseX, int mouseY) {
        boolean over = mouseX >= x - 2 && mouseX < x + w && mouseY >= y - 2 && mouseY < y + LINE + 1;
        if (over) {
            graphics.fill(x - 2, y - 2, x + w + 2, y + LINE + 1, HOVER);
        }
        String text = fit(key(".reply." + (index + 1)), w - REPLY_INDENT);
        graphics.text(this.font, ">", x, y, INK_DIM, FLAT);
        graphics.text(this.font, text, x + REPLY_INDENT, y, over ? INK_RED : INK_BLUE, FLAT);
        replySpots.add(new Spot(index, x - 2, y - 2, w + 4, LINE + 3));
    }

    private void drawClose(GuiGraphicsExtractor graphics, int x, int y, int w, int mouseX, int mouseY) {
        boolean over = mouseX >= x - 2 && mouseX < x + w && mouseY >= y - 2 && mouseY < y + LINE + 1;
        if (over) {
            graphics.fill(x - 2, y - 2, x + w + 2, y + LINE + 1, HOVER);
        }
        String text = fit(Component.translatable("campaign.tolkienmobs.talk.close").getString(), w - REPLY_INDENT);
        graphics.text(this.font, ">", x, y, INK_DIM, FLAT);
        graphics.text(this.font, text, x + REPLY_INDENT, y, over ? INK_RED : INK_DIM, FLAT);
        closeSpot = new Spot(-1, x - 2, y - 2, w + 4, LINE + 3);
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

    /** Wraps a sentence onto the leaf, fitting what is left of the last line rather than running off it. */
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

    /** Draws a paragraph onto the leaf and answers the y it finished on, so what follows sits under it. */
    private int wrapped(GuiGraphicsExtractor graphics, int x, int y, int width, String text, int ink, int maxLines) {
        int lineY = y;
        for (String line : wrap(text, width, Math.max(1, maxLines))) {
            graphics.text(this.font, line, x, lineY, ink, FLAT);
            lineY += LINE;
        }
        return lineY;
    }

    // ---- input ----

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (Spot spot : replySpots) {
                if (!spot.holds(event)) continue;
                this.chosen = spot.index();
                playSound(TolkienSounds.LEDGER_PAGE.get(), 1.0F);
                return true;
            }
            if (closeSpot != null && closeSpot.holds(event)) {
                playSound(TolkienSounds.LEDGER_CLOSE.get(), 0.95F);
                this.onClose();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    /**
     * Plays one of the book's own sounds on this client. A screen cannot ask the server for one - a page
     * has no place in the world to be heard from - so the client that is looking at the page plays it.
     */
    private static void playSound(SoundEvent sound, float pitch) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, 0.55F));
    }

    @Override
    public void onClose() {
        // The leaf says which page of talk it was on the way out. A dialogue page in the book is
        // finished by hearing the words out, and this is the only moment the client knows that
        // happened - the words themselves never leave the client.
        // Only a page handed over can be finished by reading it: a re-read was finished long ago, and
        // a refusal is not work at all.
        if (this.mode == ConversationManager.Mode.OPEN && this.chosen >= 0 && !this.page.isEmpty()) {
            ClientPacketDistributor.sendToServer(new ConversationDoneManager(this.page));
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** A row as it was drawn: which reply, in screen coordinates, so a click can be matched to it. */
    private record Spot(int index, int x, int y, int w, int h) {
        boolean holds(MouseButtonEvent event) {
            return event.x() >= x && event.x() < x + w && event.y() >= y && event.y() < y + h;
        }
    }
}
