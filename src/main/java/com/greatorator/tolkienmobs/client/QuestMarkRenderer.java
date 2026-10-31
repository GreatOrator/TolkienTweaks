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
 * The mark over a giver's head, in the three states an RPG has always used.
 * <p>
 * It is drawn as part of the nameplate rather than as a billboard of its own: a nameplate already sits
 * over the head, already turns to face the player and is already hidden behind walls and at distance,
 * so a mark written onto it inherits all of that without a texture or a second render pass. The mark
 * is the first thing on the plate, which reads as a glyph above the name.
 * <p>
 * Three states, all of them per player and all of them read from the quest sync:
 * <ul>
 *   <li><b>!</b> in gold - there is work here you have not taken.</li>
 *   <li><b>!</b> in grey - you have taken work here and it is not finished. It is deliberately the
 *   same shape as the gold one so it reads as the same promise, dimmed: the errand is still yours.</li>
 *   <li><b>?</b> in green - the work is done and this giver will take it.</li>
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

    private static Component glyph(QuestLedger.Mark mark) {
        return switch (mark) {
            case AVAILABLE -> Component.literal("! ").withStyle(ChatFormatting.GOLD);
            case IN_PROGRESS -> Component.literal("! ").withStyle(ChatFormatting.GRAY);
            case TURN_IN -> Component.literal("? ").withStyle(ChatFormatting.GREEN);
        };
    }
}
