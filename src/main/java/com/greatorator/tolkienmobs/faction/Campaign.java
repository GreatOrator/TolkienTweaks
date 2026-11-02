package com.greatorator.tolkienmobs.faction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.greatorator.tolkienmobs.entity.boss.WitchKingEntity;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.init.TolkienItems;
import com.greatorator.tolkienmobs.network.manager.CampaignSyncManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.ai.goal.FleeSunGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RestrictSunGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * How far a player has got on each of the three fronts, which is the one thing the mod was missing to
 * make its quests a campaign rather than a handful of good errands.
 * <p>
 * A front is a numbered line of pages. The page for a stage is not random - it is written out below,
 * because the line's business is the campaign's and not a farmer's fence - and the herald standing in
 * that alliance's hall is the only body who hands one over. The stage moves on only when the ledger
 * actually pays the page off: {@link #settle} is handed the quest the ledger just settled and compares
 * it against the page this front handed out, so a page dropped from the book, or a command clearing it,
 * leaves the front exactly where it was.
 * <p>
 * The Rider is the one page that is not a front's. He belongs to nobody and everybody fears him, so all
 * three countries ask for him together, and only once all three lines are settled: one step for the
 * campaign as a whole, kept in a fourth slot beside the three fronts. That is also what gives the thing
 * an ending - a campaign whose every page belongs to a front has no way to be over.
 * <p>
 * The stage is synced to its owner because the mark over the herald's head is drawn on the client, and
 * that mark has to stop when nothing is left to ask.
 */
public final class Campaign extends SavedData {
    private static final Logger LOGGER = LoggerFactory.getLogger(Campaign.class);

    private static final String SAVE_DATA_NAME = "campaign";
    /**
     * What each page of a front asks for, in order: soldiers of one rival army, then soldiers of the
     * other, and then the front's own errand - a beast of its own country, or a scroll off the enemy's
     * road - which is the page that stops the three lines reading as one line repainted. The list is the
     * line - a stage past its end is a front with nothing left to hand over - so
     * writing one more page here is all the campaign needs to go on, and a front has as many pages as
     * this list is long.
     * <p>
     * The courage a page pays rises along the line, from the first muster to the front's own errand:
     * the third page is that country's business rather than a rival's soldiers, and it is worth
     * what the walk to the barrows or the sand is worth.
     */
    /**
     * The stone's two askings, in the order the old campaign had them.
     * <p>
     * The first is one landmark: the beryl is held aloft at the Ford of Bruinen, the last ground anyone
     * knows the missing Rider touched, and carried back to Glorfindel. The second is the search that
     * follows it - caves, which in the old world were three named and pointed at, and here are any three
     * with enough rock over their heads, asked in the deepest chamber of each because that is where a hurt
     * thing would have gone to ground. One cave is one answer and the distance is wide, since two mouths
     * of one system are still one cave.
     */
    private static final String FORD_PLACE = "ford";
    private static final String FORD_TARGET = Quest.placeTarget(FORD_PLACE);
    private static final int STONE_CAVES = 3;
    private static final int STONE_APART = 64;
    /** How much world has to stand over a player's head before the stone agrees it is in a cave. */
    private static final int CAVE_COVER = 8;
    /** And how much before it agrees it is in the deepest chamber rather than at the mouth of one. */
    private static final int CAVE_DEEP = 16;
    /** How many cave readings are written. The cave's own blocks pick which, so a cave keeps its answer. */
    private static final int CAVE_READINGS = 5;
    /**
     * The three caves the notes name, in the order the page asks them. The old campaign gave each cave
     * its own name, its own quest and its own token, and the names are kept here: a player holding a
     * stone up in the dark wants to know which cave they are standing at the bottom of.
     */
    private static final List<String> CAVE_NAMES = List.of("Bandrith", "Hoklond", "Sholdroth");
    /**
     * What the stone says in one of those named caves. It stays dark, and that is the answer rather than
     * the failure: the notes promise the beryl will say where he has not been as well as where he has.
     */
    private static final String STONE_DARK = "campaign.tolkienmobs.cave.dark";
    /**
     * The Elf the stone's work belongs to, the valley the map keeps for him, and the tag the body who
     * hands the work wears. The valley is a name of the catalogue rather than a building: nothing in the
     * pack has ever built Rivendell, and a named place is ground the map already knows how to put where
     * the books put it.
     */
    private static final String GLORFINDEL = "Glorfindel";
    private static final String SUMMON_PLACE = "rivendell";
    private static final String GLORFINDEL_TAG = "tolkienmobs:glorfindel";
    /** How near the valley a player has to be for him to be standing in it, in blocks. */
    private static final double GLORFINDEL_REACH = 128.0D;
    /**
     * How far from its written step a keeper may be stood, in blocks. A step is paving only if the ground
     * is where the survey said; a wood, a rock or a laid square a pace off is enough to lose the spot.
     */
    private static final int KEEPER_SEARCH = 6;
    /**
     * Elladan, and the camp of Thorenhad he keeps in the Trollshaws. The beryl changes hands at the end of
     * the cave search rather than at the start: the notes have Elladan take the stone back to Glorfindel
     * himself once the three caves have been asked, and it is he who sends the player on to the next lead.
     * He stands while the cave page is the step in hand, which is the page his camp is the end of.
     */
    private static final String ELLADAN = "Elladan";
    private static final String THORENHAD_PLACE = "thorenhad";
    private static final String ELLADAN_TAG = "tolkienmobs:elladan";
    private static final double ELLADAN_REACH = 128.0D;
    /** How wide a circle Elladan's camp is, in blocks: enough to walk into from any side of it. */
    private static final int CAMP_RADIUS = 48;
    /** The tent the camp is: the same template the Rangers' camp errand pitches, laid down at Thorenhad. */
    private static final String CAMP_TENT = Sites.WILD_TENT;
    /**
     * Barachen, and the camp of his the notes send the player to once the caves have answered nothing.
     * He is the West's scout rather than an Elf, and his word is what turns the line from a search of
     * the dark into a Giant somebody saw: the report is his to give, and the page walks to his fire.
     * The camp is a named place like Thorenhad - a clearing and a fire, not a building the pack would
     * have to raise - and it is canon to the Trollshaws so it lands south of the road the notes name
     * rather than wherever the page happened to be taken.
     */
    private static final String BARACHEN = "Barachen";
    private static final String BARACHEN_PLACE = "barachen";
    private static final String BARACHEN_TAG = "tolkienmobs:barachen";
    /** How near his fire a player has to be for him to be standing at it, in blocks. */
    private static final double BARACHEN_REACH = 128.0D;
    /**
     * The Giant the scout saw. The notes call him a Giant and then have Elrond answer that the player could
     * not have faced a true Onod, which is the pack's tree-ent exactly: a creature standing among the trees
     * as tall as they are. The page is a hunt, so the campaign stands the quarry up ahead of the player the
     * way it does for any other hunt, and the camp takes it back once the kill is made.
     */
    private static final String GIANT_TARGET = "tolkienmobs:entityttmtreeent";
    /**
     * Elrond, who the line comes back to once the Giant is dead: the notes have him name what the creature
     * was and arrange the audience with the company from Mirkwood. He waits in the valley the map already
     * keeps for the Elves - a place rather than a building, so nothing has to be raised for him - and the
     * page that walks there is titled after his library, which is how he is found again off the line.
     */
    private static final String ELROND = "Elrond";
    private static final String ELROND_TAG = "tolkienmobs:elrond";
    private static final double VALLEY_REACH = 128.0D;
    /** Legolas, who Elrond sends the player to: the guest room in the same valley. */
    private static final String LEGOLAS = "Legolas";
    private static final String LEGOLAS_TAG = "tolkienmobs:legolas";
    private static final String LEGOLAS_TITLE = "campaign.tolkienmobs.page.legolas";
    /** The Elves who walk the valley's square, who are ordinary bodies rather than givers. */
    private static final String VALLEY_ELF_TAG = "tolkienmobs:valley_elf";
    private static final int VALLEY_ELVES = 6;
    /** How far from the middle of the square the valley's Elves are allowed to stroll. */
    private static final int VALLEY_HOME = 26;
    private static final int[][] VALLEY_PATHS = {
            {-12, 4}, {12, 4}, {4, -10}, {-4, 10}, {14, -6}, {-14, -6}};

    /**
     * One piece of the valley: its template under {@code tolkienmobs:key_structures/}, and where the middle
     * of its footprint sits relative to the middle of the place.
     * <p>
     * The offsets are written down rather than chosen, so that two players in two worlds walk into the same
     * Rivendell: the square at the middle, the library off its western side, the guest house north of it,
     * the second house east, and the gate on the southern road with a banner either side of it. The lamps
     * stand at the square's four corners, which is where its roads leave it.
     */
    private record Part(String name, int dx, int dz) {
    }

    private static final List<Part> VALLEY_PARTS = List.of(
            new Part("elven_square", 0, 0),
            new Part("elven_library", -48, 0),
            new Part("elven_house_1", -6, -26),
            new Part("elven_house_2", 32, 0),
            new Part("elven_gate", 0, 23),
            new Part("elven_lamp", -17, -14),
            new Part("elven_lamp", 16, -14),
            new Part("elven_lamp", -17, 14),
            new Part("elven_lamp", 16, 14),
            new Part("elven_banner", -25, 23),
            new Part("elven_banner", 25, 23));

    /**
     * Where each of the three Elves of the line stands, as a step from the middle of the square: Glorfindel
     * out on its southern road where the stone is asked, Elrond at the door of the library he keeps, and
     * Legolas at the door of the guest house. The roads are the square's, so a stand point is paving rather
     * than the inside of a wall.
     */
    private static final Map<String, int[]> VALLEY_STANDS = Map.of(
            GLORFINDEL_TAG, new int[]{0, 8},
            ELROND_TAG, new int[]{-18, 0},
            LEGOLAS_TAG, new int[]{-6, -15});
    /**
     * The Wood-troll Legolas names, and the tag of the one body his page is about.
     * <p>
     * The page's target is a name rather than an entity type, because the pack ships no wood-troll: a
     * hunt of the cave troll would be settled by any troll in Eriador, and the notes' page is about one
     * creature that has a name. An id no registry answers prints in the book as the name it is, and the
     * kill is attributed to the tagged body alone (see {@link #namesBody}).
     */
    private static final String MATUNA = "Matuna";
    private static final String MATUNA_TAG = "tolkienmobs:campaign_matuna";
    private static final String MATUNA_NAME = "campaign.tolkienmobs.matuna.name";
    private static final String MATUNA_TRACKS = "campaign.tolkienmobs.matuna.tracks";
    private static final String MATUNA_TITLE = "campaign.tolkienmobs.page.matuna";
    private static final String MATUNA_CLOSING = "campaign.tolkienmobs.page.matuna.closing";
    /** The notes' own price on the kill, paid in the coin the old campaign paid in, and its experience. */
    private static final int MATUNA_SILVER = 8;
    private static final int MATUNA_BRONZE = 40;
    private static final int MATUNA_XP = 50;
    /** The notes' last word on the creature: the tidings carried back to the Elf who asked for them. */
    private static final String DEPARTURE_TITLE = "campaign.tolkienmobs.page.departure";
    private static final String DEPARTURE_PLACE = "campaign.tolkienmobs.page.departure.place";
    private static final String DEPARTURE_CLOSING = "campaign.tolkienmobs.page.departure.closing";
    /**
     * The notes' Missing Rider: the hoofprints south of the road, the ruins in the narrow channels of the
     * Trollshaws, and the black horse found there with no rider. The notes hand the finding to the horse
     * itself, and the pack keeps no steed, so the finding is the ground: the page is a walk to the ruins
     * and the corpse is told in the closing line, which is what the notes' own completion text is.
     */
    private static final String MISSING_RIDER_TARGET = Quest.siteTarget(Sites.WOOD_RUIN);
    private static final String MISSING_RIDER_TITLE = "campaign.tolkienmobs.page.missingrider";
    /** The report Elladan hears at his camp, which is the second half of the same beat. */
    private static final String RIDER_RETURN_TITLE = "campaign.tolkienmobs.page.riderreport";
    /** Glorfindel's counsel at the waterfalls, which the notes put before ever touching the stone. */
    private static final String WISDOM_TITLE = "campaign.tolkienmobs.page.wisdom";
    /** 1-6, the return to Glorfindel with the beryl: the reference's sixth quest. */
    private static final String WISDOM3_TITLE = "campaign.tolkienmobs.page.wisdom3";
    private static final String WISDOM3_PLACE = "campaign.tolkienmobs.page.wisdom3.place";
    private static final String WISDOM3_CLOSING = "campaign.tolkienmobs.page.wisdom3.closing";
    /** The return to the camp with the caves searched, where the stone's next asking is decided. */
    private static final String REGAIN_TITLE = "campaign.tolkienmobs.page.regain";
    /**
     * The notes' second page of the chapter, which the line had put at the end of it: the walk to
     * Thorenhad where Elladan says what has been found in the wilderness. The ids settle it - quest eight
     * hands into the Horse and Rider dialogues, and nine's completion text is him greeting a player his
     * father sent - so the chapter ends on the tidings carried back to Elrond, as the notes end it.
     */
    private static final String HORSE_TITLE = "campaign.tolkienmobs.page.horse";
    /** How far ahead of the player he is stood up, sideways off that line, and how far out he counts. */
    private static final int MATUNA_WAY_AHEAD = 16;
    private static final int MATUNA_WAY_SPREAD = 10;
    private static final double MATUNA_SEARCH = 128.0D;
    private static final double MATUNA_CALL_DISTANCE = 24.0D;
    /** The hand-over that owes the meeting, so a page does not restock the road he was killed on. */
    private static final Map<UUID, Call> MATUNA_CALLED = new HashMap<>();
    private static final String AUDIENCE_TITLE = "campaign.tolkienmobs.page.audience";
    /** What he says as he hands it over, what it says when it comes back, and the two ways it is quiet. */
    private static final String STONE_GIVEN = "campaign.tolkienmobs.beryl.given";
    private static final String STONE_RETURNED = "campaign.tolkienmobs.beryl.returned";
    private static final String STONE_DAYLIGHT = "campaign.tolkienmobs.beryl.daylight";
    private static final String STONE_SHALLOW = "campaign.tolkienmobs.beryl.shallow";
    private static final String STONE_ELSEWHERE = "campaign.tolkienmobs.beryl.elsewhere";
    private static final String STONE_SAME = "campaign.tolkienmobs.beryl.same";
    /** What a cleared chamber says, and what the stone says while its keeper is still standing. */
    private static final String STONE_CLEARED = "campaign.tolkienmobs.cave.reached";
    private static final String STONE_GUARDED = "campaign.tolkienmobs.cave.guarded";
    /** Where each player last held the stone up, and when, so one spot gets one question. */
    private static final Map<UUID, BlockPos> STONE_LAST = new HashMap<>();
    private static final Map<UUID, Long> STONE_RAISED = new HashMap<>();
    /** How often the stone can be held up at all, in ticks, so a held mouse is not a hundred questions. */
    private static final int STONE_RAISE_INTERVAL = 10;
    /**
     * How far above or below a place's own middle still counts as standing on it, in blocks. A ford is
     * surveyed on the water and walked on the bank, and a hall's middle is a floor with a stair in it.
     */
    private static final int PLACE_UP_AND_DOWN = 16;
    /** Players already told the ground under their feet is the ground the stone wants. */
    private static final Set<UUID> STONE_HERE_SPOKEN = new HashSet<>();
    /**
     * The chapter as the old series wrote it.
     * <p>
     * The notes' chapter one is eighteen quests numbered eight to twenty-five, and the West's line is
     * now those beats and nothing else: the pages this line had invented to fill the gaps between them -
     * the muster, the south road, the barrow hunt, the walk to the valley, the hilltop, the hunters, the
     * ford that was raised, Bree, Tharbad, the Old Forest and the last muster - are gone rather than left
     * standing beside the notes, because a line answering two books at once is a line nobody can check
     * against either of them.
     * <p>
     * One pair of the notes' quests shares a page here. Their thirteen is the stone held aloft at the
     * ford; their fourteen is the return to Glorfindel to say what it did. A stone's asking is settled in
     * the hand that lent it - the ford is where it is asked and he is where it is given back - so the two
     * are one page, and a second page of talk with him would be the same Elf twice in a row with nothing
     * between. Everything else is one page to one quest.
     */
    /** The notes' Troubles of Rivendell, which is where the chapter opens: Elrond in his library. */
    private static final String TROUBLES_TITLE = "campaign.tolkienmobs.page.troubles";
    private static final String TROUBLES_PLACE = "campaign.tolkienmobs.page.troubles.place";
    private static final String TROUBLES_CLOSING = "campaign.tolkienmobs.page.troubles.closing";
    /**
     * The notes' three trolls, one to each cave the stone is asked in. They are three pages rather than
     * one because the notes wrote three, and the pack's cave troll is the body asked for: the notes'
     * trolls are the creature that gives the Trollshaws its name, and the chambers the stone is carried
     * into are theirs.
     */
    private static final String CAVE_TROLL_TARGET = "tolkienmobs:entityttmtroll";
    /** The tag the crag's keeper carries, which is how the next look knows the chamber is not empty. */
    public static final String CAVE_TROLL_TAG = "tolkienmobs:cave_troll";
    /** What the keeper is called: the cave the asking has reached, by its name. */
    private static final String CAVE_TROLL_NAME = "campaign.tolkienmobs.cave.troll";
    /** Said once when he stands, so a player walking in knows the chamber is not empty. */
    private static final String CAVE_TROLL_GUARD = "campaign.tolkienmobs.cave.guard";
    /** How near the crag a player has to be for its keeper to be stood up, in blocks. */
    private static final int CAVE_TROLL_WAY = 64;
    /** How far across the crag's middle the floor of its deepest chamber is looked for. */
    private static final int CAVE_TROLL_RING = 10;
    private static final String CAVE_BANDRITH_TITLE = "campaign.tolkienmobs.page.cave.bandrith";
    private static final String CAVE_BANDRITH_PLACE = "campaign.tolkienmobs.page.cave.bandrith.place";
    private static final String CAVE_BANDRITH_CLOSING = "campaign.tolkienmobs.page.cave.bandrith.closing";
    private static final String CAVE_HOKLOND_TITLE = "campaign.tolkienmobs.page.cave.hoklond";
    private static final String CAVE_HOKLOND_PLACE = "campaign.tolkienmobs.page.cave.hoklond.place";
    private static final String CAVE_HOKLOND_CLOSING = "campaign.tolkienmobs.page.cave.hoklond.closing";
    private static final String CAVE_SHOLDROTH_TITLE = "campaign.tolkienmobs.page.cave.sholdroth";
    private static final String CAVE_SHOLDROTH_PLACE = "campaign.tolkienmobs.page.cave.sholdroth.place";
    private static final String CAVE_SHOLDROTH_CLOSING = "campaign.tolkienmobs.page.cave.sholdroth.closing";
    /** Barachen's own page, named here so the scout can be stood for it the way the Elves are. */
    private static final String BARACHEN_TITLE = "campaign.tolkienmobs.page.barachen";
    /** The notes' purse on the Giant: four silver and sixteen bronze, and their experience beside it. */
    private static final int GIANT_SILVER = 4;
    private static final int GIANT_BRONZE = 16;
    private static final int GIANT_XP = 50;

    /**
     * The pages each front works through, and what they pay.
     * <p>
     * The standing written here is the notes' own figures at a tenth. Their ladder ran to five or ten
     * thousand and this one tops out at fifteen hundred, so a page the notes price at a hundred pays ten
     * and one they price at nine hundred pays ninety. The flat tenth is the whole rule: it is easier to
     * check against the notes than a figure worked out per page, and it cannot be argued with. The pages
     * the notes never priced - the fronts' own wars - keep the figures this line has always paid them,
     * since those were chosen for this ladder in the first place.
     * <p>
     * Where the notes name a race or a realm as the one that pays - the High Elves, the Dwarves, Gondor
     * - the line reads that as the front the act belongs to and nothing more: the notes' Elves are this
     * line's West, so an errand they price for the Elves pays West standing. A page pays one of the three
     * countries, never a people, which is also why a front's own war pages name no race at all.
     * Which people stands with which side - Elves and Dwarves with the West, Orcs and Angmar with
     * the East, the Haradrim and the Corsairs with the Harad - is written out on {@link Alliance}.
     */
    private static final Map<Alliance, List<Page>> PAGE_LINES = Map.of(
            Alliance.WEST, List.of(
                    // The reference chapter, whose eighteen quests are numbered 8 to 25 and are walked in
                    // the order the speakers hand them over - fourteen, then the trail regained, then the
                    // caves - rather than in numeric order. Every page names the body who holds it and the
                    // body who takes it back, because the chapter is a relay of speakers: Aragorn meets the
                    // player at the gate and Elrond takes the news, Elrond sends them to Thorenhad and
                    // Elladan takes it, and so on down the line. The notes number the quests; this list is
                    // the order they are met in.
                    //
                    // 1-1 - Troubles of Rivendell. The chapter opens at the West's herald, who hands the
                    // errand over at his bench the way he hands the front's own work, and Elrond hears it in
                    // the library at the end of the walk. Everything past this page belongs to a speaker
                    // rather than to a hall: the herald starts the chapter and the road carries it.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(ELROND), 1, 5, 3,
                            "", ELROND, TROUBLES_TITLE, TROUBLES_PLACE, TROUBLES_CLOSING),
                    // 9 - Where is the Horse and the Rider? Elrond explains the missing Rider and sends the
                    // player to his sons; Elladan hears it at Thorenhad.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(ELLADAN), 1, 5, 4,
                            ELROND, ELLADAN, HORSE_TITLE, "campaign.tolkienmobs.page.horse.place",
                            "campaign.tolkienmobs.page.horse.closing"),
                    // 10 - The Missing Rider I: Elladan's page, walked south into the narrow channels where
                    // the black horse lies in the ruin. A corpse is not a hunt, so the page is the ground.
                    new Page(Quest.Kind.LOCATION, MISSING_RIDER_TARGET, 1, 5, 4,
                            ELLADAN, "", MISSING_RIDER_TITLE, "campaign.tolkienmobs.page.missingrider.place",
                            "campaign.tolkienmobs.page.missingrider.closing"),
                    // 11 - The Missing Rider II: the bridle carried back to the camp, which is the half of
                    // the beat the reference ends on Elladan's answer.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(ELLADAN), 1, 5, 4,
                            ELLADAN, ELLADAN, RIDER_RETURN_TITLE, "campaign.tolkienmobs.page.riderreport.place",
                            "campaign.tolkienmobs.page.riderreport.closing"),
                    // 12 - Wisdom of Lord Glorfindel I: Elladan sends the player to the Elf-lord above the
                    // water, who says what a riderless horse means.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(GLORFINDEL), 1, 10, 4,
                            ELLADAN, GLORFINDEL, WISDOM_TITLE, "campaign.tolkienmobs.page.wisdom.place",
                            "campaign.tolkienmobs.page.wisdom.closing"),
                    // 1-5 and 1-6 - the beryl held aloft at the ford, and the return to Glorfindel with it.
                    // The reference's quest list makes them two: the fifth completes itself at the water
                    // (its own note is "completion - automatic"), and the sixth is a page of talk that ends
                    // with him asking what the stone did. The dialogue tree reads as one conversation
                    // because he says return to me once you have done so in the same breath as the lending.
                    new Page(Quest.Kind.SURVEY, FORD_TARGET, 1, 10, 4,
                            GLORFINDEL, GLORFINDEL, "campaign.tolkienmobs.page.beryl",
                            "campaign.tolkienmobs.page.beryl.place", ""),
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(GLORFINDEL), 1, 10, 4,
                            GLORFINDEL, GLORFINDEL, WISDOM3_TITLE, WISDOM3_PLACE, WISDOM3_CLOSING),
                    // 19 - Regaining the Trail. Glorfindel leaves the stone in the player's care and sends
                    // them back to Elladan, who decides where it must be brought next. It is the
                    // reference's nineteenth quest and its eighth page.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(ELLADAN), 1, 8, 5,
                            GLORFINDEL, ELLADAN, REGAIN_TITLE, "campaign.tolkienmobs.page.regain.place",
                            "campaign.tolkienmobs.page.regain.closing"),
                    // 1-8a, 1-8b and 1-8c - Hiding in the Dark: three caves, an area kill in the deepest
                    // chamber of each, and a completion token for the troll that keeps it. One page with
                    // three readings, because the reference writes the three chambers as one search and
                    // the kill that clears each is counted where the stone is raised. Then 1-8d is the
                    // camp's item page, which is why Elladan will not take the stone back short of the
                    // three tokens (see stoneHeldBack).
                    new Page(Quest.Kind.SURVEY, "", STONE_CAVES, 10, 5,
                            ELLADAN, ELLADAN, "campaign.tolkienmobs.page.caves",
                            "campaign.tolkienmobs.page.caves.place", ""),
                    // 20 - Knowledge of Giants I: Elladan sends the player to the scout's fire, and
                    // Barachen tells what he saw.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(BARACHEN), 1, 10, 5,
                            ELLADAN, BARACHEN, BARACHEN_TITLE, "campaign.tolkienmobs.page.barachen.place", ""),
                    // 21 - Knowledge of Giants II: the Giant killed in the valley of the giants, and the
                    // kill carried back to Elladan. One land, two men's business, as the reference has it.
                    // Barachen's words are "speak with this creature", and that is the trap rather than a
                    // stage direction: the thing attacks. The player is sent to parley and ends up fighting,
                    // which is what the reference wants the valley to do to them - do not soften it back
                    // into a page of talk because the dialogue reads like an invitation.
                    new Page(Quest.Kind.HUNT, GIANT_TARGET, 1, 90, 6,
                            BARACHEN, ELLADAN, "campaign.tolkienmobs.page.giant",
                            "campaign.tolkienmobs.page.giant.place", "",
                            GIANT_SILVER, GIANT_BRONZE, GIANT_XP),
                    // 22 - Aid of Mirkwood I: Elladan sends the player back to Elrond, who names what the
                    // creature was and arranges the audience.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(ELROND), 1, 25, 6,
                            ELLADAN, ELROND, AUDIENCE_TITLE, "campaign.tolkienmobs.page.audience.place", ""),
                    // 23 - Aid of Mirkwood II: Elrond sends them to Legolas in the guest room.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(LEGOLAS), 1, 10, 6,
                            ELROND, LEGOLAS, LEGOLAS_TITLE, "campaign.tolkienmobs.page.legolas.place", ""),
                    // 24 - Aid of Mirkwood III: Legolas names the Wood-troll and hands over the hunt.
                    // Legolas offers to take the player where the Wood-trolls are, and the errand reads as
                    // an escort: it is a fight. The misdirection is the point of the page, so the hunt is
                    // not a stand-in for a conversation that was cut - 1-13 is an area kill in the
                    // reference's own list.
                    new Page(Quest.Kind.HUNT, MATUNA, 1, 120, 7,
                            LEGOLAS, LEGOLAS, MATUNA_TITLE, "campaign.tolkienmobs.page.matuna.place",
                            MATUNA_CLOSING, MATUNA_SILVER, MATUNA_BRONZE, MATUNA_XP),
                    // 25 - Hasten Their Departure: Legolas sends the tidings back to Elrond, which is
                    // where the reference ends the chapter.
                    new Page(Quest.Kind.DIALOGUE, Quest.personTarget(ELROND), 1, 10, 8,
                            LEGOLAS, ELROND, DEPARTURE_TITLE, DEPARTURE_PLACE, DEPARTURE_CLOSING)),
            Alliance.EAST, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60, 2,
                            "", "", "campaign.tolkienmobs.page.marches", "campaign.tolkienmobs.page.marches.place", ""),
                    new Page(Quest.Kind.FACTION, "", 12, 120, 4,
                            "", "", "campaign.tolkienmobs.page.levy", "campaign.tolkienmobs.page.levy.place", ""),
                    new Page(Quest.Kind.FETCH, "tolkienmobs:item_scroll", 1, 0, 7,
                            "", "", "campaign.tolkienmobs.page.steward", "campaign.tolkienmobs.page.steward.place", "campaign.tolkienmobs.page.steward.closing")),
            Alliance.HARAD, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60, 2,
                            "", "", "campaign.tolkienmobs.page.sun", "campaign.tolkienmobs.page.sun.place", ""),
                    new Page(Quest.Kind.FACTION, "", 12, 120, 4,
                            "", "", "campaign.tolkienmobs.page.ford", "campaign.tolkienmobs.page.ford.place", ""),
                    new Page(Quest.Kind.HUNT, "tolkienmobs:entityttmmumakil", 1, 150, 7,
                            "", "", "campaign.tolkienmobs.page.mumak", "campaign.tolkienmobs.page.mumak.place", "campaign.tolkienmobs.page.mumak.closing"))
    );

    /**
     * One page of a front's line: what it asks of the player, what the country pays for it - standing,
     * and courage, which is the ladder this whole mod is climbed with - and the two things that say
     * what the step <em>is</em>: its name, and the place it happens in.
     * <p>
     * Both are translation keys rather than words, so a page is read in its reader's language, and both
     * are blank on a page that has no name of its own: the book then falls back to the objective alone
     * rather than printing an empty heading.
     */
    private record Page(Quest.Kind kind, String target, int goal, int standing, int courage,
                        String giver, String turnIn, String title, String place, String closing,
                        int silver, int bronze, int xp) {
        /**
         * A page that pays nothing but its standing, which is most of them: the ladder the campaign is
         * climbed with is standing and courage, and the notes only put a purse on the odd errand. The
         * coins are written where the notes wrote them and nowhere else, so the line does not turn into
         * a wage.
         */
        Page(Quest.Kind kind, String target, int goal, int standing, int courage,
             String giver, String turnIn, String title, String place, String closing) {
            this(kind, target, goal, standing, courage, giver, turnIn, title, place, closing, 0, 0, 0);
        }
    }

    /** How many pages a front has. */
    public static int pages(Alliance alliance) {
        return PAGE_LINES.get(alliance).size();
    }

    /**
     * The slot the campaign's own page lives in, one past the three fronts. It is a stage like any
     * other - 0 until the Rider is paid off - so it needs no machinery of its own.
     */
    private static final int RIDER_SLOT = Alliance.values().length;
    /**
     * What the Rider is worth. All three countries pay it, each in full: they were all afraid of him
     * and all wanted him gone, and none of them sent the player alone.
     */
    private static final int RIDER_STANDING = 200;
    /**
     * The name of the campaign's own page, and the place the book says it is met at. He belongs to no
     * front, so his name cannot sit in a front's line and is written here instead.
     */
    private static final String RIDER_TITLE = "campaign.tolkienmobs.page.rider";
    private static final String RIDER_PLACE = "campaign.tolkienmobs.page.rider.place";
    /**
     * How many pages the campaign's own slot walks through: the Rider, the castle, the arena the war
     * raises, and what waits in it. The stage is what the client is told, so a herald's mark stays up
     * until the last of them is paid.
     */
    private static final int FINALE_STAGES = 4;
    /** Where each last act happens, what it pays, and what the book calls it. */
    private static final String CASTLE_TARGET = Quest.siteTarget(Sites.BOSS_CASTLE);
    private static final String CASTLE_TITLE = "campaign.tolkienmobs.page.castle";
    private static final String CASTLE_PLACE = "campaign.tolkienmobs.page.castle.place";
    private static final String WITCHKING_TITLE = "campaign.tolkienmobs.page.witchking";
    private static final String WITCHKING_PLACE = "campaign.tolkienmobs.page.witchking.place";
    private static final String WITCHKING_CLOSING = "campaign.tolkienmobs.page.witchking.closing";
    /**
     * The arena: the last ground the campaign raises, and the only one of its buildings nobody is sent
     * to until the thing the castle was built around has been felled. Its words are the ones the war
     * ends on, because a fortress is what a war is fought with and an arena is what it leaves behind.
     */
    private static final String ARENA_TARGET = Quest.siteTarget(Sites.BOSS_ARENA);
    private static final String ARENA_TITLE = "campaign.tolkienmobs.page.arena";
    private static final String ARENA_PLACE = "campaign.tolkienmobs.page.arena.place";
    private static final String ARENA_CLOSING = "campaign.tolkienmobs.page.arena.closing";
    /** What the Witch-king's page pays: a country's thanks for a crown, not the price of a beast. */
    private static final int WITCHKING_STANDING = 300;
    /** The rung the ladder has been holding back for the arena, paid when the arena page is settled. */
    private static final int ARENA_COURAGE = 75;
    /** The tag the Witch-king wears when the campaign stands him up, and how far he is looked for. */
    private static final String WITCHKING_TAG = "tolkienmobs:campaign_witchking";
    private static final double WITCHKING_SEARCH = 96.0D;
    /**
     * The tag the three keepers wear, how many of them there are, and how far out from the middle of
     * the floor they stand. The ring is walked in a circle rather than laid out by hand, so the crown
     * is in the middle of it wherever the arena's own walls happen to be.
     */
    private static final String GUARD_TAG = "tolkienmobs:campaign_guard";
    private static final int GUARD_COUNT = 3;
    private static final int GUARD_RING = 8;
    /** How near the king a keeper still counts as standing over him, in blocks. */
    private static final double GUARD_REACH = 48.0D;
    /**
     * How close a player has to be to the arena for the crown to come up in it, in blocks. A body added
     * to a chunk that is not loaded is dropped without a word, and a ring stood up while the player is
     * in another country is a ring nobody arrives at - the page reads better as something the sand does
     * when they walk onto it.
     */
    private static final double WITCHKING_REACH = 128.0D;
    /**
     * The tag the crown wears when he is met in his own house, before the sand, and how the seat he is met
     * in is found.
     * <p>
     * The seat is a block of the build rather than a spot worked out from the middle: a castle is a plan,
     * and a plan has a chair in it. It is the block the debug screen names with the crosshair on that
     * chair in the castle the war raises, so the meeting happens where the build meant it to instead of
     * wherever a search found air; a build with no chair named keeps the search, which is air to stand in
     * under a roof, nearest the middle.
     */
    private static final String CASTLE_TAG = "tolkienmobs:campaign_king_castle";
    /** The tag the chair he sits on wears, so it is put away with him rather than left standing. */
    private static final String SEAT_TAG = "tolkienmobs:campaign_seat";
    @Nullable
    private static final BlockPos CASTLE_SEAT = new BlockPos(3850, 107, -7425);
    private static final int CASTLE_ROOM_REACH = 16;
    private static final int CASTLE_ROOM_UP = 16;
    /** How near the seat the player has to come to be spoken to, in blocks. */
    private static final double CASTLE_MEET = 10.0D;
    /** The players the crown has already said his piece to in his own house, so he says it once. */
    private static final Set<UUID> SUMMONED = new HashSet<>();
    /**
     * How far up and down from the middle of a building's floor a place to stand a body is looked for.
     * An arena is laid on its own sand, so this only ever covers a step or a threshold.
     */
    private static final int FLOOR_UP = 12;
    private static final int FLOOR_DOWN = 4;
    /** How often the crown will explain why nothing is landing, in ticks, and when it last did. */
    private static final int SHIELD_SAY_INTERVAL = 60;
    private static final Map<UUID, Long> SHIELD_SAID = new HashMap<>();
    /**
     * The pages whose prose has been written into their talk keys, and so are read aloud rather than
     * summarised when a herald hands one over. One, for now: the West's first muster, which is where
     * the campaign opens and where the script's argument for a conversation is made.
     */
    private static final Set<String> SPOKEN = Set.of("campaign.tolkienmobs.page.wisdom",
            "campaign.tolkienmobs.page.beryl", "campaign.tolkienmobs.page.caves");

    private static final Map<UUID, Front[]> PROGRESS = new HashMap<>();
    /** What {@link #instance} answers with when there is no server to hold a file. */
    private static final Campaign NO_SERVER = new Campaign(false);
    private static final int[] CLIENT = new int[Alliance.values().length];
    private static int clientRider;

    /** One player's place in one front: how far it has got, and the page it is holding out. */
    public record Front(int stage, @Nullable Quest handed) {
        static final Codec<Front> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.optionalFieldOf("stage", 0).forGetter(Front::stage),
                Quest.CODEC.optionalFieldOf("handed").xmap(o -> o.orElse(null), Optional::ofNullable)
                        .forGetter(Front::handed)
        ).apply(instance, Front::new));
    }

    /**
     * The campaign's Rider, tracked per player while he is out there. The map is a shortcut rather
     * than a record: a reloaded world forgets it, and {@link #livingRider} falls back to the tag he
     * carries before it concludes there is nobody to find.
     */
    private static final Map<UUID, UUID> RIDER_ENTITY = new HashMap<>();
    /** The tag every campaign Rider wears, and the tag that ties one to the player he was sent for. */
    private static final String RIDER_TAG = "tolkienmobs:campaign_rider";
    /**
     * Where a player stood when the Rider's page came into their hands. He is owed to the road, not
     * to the room the page was handed over in, so this is where the ride out has to begin.
     */
    private static final Map<UUID, Call> RIDER_CALLED = new HashMap<>();
    /** How far from that door, and how far ahead, he is stood up. */
    private static final int RIDER_CALL_DISTANCE = 24;
    private static final int RIDER_WAY_AHEAD = 36;
    private static final int RIDER_WAY_SPREAD = 12;
    /** How wide a search for a Rider who is already out there, and how often to look. */
    private static final double RIDER_SEARCH = 128.0D;
    private static final int RIDER_CHECK_INTERVAL = 20;

    /**
     * A page's quarry, owed to the road rather than to the bench: where the player stood when the page
     * came into their hands, when it is owed, and whether it has already been paid out once.
     */
    private static final Map<UUID, Call> QUARRY_CALLED = new HashMap<>();

    /** A quarry owed to the road. Held in memory only, so a reloaded world starts the clock again. */
    private record Call(BlockPos from, long due, boolean spent) {
    }

    /**
     * How long a page's quarry keeps away, in ticks: a minute and a half.
     *
     * <p>A page that puts its quarry on the ground as it is handed over makes the walk to them part of
     * the hand-over, and the meeting the page promised happens beside the bench with the herald still
     * watching. A page that holds them back makes the meeting something the player was already doing
     * when it arrived, which is the difference between an errand and an adventure.
     */
    private static final int AMBUSH_DELAY = 1800;

    /**
     * How far the player has to have come from that door before anything is owed at all. Time alone
     * would mean a player who stood still at the bench for two minutes being found where they stood;
     * the walk is the other half of what makes it a meeting.
     */
    private static final int AMBUSH_WAY = 96;

    /**
     * Where a front page's quarry is found, once it is owed at all: far enough ahead that it is a
     * meeting on the road rather than a spawn beside the player, and spread across the way so it
     * reads as ground somebody has already walked. Always fewer than the page asks for, because the
     * rest of the hunt is still the player's to find.
     */
    private static final int QUARRY_AHEAD = 40;
    private static final int QUARRY_SPREAD = 24;
    private static final int QUARRY_SIDE = 16;
    private static final int QUARRY_LIMIT = 3;
    /** The tag the quarry a page put on the ground wears, so a second hand-over does not double it. */
    private static final String QUARRY_TAG = "tolkienmobs:campaign_quarry";
    /** How far off a page's quarry is looked for before more is put down. */
    private static final double QUARRY_SEARCH = 64.0D;

    public static final Codec<Campaign> CODEC = Entry.CODEC.listOf()
            .xmap(Campaign::fromEntries, Campaign::toEntries);

    public static final SavedDataType<Campaign> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Campaign::new, CODEC);

    /**
     * The instance a world with no campaign file starts from. The clearing is the point: these maps are
     * static, so a player who leaves one world and starts another in the same session would otherwise
     * find the last world's stages still standing in them, with no file to overwrite them. When a world
     * does have a file, {@link #fromEntries} clears the same map before it fills it.
     */
    private Campaign() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Campaign(boolean fresh) {
        super();
        if (fresh) PROGRESS.clear();
    }

    // ---- reading ----

    /** The stage the local player has reached on one front, as last synced from the server. */
    public static int clientStage(Alliance alliance) {
        return CLIENT[alliance.ordinal()];
    }

    /** The local player's stage on the campaign's own page: 0 while the Rider is still out there. */
    public static int clientRider() {
        return clientRider;
    }

    /** Whether all three fronts are settled on the client's last-synced copy. */
    public static boolean clientSealed() {
        for (Alliance alliance : Alliance.values()) {
            if (CLIENT[alliance.ordinal()] < pages(alliance)) return false;
        }
        return true;
    }

    /**
     * Whether this herald still has something to say to the player, which is what his mark means: a front
     * with pages left, or the campaign's own page once every front is settled and the Rider is not yet
     * behind the player.
     */
    public static boolean clientHasWork(Alliance alliance) {
        if (CLIENT[alliance.ordinal()] < pages(alliance)) return true;
        return clientSealed() && clientRider < FINALE_STAGES;
    }

    /**
     * The name of the step a front is on, as the book draws it, or an empty component when that step has
     * no name of its own. Read off the synced stage rather than off the page itself: a stage only moves
     * when the ledger settles a page, so while one is carried the stage is that page's index.
     */
    public static Component clientTitle(Alliance alliance) {
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = CLIENT[alliance.ordinal()];
        return stage < line.size() ? named(line.get(stage).title()) : Component.empty();
    }

    /** Where the front's current step happens, or an empty component when the page does not say. */
    public static Component clientPlace(Alliance alliance) {
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = CLIENT[alliance.ordinal()];
        return stage < line.size() ? named(line.get(stage).place()) : Component.empty();
    }

    /** The words a front's last page ends on, or empty when this stage is not that page. */
    /**
     * What a front's page in hand pays besides its standing and courage, one line per kind of coin, for
     * the ledger to say on a hover. A page with no purse answers with nothing at all, which is what
     * keeps the leaf's reward row exactly as wide as it was.
     */
    public static List<Component> clientPurse(Alliance alliance) {
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = CLIENT[alliance.ordinal()];
        if (stage < 0 || stage >= line.size()) return List.of();
        Page page = line.get(stage);
        List<Component> lines = new ArrayList<>();
        if (page.silver() > 0) lines.add(Component.translatable("screen.tolkienmobs.quests.purse.silver",
                Component.literal(String.valueOf(page.silver()))));
        if (page.bronze() > 0) lines.add(Component.translatable("screen.tolkienmobs.quests.purse.bronze",
                Component.literal(String.valueOf(page.bronze()))));
        if (page.xp() > 0) lines.add(Component.translatable("screen.tolkienmobs.quests.purse.exp",
                Component.literal(String.valueOf(page.xp()))));
        return lines;
    }

    public static Component clientClosing(Alliance alliance) {
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = CLIENT[alliance.ordinal()];
        return stage < line.size() ? named(line.get(stage).closing()) : Component.empty();
    }

    /**
     * What the campaign's own page is called, and where it happens, as the leaf draws it: whichever of
     * the last acts the player has reached. It read as the Rider's page for all of them until the
     * castle, the hall and the arena were given names of their own to be drawn under.
     */
    public static Component clientFinaleTitle() {
        return named(switch (clientRider) {
            case 1 -> CASTLE_TITLE;
            case 2 -> ARENA_TITLE;
            case 3 -> WITCHKING_TITLE;
            default -> RIDER_TITLE;
        });
    }

    public static Component clientFinalePlace() {
        return named(switch (clientRider) {
            case 1 -> CASTLE_PLACE;
            case 2 -> ARENA_PLACE;
            case 3 -> WITCHKING_PLACE;
            default -> RIDER_PLACE;
        });
    }

    private static Component named(String key) {
        return key.isEmpty() ? Component.empty() : Component.translatable(key);
    }

    public static void acceptClient(int[] stages) {
        for (Alliance alliance : Alliance.values()) {
            CLIENT[alliance.ordinal()] = alliance.ordinal() < stages.length ? stages[alliance.ordinal()] : 0;
        }
        clientRider = stages.length > RIDER_SLOT ? stages[RIDER_SLOT] : 0;
    }

    public static int stage(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        return row(player)[alliance.ordinal()].stage();
    }

    /** Whether every front has been carried to the end of its line. */
    public static boolean sealed(ServerPlayer player) {
        ensureLoaded(player.level());
        return sealed(row(player));
    }

    /** Whether the campaign's own page is behind the player, which is the campaign finished. */
    public static boolean done(ServerPlayer player) {
        ensureLoaded(player.level());
        return row(player)[RIDER_SLOT].stage() >= FINALE_STAGES;
    }

    /**
     * The page this alliance's herald is holding for this player, or null once there is nothing left to
     * ask. A herald whose own front is settled still has something to give while the other two fight, and
     * once all three are settled any of them hands over the campaign's last page - whoever the player
     * reaches first, since it is the same quarry.
     * <p>
     * The giver is left blank here and filled in where it is handed over, so the page itself is the same
     * for every player.
     */
    @Nullable
    public static Quest page(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        Front[] row = row(player);
        int stage = row[alliance.ordinal()].stage();
        List<Page> line = PAGE_LINES.get(alliance);
        if (stage < line.size()) {
            Page page = line.get(stage);
            // The soldier pages name a different rival each, so the line alternates whose soldiers it
            // wants rather than asking for the same ones twice running.
            String target = page.target();
            if (page.kind() == Quest.Kind.FACTION) {
                List<Alliance> rivals = alliance.rivals();
                target = rivals.get(stage % rivals.size()).id();
            }
            Quest built = new Quest(page.kind(), target, page.goal(), 0, alliance.id(), page.standing(),
                    List.of(), "", BlockPos.ZERO, Quest.NOBODY, true, "");
            // Who hands a page and who takes it back is the line's own business: the chapter is a relay
            // of speakers, so each page carries both names rather than having them worked out here from
            // what the page happens to ask for.
            return page.turnIn().isEmpty() ? built : built.withTurnIn(page.turnIn());
        }
        if (!sealed(row)) return null;
        // The slot the campaign keeps for itself walks through its last acts: the Rider, then the
        // castle raised after him, then what is waiting in it. A herald who has nothing left of his
        // own front still has these to hand over, so the war ends at a bench like everything else.
        return switch (row[RIDER_SLOT].stage()) {
            case 0 -> riderPage();
            case 1 -> castlePage();
            case 2 -> arenaPage();
            case 3 -> witchKingPage();
            default -> null;
        };
    }

    /**
     * The body holding this front's next page, by name: a speaker where the line names one, and otherwise
     * the front's own herald. Read off the line, so moving a page to another speaker moves the hand that
     * hands it over.
     */
    /**
     * The ground the line keeps a named body on, or null when the name is nobody the campaign stands.
     * <p>
     * A page that says "speak to Elrond" names a person rather than a place, and a player who has never
     * been to the valley has no way to know that Elrond is in it. The three Elves of the valley are all
     * kept there, Elladan in his camp east of it, and Barachen at his fire south of the road, so a page
     * that names one of them can be pointed at the town it belongs to without asking where the body is.
     */
    @Nullable
    public static String groundOf(String person) {
        if (person == null || person.isEmpty()) return null;
        if (person.equalsIgnoreCase(ELROND) || person.equalsIgnoreCase(LEGOLAS)
                || person.equalsIgnoreCase(GLORFINDEL)) {
            return SUMMON_PLACE;
        }
        if (person.equalsIgnoreCase(ELLADAN)) return THORENHAD_PLACE;
        if (person.equalsIgnoreCase(BARACHEN)) return BARACHEN_PLACE;
        // A page may name a body at length - "Elrond Half-elven", "Barachen the scout" - and a needle
        // that gave up on the extra words would be no needle at all for that page.
        String short_ = person.toLowerCase(java.util.Locale.ROOT);
        if (short_.contains(ELROND.toLowerCase(java.util.Locale.ROOT))
                || short_.contains(LEGOLAS.toLowerCase(java.util.Locale.ROOT))
                || short_.contains(GLORFINDEL.toLowerCase(java.util.Locale.ROOT))) {
            return SUMMON_PLACE;
        }
        if (short_.contains(ELLADAN.toLowerCase(java.util.Locale.ROOT))) return THORENHAD_PLACE;
        if (short_.contains(BARACHEN.toLowerCase(java.util.Locale.ROOT))) return BARACHEN_PLACE;
        return null;
    }

    /**
     * Where a named body is standing, as near as the world can say: the body itself when it is out there,
     * and otherwise the step the line keeps it on.
     * <p>
     * A page that says "speak with Glorfindel" was pointed at the valley, and the valley's middle is a
     * fountain: the needle sent the player to the water and the Elf was eight blocks off it on the road.
     * The middle of a place is where the place is, not where anybody is, so the ground a page of talk
     * points at is the body's own stand - read off him while he is standing, since a keeper drifts about
     * his post, and off {@link #VALLEY_STANDS} when he is not.
     */
    @Nullable
    public static BlockPos standOf(ServerPlayer player, String person) {
        String place = groundOf(person);
        if (place == null || !(player.level() instanceof ServerLevel level)) return null;
        Places.Place where = Places.place(level, place);
        if (where == null || !where.dimension().equals(level.dimension())) return null;
        String tag = keeperTag(person);
        if (tag == null) return where.centre();
        List<Entity> found = level.getEntities((Entity) null, new AABB(where.centre()).inflate(KEEPER_WAY),
                seen -> seen.entityTags().contains(tag));
        if (!found.isEmpty()) return found.getFirst().blockPosition();
        int[] step = VALLEY_STANDS.get(tag);
        return step == null ? where.centre() : where.centre().offset(step[0], 0, step[1]);
    }

    /**
     * The tag a named keeper wears, or null when the name is nobody the line stands anywhere. Read off
     * the name rather than written beside every page, so a page that names him at length still finds him.
     */
    @Nullable
    public static String keeperTag(String person) {
        if (person == null || person.isEmpty()) return null;
        String lower = person.toLowerCase(java.util.Locale.ROOT);
        if (lower.contains("glorfindel")) return GLORFINDEL_TAG;
        if (lower.contains("elladan")) return ELLADAN_TAG;
        if (lower.contains("barachen")) return BARACHEN_TAG;
        if (lower.contains("elrond")) return ELROND_TAG;
        if (lower.contains("legolas")) return LEGOLAS_TAG;
        return null;
    }

    /** How far around a place its keeper is looked for, in blocks: a camp is wider than a square. */
    private static final double KEEPER_WAY = 96.0D;

    public static String holder(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = stage(player, alliance);
        if (stage < line.size() && !line.get(stage).giver().isEmpty()) return line.get(stage).giver();
        return QuestGivers.campaignName(alliance);
    }

    /**
     * The step after the one in hand: the body holding this front's next page, and the ground the line
     * keeps them on - which is null for a body the campaign does not keep in a place of its own, such as
     * a front's herald, who is wherever his own hall was built.
     */
    public record Next(String person, String place) {
    }

    /**
     * Who the chapter goes to next, or null when this front's line has run out and there is nobody left
     * to speak to. Read from the page that is waiting rather than from the one just settled.
     */
    @Nullable
    public static Next nextStep(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        if (page(player, alliance) == null) return null;
        String person = holder(player, alliance);
        if (person.isEmpty()) return null;
        return new Next(person, groundOf(person));
    }

    /**
     * Tells a player whose hand the chapter's next page is in, which is the one thing an empty book
     * cannot say. A page names the work; the relay is the other half of it, and a player who has just
     * finished a step is left holding a book with nothing in it and no way of knowing whether the fault
     * is theirs. Said where a page is settled, so a player who logs back in is not told again.
     */
    public static void announceNext(ServerPlayer player) {
        // A page that followed the last one into the book needs no pointer: the player is already
        // carrying what there is to carry.
        if (QuestLedger.carriesStory(player)) return;
        Next next = nextStep(player, Alliance.WEST);
        if (next == null) return;
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.next.person", next.person())
                .withStyle(ChatFormatting.GOLD));
        if (next.place() != null) {
            player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.next.place",
                    Component.translatable("place.tolkienmobs." + next.place()))
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    /**
     * What a player is told when a line runs out with the war still on. Every other gap is covered by
     * {@link #announceNext}: a settled page names the body holding the next one and the ground they keep
     * it on. The end of a line has no such body, so the fronts that still have pages are named instead,
     * and once none of them has one, the Rider - who belongs to all three and is the step no front owns.
     * A line that said nothing here would end the chapter by leaving the player in silence.
     */
    private static void sayWhatIsLeft(ServerPlayer player) {
        if (riderStage(player) >= FINALE_STAGES) return;
        boolean any = false;
        for (Alliance alliance : Alliance.values()) {
            if (stage(player, alliance) >= pages(alliance)) continue;
            any = true;
            player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.line.other",
                            Component.translatable("alliance.tolkienmobs." + alliance.id()))
                    .withStyle(ChatFormatting.GOLD));
        }
        if (any) return;
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.line.sealed")
                .withStyle(ChatFormatting.GOLD));
    }

    /**
     * Puts a player back on the line when they join carrying nothing. The gap between one page and the
     * next outlives a logout, and a player who comes back a week later has no memory of the sentence that
     * closed the last page, so the pointer is said again rather than assumed.
     */
    public static void remindNext(ServerPlayer player) {
        if (QuestLedger.carriesStory(player)) return;
        if (nextStep(player, Alliance.WEST) != null) {
            announceNext(player);
            return;
        }
        sayWhatIsLeft(player);
    }

    /** The campaign's own stage: 0 while the Rider is out there, {@link #FINALE_STAGES} once it is over. */
    public static int riderStage(ServerPlayer player) {
        ensureLoaded(player.level());
        Front[] row = PROGRESS.get(player.getUUID());
        return row == null ? 0 : row[RIDER_SLOT].stage();
    }

    /**
     * Whether this body is the West's business at the stage the player is on: the page ahead is theirs to
     * hand over, or the one in the player's book is theirs to take back.
     */
    public static boolean holdsOrTakes(ServerPlayer player, String name) {
        ensureLoaded(player.level());
        List<Page> line = PAGE_LINES.get(Alliance.WEST);
        int stage = stage(player, Alliance.WEST);
        if (stage >= line.size()) return false;
        Page page = line.get(stage);
        return name.equalsIgnoreCase(page.giver()) || name.equalsIgnoreCase(page.turnIn());
    }

    /**
     * The Rider, as a page: a hunt of two, priced by all three countries together. Asked of the registry
     * rather than written out, so a page cannot name a mob the world does not have - a hunt whose target
     * is in no registry can never be finished, and the campaign would sit on it for good.
     * <p>
     * Two, and not one, because the page's whole business is the kill that changes nothing. Felling him
     * once paid the page, so the meeting the act turns on - him standing there afterwards, unharmed and
     * uninterested - happened only as a sentence in a changelog. The campaign stands him up again while
     * the page is still carried (see {@link #tick}), so the second meeting is the one the player has to
     * come to terms with, and the second fall is what settles the page.
     */
    private static Quest riderPage() {
        return new Quest(Quest.Kind.HUNT, riderId(), 2, 0, "", RIDER_STANDING, List.of(),
                "", BlockPos.ZERO, Quest.NOBODY, true, "");
    }

    private static String riderId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(TolkienEntities.ENTITY_TTM_NAZGUL.get()).toString();
    }

    /**
     * The castle: the first page in the campaign that asks for ground the campaign raises itself, and
     * the first thing the war builds rather than the last thing it finds. It stands where the player was
     * when they took the page, forty-eight blocks on and out of sight, so the last act of the campaign
     * is somewhere they have to walk to.
     */
    private static Quest castlePage() {
        return new Quest(Quest.Kind.LOCATION, CASTLE_TARGET, 1, 0, "", 0, List.of(),
                "", BlockPos.ZERO, Quest.NOBODY, true, "");
    }

    /**
     * What is waiting on the sand. He is not alone and never was meant to be: three of the Nine stand
     * round him at the edge of the ring, two the player has met before and one they have not, and while
     * one of them still stands nothing lands on the crown at all - so the page asks for four bodies and
     * not for one, and the last of them is the one it was written about.
     * <p>
     * Asked of the registry for the same reason the Rider is: a page naming a boss the pack does not
     * have can never be finished.
     */
    private static Quest witchKingPage() {
        return new Quest(Quest.Kind.HUNT, witchKingId(), 1, 0, "", WITCHKING_STANDING, List.of(),
                "", BlockPos.ZERO, Quest.NOBODY, true, "");
    }

    private static String witchKingId() {
        return BuiltInRegistries.ENTITY_TYPE.getKey(TolkienEntities.ENTITY_TTM_WITCHKING.get())
                .toString();
    }

    /**
     * The arena: ground the campaign raises as its own last act rather than ground it finds. The castle
     * is where the war's business is finished; the arena is what stands afterwards and empty, which is
     * the one thing a campaign that ends on a kill has never had a way to say. It is a walk like the
     * hilltop and the castle before it, settled by standing in it, so nobody places anything by hand.
     * <p>
     * It comes between the castle and the crown, because the sand is where the crown is fought for: the
     * arena is raised and walked into first, and only then does the page that names him put him in the
     * middle of it. A player keeps one site at a time, so the order is also what makes it possible - the
     * fortress is built where the map puts it, and the arena is surveyed once the fortress has been
     * walked into.
     */
    private static Quest arenaPage() {
        return new Quest(Quest.Kind.LOCATION, ARENA_TARGET, 1, 0, "", 0, List.of(),
                "", BlockPos.ZERO, Quest.NOBODY, true, "");
    }

    // ---- handing it over and settling it ----

    /** Notes the page that was really handed over, so settling it can be told apart from any other. */
    public static void noteHanded(ServerPlayer player, Alliance alliance, Quest page) {
        // A troll that comes down out of a crag has a lair, and the pack already cuts one: the cave is
        // pitched when the page is taken, so the player tracks the creature toward ground that can be
        // recognised rather than toward an open field with one monster standing in it. A player already
        // walking to a site of their own keeps it, because the page in their hand sent them there.
        if (page.target().equals(MATUNA) && Sites.siteOf(player) == null) {
            Sites.plan(player, caveFor(player), false);
        }
        Front[] row = row(player);
        int slot = slotOf(page, alliance);
        row[slot] = new Front(row[slot].stage(), page);
        store(player).setDirty();
        // Nothing is put on the ground in the room the page is handed over in: the page is taken at a
        // bench, and a wraith conjured beside it reads as a trick. Where the player stood is noted,
        // and {@link #tick} stands the quarry up later, once the clock has run out and they are out
        // under open sky and a walk away from the door, which is a meeting rather than a magic trick.
        if (isCampaignPage(player, page)) {
            RIDER_CALLED.put(player.getUUID(), call(player));
        } else if (carriesQuarry(page)) {
            QUARRY_CALLED.put(player.getUUID(), call(player));
        }
        // The one page whose subject is an object: he says hold out your hand, so the stone goes into
        // the hand here, in the room the words are said in.
        if (asksForStone(page)) giveStone(player, STONE_GIVEN);
    }

    /**
     * Puts a front's next page in a player's book without their going back to ask for it.
     * <p>
     * The missing rider is the one beat of the chapter whose subject is an object. The notes take the dark
     * saddle off the horse in the ruin and carry it back, so the report to Elladan is not a page he hands
     * over - it is the walk back, with the tack in the pack. A player who finds the horse and is left
     * holding an empty book has been sent to the camp twice for one errand: once for the page, and again
     * for the answer to it.
     */
    private static void handOnward(ServerPlayer player, Alliance alliance) {
        Quest next = page(player, alliance);
        if (next == null) return;
        noteHanded(player, alliance, next);
        QuestLedger.accept(player, next);
    }

    /** Whether a page is the Elf-lord's stone: the survey of ground it is lent for. */
    private static boolean asksForStone(Quest page) {
        return page.story() && page.kind() == Quest.Kind.SURVEY;
    }

    /**
     * Holds the stone up, which is the only thing the beryl does, and the two askings it is put to are
     * not the same shape.
     * <p>
     * A page that names ground is one hold at one landmark - the ford, first - and the page is finished by
     * standing there and holding it aloft, because the stone is a tracker and the ford is where the trail
     * was last known to run. A page that names none is the search of the dark: it wants rock over its head,
     * and the bottom of the cave rather than the mouth of it, since a thing that has been hurt goes as deep
     * as it can. One cave is one answer, and a raise inside a walk of the last cave answered is the same
     * cave asked twice and says so rather than counting.
     * <p>
     * Ground of the catalogue with an answer written still speaks wherever it is asked, above ground
     * included: that answer is the whole point of the place, and a stone that went quiet at the ford
     * because the sky was showing would be a stone that did not know what it had been lent for.
     */
    public static void raiseStone(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        long now = level.getGameTime();
        Long before = STONE_RAISED.get(player.getUUID());
        if (before != null && now - before < STONE_RAISE_INTERVAL) return;
        STONE_RAISED.put(player.getUUID(), now);
        BlockPos here = player.blockPosition();
        var named = Places.readingAt(player);
        String wanted = wantedGround(player);
        if (!wanted.isEmpty()) {
            // A page that names ground is held aloft there and nowhere else.
            // A place is a circle of ground, not a sphere in the air: the middle of a ford was surveyed
            // on the water, and the player standing on its own bank is two or three blocks above it.
            // Read as a sphere, the stone told them they were not at the ford while they stood at it.
            if (!standingOn(player, wanted)) {
                int away = (int) Math.max(1, Math.round(flatTo(player, wanted)));
                player.sendSystemMessage(Component.translatable(STONE_ELSEWHERE,
                                Component.translatable("place.tolkienmobs." + wanted),
                                Component.literal(String.valueOf(away)))
                        .withStyle(ChatFormatting.GRAY));
                return;
            }
            player.sendSystemMessage(named != null ? named : readingFor(wanted, here));
            QuestLedger.progressSurvey(player);
            return;
        }
        if (!underRock(level, here, CAVE_COVER)) {
            // The place's own answer comes first where there is one: the stone remembers ground whether or
            // not the hand is somewhere it can be asked for anything new.
            if (named != null) player.sendSystemMessage(named);
            player.sendSystemMessage(Component.translatable(STONE_DAYLIGHT).withStyle(ChatFormatting.GRAY));
            return;
        }
        if (!underRock(level, here, CAVE_DEEP)) {
            player.sendSystemMessage(Component.translatable(STONE_SHALLOW).withStyle(ChatFormatting.GRAY));
            return;
        }
        BlockPos last = STONE_LAST.get(player.getUUID());
        if (last != null && apart(last, here) < STONE_APART) {
            player.sendSystemMessage(Component.translatable(STONE_SAME).withStyle(ChatFormatting.GRAY));
            return;
        }
        STONE_LAST.put(player.getUUID(), here);
        String cave = caveName(stoneQuest(player));
        player.sendSystemMessage(named != null ? named
                : cave.isEmpty() ? caveReading(here)
                : Component.translatable(STONE_DARK, cave).withStyle(ChatFormatting.ITALIC));
        // The notes make each cave an area kill rather than a place to hold a stone up in, so the raise
        // is the reading and the chamber is cleared by the troll that keeps it. Standing at the bottom of
        // an empty cave and asking again would otherwise pay a page for walking.
        player.sendSystemMessage(Component.translatable(STONE_GUARDED).withStyle(ChatFormatting.GRAY));
    }

    /**
     * Whether a player is standing on the ground a place names.
     * <p>
     * The circle is read flat, with an allowance up and down for a bank, a stair or a fallen tree. A
     * place is ground to be stood on rather than a point in the air, and the middle of a ford is the
     * water rather than the path beside it, so the sphere the stone used to check was a check a player
     * standing on the ford's own bank could fail.
     */
    private static boolean standingOn(ServerPlayer player, String id) {
        Places.Place place = placeFor(player, id);
        if (place == null) return false;
        return flatTo(player, id) <= place.radius()
                && Math.abs(place.centre().getY() - player.getY()) <= PLACE_UP_AND_DOWN;
    }

    /** How far a player's feet are from a place's middle, flat; {@code -1} where the place is unwritten. */
    private static double flatTo(ServerPlayer player, String id) {
        Places.Place place = placeFor(player, id);
        if (place == null) return -1;
        double dx = place.centre().getX() + 0.5D - player.getX();
        double dz = place.centre().getZ() + 0.5D - player.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /** A place this world has written on this player's ground, or null when the name is not written. */
    @Nullable
    private static Places.Place placeFor(ServerPlayer player, String id) {
        Places.Place place = Places.place(player.level(), id);
        return place != null && place.dimension().equals(player.level().dimension()) ? place : null;
    }

    /** What the stone answers over a place it was lent for: the place's own line where it has one. */
    private static Component readingFor(String wanted, BlockPos here) {
        Places.Name name = Places.known(wanted);
        return name != null && name.beryl()
                ? Component.translatable(name.berylKey()).withStyle(ChatFormatting.ITALIC)
                : caveReading(here);
    }

    /**
     * Says where the stone is to be held, once, when the player finally stands there.
     * <p>
     * The page says "hold the stone aloft in the ford" and the walk to it ends at a milestone, which is
     * a stone for coming back by rather than the stone to ask anything of: nothing said that the last
     * step is a use of the beryl, or that standing on the ground is the test rather than standing at a
     * marker. That left the page's own instruction to be guessed, and the milestone is the wrong guess.
     * Said once per arrival, and again after the player walks out of the circle.
     */
    private static void tellStoneHere(ServerPlayer player) {
        Quest stone = stoneQuest(player);
        String wanted = stone == null || stone.complete() ? "" : stone.namedGround();
        if (wanted.isEmpty() || !standingOn(player, wanted)) {
            STONE_HERE_SPOKEN.remove(player.getUUID());
            return;
        }
        if (!STONE_HERE_SPOKEN.add(player.getUUID())) return;
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.beryl.here",
                        Component.translatable("place.tolkienmobs." + wanted))
                .withStyle(ChatFormatting.AQUA));
    }

    /**
     * Counts a cave troll felled in the dark toward the search of the caves.
     * <p>
     * The notes write each of the three caves as an area kill of a cave troll rather than as ground to be
     * stood on, and the stone is dark in all three, so the kill is the step: one troll at depth is one
     * cave answered, and the name in the message is the cave the page has reached rather than the cave the
     * body happens to be in - a player killing one in the wrong valley is still one chamber the Nazgul
     * cannot be sheltering in.
     * <p>
     * Depth is read off the body rather than off the killer, because a troll dragged up into the mouth of
     * the cave and killed there was not the thing the page asks for. The book that moves is the killer's,
     * so the killer is the player the page is looked for on.
     */
    public static void onCaveTrollDeath(ServerPlayer player, Entity victim) {
        if (victim.getType() != TolkienEntities.ENTITY_TTM_TROLL.get()) return;
        Quest stone = stoneQuest(player);
        if (stone == null || stone.complete() || !stone.namedGround().isEmpty()) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!underRock(level, victim.blockPosition(), CAVE_DEEP)) return;
        String cave = caveName(stone);
        QuestLedger.progressSurvey(player);
        // The notes pay each cave a token of its own, and the camp asks for the three of them back
        // with the stone: the token is the proof of a chamber emptied, so it is handed out where
        // the chamber is, not at the end of the search.
        ItemStack proof = new ItemStack(TolkienItems.ITEM_CAVECOMPLETE.get());
        if (!player.getInventory().add(proof)) player.drop(proof, false);
        player.sendSystemMessage(Component.translatable(STONE_CLEARED, cave).withStyle(ChatFormatting.AQUA));
        player.sendSystemMessage(Component.translatable(STONE_DARK, cave).withStyle(ChatFormatting.ITALIC));
    }

    /** The ground the stone's current page has to be held over, or an empty string when it asks for caves. */
    private static String wantedGround(ServerPlayer player) {
        Quest stone = stoneQuest(player);
        return stone == null ? "" : stone.namedGround();
    }

    /** The stone's page in hand, or null when the player is carrying none. */
    private static Quest stoneQuest(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (asksForStone(quest)) return quest;
        }
        return null;
    }

    /**
     * Which of the page's three named caves this asking is for, or empty when the asking is not theirs.
     * <p>
     * The notes name three caves and the stone is asked in each before the page pays, so the count of
     * askings already made is the cave the player is standing in: the first is Bandrith's, the second
     * Hoklond's, the third Sholdroth's.
     */
    private static String caveName(Quest stone) {
        if (stone == null || stone.kind() != Quest.Kind.SURVEY || !stone.namedGround().isEmpty()) return "";
        int progress = stone.progress();
        return progress >= 0 && progress < CAVE_NAMES.size() ? CAVE_NAMES.get(progress) : "";
    }

    /**
     * Whether enough of the world stands over a player's head for the stone to agree it is in a cave: no
     * sky in sight and the ground's own height above them by the given margin. Both halves earn their keep
     * - the first rules out standing in the open, and the second rules out a house, where the height over
     * the floor is a roof rather than a mountain.
     */
    private static boolean underRock(ServerLevel level, BlockPos feet, int cover) {
        if (level.canSeeSky(feet)) return false;
        int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, feet.getX(), feet.getZ());
        return surface >= feet.getY() + cover;
    }

    /**
     * Stands a troll in the crag while the stone's cave page is asking there.
     * <p>
     * The crags the campaign cuts are forty thousand blocks of rock with nothing living in them, and the
     * page is finished by a kill at depth: with an empty chamber the search of the caves could not be paid
     * at all. So the keeper is stood when the page is carried and the crag is built, at the bottom of the
     * deepest chamber found, and never topped up - a kill at depth moves the page to the next crag, while
     * one killed somewhere shallow is still owed a body down here.
     */
    private static void keepCaveTroll(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Quest stone = stoneQuest(player);
        if (stone == null || stone.complete() || !stone.namedGround().isEmpty()) return;
        Sites.Site site = Sites.siteOf(player);
        if (site == null || !Sites.isCave(site.structure()) || !site.built()) return;
        if (!level.dimension().equals(site.dimension())) return;
        if (site.centre().distToCenterSqr(player.position()) > CAVE_TROLL_WAY * CAVE_TROLL_WAY) return;
        if (caveTrollNear(player)) return;
        BlockPos stand = caveFloor(level, site.centre());
        if (stand == null) return;
        Entity entity = TolkienEntities.ENTITY_TTM_TROLL.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob troll)) return;
        troll.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        troll.setPersistenceRequired();
        troll.setCustomName(Component.translatable(CAVE_TROLL_NAME, Component.literal(caveName(stone))));
        troll.setCustomNameVisible(true);
        troll.addTag(CAVE_TROLL_TAG);
        troll.addTag(CAVE_TROLL_TAG + "_" + player.getUUID());
        if (!level.addFreshEntity(troll)) return;
        player.sendSystemMessage(Component.translatable(CAVE_TROLL_GUARD).withStyle(ChatFormatting.DARK_GREEN));
    }

    /**
     * The bottom of the deepest chamber under a crag's middle, or null when the rock has no floor in it.
     * <p>
     * Read by walking across the middle a ring at a time and keeping the lowest stand any column offers,
     * because the template's own middle is as likely to be a wall as a floor: what is wanted is somewhere
     * a body can be stood with sixteen blocks of mountain over its head, not the arithmetic centre of the
     * build. A stand outside the mountain has no rock over it and is refused by the same test the stone
     * makes when the player raises it, so the keeper is always where the page can be answered.
     */
    @Nullable
    private static BlockPos caveFloor(ServerLevel level, BlockPos centre) {
        BlockPos deepest = null;
        for (int ring = 0; ring <= CAVE_TROLL_RING; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
                    BlockPos column = centre.offset(dx, 0, dz);
                    if (!level.hasChunkAt(column)) continue;
                    BlockPos stand = deepStand(level, column);
                    if (stand != null && (deepest == null || stand.getY() < deepest.getY())) deepest = stand;
                }
            }
        }
        return deepest;
    }

    /** The highest air in one column with a floor under it and rock over it, or null for none. */
    @Nullable
    private static BlockPos deepStand(ServerLevel level, BlockPos column) {
        for (int dy = 20; dy >= -24; dy--) {
            BlockPos at = column.offset(0, dy, 0);
            if (!level.getBlockState(at).isAir() || !level.getBlockState(at.above()).isAir()) continue;
            if (!level.getBlockState(at.below()).blocksMotion()) continue;
            if (!underRock(level, at, CAVE_DEEP)) continue;
            return at;
        }
        return null;
    }

    /** Whether the crag's keeper is already standing in it, tagged rather than counted. */
    private static boolean caveTrollNear(ServerPlayer player) {
        return !player.level().getEntities(player, player.getBoundingBox().inflate(CAVE_TROLL_WAY * 2),
                found -> found.entityTags().contains(CAVE_TROLL_TAG)).isEmpty();
    }

    /** What the stone says in a cave: picked by the cave rather than by the player, so it says it again next time. */
    private static Component caveReading(BlockPos at) {
        int which = Math.floorMod(at.hashCode(), CAVE_READINGS) + 1;
        return Component.translatable("campaign.tolkienmobs.cave." + which)
                .withStyle(ChatFormatting.ITALIC);
    }

    /**
     * Stands Glorfindel in the valley while the stone's work is the West's current step.
     * <p>
     * The work is his and nobody else's, so it is not handed at a bench: the herald sends the player up
     * the eastern road, and this is the body waiting at the end of it. He is a campaign giver like the one
     * in the hall, which is what lets the same right-click hand the page and take it back when the four
     * spots are walked - the only difference between him and a herald is that his work is a stone rather
     * than a country's war. He is stood once and found by his tag afterwards, so a reloaded world keeps
     * one Elf in the valley instead of one every time the player looks.
     */
    private static void keepGlorfindel(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        // A page of talk that names him stands him wherever it sits in the line: the notes have the player
        // come to him for counsel before the stone is ever held up, and a page owed to an Elf who is not
        // standing in the valley is a page nobody can finish.
        // He stands while the line gives him the page the player is on, at either end: the stone's
        // askings are his to hand over, and the ford's answer is his to hear. Reading it off the line
        // rather than writing a window here means a page moved to him stands him without a second edit.
        if (!carriesWordFor(player, GLORFINDEL) && !holdsOrTakes(player, GLORFINDEL)) return;
        if (Places.place(player.level(), SUMMON_PLACE) == null) Places.ensure(player, SUMMON_PLACE, CAMP_RADIUS);
        Places.Place valley = Places.place(level, SUMMON_PLACE);
        if (valley == null || !valley.dimension().equals(level.dimension())) return;
        if (valley.centre().distToCenterSqr(player.position()) > GLORFINDEL_REACH * GLORFINDEL_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(valley.centre()).inflate(GLORFINDEL_REACH),
                seen -> seen.entityTags().contains(GLORFINDEL_TAG)).isEmpty()) return;
        int[] spot = VALLEY_STANDS.get(GLORFINDEL_TAG);
        BlockPos stand = floorNear(level, valley.centre().offset(spot[0], 0, spot[1]), player);
        if (stand == null) return;
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob elf)) return;
        elf.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        elf.setPersistenceRequired();
        // A giver, a campaign giver, and a front's own: the first three tags are what the right-click and
        // the mark over his head already read, and the last is only how this class finds him again.
        elf.addTag(QuestGivers.GIVER_TAG);
        elf.addTag(QuestGivers.CAMPAIGN_TAG);
        elf.addTag(QuestGivers.CAMPAIGN_GIVER_TAG);
        elf.addTag(Alliance.WEST.giverTag());
        elf.addTag(GLORFINDEL_TAG);
        elf.setCustomName(Component.literal(GLORFINDEL).withStyle(ChatFormatting.GOLD));
        elf.setCustomNameVisible(true);
        level.addFreshEntity(elf);
    }

    /**
     * Stands Elladan at the camp of Thorenhad while the cave search is the step in hand.
     * <p>
     * The notes end the cave search at his camp rather than at the valley: the stone is his to carry back
     * to Glorfindel, and he is the one with the next lead. He is a campaign giver wearing the same three
     * tags as the Elf in the valley, so the same right-click takes the finished page off the player and
     * the beryl with it, and he is stood once and found by his tag afterwards, so a reloaded world keeps
     * one Elladan at the fire instead of one every time the player looks.
     */
    private static void keepElladan(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        // He stands from the cave page until the Giant's has been taken off him, rather than only while the
        // stone is in somebody's hand: the notes send the player back to the camp with the kill, so a camp
        // that emptied as the caves were paid would have nobody to give the Giant's page or take it back.
        // He stands from the first of the notes' rider beats, not only from the cave search: two of those
        // pages are his to hear at the camp, and a camp that only filled in once the caves were paid would
        // have nobody in it to hear what the ruins held. He stays until the chapter's last page, which is
        // the one he hands on.
        // He stands while he holds the page the West is on and while he is owed the one in the player's
        // book, which the line itself says: he hands most of the chapter over, so a range written down
        // here would go stale the moment a page was inserted above him.
        if (!carriesWordFor(player, ELLADAN) && !holdsOrTakes(player, ELLADAN)) return;
        Places.Place camp = Places.ensure(player, THORENHAD_PLACE, CAMP_RADIUS);
        if (camp == null || !camp.dimension().equals(level.dimension())) return;
        if (camp.centre().distToCenterSqr(player.position()) > ELLADAN_REACH * ELLADAN_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(camp.centre()).inflate(ELLADAN_REACH),
                seen -> seen.entityTags().contains(ELLADAN_TAG)).isEmpty()) return;
        // The camp is a camp before it is a man: the tent goes down first, and he is stood at it in the
        // same look, so there is never an Elf in a field waiting for a tent that never comes.
        if (!standTent(level, camp)) return;
        BlockPos stand = floorNear(level, camp.centre());
        if (stand == null) return;
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob elf)) return;
        elf.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        elf.setPersistenceRequired();
        elf.addTag(QuestGivers.GIVER_TAG);
        elf.addTag(QuestGivers.CAMPAIGN_TAG);
        elf.addTag(QuestGivers.CAMPAIGN_GIVER_TAG);
        elf.addTag(Alliance.WEST.giverTag());
        elf.addTag(ELLADAN_TAG);
        elf.setCustomName(Component.literal(ELLADAN).withStyle(ChatFormatting.GOLD));
        elf.setCustomNameVisible(true);
        level.addFreshEntity(elf);
    }

    /**
     * Stands Barachen at his camp while the camp is the step the West is on.
     * <p>
     * He is the scout whose word turns the line from a search of the dark into a Giant somebody saw, and
     * the notes put his fire south of the Great East Road: a camp is a clearing and a fire, so it is a
     * named place the map already draws ground for rather than a hall the campaign raises. He wears the
     * same three tags as the Elves, so the same right-click hands and takes his pages, and he is stood
     * once and found by tag afterwards, so a reloaded world keeps one scout at the fire.
     * <p>
     * What puts him there is the stage rather than the page in his own name: a walk page settles the
     * moment the player stands in the circle, so a scout who vanished as they arrived would never be
     * spoken to. He stands from the page that walks to him until the page he hands has been taken off
     * him - one stage later - and no further, so the camp does not follow the player across the map.
     */
    private static void keepBarachen(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!carriesWordFor(player, BARACHEN) && !holdsOrTakes(player, BARACHEN)) return;
        Places.Place fire = Places.ensure(player, BARACHEN_PLACE, CAMP_RADIUS);
        if (fire == null || !fire.dimension().equals(level.dimension())) return;
        if (fire.centre().distToCenterSqr(player.position()) > BARACHEN_REACH * BARACHEN_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(fire.centre()).inflate(BARACHEN_REACH),
                seen -> seen.entityTags().contains(BARACHEN_TAG)).isEmpty()) return;
        // The camp is a camp before it is a man: the tent goes down first, so there is never a scout in a
        // field waiting for a tent that never comes.
        if (!standTent(level, fire)) return;
        BlockPos stand = floorNear(level, fire.centre());
        if (stand == null) return;
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob scout)) return;
        scout.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        scout.setPersistenceRequired();
        scout.addTag(QuestGivers.GIVER_TAG);
        scout.addTag(QuestGivers.CAMPAIGN_TAG);
        scout.addTag(QuestGivers.CAMPAIGN_GIVER_TAG);
        scout.addTag(Alliance.WEST.giverTag());
        scout.addTag(BARACHEN_TAG);
        scout.setCustomName(Component.literal(BARACHEN).withStyle(ChatFormatting.GOLD));
        scout.setCustomNameVisible(true);
        level.addFreshEntity(scout);
    }

    /** The slot a page of this shape holds in the West's line, or -1 when the line has none of them. */
    private static int stageOf(Quest.Kind kind, String target) {
        List<Page> line = PAGE_LINES.get(Alliance.WEST);
        for (int i = 0; i < line.size(); i++) {
            Page page = line.get(i);
            if (page.kind() == kind && page.target().equals(target)) return i;
        }
        return -1;
    }

    /**
     * The slot the scout's own page holds in the West's line, read off the line rather than written down a
     * second time: a page inserted above his would otherwise leave him standing at the wrong step. His page
     * names him rather than his fire now that the notes have him spoken to rather than merely walked to.
     */
    private static int campStage() {
        return stageOfTitle(BARACHEN_TITLE);
    }

    /**
     * Puts the camp's tent down on the ground Thorenhad is named for, and answers whether it is standing.
     * <p>
     * Thorenhad is a place rather than a site - one camp in the Trollshaws for everybody in the world,
     * not ground raised per player - so the tent is laid straight from the template instead of through
     * {@link Sites}, which keeps one site per player and is already holding the troll cave this page was
     * walked to. It is seated the way a site is: on the highest ground under the footprint, with the
     * drop beneath it filled, so a tent on a slope stands rather than hangs off one side of it.
     * <p>
     * Nothing is built into a chunk that is not loaded, since a block written there is quietly dropped;
     * the camp is only looked at when the player is within sight of it, so the tent is laid on the visit
     * that sees it. Laying it twice is laying the same blocks in the same place, which is why a camp
     * whose Elladan has been killed rather than merely mislaid simply puts him back.
     */
    private static boolean standTent(ServerLevel level, Places.Place camp) {
        Identifier id = Identifier.tryParse(CAMP_TENT);
        if (id == null) return true;
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) return true;
        int wide = template.getSize().getX();
        int deep = template.getSize().getZ();
        if (wide <= 0 || deep <= 0) return true;
        BlockPos corner = camp.centre().offset(-wide / 2, 0, -deep / 2);
        if (!level.hasChunkAt(corner) || !level.hasChunkAt(corner.offset(wide, 0, deep))) return false;
        // The highest column under the tent is the tent's floor, so the ground comes up to it rather than
        // through it, whichever side of the camp is the uphill one.
        int floor = Integer.MIN_VALUE;
        for (int dx = 0; dx <= wide; dx += Math.max(1, wide / 4)) {
            for (int dz = 0; dz <= deep; dz += Math.max(1, deep / 4)) {
                floor = Math.max(floor, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        corner.getX() + dx, corner.getZ() + dz));
            }
        }
        corner = new BlockPos(corner.getX(), floor, corner.getZ());
        template.placeInWorld(level, corner, corner, new StructurePlaceSettings(), level.getRandom(), 2);
        underpin(level, corner, wide, deep);
        return true;
    }

    /**
     * Builds the valley the notes call the last homely house, out of the pieces the pack ships for it.
     * <p>
     * The tent's rules hold here: nothing is raised into a chunk that is not loaded, and laying a piece
     * twice is laying the same blocks in the same place. What the valley adds is one floor for the whole
     * of it - the square is seated on the highest ground under it and every other piece is laid at that
     * same height, so the houses meet the paving rather than stepping up and down a hillside - and ground
     * under each piece, so a building on the downhill side stands on dirt rather than on air.
     */
    private static boolean standValley(ServerLevel level, Places.Place valley) {
        StructureTemplate square = valleyPiece(level, "elven_square");
        if (square == null) return false;
        int floor = seatOf(level, cornerOf(valley.centre(), 0, 0, square), square);
        if (floor == Integer.MIN_VALUE) return false;
        // The paving is laid into a hole rather than under a hill. The floor is the highest ground under
        // the square, so on a slope the hillside above it hangs over the plaza and the trees come up
        // through the middle of it: the air over the square's own footprint is taken out first, and the
        // pieces then fill the floor that was cleared. It only ever runs on the visit that lays the
        // valley, since a built valley is never laid again.
        clearAbove(level, cornerOf(valley.centre(), 0, 0, square), square, floor);
        boolean whole = true;
        for (Part part : VALLEY_PARTS) {
            StructureTemplate piece = valleyPiece(level, part.name());
            if (piece == null) continue;
            BlockPos corner = cornerOf(valley.centre(), part.dx(), part.dz(), piece);
            // A piece whose ground is not loaded is left for a visit that can see it: the rest of the
            // valley is already standing, and laying it again changes nothing.
            if (seatOf(level, corner, piece) == Integer.MIN_VALUE) {
                whole = false;
                continue;
            }
            BlockPos at = new BlockPos(corner.getX(), floor, corner.getZ());
            piece.placeInWorld(level, at, at, new StructurePlaceSettings(), level.getRandom(), 2);
            underpin(level, at, piece.getSize().getX(), piece.getSize().getZ());
        }
        return whole;
    }

    /** One of the valley's templates, or null when the pack has no such piece. */
    private static StructureTemplate valleyPiece(ServerLevel level, String name) {
        Identifier id = Identifier.tryParse("tolkienmobs:key_structures/" + name);
        return id == null ? null : level.getStructureManager().get(id).orElse(null);
    }

    /** The corner a piece is laid from, with its middle put at that step from the place's middle. */
    private static BlockPos cornerOf(BlockPos centre, int dx, int dz, StructureTemplate piece) {
        return centre.offset(dx - piece.getSize().getX() / 2, 0, dz - piece.getSize().getZ() / 2);
    }

    /**
     * The highest ground under a piece's footprint, which is the height its floor is laid at, or
     * {@code Integer.MIN_VALUE} when a corner of that footprint is in a chunk nobody has loaded - a block
     * written into an unloaded chunk is quietly dropped, so that piece has to wait.
     */
    private static int seatOf(ServerLevel level, BlockPos corner, StructureTemplate piece) {
        int wide = piece.getSize().getX();
        int deep = piece.getSize().getZ();
        if (wide <= 0 || deep <= 0) return Integer.MIN_VALUE;
        if (!level.hasChunkAt(corner) || !level.hasChunkAt(corner.offset(wide, 0, deep))) {
            return Integer.MIN_VALUE;
        }
        int floor = Integer.MIN_VALUE;
        for (int dx = 0; dx <= wide; dx += Math.max(1, wide / 4)) {
            for (int dz = 0; dz <= deep; dz += Math.max(1, deep / 4)) {
                floor = Math.max(floor, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                        corner.getX() + dx, corner.getZ() + dz));
            }
        }
        return floor;
    }

    /** How much air is kept over the square, in blocks: taller than any piece the valley lays. */
    private static final int PLAZA_AIR = 16;

    /**
     * Takes the air over a piece's footprint, so a valley cut into a hillside has a plaza rather than a
     * hillside with paving under it. Only the visit that lays the valley calls this, and that visit only
     * happens where nothing of the valley stands yet, so nothing a player raised there is cleared later.
     */
    private static void clearAbove(ServerLevel level, BlockPos corner, StructureTemplate piece, int floor) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int wide = piece.getSize().getX();
        int deep = piece.getSize().getZ();
        for (int dx = 0; dx < wide; dx++) {
            for (int dz = 0; dz < deep; dz++) {
                for (int dy = 1; dy <= PLAZA_AIR; dy++) {
                    cursor.set(corner.getX() + dx, floor + dy, corner.getZ() + dz);
                    if (!level.hasChunkAt(cursor)) continue;
                    if (level.getBlockState(cursor).isAir()) continue;
                    level.setBlock(cursor, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    /**
     * Names whatever stones the valley's own templates carry. The stone's place in the square is written
     * into the piece that stands there, which is where a building's middle is actually known, rather than
     * into a coordinate here that goes stale the moment a piece is moved - and a piece with no stone in it
     * simply names nothing.
     */
    private static void nameValleyStones(ServerLevel level, ServerPlayer player, Places.Place valley) {
        String name = Component.translatable("place.tolkienmobs." + SUMMON_PLACE).getString();
        for (Part part : VALLEY_PARTS) {
            StructureTemplate piece = valleyPiece(level, part.name());
            if (piece == null) continue;
            Stones.nameLaid(level, cornerOf(valley.centre(), part.dx(), part.dz(), piece),
                    piece.getSize(), name, player);
        }
    }

    /** Fills the ground under a piece that has been laid, so nothing on a slope stands on air. */
    private static void underpin(ServerLevel level, BlockPos corner, int wide, int deep) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < wide; dx++) {
            for (int dz = 0; dz < deep; dz++) {
                for (int dy = 1; dy <= 4; dy++) {
                    cursor.set(corner.getX() + dx, corner.getY() - dy, corner.getZ() + dz);
                    if (!level.getBlockState(cursor).isAir()) break;
                    level.setBlock(cursor, net.minecraft.world.level.block.Blocks.DIRT.defaultBlockState(), 2);
                }
            }
        }
    }

    /**
     * Whether the valley is already standing. Its square is bedded on glowstone, which no ground grows by
     * itself, so a run of glowstone under the middle of the place is that square and nothing else: the
     * reading costs a few block reads and keeps a built valley from being laid again on every visit, which
     * would put back anything a player had built inside it.
     */
    private static boolean valleyBuilt(ServerLevel level, Places.Place valley) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dy = -16; dy <= 12; dy++) {
            cursor.set(valley.centre().getX(), valley.centre().getY() + dy, valley.centre().getZ());
            if (level.getBlockState(cursor).is(net.minecraft.world.level.block.Blocks.GLOWSTONE)) return true;
        }
        return false;
    }

    /**
     * Keeps the valley built and lived in while the player is close enough to see it: the buildings stood
     * once, and the Elves who walk the square kept up to their number.
     */
    private static void keepValleyStanding(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Places.Place valley = Places.place(level, SUMMON_PLACE);
        if (valley == null || !valley.dimension().equals(level.dimension())) return;
        if (valley.centre().distToCenterSqr(player.position()) > VALLEY_REACH * VALLEY_REACH) return;
        if (!valleyBuilt(level, valley)) {
            standValley(level, valley);
            // A stone the pieces carry is named by the visit that lays them, rather than planted at a
            // coordinate the class has to know: the template says where the stone is, and this says
            // what it is called.
            nameValleyStones(level, player, valley);
        }
        standValleyElves(level, valley);
    }

    /**
     * Stands the valley's own Elves on the square, up to their number and no further.
     * <p>
     * They are ordinary bodies and not givers: no tag a right-click reads, no gold name over them, and no
     * work to hand. What makes them stay is a home as wide as the square, because a villager's stroll is
     * written for a village - without a home it walks toward the nearest meeting point, which from a valley
     * means out of the gate and across the country. A body counts toward the number by its tag, so one that
     * is killed is replaced on a later visit and one that is only reloaded is not doubled.
     */
    private static void standValleyElves(ServerLevel level, Places.Place valley) {
        int walking = level.getEntities((Entity) null, new AABB(valley.centre()).inflate(VALLEY_REACH),
                seen -> seen.entityTags().contains(VALLEY_ELF_TAG)).size();
        for (int i = walking; i < VALLEY_ELVES && i < VALLEY_PATHS.length; i++) {
            BlockPos stand = floorOf(level,
                    valley.centre().offset(VALLEY_PATHS[i][0], 0, VALLEY_PATHS[i][1]));
            if (stand == null) continue;
            // The pack's own Elves, not the human body the named Elves of the line are stood in: these
            // six are a people going about a valley and should read as Elves at a glance.
            Entity entity = TolkienEntities.ENTITY_TTM_ELVES.get().create(level, EntitySpawnReason.EVENT);
            if (!(entity instanceof Mob elf)) continue;
            elf.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                    level.getRandom().nextFloat() * 360.0F, 0.0F);
            elf.setPersistenceRequired();
            elf.addTag(VALLEY_ELF_TAG);
            elf.setHomeTo(valley.centre(), VALLEY_HOME);
            elf.setCustomName(Component.translatable("campaign.tolkienmobs.valley_elf." + (i + 1)));
            elf.setCustomNameVisible(true);
            level.addFreshEntity(elf);
        }
    }

    /**
     * Elrond, who takes the news at the top of the chapter, sends the player to his sons, and hears the
     * tidings carried back at the end of it.
     */
    private static void keepElrond(ServerPlayer player) {
        keepElfInValley(player, ELROND, ELROND_TAG);
    }

    /** Legolas, who Elrond sends the player on to, in the guest room of the same valley. */
    private static void keepLegolas(ServerPlayer player) {
        keepElfInValley(player, LEGOLAS, LEGOLAS_TAG);
    }

    /**
     * Stands one of the Elves in the valley while the page titled after their part of it is the step the
     * West is on, and for the page after it as well, so the page they hand can be taken off them. The
     * valley is one place and the pages do not overlap, so only one of them stands there at a time:
     * Glorfindel while the stone is asked at the ford, Elrond for the audience, Legolas for the guest room.
     */
    private static void keepElfInValley(ServerPlayer player, String name, String tag) {
        if (!(player.level() instanceof ServerLevel level)) return;
        // A page of talk names the body it is owed to, and that body has to be somewhere to be spoken
        // to: the word Legolas sends back to Elrond arrives at a library that would otherwise be empty,
        // because the step it belongs to is several pages past the one that stood him there.
        if (!carriesWordFor(player, name) && !holdsOrTakes(player, name)) return;
        Places.Place valley = Places.ensure(player, SUMMON_PLACE, CAMP_RADIUS);
        if (valley == null || !valley.dimension().equals(level.dimension())) return;
        if (valley.centre().distToCenterSqr(player.position()) > VALLEY_REACH * VALLEY_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(valley.centre()).inflate(VALLEY_REACH),
                seen -> seen.entityTags().contains(tag)).isEmpty()) return;
        int[] spot = VALLEY_STANDS.getOrDefault(tag, VALLEY_STANDS.get(ELROND_TAG));
        BlockPos stand = floorNear(level, valley.centre().offset(spot[0], 0, spot[1]), player);
        if (stand == null) return;
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob elf)) return;
        elf.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        elf.setPersistenceRequired();
        elf.addTag(QuestGivers.GIVER_TAG);
        elf.addTag(QuestGivers.CAMPAIGN_TAG);
        elf.addTag(QuestGivers.CAMPAIGN_GIVER_TAG);
        elf.addTag(Alliance.WEST.giverTag());
        elf.addTag(tag);
        elf.setCustomName(Component.literal(name).withStyle(ChatFormatting.GOLD));
        elf.setCustomNameVisible(true);
        level.addFreshEntity(elf);
    }

    /**
     * The slot a page of this title holds in the West's line, or -1 when the line has none. Read by title
     * rather than by target because two pages of the line can name the same ground - the valley is walked to
     * twice - and only the title tells them apart.
     */
    private static int stageOfTitle(String title) {
        List<Page> line = PAGE_LINES.get(Alliance.WEST);
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).title().equals(title)) return i;
        }
        return -1;
    }

    /**
     * Whether a page of talk in hand names this body. What it is asked of the book rather than of the
     * line, so an errand from a herald stands its speaker just as a campaign page stands Elrond.
     */
    private static boolean carriesWordFor(ServerPlayer player, String name) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.talks() && name.equalsIgnoreCase(quest.person())) return true;
        }
        return false;
    }

    /**
     * Raises a troll cave ahead of the player while the cave page is the stone's asking in hand.
     * <p>
     * The old campaign named three caves on a map that was drawn by hand, and the pack's own caves are a
     * Mirkwood jigsaw: a player carrying this page through Eriador could walk a country that has no cave
     * in it at all, which is a page nobody can finish. So the campaign cuts its own, the way it raises the
     * camp and the hilltop, and pitches it on the road rather than at a landmark's country: the cave is
     * ground the page is walked to, so it belongs ahead of the player and not in the middle of Eriador.
     * <p>
     * It is pitched once. A second page to ground the player has not reached yet is the same ground, so
     * this only asks when they have no site of their own - and the ford's asking names the ford, which is
     * ground the map already has, so nothing is built for it.
     */
    private static void keepCaveOpen(ServerPlayer player) {
        Quest stone = null;
        for (Quest quest : QuestLedger.active(player)) {
            if (asksForStone(quest)) {
                stone = quest;
                break;
            }
        }
        if (stone == null || stone.kind() != Quest.Kind.SURVEY || !stone.namedGround().isEmpty()) return;
        String cave = caveFor(player);
        Sites.Site site = Sites.siteOf(player);
        // The crag already pitched is the one this asking is made in, so long as it has not been walked
        // into yet: a crag the player has already searched is the last asking's, and the next asking wants
        // a hill of its own rather than a second walk to the first one.
        if (site != null && !Sites.isCave(site.structure())) {
            // A site of another kind is another page's ground while that page is carried - and the
            // leftovers of a page that is done. A crag is the one thing the stone's cave page cannot be
            // answered without, so ground nobody is walking to any more is ground the crag may have.
            if (carriesSitePage(player)) return;
            site = null;
        }
        if (site != null && (!site.found() || site.structure().equals(cave))) return;
        Sites.plan(player, cave, false);
    }

    /**
     * The crag the stone's next asking is made in: the first asking is Bandrith's crag, the second
     * Hoklond's, the third Sholdroth's, and the count of askings already made is the cave the page has
     * reached. The notes name three caves and expect three walks, so the campaign cuts three crags rather
     * than raising one hill three times.
     */
    /** Whether the book still carries a page whose ground is a site, which is whose site is not spare. */
    private static boolean carriesSitePage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.isSite()) return true;
        }
        return false;
    }

    private static String caveFor(ServerPlayer player) {
        Quest stone = stoneQuest(player);
        return Sites.caveAt(stone == null ? 0 : stone.progress());
    }

    /**
     * Whether the stone has been asked everything it was lent for, which is the last of its pages and not
     * the first: the ford is one asking and the caves are another, and a stone given back after the ford
     * would leave the player searching caves with their hands empty.
     */
    private static boolean stonePaid(List<Page> line, int paid) {
        if (paid >= line.size() || line.get(paid).kind() != Quest.Kind.SURVEY) return false;
        // The stone is lent twice and goes back once: the ford is one asking and the caves another, and
        // the page that names ground is the first. Reading the pages' own targets keeps the two apart
        // even though the return to Glorfindel now stands between them.
        return line.get(paid).target().isEmpty();
    }

    /** How many cave completion tokens a player carries, counted off the pack rather than noted down. */
    private static int countCaveTokens(ServerPlayer player) {
        var inventory = player.getInventory();
        int held = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(TolkienItems.ITEM_CAVECOMPLETE.get())) held += stack.getCount();
        }
        return held;
    }

    /** Takes the tokens the camp asks for, and no more than it asked for: spare ones are the player's. */
    private static void takeCaveTokens(ServerPlayer player, int wanted) {
        var inventory = player.getInventory();
        int taken = 0;
        for (int slot = 0; slot < inventory.getContainerSize() && taken < wanted; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(TolkienItems.ITEM_CAVECOMPLETE.get())) continue;
            int take = Math.min(stack.getCount(), wanted - taken);
            stack.shrink(take);
            taken += take;
        }
    }

    /**
     * Whether the camp holds the stone back because the chambers' tokens are not all in hand.
     * <p>
     * The notes make the end of the cave search an item page: three cave completion tokens and the beryl,
     * handed to Elladan. The tokens are the proof of three chambers emptied, so they are what the page is
     * checked against at the door rather than something the camp asks for afterwards - a player who has
     * searched one cave and walks into the camp is told what is short instead of being sent on to the next
     * lead with a stone he did not earn.
     *
     * @return whether the hand-over was refused, having said why
     */
    public static boolean stoneHeldBack(ServerPlayer player, String giver) {
        if (!ELLADAN.equalsIgnoreCase(giver)) return false;
        Quest stone = stoneQuest(player);
        if (stone == null || !stone.complete() || !stone.namedGround().isEmpty()) return false;
        int held = countCaveTokens(player);
        if (held >= STONE_CAVES) return false;
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.cave.short",
                        Component.literal(String.valueOf(held)), Component.literal(String.valueOf(STONE_CAVES)))
                .withStyle(ChatFormatting.GRAY));
        return true;
    }

    /**
     * Whether the camp holds the tale back because the bridle is not in hand.
     * <p>
     * The notes' missing rider is reported with his horse's tack: the dark saddle is taken off the beast
     * in the ruin, and Elladan hears the rest of the tale when it is laid in front of him. Asking for it at
     * the door rather than after the hand-over is what stops a player being paid for a report they cannot
     * make - and it is the one page of the chapter whose subject is an object rather than a place.
     */
    public static boolean bridleHeldBack(ServerPlayer player, String giver) {
        if (!ELLADAN.equalsIgnoreCase(giver)) return false;
        Front[] row = PROGRESS.get(player.getUUID());
        if (row == null) return false;
        Front front = row[Alliance.WEST.ordinal()];
        List<Page> line = PAGE_LINES.get(Alliance.WEST);
        Quest handed = front.handed();
        if (handed == null || !handed.complete() || front.stage() >= line.size()) return false;
        if (!RIDER_RETURN_TITLE.equals(line.get(front.stage()).title())) return false;
        if (countBridles(player) > 0) return false;
        // A chapter walked before the bridle was an item can be holding this page with no saddle to show
        // for it. The tack is put in the pack here rather than leaving a page that cannot be finished, so
        // the next word at the fire is heard.
        handOver(player, TolkienItems.ITEM_DARKSADDLE.get(), 1);
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.bridle.short")
                .withStyle(ChatFormatting.GRAY));
        return true;
    }

    /** How many dark saddles a player carries, counted off the pack rather than noted down. */
    private static int countBridles(ServerPlayer player) {
        var inventory = player.getInventory();
        int held = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(TolkienItems.ITEM_DARKSADDLE.get())) held += stack.getCount();
        }
        return held;
    }

    /** Takes the bridle back, one saddle and no more: a second one is the player's. */
    private static void takeBridle(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(TolkienItems.ITEM_DARKSADDLE.get())) continue;
            stack.shrink(1);
            return;
        }
    }

    /**
     * What the camp puts in a player's hands when the cave search comes home.
     * <p>
     * The notes pay the end of the search with random equipment, an enchantment and some ore. This pack does
     * not roll loot off a page, and a rolled page would be a table to keep in step with every item added, so
     * the reward is the two of the three that can be carried and used: a blade out of the Trollshaws' own
     * smiths and the mithril that came up with it. The enchantment is left out rather than handed over as a
     * blank book nobody can put on anything.
     * <p>
     * The standing the page pays is a tenth of the notes' hundred - ten of the West's - because a hundred is
     * most of a rank in this pack and the cave search is one act of several.
     */
    private static void payCaveReward(ServerPlayer player) {
        handOver(player, TolkienItems.ITEM_REFORGEDSWORD.get(), 1);
        handOver(player, TolkienItems.ITEM_MITHRILNUGGET.get(), 4);
    }

    /** Puts a reward in the pack, or on the ground at the player's feet when the pack has no room for it. */
    private static void handOver(ServerPlayer player, net.minecraft.world.item.Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    /** Takes the lent stone back out of the player's pack, one stone and no more. */
    private static void takeStone(ServerPlayer player) {
        var inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            if (!inventory.getItem(slot).is(TolkienItems.ITEM_BERYL.get())) continue;
            inventory.getItem(slot).shrink(1);
            return;
        }
    }

    /** How far apart two blocks stand, in blocks and on the level: the stone is asked about ground, not height. */
    private static double apart(BlockPos from, BlockPos to) {
        double dx = from.getX() - to.getX();
        double dz = from.getZ() - to.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * Puts the stone in a player's hand and says so. A player already holding it is left alone, which
     * is what keeps the line on the tick from repeating itself.
     */
    private static void giveStone(ServerPlayer player, String line) {
        if (Places.carriesBeryl(player)) return;
        ItemStack stone = new ItemStack(TolkienItems.ITEM_BERYL.get());
        if (!player.getInventory().add(stone)) player.drop(stone, false);
        player.sendSystemMessage(Component.translatable(line).withStyle(ChatFormatting.AQUA));
    }

    /**
     * Keeps the stone in the hand of a player carrying the page it was given with.
     * <p>
     * That page is finished by ground the stone answers, and ground only answers a hand that is holding
     * it - so a stone dropped in a river would stop the West's line for good on an errand nobody could
     * finish, which is worse than the page that gave the player nothing at all. What was lent is lent
     * for the length of the errand: it comes back to the hand it was put in, and says so, so it reads
     * as the stone's own business rather than as a pack tidying itself up.
     */
    private static void keepStoneInHand(ServerPlayer player) {
        for (Quest page : QuestLedger.active(player)) {
            if (!asksForStone(page)) continue;
            giveStone(player, STONE_RETURNED);
            return;
        }
    }

    /**
     * Writes down the ground a page in hand names, when this world has never had it written.
     * <p>
     * A herald will not offer a walk to a place the world has not placed - the offer is checked, and the
     * place is named from the drawing if it never was - but the campaign's own pages are a relay: they are
     * handed over by the speaker before, not offered, and nothing checked their ground. A page that names
     * a ford nobody has ever written is a page with two faults at once: the book can only say "the ford",
     * and the needle has nothing to point at, so the player is told the name of a place and no way to find
     * it. The ground is written here, once, and the needle is sent again the moment it exists, because the
     * one it was sent with was empty.
     */
    private static void keepGroundWritten(ServerPlayer player) {
        for (Quest page : QuestLedger.active(player)) {
            if (!page.story() || page.isSite()) continue;
            // A page of the stone names its ground in a field of its own rather than in a spot, because
            // it is settled by holding the beryl over a place instead of by arriving at it - and that is
            // why this skipped the very page it was written for.
            String named = namedGroundOf(page);
            if (named == null) continue;
            if (Places.place(player.level(), named) != null) continue;
            Quest.Spot spot = page.spot();
            int radius = spot == null ? Places.DEFAULT_RADIUS : spot.radius();
            if (Places.ensure(player, named, radius) == null) continue;
            LOGGER.info("Wrote {} for the page carried by {}", named, player.getName().getString());
            Waypoint.send(player, QuestLedger.active(player));
            return;
        }
    }

    /**
     * The place a story page names, or null when it names none this world would have to write down. A
     * walked page carries it in its spot; a page of the stone carries it as the ground to hold over.
     */
    @Nullable
    private static String namedGroundOf(Quest page) {
        Quest.Spot spot = page.spot();
        if (spot != null) return spot.place();
        String named = page.namedGround();
        return named.isEmpty() ? null : named;
    }

    /**
     * Puts the quarry a front's page names on the ground ahead of the player, so the page can be walked
     * the moment it is taken.
     * <p>
     * A page that asks for two wights is asking for the barrow downs, and the barrows are a country a
     * world may not have within a day's ride: the hunt then reads as a wild goose chase rather than as
     * the errand the herald just described. The campaign has already learned to stand the Rider up on
     * the road for exactly this reason, and this is the same courtesy done for lesser quarry - a few of
     * them, ahead of the player on the surface, marked so a second hand-over of the same page does not
     * double them.
     * <p>
     * Only part of the page is ever put down ({@link #QUARRY_LIMIT}), and only for a hunt of a front's
     * own: the campaign's own page has his own arrival, a fetch has nothing to stand up, and the rest of
     * a long hunt is the hunt.
     */
    private static boolean standQuarryUp(ServerPlayer player, Quest page) {
        if (!(player.level() instanceof ServerLevel level)) return false;
        // The campaign's own last acts are not stood up on the road: the Rider has his own arrival
        // out under open sky, and the Witch-king belongs in the castle rather than beside the bench
        // the page was taken at.
        if (page.kind() != Quest.Kind.HUNT || isFinale(page)) return false;
        EntityType<?> quarry = page.entityType();
        if (quarry == null) return false;
        if (quarryOnTheGround(player, quarry)) return false;
        RandomSource random = level.getRandom();
        Direction facing = player.getDirection();
        Direction across = facing.getClockWise();
        int wanted = Math.min(page.goal(), QUARRY_LIMIT);
        int stood = 0;
        for (int i = 0; i < wanted; i++) {
            int reach = QUARRY_AHEAD + random.nextInt(QUARRY_SPREAD);
            int side = random.nextInt(QUARRY_SIDE * 2 + 1) - QUARRY_SIDE;
            int x = player.blockPosition().getX() + facing.getStepX() * reach + across.getStepX() * side;
            int z = player.blockPosition().getZ() + facing.getStepZ() * reach + across.getStepZ() * side;
            // A chunk with no ground under it yet is skipped rather than waited for: the rest of the
            // hunt is out there, and this is the page being generous, not the page being complete.
            if (!level.hasChunkAt(new BlockPos(x, player.blockPosition().getY(), z))) continue;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            Entity entity = quarry.create(level, EntitySpawnReason.EVENT);
            if (!(entity instanceof Mob mob)) continue;
            mob.snapTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
            mob.setPersistenceRequired();
            mob.addTag(QUARRY_TAG);
            if (level.addFreshEntity(mob)) stood++;
        }
        if (stood > 0) {
            player.sendSystemMessage(Component.translatable("quest.tolkienmobs.quarry.road",
                            page.targetName())
                    .withStyle(ChatFormatting.DARK_RED));
        }
        return stood > 0;
    }

    /**
     * Keeps a front page's quarry coming: it is owed to the road, so every look asks whether it is
     * owed yet, and pays it out once. Three things have to be true first - the page still carried and
     * unfinished, the clock run out, and the player out under open sky and a walk away from the bench
     * the page was taken at. What that buys is a meeting the player was already walking into when it
     * arrived, rather than a mob standing beside the herald who described it.
     */
    private static void keepQuarryComing(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Quest page = carriedQuarryPage(player);
        if (page == null) {
            QUARRY_CALLED.remove(player.getUUID());
            return;
        }
        Call call = QUARRY_CALLED.get(player.getUUID());
        if (call == null) {
            // Owed with no road remembered - a reloaded world - so the clock starts again here.
            QUARRY_CALLED.put(player.getUUID(), call(player));
            return;
        }
        // One hand-over owes one meeting: quarry killed is a hunt that moves on, not a page that
        // keeps restocking the road the player is standing on.
        if (call.spent()) return;
        if (quarryOnTheGround(player, page.entityType())) return;
        if (level.getGameTime() < call.due()) return;
        if (!player.level().canSeeSky(player.blockPosition())) return;
        int dx = player.blockPosition().getX() - call.from().getX();
        int dz = player.blockPosition().getZ() - call.from().getZ();
        if (dx * dx + dz * dz < AMBUSH_WAY * AMBUSH_WAY) return;
        if (standQuarryUp(player, page)) {
            QUARRY_CALLED.put(player.getUUID(), spent(call));
        }
    }

    /** The quarry a front page asks for, while the player is carrying one; null when nothing is owed. */
    @Nullable
    private static Quest carriedQuarryPage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (carriesQuarry(quest)) return quest;
        }
        return null;
    }

    /**
     * Whether taking a page puts quarry on the ground, which is a front's hunt and no other: a fetch
     * has nothing to stand up, a walk or a conversation is not about a body, and the campaign's own
     * last acts are placed by their own pages (the Rider on the road, the Witch-king in his castle).
     */
    private static boolean carriesQuarry(Quest page) {
        return page.kind() == Quest.Kind.HUNT && page.entityType() != null && !isFinale(page);
    }

    /** The road a page's quarry is owed to: where the player stands now, and when it is owed. */
    private static Call call(ServerPlayer player) {
        return new Call(player.blockPosition(), player.level().getGameTime() + AMBUSH_DELAY, false);
    }

    /** The same call, paid out: this hand-over owes no more quarry. */
    private static Call spent(Call call) {
        return new Call(call.from(), call.due(), true);
    }

    /** Whether a page's quarry is already standing near the player, put there by an earlier hand-over. */
    private static boolean quarryOnTheGround(ServerPlayer player, EntityType<?> quarry) {
        return !player.level().getEntities(player,
                player.getBoundingBox().inflate(QUARRY_SEARCH),
                found -> found.getType() == quarry && found.entityTags().contains(QUARRY_TAG)).isEmpty();
    }

    /**
     * Whether a page that names one body is answered by this death.
     * <p>
     * A hunt is normally answered by a kind of mob, read off the target id. The Wood-troll's page names
     * a creature instead, so the tag is the whole of the test: the campaign put it on the one body it
     * stood up, and a troll met in a cave is a troll rather than the page's quarry.
     */
    public static boolean namesBody(Quest quest, Entity victim) {
        return quest.kind() == Quest.Kind.HUNT && MATUNA.equals(quest.target())
                && victim.entityTags().contains(MATUNA_TAG);
    }

    /**
     * Stands the Wood-troll up ahead of the player while his page is carried and unfinished.
     * <p>
     * The Rider's rules hold here, and for the same reason: a page taken at an Elf's shoulder cannot be
     * finished by a body standing at it, and a page whose quarry the country may not have within a day's
     * ride is a goose chase. So he is put down once the player is out under open sky and a short walk
     * from where the page was taken, some way ahead of them and off the line they are walking, and he
     * stays where he was put: one meeting per hand-over, which is what {@link Call} remembers. What
     * keeps a second one from being stood up beside the first is the tag.
     */
    private static void keepMatuna(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Quest page = carriedPage(player, MATUNA);
        if (page == null || page.complete()) {
            MATUNA_CALLED.remove(player.getUUID());
            return;
        }
        if (matunaOnTheGround(player)) return;
        Call called = MATUNA_CALLED.get(player.getUUID());
        if (called == null) {
            // Owed with no road remembered - a reloaded world - so the clock starts again here.
            MATUNA_CALLED.put(player.getUUID(), call(player));
            return;
        }
        if (called.spent()) return;
        if (level.getGameTime() < called.due()) return;
        if (!player.level().canSeeSky(player.blockPosition())) return;
        int dx = player.blockPosition().getX() - called.from().getX();
        int dz = player.blockPosition().getZ() - called.from().getZ();
        if (dx * dx + dz * dz < MATUNA_CALL_DISTANCE * MATUNA_CALL_DISTANCE) return;
        if (standMatunaUp(player)) {
            MATUNA_CALLED.put(player.getUUID(), spent(called));
        }
    }

    /** Puts the one Wood-troll the page is about on the ground ahead, named and tagged. */
    private static boolean standMatunaUp(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return false;
        RandomSource random = level.getRandom();
        Direction facing = player.getDirection();
        Direction across = facing.getClockWise();
        int reach = MATUNA_WAY_AHEAD + random.nextInt(MATUNA_WAY_SPREAD);
        int side = random.nextInt(9) - 4;
        int x = player.blockPosition().getX() + facing.getStepX() * reach + across.getStepX() * side;
        int z = player.blockPosition().getZ() + facing.getStepZ() * reach + across.getStepZ() * side;
        // A chunk that is not loaded has no ground to stand on yet: the next look will find one.
        if (!level.hasChunkAt(new BlockPos(x, player.blockPosition().getY(), z))) return false;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        Entity entity = TolkienEntities.ENTITY_TTM_TROLL.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob troll)) return false;
        troll.snapTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        troll.setPersistenceRequired();
        troll.setCustomName(Component.translatable(MATUNA_NAME));
        troll.setCustomNameVisible(true);
        troll.addTag(MATUNA_TAG);
        if (!level.addFreshEntity(troll)) return false;
        player.sendSystemMessage(Component.translatable(MATUNA_TRACKS).withStyle(ChatFormatting.DARK_GREEN));
        return true;
    }

    /** Whether the Wood-troll of this page is already standing near the player. */
    private static boolean matunaOnTheGround(ServerPlayer player) {
        return !player.level().getEntities(player, player.getBoundingBox().inflate(MATUNA_SEARCH),
                found -> found.entityTags().contains(MATUNA_TAG)).isEmpty();
    }

    /** A page of the campaign the player is carrying whose target is this one, or null. */
    @Nullable
    private static Quest carriedPage(ServerPlayer player, String target) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.story() && quest.kind() == Quest.Kind.HUNT && target.equals(quest.target())) {
                return quest;
            }
        }
        return null;
    }
    /**
     * Stands the campaign's Rider up in front of the player, out on the ground, and marks him as the
     * campaign's so his death can be told apart from any other Nazgûl the player meets. He is placed
     * at riding distance rather than beside the player, because the meeting the page promises is one
     * the player has to ride out to.
     */
    private static void standRiderUp(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        RandomSource random = level.getRandom();
        Direction facing = player.getDirection();
        int reach = RIDER_WAY_AHEAD + random.nextInt(RIDER_WAY_SPREAD);
        int x = player.blockPosition().getX() + facing.getStepX() * reach;
        int z = player.blockPosition().getZ() + facing.getStepZ() * reach;
        // A chunk that is not loaded has no ground to stand on yet: the next look will find one.
        if (!level.hasChunkAt(new BlockPos(x, player.blockPosition().getY(), z))) return;
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        Entity entity = TolkienEntities.ENTITY_TTM_NAZGUL.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob rider)) return;
        rider.snapTo(x + 0.5D, y, z + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        rider.setPersistenceRequired();
        rider.setCustomName(Component.literal("The Rider"));
        rider.setCustomNameVisible(true);
        rider.addTag(RIDER_TAG);
        rider.addTag("tolkienmobs:rider_" + player.getUUID());

        if (level.addFreshEntity(rider)) {
            RIDER_ENTITY.put(player.getUUID(), rider.getUUID());
        }
    }

    /**
     * Keeps the campaign's Rider in the world while his page is carried and unfinished.
     * <p>
     * Two things are settled here that a one-off spawn at the hand-over could not settle. The first is
     * <em>where</em>: while the player is indoors, or still within sight of the bench the page was
     * taken at, nothing happens - and once they are out under open sky and a ride away from that door,
     * he is waiting a little ahead of them. The second is <em>whether he still is</em>: a world reloaded
     * forgets which body was his, a player who walks to another country leaves him behind, and either
     * way the page must not turn into a hunt with nothing left to hunt. What keeps a second Rider from
     * being stood up after the first is killed is the page itself - the kill is counted while the page
     * is still carried, so a finished page means the meeting has already happened.
     */
    public static void tick(ServerPlayer player) {
        if (player.tickCount % RIDER_CHECK_INTERVAL != 0) return;
        keepCrownSeated(player);
        keepWitchKing(player);
        keepQuarryComing(player);
        keepMatuna(player);
        keepStoneInHand(player);
        keepGroundWritten(player);
        Waypoint.keepCurrent(player);
        tellStoneHere(player);
        keepValleyStanding(player);
        keepGlorfindel(player);
        keepCaveOpen(player);
        keepCaveTroll(player);
        keepElladan(player);
        keepBarachen(player);
        keepElrond(player);
        keepLegolas(player);
        Quest page = carriedCampaignPage(player);
        if (page == null || page.complete()) {
            RIDER_CALLED.remove(player.getUUID());
            return;
        }
        if (livingRider(player) != null) return;
        Call called = RIDER_CALLED.get(player.getUUID());
        if (called == null) {
            // Owed with no road remembered - a reloaded world - so the clock starts again here.
            RIDER_CALLED.put(player.getUUID(), call(player));
            return;
        }
        if (player.level().getGameTime() < called.due()) return;
        if (!player.level().canSeeSky(player.blockPosition())) return;
        int dx = player.blockPosition().getX() - called.from().getX();
        int dz = player.blockPosition().getZ() - called.from().getZ();
        if (dx * dx + dz * dz < RIDER_CALL_DISTANCE * RIDER_CALL_DISTANCE) return;
        standRiderUp(player);
        if (RIDER_ENTITY.containsKey(player.getUUID())) {
            // The first meeting is what the page promised. Every one after it is him answering the only
            // thing the player has to say to him, which is that they killed him.
            player.sendSystemMessage(Component.translatable(page.progress() > 0
                            ? "quest.tolkienmobs.rider.again"
                            : "quest.tolkienmobs.rider.coming")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
    }

    /**
     * Stands the Witch-king up in the middle of the arena the campaign raised, once the player is
     * carrying his page and the sand is standing, and stands three of the Nine round him.
     * <p>
     * The arena and nothing else: a page taken while the player's own site is still the castle waits
     * rather than standing him in a citadel, because the ground the crown is fought on is the ground
     * the war raised for it. He is not put on the road ahead of the player the way lesser quarry is -
     * the page that names him is taken at a bench, and he belongs in the ring. What keeps a second one
     * from being stood up is the tag, so a reloaded world or a player who walked away finds the same
     * one waiting rather than a fresh one every look; his keepers are stood once with him and never
     * topped up, because a keeper who falls has been dealt with.
     */
    private static void keepWitchKing(ServerPlayer player) {
        if (carriedWitchKingPage(player) == null) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        Sites.Site site = Sites.siteOf(player);
        if (site == null || !site.built()) return;
        if (!Sites.BOSS_ARENA.equals(site.structure())) return;
        // Nothing is stood up in ground nobody is near: the arena has to be in front of the player for
        // the crown to be met in it, and a chunk that is not loaded would drop the body that stood there.
        if (site.centre().distToCenterSqr(player.position()) > WITCHKING_REACH * WITCHKING_REACH) return;
        // The arena a player is standing in is not necessarily the arena in the world: a site is
        // recorded against the dimension it was raised in, and only built in that one.
        if (level.dimension().equals(site.dimension()) && witchKingLiving(level, site.centre())) {
            // A body written down and read back is built afresh, habits and all.
            steadyShade(level, site.centre());
            return;
        }
        BlockPos stand = floorNear(level, site.centre());
        if (stand == null) return;
        Entity entity = TolkienEntities.ENTITY_TTM_WITCHKING.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob king)) return;
        king.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        king.setPersistenceRequired();
        king.addTag(WITCHKING_TAG);
        shadeBlind(king);
        level.addFreshEntity(king);
        standGuards(level, stand);
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.witchking.rising")
                .withStyle(ChatFormatting.DARK_PURPLE));
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.witchking.guarded")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }


    /**
     * Keeps the crown in his own house while the player is walking into it, and has him say where the war
     * is settled.
     * <p>
     * The last act is two meetings, not one. The castle page does not send the player to a fortress and
     * then to a different boss: it sends them into the house the war raised, where he is waiting in it,
     * seated, unkillable and not interested in a fight yet - and it is there, close enough to hear him, that
     * he names the sand. The page that follows is his invitation rather than a herald's, which is the whole
     * reason the arena stands before the crown instead of after it.
     * <p>
     * He does not get up: he is stood with his mind switched off (see {@link #seated}), so the meeting is a
     * man in a chair rather than a boss fight the campaign has not asked for yet - and being unhurtable
     * here as well as there is what stops a player from ending the war at the castle door.
     * <p>
     * Once the page has moved on he is dismissed the next time the player looks, leaving an empty chair
     * behind him - but only after he has spoken. Arriving at the gate is not the meeting, and a page that
     * settles on the doorstep must not take the words with it.
     */
    private static void keepCrownSeated(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        Sites.Site site = Sites.siteOf(player);
        if (site == null || !site.built() || !Sites.BOSS_CASTLE.equals(site.structure())) return;
        if (!level.dimension().equals(site.dimension())) return;
        if (site.centre().distToCenterSqr(player.position()) > WITCHKING_REACH * WITCHKING_REACH) return;
        Entity king = seatedKing(level, site.centre());
        boolean waited = carriedCastlePage(player) != null;
        if (king == null) {
            if (!waited) return;
            standSeatedKing(level, site.centre(), player);
            return;
        }
        // A body written down and read back is built afresh, habits and all.
        steadyShade(level, site.centre());
        if (SUMMONED.contains(player.getUUID())) {
            // He has said it. The page moving on is what empties the chair, and the next look does it.
            if (waited) return;
            dismissSeat(level, site.centre());
            SUMMONED.remove(player.getUUID());
            player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.witchking.gone")
                    .withStyle(ChatFormatting.DARK_PURPLE));
            return;
        }
        if (king.distanceToSqr(player) > CASTLE_MEET * CASTLE_MEET) return;
        SUMMONED.add(player.getUUID());
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.witchking.summons")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

    /** The crown as he is met in his own house, if he is already standing there. */
    @Nullable
    private static Entity seatedKing(ServerLevel level, BlockPos centre) {
        List<Entity> seated = level.getEntities((Entity) null, new AABB(centre).inflate(WITCHKING_SEARCH),
                seen -> seen instanceof WitchKingEntity && seen.entityTags().contains(CASTLE_TAG));
        return seated.isEmpty() ? null : seated.get(0);
    }

    /**
     * Seats him in his own house: no mind of his own, so he does not walk the rooms, no way to be killed
     * there - which the event that hears the blows is what enforces - and a chair under him, because a
     * crown standing in front of its own throne reads as a man who has lost the war already.
     * <p>
     * The chair is a body with nothing drawn of it rather than a pose: his own idle controller plays its
     * `sitting` clip whenever he is a passenger, which is how he sits a fell beast, so mounting him on an
     * invisible stand is the whole of it. It is put in the world before he is, because a rider with nowhere
     * to sit is dropped back onto the floor, and it is marked unbreakable and gravityless so a stray arrow
     * or a broken floor cannot take the seat out from under a meeting.
     */
    private static void standSeatedKing(ServerLevel level, BlockPos centre, ServerPlayer player) {
        BlockPos seat = CASTLE_SEAT != null ? onTopOf(level, CASTLE_SEAT) : seatOf(level, centre);
        if (seat == null) return;
        // He faces whoever walks in rather than the wall the chair was built against, and he does not turn
        // afterwards: a crown that follows you round the room is a statue, and this one is waiting.
        double dx = player.getX() - (seat.getX() + 0.5D);
        double dz = player.getZ() - (seat.getZ() + 0.5D);
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        ArmorStand chair = EntityType.ARMOR_STAND.create(level, EntitySpawnReason.EVENT);
        if (chair == null) return;
        chair.snapTo(seat.getX() + 0.5D, seat.getY(), seat.getZ() + 0.5D, yaw, 0.0F);
        chair.setInvisible(true);
        chair.setNoGravity(true);
        chair.setInvulnerable(true);
        chair.addTag(SEAT_TAG);
        if (!level.addFreshEntity(chair)) return;
        Entity entity = TolkienEntities.ENTITY_TTM_WITCHKING.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob king)) return;
        king.snapTo(seat.getX() + 0.5D, seat.getY(), seat.getZ() + 0.5D, yaw, 0.0F);
        king.setPersistenceRequired();
        king.setNoAi(true);
        king.addTag(CASTLE_TAG);
        shadeBlind(king);
        if (!level.addFreshEntity(king)) return;
        king.startRiding(chair, true, true);
    }

    /** Puts the crown and his chair away together, so an empty hall has no invisible chair standing in it. */
    private static void dismissSeat(ServerLevel level, BlockPos centre) {
        for (Entity found : level.getEntities((Entity) null, new AABB(centre).inflate(WITCHKING_SEARCH),
                seen -> seen.entityTags().contains(CASTLE_TAG) || seen.entityTags().contains(SEAT_TAG))) {
            found.discard();
        }
    }

    /**
     * Where a body stands on a block a build calls a seat: in it when it is air, on it when it is not. The
     * block a crosshair names is the chair rather than the air above the chair, and a body stood inside a
     * chair block is a body stuck in the floor.
     */
    private static BlockPos onTopOf(ServerLevel level, BlockPos seat) {
        return level.getBlockState(seat).isAir() ? seat : seat.above();
    }

    /**
     * Where a body is seated in a building: air to stand in with a roof over it, the nearest such block to
     * the middle. Walked out in rings rather than by height, so the first hit is the one closest to the
     * middle of the house instead of the first one under any floor at all.
     */
    @Nullable
    private static BlockPos seatOf(ServerLevel level, BlockPos centre) {
        for (int out = 0; out <= CASTLE_ROOM_REACH; out++) {
            for (int dx = -out; dx <= out; dx++) {
                for (int dz = -out; dz <= out; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != out) continue;
                    for (int dy = -FLOOR_DOWN; dy <= CASTLE_ROOM_UP; dy++) {
                        BlockPos feet = centre.offset(dx, dy, dz);
                        if (stands(level, feet) && roofed(level, feet)) return feet;
                    }
                }
            }
        }
        return null;
    }

    /** Whether anything at all is over a body's head within a room's height, which is what a roof is. */
    private static boolean roofed(ServerLevel level, BlockPos feet) {
        for (int up = 2; up <= CASTLE_ROOM_UP; up++) {
            if (!level.getBlockState(feet.above(up)).isAir()) return true;
        }
        return false;
    }

    /** Whether the crown is the one met in his own house, which nothing is allowed to kill. */
    public static boolean seated(Entity king) {
        return king.entityTags().contains(CASTLE_TAG);
    }

    /** Says that the crown does not fall in his own house, once every few seconds rather than on every blow. */
    public static void tellSeated(Entity king, ServerPlayer player) {
        long now = king.level().getGameTime();
        Long said = SHIELD_SAID.get(king.getUUID());
        if (said != null && now - said < SHIELD_SAY_INTERVAL) return;
        SHIELD_SAID.put(king.getUUID(), now);
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.witchking.seated")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

    /**
     * Stands three of the Nine round him, evenly on a ring of the arena's own floor. They are ordinary
     * Ring-wraiths with a tag, which is all that makes them his: the tag is what the shield reads after
     * a reload, and what keeps a keeper apart from any Nazgul a player meets on the road.
     */
    private static void standGuards(ServerLevel level, BlockPos middle) {
        RandomSource random = level.getRandom();
        for (int guard = 0; guard < GUARD_COUNT; guard++) {
            double angle = Math.PI * 2.0D * guard / GUARD_COUNT;
            int x = middle.getX() + (int) Math.round(Math.cos(angle) * GUARD_RING);
            int z = middle.getZ() + (int) Math.round(Math.sin(angle) * GUARD_RING);
            BlockPos stand = floorNear(level, new BlockPos(x, middle.getY(), z));
            if (stand == null) continue;
            Entity entity = TolkienEntities.ENTITY_TTM_NAZGUL.get().create(level, EntitySpawnReason.EVENT);
            if (!(entity instanceof Mob keeper)) continue;
            keeper.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                    random.nextFloat() * 360.0F, 0.0F);
            keeper.setPersistenceRequired();
            keeper.addTag(GUARD_TAG);
            shadeBlind(keeper);
            level.addFreshEntity(keeper);
        }
    }


    /**
     * Takes the sun out of the head of one of the bodies this fight is made of.
     * <p>
     * The Nine, and the crown above them, are built on {@link com.greatorator.tolkienmobs.entity.TolkienMonsterEntity},
     * which walks a servant of the dark out of daylight and into shade. That is the right habit on the road and
     * the wrong one on the sand: an arena has no shade to reach and no door to duck through, so the ring would
     * come apart in the morning and the crown would walk off the floor he was stood on. The goals are dropped
     * from the body rather than from the class, so the Rider, and every Ring-wraith a player meets anywhere
     * else in the world, keeps the habits it was given.
     */
    private static void shadeBlind(Mob mob) {
        List<Goal> sun = new ArrayList<>();
        for (WrappedGoal wrapped : mob.goalSelector.getAvailableGoals()) {
            Goal goal = wrapped.getGoal();
            if (goal instanceof RestrictSunGoal || goal instanceof FleeSunGoal) sun.add(goal);
        }
        sun.forEach(mob.goalSelector::removeGoal);
    }

    /**
     * Whether the crown is still behind its keepers. While one of the three stands within reach of him
     * nothing lands on the Witch-king at all, which is what makes the last page ask for four bodies
     * rather than for one: a player who ignores the ring is told why their blows are not counting.
     * <p>
     * The ring is read off the world rather than off a list kept here, because a reloaded world has no
     * list and a keeper's tag survives being written down while a map does not.
     */
    public static boolean guarded(Entity king) {
        if (!king.entityTags().contains(WITCHKING_TAG)) return false;
        return !king.level().getEntities(king, king.getBoundingBox().inflate(GUARD_REACH),
                found -> found.entityTags().contains(GUARD_TAG)).isEmpty();
    }

    /** Says why that blow did not land, once every few seconds rather than on every one of them. */
    public static void tellGuarded(Entity king, ServerPlayer player) {
        long now = king.level().getGameTime();
        Long said = SHIELD_SAID.get(king.getUUID());
        if (said != null && now - said < SHIELD_SAY_INTERVAL) return;
        SHIELD_SAID.put(king.getUUID(), now);
        player.sendSystemMessage(Component.translatable("campaign.tolkienmobs.witchking.shielded")
                .withStyle(ChatFormatting.DARK_PURPLE));
    }

    /** The page that names the castle, if the player is carrying it. */
    @Nullable
    private static Quest carriedCastlePage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.story() && quest.target().equals(CASTLE_TARGET)) return quest;
        }
        return null;
    }

    /** The page that names the Witch-king, if the player is carrying it. */
    @Nullable
    private static Quest carriedWitchKingPage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.story() && quest.target().equals(witchKingId())) return quest;
        }
        return null;
    }

    /** Whether the campaign's Witch-king is already standing near the castle. */
    /**
     * Takes the sun out of the fight's eyes again, for a fight that has been through a save.
     * <p>
     * Goals are registered when a body is constructed, so a keeper or a crown read back off the world comes
     * back with the two sun goals the last load took off it. This walks the bodies this fight is made of and
     * strips them again, which costs a scan of a small box once a second and is idempotent - a body already
     * blind to the sun has nothing left to take off.
     */
    private static void steadyShade(ServerLevel level, BlockPos centre) {
        for (Entity found : level.getEntities((Entity) null, new AABB(centre).inflate(WITCHKING_SEARCH),
                seen -> seen.entityTags().contains(WITCHKING_TAG) || seen.entityTags().contains(GUARD_TAG)
                        || seen.entityTags().contains(CASTLE_TAG))) {
            if (found instanceof Mob mob) shadeBlind(mob);
        }
    }

    private static boolean witchKingLiving(ServerLevel level, BlockPos centre) {
        return !level.getEntities((Entity) null, new AABB(centre).inflate(WITCHKING_SEARCH),
                found -> found.entityTags().contains(WITCHKING_TAG)).isEmpty();
    }

    /**
     * The place at the middle of a piece of built ground where a body would stand: two blocks of air
     * over its own floor, looked for a step up and a step down from the height the building was laid at
     * so that a rim or a threshold is covered and a colonnade is not.
     */
    @Nullable
    private static BlockPos floorOf(ServerLevel level, BlockPos centre) {
        for (int dy = -FLOOR_DOWN; dy <= FLOOR_UP; dy++) {
            BlockPos feet = centre.offset(0, dy, 0);
            if (stands(level, feet)) return feet;
        }
        return null;
    }

    /** Two blocks of air over a solid floor, which is what a body needs to stand at all. */
    private static boolean stands(ServerLevel level, BlockPos feet) {
        return level.getBlockState(feet).isAir() && level.getBlockState(feet.above()).isAir()
                && level.getBlockState(feet.below()).isSolid();
    }

    /**
     * The nearest ground to a written spot that a body can actually stand on, looked for in rings.
     * <p>
     * An Elf is stood on a step from the middle of the square - the southern road, the library door, the
     * guest house - and those steps are only paving if the valley was laid where the survey said it would
     * be, which in a wood it often was not. {@link #floorOf} answers nothing for a step whose two blocks
     * are a tree trunk, or whose ground is more than a pace off the height the middle was surveyed at,
     * and the keeper then returned without a word: the road up to the valley was walked by a player owed
     * counsel by an Elf who was never stood. The search widens instead, so the speaker is beside the road
     * and not missing from it.
     */
    @Nullable
    private static BlockPos floorNear(ServerLevel level, BlockPos around) {
        for (int ring = 0; ring <= KEEPER_SEARCH; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
                    BlockPos stand = floorOf(level, around.offset(dx, 0, dz));
                    if (stand != null) return stand;
                }
            }
        }
        return null;
    }

    /**
     * The same search with the player's own feet as the last resort.
     * <p>
     * A page that says "take it to Glorfindel" cannot be finished by a player who has walked to the valley
     * and found nobody in it, so an Elf the line is waiting on is never left unstood for want of paving: if
     * the step and the six blocks around it are all trunk or all wall, he is stood on open ground in front
     * of the player, a pace off the road he was meant to be found on.
     */
    @Nullable
    private static BlockPos floorNear(ServerLevel level, BlockPos around, ServerPlayer player) {
        BlockPos stand = floorNear(level, around);
        if (stand != null) return stand;
        BlockPos beside = player.blockPosition().offset(player.getDirection().getStepX() * 3, 0,
                player.getDirection().getStepZ() * 3);
        stand = floorNear(level, beside);
        // Said out loud, because a keeper stood beside the player instead of at their post is a placement
        // failing that used to be silent, and this is the line that says it happened.
        LOGGER.info("No ground for a keeper at {}; standing beside {} at {} instead", around,
                player.getName().getString(), stand);
        return stand;
    }

    /** The campaign's own page if the player is carrying it and has not finished it yet. */
    @Nullable
    private static Quest carriedCampaignPage(ServerPlayer player) {
        for (Quest quest : QuestLedger.active(player)) {
            if (isCampaignPage(player, quest)) return quest;
        }
        return null;
    }

    /**
     * The Rider this player is owed, if he is already out there. The tracked id is tried first, and a
     * Rider found by tag is written back into it - which is how a reloaded world, or a player who
     * walked to another country, is kept from being handed a second one.
     */
    @Nullable
    private static Entity livingRider(ServerPlayer player) {
        UUID known = RIDER_ENTITY.get(player.getUUID());
        if (known != null) {
            Entity rider = player.level() instanceof ServerLevel level ? level.getEntity(known) : null;
            if (rider != null && !rider.isRemoved()) return rider;
            RIDER_ENTITY.remove(player.getUUID());
        }
        for (Entity rider : player.level().getEntities(player,
                player.getBoundingBox().inflate(RIDER_SEARCH), found -> isCampaignRider(player, found))) {
            RIDER_ENTITY.put(player.getUUID(), rider.getUUID());
            return rider;
        }
        return null;
    }

    /**
     * Checks whether a killed entity is the campaign's Rider for a given player. The Rider is
     * identified by his tags, which include the player's UUID.
     */
    public static boolean isCampaignRider(ServerPlayer player, Entity victim) {
        return victim.entityTags().contains(RIDER_TAG)
                && victim.entityTags().contains("tolkienmobs:rider_" + player.getUUID());
    }

    /**
     * Called when the campaign's Rider dies. The Rider is removed from the tracking map, but the
     * quest is not settled here - that happens through the normal quest ledger when the player
     * hands in the completed quest. The Rider can be respawned fresh for the next stage.
     */
    public static void onRiderDeath(ServerPlayer player) {
        RIDER_ENTITY.remove(player.getUUID());
    }

    /**
     * Whether a page is the campaign's own rather than one of a front's. Read off what the page asks for
     * rather than off what was noted as handed over, because this is asked while a page is still being
     * handed out - before the slot knows about it - and again of every copy already in a book.
     */
    public static boolean isCampaignPage(ServerPlayer player, Quest quest) {
        return quest.story() && quest.kind() == Quest.Kind.HUNT && quest.target().equals(riderId());
    }

    /**
     * Whether the player is already carrying a page of this front, whoever handed it over. Two halls of
     * one alliance - an old one and a new one, or a hall beside a giver stood up by a command - would
     * otherwise each write a page for the same step, and only the last of them could ever settle it.
     */
    public static boolean holdsFrontPage(ServerPlayer player, Alliance alliance) {
        for (Quest quest : QuestLedger.active(player)) {
            if (quest.story() && !isCampaignPage(player, quest) && alliance.id().equals(quest.employer())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Whether the page paid off is the page a slot handed out. Compared by its terms rather than by the
     * record itself, because the copy the player carries has moved on by every blow they landed: the
     * noted page sits at no progress from the moment it is handed over, so equality would never recognise
     * a finished one, and the front would hand the same page out again for ever.
     */
    private static boolean sameErrand(@Nullable Quest handed, Quest paid) {
        return handed != null && handed.kind() == paid.kind()
                && handed.target().equals(paid.target())
                && handed.employer().equals(paid.employer())
                && handed.goal() == paid.goal();
    }

    /**
     * Moves a front on when the page it handed out is paid, and answers whether the page settled was the
     * campaign's own rather than a front's - the ledger pays that one differently, because it has no
     * single employer. The ledger calls this from the one place a quest is settled, which is what keeps a
     * page dropped, abandoned or cleared from counting as done.
     */
    public static boolean settle(ServerPlayer player, Quest quest) {
        if (!(player.level() instanceof ServerLevel)) return false;
        ensureLoaded(player.level());
        Front[] row = PROGRESS.get(player.getUUID());
        if (row == null) return false;
        for (Alliance alliance : Alliance.values()) {
            Front front = row[alliance.ordinal()];
            if (!sameErrand(front.handed(), quest)) continue;
            // A front's page is worth courage as well as standing: the ladder the campaign is built
            // on is climbed by walking the war, and a player who carries three fronts to the end is
            // not the frightened one they started as. The Rider's page needs no figure of its own -
            // felling one of the Nine already pays a deed, and his page asks for him twice.
            List<Page> line = PAGE_LINES.get(alliance);
            boolean riderFound = false;
            if (front.stage() < line.size()) {
                Page paid = line.get(front.stage());
                CourageLedger.add(player, paid.courage());
                // The country's coin, where the notes put a price on the work: a kill worth a purse is
                // worth it in the coin the pack already trades in, and paid through the same hand-over
                // as the standing so a page cannot be worth one and not the other.
                payPurse(player, paid);
                payGift(player, paid);
                // The missing rider is found with his horse's tack still in the ruin, and the notes make
                // the bridle the thing carried back rather than the tale alone: the page that finds him
                // puts the dark saddle in the player's hand, and the report to Elladan is not heard
                // without it. It is taken off the page that hands it over, the way the stone is.
                if (MISSING_RIDER_TITLE.equals(paid.title())) {
                    handOver(player, TolkienItems.ITEM_DARKSADDLE.get(), 1);
                }
                if (RIDER_RETURN_TITLE.equals(paid.title())) takeBridle(player);
                // The lending is a sentence rather than an errand. Glorfindel says "take this" while he
                // hears the report, so the stone has to be in the player's hand in that conversation -
                // and it was not: the old rule handed it over when the survey it is lent for was taken,
                // which is a visit later, so a player who had just been told to take the beryl walked
                // out of the valley with nothing in their pack and no way to know they had been robbed.
                if (WISDOM_TITLE.equals(paid.title())) giveStone(player, STONE_GIVEN);
                // The stone goes back with the work it was lent for: an errand finished is the end of a
                // loan rather than the beginning of a keepsake.
                // The stone goes back with the chambers' tokens, which are the proof the search was
                // made rather than walked: both are spent in the one hand-over.
                if (stonePaid(line, front.stage())) {
                    takeStone(player);
                    takeCaveTokens(player, STONE_CAVES);
                    payCaveReward(player);
                }
                // The page that ends a front has the front's last words on it, and this is where
                // they are said: the front moves on the moment its last page is paid, so a line
                // left to the next page would be spoken by a page that no longer exists.
                // A page that settles itself is never read back - it leaves the book the moment it is
                // done - so its closing line is said here or it is never said at all. A page carried to
                // a giver keeps its words on the leaf until it is handed over, and is said here only when
                // it is the front's last, because those are the words a front ends on.
                boolean readBack = paid.kind() == Quest.Kind.DIALOGUE || paid.kind() == Quest.Kind.LOCATION;
                if ((front.stage() + 1 == line.size() || readBack) && !paid.closing().isEmpty()) {
                    player.sendSystemMessage(Component.translatable(paid.closing())
                            .withStyle(ChatFormatting.GOLD));
                }
                riderFound = MISSING_RIDER_TITLE.equals(paid.title());
            }
            row[alliance.ordinal()] = new Front(front.stage() + 1, null);
            // A line that has just run out is the one moment the book empties with the war still
            // on: it must not let go of the player without saying where the rest of it is.
            if (front.stage() + 1 >= line.size()) sayWhatIsLeft(player);
            store(player).setDirty();
            if (riderFound) handOnward(player, alliance);
            sync(player);
            return false;
        }
        Front finale = row[RIDER_SLOT];
        if (!sameErrand(finale.handed(), quest)) return false;
        int next = finale.stage() + 1;
        // The war ends on the sand, so the last of it is said there: the crown's line first, then what
        // the arena is now that the shouting is over. The courage the ladder has been holding back for
        // the arena is paid with them; the Rider's own fall pays none, because felling one of the Nine
        // is already a deed and his page asks for him twice.
        if (next >= FINALE_STAGES) {
            CourageLedger.add(player, ARENA_COURAGE);
            player.sendSystemMessage(Component.translatable(WITCHKING_CLOSING)
                    .withStyle(ChatFormatting.GOLD));
            player.sendSystemMessage(Component.translatable(ARENA_CLOSING)
                    .withStyle(ChatFormatting.GOLD));
        }
        row[RIDER_SLOT] = new Front(next, null);
        store(player).setDirty();
        sync(player);
        return true;
    }

    /**
     * Which slot of a player's row a page belongs to: the campaign's own, or the herald's front. The
     * Rider is picked out by what he asks for rather than by the kind of work, because a front can end
     * on a hunt of its own country's beast - and a hunt written into the campaign's slot would end the
     * war on the first wight a player felled, long before any front was settled.
     */
    /**
     * The thing a page hands over besides its coin, keyed by the page's own name.
     * <p>
     * The reference gives most pages a purse or an object, and the objects are what make a chapter feel
     * like a country looking after its own: waybread for a road it is sending you down, a blade after a
     * report it believed, a potion after three chambers in the dark. What goes in this table has to work -
     * the pack's decorative "fancy" swords, picks and shields are props and are not rewards - and the
     * trinkets are rationed to one in the chapter, because a charm is a powerful thing to be able to hold.
     * <p>
     * The list and the reason for each choice are in {@code docs/chapter-one-rewards.md}, so the next
     * chapter's rewards start from a decision rather than from the pool again.
     */
    private record Gift(Supplier<ItemStack> stack) {
    }

    private static final Map<String, Gift> PAGE_GIFTS = Map.of(
            TROUBLES_TITLE, new Gift(() -> new ItemStack(TolkienItems.CRAM.get(), 4)),
            HORSE_TITLE, new Gift(() -> new ItemStack(TolkienItems.LEMBAS.get(), 2)),
            RIDER_RETURN_TITLE, new Gift(() -> new ItemStack(TolkienItems.SWORD_AMMOLITE.get(), 1)),
            "campaign.tolkienmobs.page.caves",
            new Gift(() -> PotionContents.createItemStack(Items.POTION, Potions.HEALING)),
            DEPARTURE_TITLE, new Gift(() -> new ItemStack(TolkienItems.FACTION_CHARM_WEST.get(), 1)));

    /** Hands over the item a page carries, where the reference gives one rather than a purse. */
    private static void payGift(ServerPlayer player, Page page) {
        Gift gift = PAGE_GIFTS.get(page.title());
        if (gift == null) return;
        ItemStack stack = gift.stack().get();
        if (stack.isEmpty()) return;
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    /** Pays the coin and experience a page carries, into the pack, and at the feet if the pack is full. */
    private static void payPurse(ServerPlayer player, Page page) {
        if (page.silver() > 0) payCoin(player, TolkienItems.ITEM_COIN_SILVER.get(), page.silver());
        if (page.bronze() > 0) payCoin(player, TolkienItems.ITEM_COIN_BRONZE.get(), page.bronze());
        if (page.xp() > 0) player.giveExperiencePoints(page.xp());
    }

    private static void payCoin(ServerPlayer player, Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        if (!player.getInventory().add(stack)) player.drop(stack, false);
    }

    private static int slotOf(Quest page, Alliance alliance) {
        return isFinale(page) ? RIDER_SLOT : alliance.ordinal();
    }

    /**
     * Whether a page is one of the campaign's own last acts rather than a front's. Read off what it asks
     * for rather than off what was noted as handed over, because this is asked while a page is still
     * being handed out - before the slot knows about it.
     */
    private static boolean isFinale(Quest page) {
        return page.story() && (page.target().equals(riderId())
                || page.target().equals(witchKingId())
                || page.target().equals(CASTLE_TARGET)
                || page.target().equals(ARENA_TARGET));
    }

    /**
     * The dialogue a front's current page is written out in, as the key prefix its lines hang off, or
     * null where that page's prose has not been written yet. The stage is the page's index, because it
     * only moves when the ledger settles a page, so while one is carried the stage is that page.
     * <p>
     * The list is the guard: a page that is named here is read as a conversation when it is handed
     * over, and one that is not is handed over as it always was. It is deliberately not every page -
     * a screen with no words on it would be worse than the chat line - so growing the campaign's voice
     * is naming the page here and writing its keys.
     */
    @Nullable
    public static String spokenKey(ServerPlayer player, Alliance alliance) {
        ensureLoaded(player.level());
        List<Page> line = PAGE_LINES.get(alliance);
        int stage = stage(player, alliance);
        if (stage >= line.size()) return null;
        String title = line.get(stage).title();
        return SPOKEN.contains(title) ? title : null;
    }
    // ---- plumbing ----

    public static void sync(ServerPlayer player) {
        ensureLoaded(player.level());
        Front[] row = row(player);
        PacketDistributor.sendToPlayer(player,
                new CampaignSyncManager(row[0].stage(), row[1].stage(), row[2].stage(),
                        row[RIDER_SLOT].stage()));
    }

    private static boolean sealed(Front[] row) {
        for (Alliance alliance : Alliance.values()) {
            if (row[alliance.ordinal()].stage() < pages(alliance)) return false;
        }
        return true;
    }

    private static Front[] row(Player player) {
        return PROGRESS.computeIfAbsent(player.getUUID(), ignored -> blank());
    }

    private static Front[] blank() {
        return new Front[]{new Front(0, null), new Front(0, null), new Front(0, null),
                new Front(0, null)};
    }

    private static Campaign store(ServerPlayer player) {
        return instance(player.level());
    }

    /** Reads the world's file into the map, once, before the map is read or written. */
    private static void ensureLoaded(Level level) {
        instance(level);
    }

    private static Campaign instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private static Campaign fromEntries(List<Entry> entries) {
        PROGRESS.clear();
        for (Entry entry : entries) {
            PROGRESS.put(entry.player(), new Front[]{entry.west(), entry.east(), entry.harad(),
                    entry.rider()});
        }
        // Read without clearing: the map was just filled from the file.
        return new Campaign(false);
    }

    private static List<Entry> toEntries(Campaign campaign) {
        List<Entry> entries = new ArrayList<>(PROGRESS.size());
        for (Map.Entry<UUID, Front[]> row : PROGRESS.entrySet()) {
            Front[] fronts = row.getValue();
            entries.add(new Entry(row.getKey(), fronts[0], fronts[1], fronts[2], fronts[RIDER_SLOT]));
        }
        return entries;
    }

    public record Entry(UUID player, Front west, Front east, Front harad, Front rider) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Entry::player),
                Front.CODEC.optionalFieldOf("west", new Front(0, null)).forGetter(Entry::west),
                Front.CODEC.optionalFieldOf("east", new Front(0, null)).forGetter(Entry::east),
                Front.CODEC.optionalFieldOf("harad", new Front(0, null)).forGetter(Entry::harad),
                Front.CODEC.optionalFieldOf("rider", new Front(0, null)).forGetter(Entry::rider)
        ).apply(instance, Entry::new));
    }
}
