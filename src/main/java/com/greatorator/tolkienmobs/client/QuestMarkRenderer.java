package com.greatorator.tolkienmobs.client;

import com.greatorator.tolkienmobs.entity.npc.HumanEntity;
import com.greatorator.tolkienmobs.faction.Alliance;
import com.greatorator.tolkienmobs.faction.Campaign;
import com.greatorator.tolkienmobs.faction.FactionRank;
import com.greatorator.tolkienmobs.faction.FactionStandings;
import com.greatorator.tolkienmobs.faction.Quest;
import com.greatorator.tolkienmobs.faction.QuestGivers;
import com.greatorator.tolkienmobs.faction.QuestLedger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.TriState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderNameTagEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
        Alliance herald = heraldAlliance(entity);
        QuestLedger.Mark mark = QuestLedger.clientMark(entity.getUUID());
        if (mark == null) {
            // The quest sync is where a mark comes from, and a mark can be missing from it - a page taken
            // before the sync carried the giver's id, or a copy written into the book by a command. What
            // the player is holding is the same answer and is always here, so the book is read as well.
            mark = markFromBook(entity.getUUID(), herald);
        }
        // A hall herald hands his page over to whoever asks for it, so what his plate says is what he
        // has, not what the player has earned: a mark held back until a rank is reached told a player
        // standing in front of a body holding work that there was nothing there, and the page handed over
        // a moment later proved the plate wrong. The mark still stops when the front has nothing left to
        // give, which is the one time he really has nothing to say.
        if (herald != null) {
            if (mark == null && hasEarOf(herald) && Campaign.clientHasWork(herald)) {
                mark = new QuestLedger.Mark(QuestLedger.Mark.State.AVAILABLE, true);
            }
            Component plate = withOffice(event.getContent(), herald);
            if (mark == null) {
                // Nothing to mark, so the plate keeps the visibility it would have had: his office is
                // worth reading at the distance a name is, and not one block further.
                event.setContent(plate);
                return;
            }
            event.setContent(glyph(mark).append(plate));
            event.setCanRender(TriState.TRUE);
            return;
        }
        if (mark == null) return;
        Component name = event.getContent();
        event.setContent(name == null ? glyph(mark) : glyph(mark).append(name));
        // A giver's plate is normally shown because its name is visible; the mark is worth showing even
        // where the name would be hidden, since the whole point is to be noticed.
        event.setCanRender(TriState.TRUE);
    }

    /**
     * His name with the office he holds beside it - Maelgrin, Herald of the West - in the grey a subtitle
     * is drawn in, because the name is the man and the office is what he is standing there for. A herald
     * without a name yet still says what he is, so nothing is left where a title should be.
     */
    private static Component withOffice(@Nullable Component name, Alliance herald) {
        Component office = Component.literal(" \u00b7 ").withStyle(ChatFormatting.GRAY)
                .append(Component.translatable("campaign.tolkienmobs.giver.title." + herald.id())
                        .withStyle(ChatFormatting.GRAY));
        return name == null ? office : name.copy().append(office);
    }

    /**
     * The alliance whose herald this entity is dressed as, or null for anything that is not one.
     * <p>
     * The clothes are the first answer: a giver's alliance is copied into synched data on the server so
     * a renderer on the far side can read it. That copy can reach a client as nothing - a body standing
     * in a hall placed from a template is not written by the code that sets it until its first tick - and
     * a herald read as an ordinary man lost his office and his mark while keeping the gold name the server
     * had already given him, which is exactly backwards: he was the only body the miss could not hide.
     * His name is the second answer, because a hall's giver is named for his front and a name travels with
     * the same body however the clothes are synced.
     */
    @Nullable
    private static Alliance heraldAlliance(Entity entity) {
        if (!(entity instanceof HumanEntity human)) return null;
        int skin = human.giverSkin();
        Alliance[] alliances = Alliance.values();
        if (skin > 0 && skin <= alliances.length) return alliances[skin - 1];
        Component name = human.getCustomName();
        if (name == null) return null;
        for (Alliance alliance : alliances) {
            if (QuestGivers.campaignName(alliance).equals(name.getString())) return alliance;
        }
        return null;
    }

    /**
     * The mark read off the player's own book: a body they are carrying work from still has something to
     * say, and it says it in the same three states the sync would have sent.
     * <p>
     * The giver's id is the exact answer. Failing that, a page of a front belongs to that front's herald
     * whoever handed it over and whether or not it remembers an id, and he is the body standing in its
     * hall - so the page in hand is his business, and a white name over a body whose page the player is
     * already carrying is the one reading that is never true.
     */
    @Nullable
    private static QuestLedger.Mark markFromBook(UUID giver, @Nullable Alliance herald) {
        for (Quest quest : QuestLedger.clientQuests()) {
            if (giver.equals(quest.giverId())) return markOf(quest);
        }
        if (herald == null) return null;
        for (Quest quest : QuestLedger.clientQuests()) {
            if (quest.story() && quest.employerAlliance() == herald) return markOf(quest);
        }
        return null;
    }

    /** What the plate says about one quest in hand. */
    private static QuestLedger.Mark markOf(Quest quest) {
        return new QuestLedger.Mark(quest.complete() ? QuestLedger.Mark.State.TURN_IN
                : QuestLedger.Mark.State.IN_PROGRESS, quest.story());
    }

    /**
     * Whether the country will deal with this player at all: the first rank above outsider. The plate and
     * the hand-over ask it of the same number, which is the point of asking it here - a mark offering work
     * the body will refuse is a plate that lies, and one keeping quiet about work it would hand over is a
     * plate that hides. Work already in hand is marked whoever carries it.
     */
    private static boolean hasEarOf(Alliance alliance) {
        return FactionStandings.clientStanding(alliance) >= FactionRank.STRANGER.threshold();
    }

    /**
     * The mark itself. Work in hand is grey whatever line it came from: at that point the player is
     * already carrying it and knows whose it is, and all the plate has to say is "back here".
     */
    private static MutableComponent glyph(QuestLedger.Mark mark) {
        ChatFormatting ink = switch (mark.state()) {
            case AVAILABLE, TURN_IN -> mark.mainline() ? ChatFormatting.GOLD : ChatFormatting.BLUE;
            case IN_PROGRESS -> ChatFormatting.GRAY;
        };
        String shape = mark.state() == QuestLedger.Mark.State.TURN_IN ? "? " : "! ";
        return Component.literal(shape).withStyle(ink);
    }
}
