package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.TriState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/**
 * The mark over a giver's head, in the states an RPG has always used.
 * <p>
 * It is drawn as part of the nameplate rather than as a billboard of its own: a nameplate already sits
 * over the head, already turns to face the player and is already hidden behind walls and at distance,
 * so a mark written onto it inherits all of that without a texture or a second render pass. The mark
 * is the first thing on the plate, which reads as a glyph above the name.
 * <p>
 * Two things are said at once, and they are deliberately not the same kind of thing:
 * <ul>
 *   <li><b>The shape is the state.</b> <b>!</b> for work still to take, the same mark dimmed grey for
 *   work taken and unfinished - the same promise, dimmed - and <b>?</b> for work done and waiting to
 *   be handed in.</li>
 *   <li><b>The ink is the line.</b> Blue for an ordinary errand, gold for the mainline, so the
 *   campaign's own business is told apart from a farmer's fence without reading either.</li>
 * </ul>
 * A giver this player has no business with keeps a bare name, which is what makes the marks mean
 * something: an NPC that is always shouting has taught the player nothing by the time it matters.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class QuestMarkRenderer {

    private QuestMarkRenderer() {
    }

    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent.CanRender event) {
        QuestLedger.Mark mark = QuestLedger.clientMark(event.getEntity().getUUID());
        if (mark == null) return;
        Component name = event.getContent();
        Component glyph = glyph(mark);
        event.setContent(name == null ? glyph : glyph.copy().append(name));
        // A giver's plate is normally shown because its name is visible; the mark is worth showing even
        // where the name would be hidden, since the whole point is to be noticed.
        event.setCanRender(TriState.TRUE);
    }

    /**
     * The mark itself. Work in hand is grey whatever line it came from: at that point the player is
     * already carrying it and knows whose it is, and all the plate has to say is "back here".
     */
    private static Component glyph(QuestLedger.Mark mark) {
        ChatFormatting ink = switch (mark.state()) {
            case AVAILABLE, TURN_IN -> mark.mainline() ? ChatFormatting.GOLD : ChatFormatting.BLUE;
            case IN_PROGRESS -> ChatFormatting.GRAY;
        };
        String shape = mark.state() == QuestLedger.Mark.State.TURN_IN ? "? " : "! ";
        return Component.literal(shape).withStyle(ink);
    }
}
