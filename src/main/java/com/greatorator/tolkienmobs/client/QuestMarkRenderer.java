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
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
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
 * so a mark hung on it inherits all of that without a render pass of its own. The marks themselves are
 * the mod's own artwork, one bitmap glyph an icon in a font of their own, so what the plate draws is
 * still a letter - the font is simply what paints it. The mark is the first thing on the plate, which
 * reads as a mark above the name.
 * <p>
 * Two things are said at once, and they are deliberately not the same kind of thing:
 * <ul>
 *   <li><b>The shape is the state.</b> <b>!</b> for work still to take, the same shape in white for work
 *   taken and unfinished - the same promise, without the colour - and <b>?</b> for work done and waiting
 *   to be handed in.</li>
 *   <li><b>The ink is the line.</b> Cyan for an ordinary errand, gold for the mainline, so the campaign's
 *   own business is told apart from a farmer's fence without reading either.</li>
 * </ul>
 * A giver this player has no business with keeps a bare name, which is what makes the marks mean
 * something: an NPC that is always shouting has taught the player nothing by the time it matters.
 */
@EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class QuestMarkRenderer {

    /** The font the marks were cut into, one glyph an icon: the artwork in the plate without a render pass. */
    private static final FontDescription MARK_FONT = new FontDescription.Resource(
            Identifier.fromNamespaceAndPath(MODID, "quest_marks"));
    private static final String AVAILABLE_ERRAND = "\ue000";
    private static final String AVAILABLE_MAIN = "\ue001";
    private static final String IN_PROGRESS = "\ue002";
    private static final String TURN_IN_ERRAND = "\ue003";
    private static final String TURN_IN_MAIN = "\ue004";

    private QuestMarkRenderer() {
    }

    @SubscribeEvent
    public static void onNameTag(RenderNameTagEvent.CanRender event) {
        Entity entity = event.getEntity();
        Alliance herald = heraldAlliance(entity);
        // The quest sync is where a mark comes from, and a mark can be missing from it - a page taken
        // before the sync carried the giver's id, or a copy written into the book by a command. What
        // the player is holding is the same answer and is always here, so the book is read as well.
        QuestLedger.Mark mark = markAt(entity, herald);
        // A hall herald hands his page over to whoever asks for it, so what his plate says is what he
        // has, not what the player has earned: a mark held back until a rank is reached told a player
        // standing in front of a body holding work that there was nothing there, and the page handed over
        // a moment later proved the plate wrong. The mark still stops when the front has nothing left to
        // give, which is the one time he really has nothing to say.
        if (herald != null) {
            if (mark == null && hasEarOf(herald) && Campaign.clientHasWork(herald)
                    && holdsNextPage(entity, herald)) {
                mark = new QuestLedger.Mark(QuestLedger.Mark.State.AVAILABLE, true);
            }
            Component plate = withOffice(event.getContent(), herald);
            if (mark == null) {
                // Nothing to mark, so the plate keeps the visibility it would have had: his office is
                // worth reading at the distance a name is, and not one block further.
                event.setContent(plate);
                return;
            }
            event.setContent(withMark(mark, plate));
            event.setCanRender(TriState.TRUE);
            return;
        }
        if (mark == null) return;
        event.setContent(withMark(mark, event.getContent()));
        // A giver's plate is normally shown because its name is visible; the mark is worth showing even
        // where the name would be hidden, since the whole point is to be noticed.
        event.setCanRender(TriState.TRUE);
    }

    /**
     * Whether this body is waiting for a page back - the one state the badge over a giver's head is for.
     * <p>
     * Asked through the same call the plate asks, so the badge and the mark cannot disagree about it.
     */
    public static boolean asksForReturn(Entity entity) {
        QuestLedger.Mark mark = markAt(entity, heraldAlliance(entity));
        return mark != null && mark.state() == QuestLedger.Mark.State.TURN_IN;
    }

    /** The mark a body wears for this player: the sync's answer, or the player's own book when the sync has none. */
    @Nullable
    private static QuestLedger.Mark markAt(Entity entity, @Nullable Alliance herald) {
        QuestLedger.Mark mark = QuestLedger.clientMark(entity.getUUID());
        return mark != null ? mark : markFromBook(entity, herald);
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
    private static QuestLedger.Mark markFromBook(Entity entity, @Nullable Alliance herald) {
        UUID giver = entity.getUUID();
        for (Quest quest : QuestLedger.clientQuests()) {
            if (giver.equals(quest.giverId())) return markOf(quest);
        }
        // A page that names the body it goes back to is that body's to take and nobody else's: the Elf
        // it is owed to wears the question mark wherever he is standing, since the sync only marks the
        // bodies it could see when the book was sent.
        String name = entity.getName().getString();
        for (Quest quest : QuestLedger.clientQuests()) {
            if (quest.complete() && name.equalsIgnoreCase(quest.turnIn())) {
                return new QuestLedger.Mark(QuestLedger.Mark.State.TURN_IN, quest.story());
            }
        }
        if (herald == null) return null;
        for (Quest quest : QuestLedger.clientQuests()) {
            // The herald's fallback is for work any giver will pay. A page owed to a named body is not
            // his to take whichever front it belongs to, and reading it as his put a gold question mark
            // over Elrond for a Wood-troll kill that is Legolas' to take - the one body who could not
            // pay it wearing the mark that says he would.
            if (quest.turnIn().isEmpty() && quest.story() && quest.employerAlliance() == herald) {
                return markOf(quest);
            }
        }
        return null;
    }

    /**
     * Whether the body in front of the player is the one the line says holds this front's next page.
     * <p>
     * A speaker of the campaign wears the same clothes and the same tags as a hall's herald, because the
     * right-click they answer is the same one, so the name is what tells them apart - and the name is on
     * the line the client already has. Read by the clothes alone, every Elf of the valley advertised work
     * in the herald's name, and Elrond stood in Rivendell offering a page that was Legolas' to hand. Read
     * from the line, exactly one body wears the mark, and it is the body the page is waiting at.
     */
    private static boolean holdsNextPage(Entity entity, Alliance herald) {
        Component name = entity.getCustomName();
        return name != null && name.getString().equalsIgnoreCase(Campaign.clientHolder(herald));
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
     * The mark itself, as the icon the artwork draws for it. Work in hand is white whatever line it came
     * from: at that point the player is already carrying it and knows whose it is, and all the plate has to
     * say is "back here", which is the one thing the colour of the line would not add to.
     */
    private static MutableComponent glyph(QuestLedger.Mark mark) {
        String icon = switch (mark.state()) {
            case AVAILABLE -> mark.mainline() ? AVAILABLE_MAIN : AVAILABLE_ERRAND;
            case IN_PROGRESS -> IN_PROGRESS;
            case TURN_IN -> mark.mainline() ? TURN_IN_MAIN : TURN_IN_ERRAND;
        };
        return Component.literal(icon).withStyle(style -> style.withFont(MARK_FONT));
    }

    /**
     * The mark, a space, and the plate it belongs to.
     * <p>
     * The space and the plate hang off an empty parent rather than off the icon, because a component takes
     * the style of the component it is appended to and not of the sibling before it - appended to the icon
     * they would inherit the mark font and draw every letter of the name as a missing glyph.
     */
    private static MutableComponent withMark(QuestLedger.Mark mark, @Nullable Component plate) {
        MutableComponent line = Component.empty().append(glyph(mark));
        if (plate != null) {
            line.append(" ").append(plate);
        }
        return line;
    }
}
