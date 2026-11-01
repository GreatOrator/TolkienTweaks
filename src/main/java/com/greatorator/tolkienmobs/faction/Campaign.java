package com.greatorator.tolkienmobs.faction;

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
import net.minecraft.world.item.ItemStack;
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
    private static final String SUMMON_TARGET = Quest.placeTarget(SUMMON_PLACE);
    private static final String SUMMON_TITLE = "campaign.tolkienmobs.page.summon";
    private static final String GLORFINDEL_TAG = "tolkienmobs:glorfindel";
    /** How near the valley a player has to be for him to be standing in it, in blocks. */
    private static final double GLORFINDEL_REACH = 128.0D;
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
    private static final String BARACHEN_TARGET = Quest.placeTarget(BARACHEN_PLACE);
    private static final String BARACHEN_TAG = "tolkienmobs:barachen";
    /** How near his fire a player has to be for him to be standing at it, in blocks. */
    private static final double BARACHEN_REACH = 128.0D;
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
    private static final Map<Alliance, List<Page>> PAGE_LINES = Map.of(
            Alliance.WEST, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60, 2,
                            "campaign.tolkienmobs.page.muster", "campaign.tolkienmobs.page.muster.place", ""),
                    new Page(Quest.Kind.FACTION, "", 12, 120, 4,
                            "campaign.tolkienmobs.page.southroad", "campaign.tolkienmobs.page.southroad.place", ""),
                    new Page(Quest.Kind.HUNT, "tolkienmobs:entityttmbarrow", 2, 150, 7,
                            "campaign.tolkienmobs.page.keeping", "campaign.tolkienmobs.page.keeping.place", ""),
                    // The Elves' errand, and the one page of the line a hall herald cannot hand: the
                    // Rangers' war is not his to give, so the walk to the valley is the page, and what waits
                    // at the end of the walk hands the next one.
                    new Page(Quest.Kind.LOCATION, SUMMON_TARGET, 1, 25, 4,
                            "campaign.tolkienmobs.page.summon", "campaign.tolkienmobs.page.summon.place", ""),
                    // The stone's first asking, and the one page of the line whose whole errand is a
                    // single place: held aloft at the ford, and carried back to Glorfindel who asked for it.
                    new Page(Quest.Kind.SURVEY, FORD_TARGET, 1, 20, 4,
                            "campaign.tolkienmobs.page.beryl", "campaign.tolkienmobs.page.beryl.place", ""),
                    // Then the search of the dark, which is the part of the old campaign that could not be
                    // carried over literally: three caves were named and pointed at, and here any three with
                    // rock over their heads will do, asked at the bottom of each.
                    new Page(Quest.Kind.SURVEY, "", STONE_CAVES, 25, 5,
                            "campaign.tolkienmobs.page.caves", "campaign.tolkienmobs.page.caves.place", ""),
                    // The scout's camp, which is the page the caves were a search of the road towards: the
                    // stone goes back to Elladan and the next lead is a man who saw something. A camp is
                    // ground rather than a landmark, so the page is a walk and the walk is the whole of it.
                    new Page(Quest.Kind.LOCATION, BARACHEN_TARGET, 1, 25, 4,
                            "campaign.tolkienmobs.page.barachen", "campaign.tolkienmobs.page.barachen.place",
                            ""),
                    // The hilltop is raised rather than named. A place is a circle somebody has to stand
                    // in and declare; this is ground the campaign builds where the map puts it, so the page
                    // is settled by standing in the building and nobody has to place anything by hand.
                    new Page(Quest.Kind.LOCATION, Quest.siteTarget(Sites.WEATHERTOP), 1, 50, 4,
                            "campaign.tolkienmobs.page.watched", "campaign.tolkienmobs.page.watched.place", ""),
                    new Page(Quest.Kind.FACTION, "", 8, 70, 5,
                            "campaign.tolkienmobs.page.hunters", "campaign.tolkienmobs.page.hunters.place", ""),
                    new Page(Quest.Kind.LOCATION, "place:ford", 1, 60, 5,
                            "campaign.tolkienmobs.page.raised", "campaign.tolkienmobs.page.raised.place", ""),
                    new Page(Quest.Kind.DIALOGUE, "leaf:campaign.tolkienmobs.page.heard", 1, 30, 6,
                            "campaign.tolkienmobs.page.heard", "campaign.tolkienmobs.page.heard.place", ""),
                    new Page(Quest.Kind.FACTION, "", 10, 80, 6,
                            "campaign.tolkienmobs.page.spears", "campaign.tolkienmobs.page.spears.place", ""),
                    new Page(Quest.Kind.LOCATION, "place:tharbad", 1, 70, 7,
                            "campaign.tolkienmobs.page.drowned", "campaign.tolkienmobs.page.drowned.place", ""),
                    new Page(Quest.Kind.DIALOGUE, "leaf:campaign.tolkienmobs.page.counsel", 1, 40, 8,
                            "campaign.tolkienmobs.page.counsel", "campaign.tolkienmobs.page.counsel.place", ""),
                    new Page(Quest.Kind.FACTION, "", 16, 160, 10,
                            "campaign.tolkienmobs.page.musterlast", "campaign.tolkienmobs.page.musterlast.place",
                            "campaign.tolkienmobs.page.musterlast.closing")),
            Alliance.EAST, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60, 2,
                            "campaign.tolkienmobs.page.marches", "campaign.tolkienmobs.page.marches.place", ""),
                    new Page(Quest.Kind.FACTION, "", 12, 120, 4,
                            "campaign.tolkienmobs.page.levy", "campaign.tolkienmobs.page.levy.place", ""),
                    new Page(Quest.Kind.FETCH, "tolkienmobs:item_scroll", 1, 0, 7,
                            "campaign.tolkienmobs.page.steward", "campaign.tolkienmobs.page.steward.place", "campaign.tolkienmobs.page.steward.closing")),
            Alliance.HARAD, List.of(
                    new Page(Quest.Kind.FACTION, "", 6, 60, 2,
                            "campaign.tolkienmobs.page.sun", "campaign.tolkienmobs.page.sun.place", ""),
                    new Page(Quest.Kind.FACTION, "", 12, 120, 4,
                            "campaign.tolkienmobs.page.ford", "campaign.tolkienmobs.page.ford.place", ""),
                    new Page(Quest.Kind.HUNT, "tolkienmobs:entityttmmumakil", 1, 150, 7,
                            "campaign.tolkienmobs.page.mumak", "campaign.tolkienmobs.page.mumak.place", "campaign.tolkienmobs.page.mumak.closing"))
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
                        String title, String place, String closing) {
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
    private static final Set<String> SPOKEN = Set.of("campaign.tolkienmobs.page.muster", "campaign.tolkienmobs.page.summon",
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
            // The stone's work is the Elves': it is handed by the body waiting at the end of the walk and
            // taken back by him, so the herald who sent the player there has neither to give nor to take.
            // The ford is Glorfindel's to take and the caves are Elladan's: the notes leave the stone in
            // the player's care until the three caves have been asked, and the camp is where it goes back.
            return page.kind() == Quest.Kind.SURVEY
                    ? built.withTurnIn(page.target().isEmpty() ? ELLADAN : GLORFINDEL)
                    : built;
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
            Places.Place inside = Places.at(player);
            if (inside == null || !inside.id().equals(wanted)) {
                player.sendSystemMessage(Component.translatable(STONE_ELSEWHERE,
                        Component.translatable("place.tolkienmobs." + wanted)).withStyle(ChatFormatting.GRAY));
                return;
            }
            player.sendSystemMessage(named != null ? named : caveReading(here));
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
        Quest stone = page(player, Alliance.WEST);
        if (stone == null || stone.kind() != Quest.Kind.SURVEY) return;
        // He waits at the end of the ford, not at the end of the cave search: by then the stone is
        // Elladan's to take, and an Elf standing in the valley asking for it would be the wrong hand.
        if (stone.namedGround().isEmpty()) return;
        if (Places.place(player.level(), SUMMON_PLACE) == null) Places.ensure(player, SUMMON_PLACE, CAMP_RADIUS);
        Places.Place valley = Places.place(level, SUMMON_PLACE);
        if (valley == null || !valley.dimension().equals(level.dimension())) return;
        if (valley.centre().distToCenterSqr(player.position()) > GLORFINDEL_REACH * GLORFINDEL_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(valley.centre()).inflate(GLORFINDEL_REACH),
                seen -> seen.entityTags().contains(GLORFINDEL_TAG)).isEmpty()) return;
        BlockPos stand = floorOf(level, valley.centre());
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
        Quest stone = page(player, Alliance.WEST);
        if (stone == null || stone.kind() != Quest.Kind.SURVEY || !stone.namedGround().isEmpty()) return;
        Places.Place camp = Places.ensure(player, THORENHAD_PLACE, CAMP_RADIUS);
        if (camp == null || !camp.dimension().equals(level.dimension())) return;
        if (camp.centre().distToCenterSqr(player.position()) > ELLADAN_REACH * ELLADAN_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(camp.centre()).inflate(ELLADAN_REACH),
                seen -> seen.entityTags().contains(ELLADAN_TAG)).isEmpty()) return;
        // The camp is a camp before it is a man: the tent goes down first, and he is stood at it in the
        // same look, so there is never an Elf in a field waiting for a tent that never comes.
        if (!standTent(level, camp)) return;
        BlockPos stand = floorOf(level, camp.centre());
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
        int camp = campStage();
        if (camp < 0) return;
        int step = stage(player, Alliance.WEST);
        if (step < camp || step > camp + 1) return;
        Places.Place fire = Places.ensure(player, BARACHEN_PLACE, CAMP_RADIUS);
        if (fire == null || !fire.dimension().equals(level.dimension())) return;
        if (fire.centre().distToCenterSqr(player.position()) > BARACHEN_REACH * BARACHEN_REACH) return;
        if (!level.getEntities((Entity) null, new AABB(fire.centre()).inflate(BARACHEN_REACH),
                seen -> seen.entityTags().contains(BARACHEN_TAG)).isEmpty()) return;
        // The camp is a camp before it is a man: the tent goes down first, so there is never a scout in a
        // field waiting for a tent that never comes.
        if (!standTent(level, fire)) return;
        BlockPos stand = floorOf(level, fire.centre());
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

    /**
     * The slot Barachen's camp holds in the West's line, read off the line rather than written down a
     * second time: a page inserted above his would otherwise leave the scout standing at the wrong step.
     */
    private static int campStage() {
        List<Page> line = PAGE_LINES.get(Alliance.WEST);
        for (int i = 0; i < line.size(); i++) {
            if (BARACHEN_TARGET.equals(line.get(i).target())) return i;
        }
        return -1;
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
        return true;
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
        if (Sites.siteOf(player) != null) return;
        Sites.plan(player, Sites.TROLL_CAVE, false);
    }

    /**
     * Whether the stone has been asked everything it was lent for, which is the last of its pages and not
     * the first: the ford is one asking and the caves are another, and a stone given back after the ford
     * would leave the player searching caves with their hands empty.
     */
    private static boolean stonePaid(List<Page> line, int paid) {
        if (paid >= line.size() || line.get(paid).kind() != Quest.Kind.SURVEY) return false;
        return paid + 1 >= line.size() || line.get(paid + 1).kind() != Quest.Kind.SURVEY;
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
        keepStoneInHand(player);
        keepGlorfindel(player);
        keepCaveOpen(player);
        keepElladan(player);
        keepBarachen(player);
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
        BlockPos stand = floorOf(level, site.centre());
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
            BlockPos stand = floorOf(level, new BlockPos(x, middle.getY(), z));
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
            if (front.stage() < line.size()) {
                Page paid = line.get(front.stage());
                CourageLedger.add(player, paid.courage());
                // The stone goes back with the work it was lent for: an errand finished is the end of a
                // loan rather than the beginning of a keepsake.
                if (stonePaid(line, front.stage())) takeStone(player);
                // The page that ends a front has the front's last words on it, and this is where
                // they are said: the front moves on the moment its last page is paid, so a line
                // left to the next page would be spoken by a page that no longer exists.
                if (front.stage() + 1 == line.size() && !paid.closing().isEmpty()) {
                    player.sendSystemMessage(Component.translatable(paid.closing())
                            .withStyle(ChatFormatting.GOLD));
                }
            }
            row[alliance.ordinal()] = new Front(front.stage() + 1, null);
            store(player).setDirty();
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
