package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.Campaign;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.TriState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import org.jetbrains.annotations.Nullable;

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
        Entity entity = event.getEntity();
        QuestLedger.Mark mark = QuestLedger.clientMark(entity.getUUID());
        // The mainline does not call on a stranger: a hall herald keeps a bare name until the country
        // knows the player, which is the first rank above outsider. Once it does he shows the gold mark
        // even with no page behind it, because that mark is the campaign saying it has somewhere to start.
        Alliance herald = heraldAlliance(entity);
        if (herald != null && !hasEarOf(herald)) return;
        if (mark == null) {
            if (herald == null || !Campaign.clientHasWork(herald)) return;
            mark = new QuestLedger.Mark(QuestLedger.Mark.State.AVAILABLE, true);
        }
        Component name = event.getContent();
        Component glyph = glyph(mark);
        event.setContent(name == null ? glyph : glyph.copy().append(name));
        // A giver's plate is normally shown because its name is visible; the mark is worth showing even
        // where the name would be hidden, since the whole point is to be noticed.
        event.setCanRender(TriState.TRUE);
    }

    /** The alliance whose herald this entity is dressed as, or null for anything that is not one. */
    @Nullable
    private static Alliance heraldAlliance(Entity entity) {
        if (!(entity instanceof HumanEntity human)) return null;
        int skin = human.giverSkin();
        Alliance[] alliances = Alliance.values();
        return skin > 0 && skin <= alliances.length ? alliances[skin - 1] : null;
    }

    /** Whether the player has earned the ear of an alliance: its first rank above outsider. */
    private static boolean hasEarOf(Alliance alliance) {
        return FactionStandings.clientStanding(alliance) >= FactionRank.STRANGER.threshold();
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
