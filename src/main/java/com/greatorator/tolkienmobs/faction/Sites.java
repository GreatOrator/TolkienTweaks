package com.greatorator.tolkienmobs.faction;

import com.greatorator.tolkienmobs.entity.passive.NazgulSteedEntity;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienEntities;
import com.greatorator.tolkienmobs.world.registration.ArdaMap;
import com.greatorator.tolkienmobs.world.registration.TolkienDimensions;
import com.greatorator.tolkienmobs.world.registration.ArdaSurvey;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.AABB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

/**
 * The ground the campaign has built on: a structure it put down for one player, and the laying of it.
 * <p>
 * This is not {@link Places}, and the difference is worth keeping straight. A place is a <em>name</em>
 * for ground that a world already had, set by an operator and announced when somebody walks into it. A
 * site is ground that did not exist until the campaign built it - a camp, an arena, a castle - and it is
 * built for the player who was sent there rather than found by whoever happens to pass.
 * <p>
 * The mod has always carried its buildings as templates - the halls, the towers, the arena - and had
 * never once put one down itself: they were all the world generator's, or an operator's with
 * {@code /place structure}. A page that says "go there" cannot wait for either, because "there" has to
 * be chosen when the page is taken and be standing when the player arrives. So the spot is written down
 * against the player at the moment they take the work, and nothing is built until they are within four
 * chunks - at which range the chunk is loaded anyway, so no chunk is ever force-loaded to hold a
 * building nobody may visit, and nothing is dropped into ground nobody is looking at.
 * <p>
 * Nothing here knows what a tent is. The footprint that has to be judged is the template's own size,
 * read from the file when the build is about to happen, and the tolerance for uneven ground grows with
 * it: two blocks of rise and fall across a tent is a hillside through the middle of it, and two blocks
 * across a castle courtyard is flat.
 * <p>
 * One site is kept per player, and taking a second page to a structure they have not reached yet keeps
 * the site they already have rather than pitching a rival beside it - which is what lets a quest in
 * pieces send somebody back to the same ground twice.
 * <p>
 * Where that ground is comes from the drawing rather than from the player's facing. {@link ArdaSurvey}
 * reads {@link ArdaMap} for a column the world has not generated, so a site is chosen in the country
 * its structure belongs to, on dry, level ground, before a chunk of it has ever been made. The spot is
 * fixed by the country and the structure rather than by whoever asked, which is what lets a page name
 * a country and be right, and what lets two people sent there arrive at the same door.
 */
public final class Sites extends SavedData {
    private static final String SAVE_DATA_NAME = "sites";
    private static final Logger LOGGER = LoggerFactory.getLogger(Sites.class);

    /**
     * The tent a camp errand pitches: the world generator's own canvas, cut as a piece of its own and
     * raised where the errand is taken. The campaign's own camp is {@link #BARACHENS_CAMP}.
     */
    public static final String WILD_TENT = "tolkienmobs:wild_tent";
    /**
     * Barachen's camp, cut rather than pitched: the scout's fire the stone's line walks to once the caves
     * have answered nothing, and the camp the catalogue names {@code barachen}. The campaign's own ground
     * stands in the cell that name is surveyed in rather than wherever the page happened to be taken.
     */
    public static final String BARACHENS_CAMP = "tolkienmobs:key_structures/barachans_camp";
    /**
     * Elladan's own camp at Thorenhad, cut rather than pitched: sixteen by eight by sixteen, stone and
     * podzol under spruce with the rock of the hillside still in it - the shape of a camp the Dunedain
     * keep, not the canvas a traveller carries.
     * <p>
     * It replaces the tent at Thorenhad and nowhere else. The tent is the world generator's scenery now,
     * and the campaign's own canvas ground is Barachen's camp - so this is ground only the chapter
     * raises, named here and pinned to a cell like the rest of the chapter's own buildings.
     */
    public static final String ELLADAN_CAMP = "tolkienmobs:key_structures/elledancamp";
    /**
     * The dwarf camp Glóin keeps at the east end of the pass, taken from the old world's own camp rather
     * than modelled: the pack ships no dwarf camp of its own, and the notes open the second chapter at a
     * fire a player walks up to. Captured as it stood - 28 by 8 by 22, its door in the middle of the south
     * side and a post marking the front a few blocks further out - so the ground chapter two is spoken on
     * is the ground it was written on. It stands at the east end of the High Pass, where the old world had
     * it: the road north from there runs along the vale, not over the mountains. The two marker blocks the capture used for its door and its front
     * post are plain earth now: a page names the camp, and what the old world left to point at it is not
     * something a player should have to read as scenery.
     */
    public static final String GLOIN_CAMP = "tolkienmobs:key_structures/gloin_camp";
    /**
     * The dwarven hold the chapter walks to, assembled from the geomorph sheet by
     * {@code tools/gundabad_keep.py}: 122 by 183 by 26, six rooms on one floor over an inner keep of
     * three more. It is ground the campaign pitches rather than ground the world generator makes, so
     * the page that sends the player there names this and the keep is built for them.
     */
    public static final String GUNDABAD = "tolkienmobs:key_structures/gundabad";
    /** The hilltop the West's line walks to, a landmark rather than a camp. */
    public static final String WEATHERTOP = "tolkienmobs:key_structures/weathertop";
    /** The castle the campaign raises for its last act, which no world generator ever places. */
    public static final String BOSS_CASTLE = "tolkienmobs:key_structures/boss_castle";
    /** The arena beneath the castle, the same campaign hand's other building, also never generated. */
    public static final String BOSS_ARENA = "tolkienmobs:key_structures/boss_arena";
    /**
     * The troll cave the stone's cave page is asked in. The mod's own caves are a Mirkwood jigsaw, a
     * country away from the road this line walks, so the campaign cuts its own and raises it ahead of the
     * player rather than sending them to a cave the world generator never put there. Cut by
     * {@code tools/make_troll_cave.py}: a crag with the inside taken out of it.
     */
    public static final String TROLL_CAVE = "tolkienmobs:key_structures/troll_cave";
    /** The second crag, cut by the same hand and asked for the second cave of the stone's page. */
    public static final String TROLL_CAVE_2 = "tolkienmobs:key_structures/troll_cave_2";
    /** The third crag, for the last of the three chambers the notes name. */
    public static final String TROLL_CAVE_3 = "tolkienmobs:key_structures/troll_cave_3";
    /**
     * The fourth crag, cut by the same hand for a hill of its own: Matuna keeps his lair in it.
     * <p>
     * The wood-troll's page names a body rather than a place, so its ground was the one crag nobody had cut for
     * it - the three the stone asks in were all spoken for, and a lair in one of them sent the player back into a
     * hill the stone had already emptied. This is a variant like the others (tools/make_troll_cave.py with its own
     * seed and its chambers nudged), and it is pinned like the stone's three: the world raises the lair on the hill the
     * scout marked, and the page that names him finds that lair standing rather than surveying for one of its own.
     */
    public static final String TROLL_CAVE_4 = "tolkienmobs:key_structures/troll_cave_4";
    /**
     * The crags in the order the stone asks for them: Bandrith's first, Sholdroth's last.
     * <p>
     * Three crags rather than one raised three times. The notes name three caves and send the player to
     * each in turn, and a page that raised the same hill for every one of them would be three errands in
     * one place - the player would walk a new distance to the same cave. They are one generator's three
     * askings ({@code tools/make_troll_cave.py} with its own seed and its chambers nudged), so the second
     * and third are different hills and still the same cave to be lost in.
     */
    public static final List<String> CAVE_VARIANTS = List.of(TROLL_CAVE, TROLL_CAVE_2, TROLL_CAVE_3);
    /**
     * The ruin the rider is found at. The notes put it in the narrow channels south of the road, and the
     * pack already ships a ruin for wild ground: rather than a point on the map with a reach round it,
     * the page asks for a wall to be standing there, so the player finds a place and not a field.
     * <p>
     * It is the second of the pack's large ruins rather than the first. {@code ttmruin_large} is a
     * structure the world generator places in the wild, so a page that built it would raise a copy of
     * ground the player can already walk up to out there, and the ruin the notes send them to would be one
     * of many. This one is never generated - nothing but the page puts it anywhere - so the wall the
     * bridle is found at is that wall and no other.
     */
    public static final String WOOD_RUIN = "tolkienmobs:key_structures/ttmruin_large_2";
    /**
     * The chapter's own buildings, which the world raises whether or not a page has named them.
     * <p>
     * A crag, the ruin the rider is found at and Elladan's own camp are ground the campaign cuts rather than
     * ground the generator makes, so a world that waits for a page to raise them is a world whose hills are empty
     * until somebody is sent to them - and a player who walks to Thorenhad before taking a page finds a place with
     * nothing at it. Each of these is pinned in {@link Pins}, so where it stands does not depend on who is
     * asking: the world plants it once, at the pin the scout walked, and stands it when a player comes close enough
     * for its chunks to be loaded.
     */
    /** What the world leaves standing at one of its own buildings. */
    private enum Body {
        /** Nothing: the hill, or the ruin's empty walls, is the whole of what is there. */
        NONE,
        /** One of the mod's folk at the fire, which is what a camp is. */
        KEEPER,
        /** The animal the rider came off, left where it fell. */
        STEED
    }

    /** One of the world's own buildings, and what stands at it when it is raised. */
    private record Scenery(String structure, Body body) {
        /** Whether this building wants the mod's own folk at it, which the site records as well. */
        boolean keeper() {
            return body == Body.KEEPER;
        }
    }

    private static final List<Scenery> SCENERY = List.of(
            // A camp is a fire with somebody minding it, and both camps are ground the world raises rather than
            // ground a page builds: a player who walks to Thorenhad or Barachen's fire before any page finds the
            // camp standing with its keeper, and a page that names it later finds that same camp instead of
            // pitching a second. A crag is a hill with a way in and nothing at it; the ruin keeps the animal the
            // rider came off, which the world leaves there whether or not anyone is sent to look.
            new Scenery(ELLADAN_CAMP, Body.KEEPER),
            new Scenery(BARACHENS_CAMP, Body.KEEPER),
            new Scenery(WOOD_RUIN, Body.STEED),
            new Scenery(TROLL_CAVE, Body.NONE),
            new Scenery(TROLL_CAVE_2, Body.NONE),
            new Scenery(TROLL_CAVE_3, Body.NONE));
    /**
     * The country each landmark stands in, where the books put it rather than where the page was taken.
     * <p>
     * A site named here is surveyed from the middle of that country, so it is the same ground for every
     * player in every world: Weathertop is Amon Sul, and Amon Sul is a hill in the heart of Eriador, not
     * the field behind whatever hall the page was handed over in. The same goes for the last act: the
     * Witch-king's fortress is Carn Dum and Carn Dum is in Angmar, so both the castle and the arena under
     * it stand in that country whichever hall the page came from, and neither is ever a building raised
     * beside the player. Anything not named here keeps looking around the player, which is what lets the
     * tent's wilds stay in the data.
     */
    private static final Map<String, ArdaMap.Country> CANON = Map.ofEntries(
            Map.entry(WEATHERTOP, ArdaMap.Country.ERIADOR),
            Map.entry(BOSS_CASTLE, ArdaMap.Country.ANGMAR),
            Map.entry(BOSS_ARENA, ArdaMap.Country.ANGMAR),
            Map.entry(WOOD_RUIN, ArdaMap.Country.TROLLSHAWS),
            // The camp the errands pitch and the three crags the stone asks in are the campaign's own
            // buildings, and a building that is pitched where the page was taken is a building two
            // players on one server get two of. They stand in the hills with the rest of the chapter,
            // on ground written down once.
            Map.entry(WILD_TENT, ArdaMap.Country.TROLLSHAWS),
            // Barachen's fire is south of the road in the same hills, and the camp the page walks to
            // stands in the cell that name is surveyed in.
            Map.entry(BARACHENS_CAMP, ArdaMap.Country.TROLLSHAWS),
            // Elladan's own cut is the camp the rider's pages are heard at, a walk east of the
            // valley in the same hills rather than in the valley itself.
            Map.entry(ELLADAN_CAMP, ArdaMap.Country.TROLLSHAWS),
            Map.entry(TROLL_CAVE, ArdaMap.Country.TROLLSHAWS),
            Map.entry(TROLL_CAVE_2, ArdaMap.Country.TROLLSHAWS),
            Map.entry(TROLL_CAVE_3, ArdaMap.Country.TROLLSHAWS),
            // Matuna's lair is in the wood east of the valley, the same hills the stone asks its caves in.
            Map.entry(TROLL_CAVE_4, ArdaMap.Country.TROLLSHAWS),
            // The camp is at the east end of the High Pass: the ground the pass drops into on the Anduin
            // side, which the drawing names Gladden, rather than the mountains it cuts through. That is where
            // the old world built it and where the notes send the player, north of the valley.
            Map.entry(GLOIN_CAMP, ArdaMap.Country.GLADDEN),
            // Gundabad is in the Hithaeglir - the Misty Mountains themselves - which is where the
            // notes put it and where the dwarves would have cut it. No cell is pinned for it, because
            // a building 122 by 183 can only stand where the ground is level enough to take it, and the
            // search has to be free to walk out from the country's middle to find that ground.
            Map.entry(GUNDABAD, ArdaMap.Country.HITHAEGLIR));
    /**
     * The cell of the drawing a landmark the books pinpoint is asked for in, as {column, row}.
     * <p>
     * The country alone cannot say where a small building stands in it: the ruin the rider is found at
     * is fourteen blocks across, so the middle of the Trollshaws takes it just as happily as the
     * channels south of the road do, and it came back in the anchor's own cell - a hundred and fifty
     * blocks from Elladan's fire, at the camp the page sends the player to. The ruin is asked for a
     * cell of its own now, and the country's middle remains the fallback for ground a building cannot
     * stand on.
     */
    private static final Map<String, int[]> CANON_CELL = Map.of(
            WOOD_RUIN, new int[]{19, 14},
            WILD_TENT, new int[]{17, 13},
            // Barachen's fire, the same cell the place is named in: the camp stands where its page is heard.
            BARACHENS_CAMP, new int[]{18, 14},
            // Thorenhad's own cell, which the place is named in too: the camp stands where its pages
            // are heard, a walk east of the valley and off the road.
            ELLADAN_CAMP, new int[]{19, 13},
            TROLL_CAVE, new int[]{17, 15},
            TROLL_CAVE_2, new int[]{18, 16},
            TROLL_CAVE_3, new int[]{19, 16},
            // The camp is pinned to the pass's mouth: the country alone would put it anywhere along the vale,
            // and a camp that is not on the road down out of the High Pass is a camp nobody walks past. Row 10
            // is north of the valley, which sits at row 12, and east of the ridge, which ends at column 23.
            GLOIN_CAMP, new int[]{24, 10});

    /**
     * How far from its country's middle a landmark may drift, in blocks: two cells of the map. The spot
     * does not have to be the exact hill the books name - the world's own ground decides where a building
     * can stand - but it does have to be in that part of the country rather than anywhere the country
     * reaches, which is the difference between Weathertop and a hilltop in the Shire's east marches.
     */
    private static final int CANON_REACH = ArdaMap.CELL * 2;

    /** How far out a site is picked, in blocks, and how much of that is left to chance. */
    private static final int PITCH_MIN = 48;
    private static final int PITCH_SPREAD = 48;
    /** How far off the line the spot is nudged sideways, so a site is not always dead ahead. */
    private static final int PITCH_ACROSS = 8;
    /** How close the player has to be before anything is built, in blocks: four chunks. */
    private static final int BUILD_REACH = 64;
    /**
     * How close a player has to be to the block a road pass has reached for the pass to be stepped, in blocks.
     * <p>
     * A road is not the building's business once it leaves the door. Measured from the building, the pass stopped
     * being stepped the moment a player walked along the road away from it, which is a road that stops in the middle
     * of a country. Measured from its own head, it keeps up with whoever is walking it - and a player who walks away
     * leaves it waiting at the block it had reached.
     */
    private static final int ROAD_REACH = 96;
    /**
     * How far from the page-taker a site is pitched at the nearest, in blocks. A page is a walk: a
     * landmark that rises where the page was taken is a landmark nobody had to go and find.
     */
    private static final int SITE_AWAY = 192;
    /**
     * How far out from the written spot real ground is looked for, in blocks. The drawing and the
     * world do not agree to the block - the drawing is read without the warp - so a spot the map
     * would happily take can sit on a slope the world actually made. The search used to reach eight
     * blocks and then give up without saying so, which is how a page came to wait on a tent that was
     * never going to stand.
     */
    private static final int SEARCH_REACH = 48;
    /**
     * How many layers of ground are put back under a building before the drop under it is read as a
     * cliff rather than a dip. The flatness test a site has to pass is measured in the same order, so
     * this only ever covers the shallow end of it - a building on a bank, not a building on a butte.
     */
    private static final int UNDERPIN_DEPTH = 6;
    /** How far the fill is allowed to reach when the ground under a footprint falls away badly. */
    private static final int UNDERPIN_MAX = 24;
    /**
     * How far into its own ground a building is set, in blocks, at the most: half the drop under its
     * footprint, and never more than this. Seated on the highest column alone, a building stood on a
     * plinth of its own fill the height of the slope it had been dropped on - a bare wall of dirt under
     * it and the ground a good way below the sill. Half the drop buried instead, the ground comes back
     * up around its foot and the slope shows through the bottom of the building rather than beside it.
     */
    private static final int SEAT_SINK = 4;
    /**
     * The crag's own seat. It is cut in two halves - bare rock from its foot to y 8, grass from y 9 to
     * its crown, with the mouth's sill at y 6 - so seating it like a house leaves the rock skirt
     * standing above the ground and the grass a good six blocks higher than the terrain it was dropped
     * on. Seated by its grass line instead, nine blocks in, the grass stood exactly at the ground it was
     * placed on - which is a crag that has been set down on a field rather than one the ground has taken
     * back: every block of its skirt and its shell showed, stone and deepslate, and the hill it was
     * dropped on had to be believed rather than seen. Fifteen blocks in buries the grass line six deep,
     * which is the skirt and the bare shell with it and only the mouth and the crown's own rock left
     * showing; sixteen is as deep as it is ever set, the one-block error the shape of the ground under
     * a footprint this size is allowed to leave, spent on both counts.
     */
    private static final Seat SUNK_CRAG = new Seat(15, 16, 0, 0, -1, Direction.NORTH);
    /** A building that stands on its ground rather than in it: hall, tent, house, arena. */
    private static final Seat ON_SURFACE = new Seat(0, SEAT_SINK, 0, 0, -1, Direction.NORTH);
    /**
     * Where each cut build's doorway is, in its own template's blocks: the column on the footprint's edge, and
     * the layer its sill sits on. A building is the only thing that knows which of the gaps in its walls is the
     * way in, so the numbers are read from the templates and written down here rather than guessed at build time.
     * <p>
     * The seat is then measured off that column: the ground the door opens onto is asked for, and the sill is put
     * level with it, so a player who walks up to the building steps into its floor rather than up onto it. A
     * building with no entry here keeps the older rule - half the drop under the footprint, within its own seat -
     * and the crags keep the one that buries them.
     * <p>
     * The castle and the arena are read from the lowest gap on their edge, which is the front step of both and
     * not something either template names; if one of them sits a layer off, its numbers are the line to fix.
     */
    private static final Map<String, Seat> DOORWAYS = Map.of(
            ELLADAN_CAMP, new Seat(0, SEAT_SINK, 8, 0, 1, Direction.NORTH),
            BARACHENS_CAMP, new Seat(0, SEAT_SINK, 8, 0, 1, Direction.NORTH),
            WOOD_RUIN, new Seat(0, SEAT_SINK, 0, 0, 0, Direction.NORTH),
            WILD_TENT, new Seat(0, SEAT_SINK, 0, 3, 0, Direction.WEST),
            BOSS_CASTLE, new Seat(0, SEAT_SINK, 31, 45, 2, Direction.SOUTH),
            BOSS_ARENA, new Seat(0, SEAT_SINK, 0, 0, 1, Direction.WEST));

    /**
     * What the stone standing at a building's door is called, by template: the name a player reads in the travel
     * list. A building with no line here gets no stone of its own, since an unnamed stone asks whoever arrives to
     * name it and the campaign's ground is not the player's to name.
     * <p>
     * The names are the places the books give these buildings: Thorenhad is Elladan's camp, the fire south of the
     * road is Barachen's, the three crags are the three chambers the stone is asked in, and the castle and the
     * arena under it are Carn Dum.
     */
    private static final Map<String, String> STONE_NAMES = Map.ofEntries(
            Map.entry(ELLADAN_CAMP, "Thorenhad"),
            Map.entry(BARACHENS_CAMP, "Barachen's fire"),
            Map.entry(WOOD_RUIN, "the wood ruin"),
            Map.entry(WILD_TENT, "the tent"),
            Map.entry(WEATHERTOP, "Weathertop"),
            Map.entry(GLOIN_CAMP, "Gloin's camp"),
            Map.entry(GUNDABAD, "Gundabad"),
            Map.entry(TROLL_CAVE, "Bandrith's crag"),
            Map.entry(TROLL_CAVE_2, "Hoklond's crag"),
            Map.entry(TROLL_CAVE_3, "Sholdroth's crag"),
            Map.entry(TROLL_CAVE_4, "Matuna's lair"),
            Map.entry(BOSS_CASTLE, "Carn Dum"),
            Map.entry(BOSS_ARENA, "the arena under Carn Dum"));

    /**
     * Stones that stand in every world with no building of the campaign's own underneath them: ground another hand
     * raised, where there is no template for {@link #DOORWAYS} to read a door out of. Each is a pin like any other,
     * and the stone is stood the first time a player comes near enough for the spot's chunk to be loaded.
     * <p>
     * The valley is the case this exists for. Its milestone belongs at the city rather than wherever a page's giver
     * happens to be standing, and the spot is one a scout marks on the ground once the city is standing - so the pin
     * is written here when that marker is walked, and every world after it gets the stone in the same place.
     */
    private record Stand(String pin, String name) {
    }

    private static final List<Stand> STANDING = List.of(
            new Stand("rivendell.stone", "Rivendell"));
    /** Which of the standing stones this world has already set, so the clock does not walk their ground again. */
    private static final Set<String> STOOD = new HashSet<>();

    /**
     * The name a camp's keeper answers to, by building. A camp the campaign names after a man is his fire, so the
     * body minding it is that man rather than one of the mod's folk wearing a borrowed name; a camp with no line
     * here keeps the drawn name.
     */
    private static final Map<String, String> KEEPER_NAMES = Map.of(BARACHENS_CAMP, "Barachen");

    /** The tag the body minding a camp wears, so it can be told from any other of its folk. */
    public static final String KEEPER_TAG = "tolkienmobs:camp_keeper";
    /** The tag the animal left at the ruin wears, so a second visit does not leave a second one. */
    public static final String BODY_TAG = "tolkienmobs:fallen_steed";
    /** How close to a camp's fire another keeper counts as already minding it, in blocks. */
    private static final int KEEPER_REACH = 32;
    /** How far above a written spot a body is looked for, in blocks: a camp's floor sits above its door's ground. */
    private static final int BODY_LIFT = 8;

    private static final Map<UUID, Site> SITES = new HashMap<>();
    /** The road passes being laid for the world's own builds, by the key of the site that owns each. */
    private static final Map<UUID, RoadCommands.RoadJob> ROADS = new HashMap<>();
    /** Players whose site has already been complained about, so a refusal is said once, not every tick. */
    private static final Set<UUID> UNBUILDABLE = new HashSet<>();
    /** The world's own buildings already checked against the ground this session, so a proof is walked once. */
    private static final Set<String> CHECKED = new HashSet<>();
    /** The blocks read out of each template's own file, so a template is read once rather than once a session. */
    private static final Map<String, List<Sample>> SAMPLES = new HashMap<>();
    /** How many of a template's blocks are compared with the ground: enough to tell a camp from a clearing. */
    private static final int SAMPLE = 48;
    private static final Sites NO_SERVER = new Sites(false);

    /**
     * One player's site: what is being built, where it was chosen, and how far along it is. The centre
     * is the middle of the building once it is standing, and the spot that was written down before
     * that, so a distance to it always means the same thing.
     */
    public record Site(UUID player, ResourceKey<Level> dimension, BlockPos centre, String structure,
                       boolean keeper, boolean built, boolean found, String country, boolean road) {
        static final Codec<Site> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Site::player),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Site::dimension),
                BlockPos.CODEC.fieldOf("centre").forGetter(Site::centre),
                Codec.STRING.fieldOf("structure").forGetter(Site::structure),
                Codec.BOOL.optionalFieldOf("keeper", false).forGetter(Site::keeper),
                Codec.BOOL.optionalFieldOf("built", false).forGetter(Site::built),
                Codec.BOOL.optionalFieldOf("found", false).forGetter(Site::found),
                Codec.STRING.optionalFieldOf("country", "").forGetter(Site::country),
                Codec.BOOL.optionalFieldOf("road", false).forGetter(Site::road)
        ).apply(instance, Site::new));

        Site builtAt(BlockPos middle) {
            return new Site(player, dimension, middle, structure, keeper, true, found, country, road);
        }

        /** The same site, noted as walked into. Named apart from the accessor it shadows. */
        Site markedFound() {
            return new Site(player, dimension, centre, structure, keeper, built, true, country, road);
        }

        /**
         * The same site, noted as having its road down.
         * <p>
         * Written with the building rather than kept in memory, because laying a road twice is not free: a pass
         * searches the contours over the ground and grades four hundred blocks of it, and the world would do that
         * again on every start, once for each build a player walked up to.
         */
        Site roaded() {
            return new Site(player, dimension, centre, structure, keeper, built, found, country, true);
        }
    }

    public static final Codec<Sites> CODEC = Site.CODEC.listOf().xmap(Sites::from, Sites::toList);

    public static final SavedDataType<Sites> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath("tolkienmobs", SAVE_DATA_NAME), Sites::new, CODEC);

    /** A world with no site file starts with nothing built, and forgets whatever the last world built. */
    private Sites() {
        this(true);
    }

    /** A stand-in for a level with no server behind it, which neither reads nor clears anything. */
    private Sites(boolean fresh) {
        super();
        if (fresh) {
            SITES.clear();
            STOOD.clear();
            CHECKED.clear();
            SAMPLES.clear();
        }
    }

    // ---- choosing the ground ----

    /**
     * Chooses where this player's structure will stand. It is picked in the direction they are facing
     * rather than at random, so a player who takes a page and walks on walks toward it.
     *
     * @param structure the template to build, as its own id, as in {@code tolkienmobs:wild_tent}
     * @param keeper    whether to stand one of the mod's folk at it once it is built, which a camp wants
     *                  and an arena does not
     * @return whether a spot could be written down at all
     */
    public static boolean plan(ServerPlayer player, String structure, boolean keeper) {
        if (!(player.level() instanceof ServerLevel level)) return false;
        // Nothing is pitched outside Arda. The drawing is what decides where a building stands, so a
        // level without it has no answer for a page to be built on - and the overworld, which keeps
        // vanilla's generation, is left entirely alone.
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return false;
        Sites store = instance(level);
        UNBUILDABLE.remove(player.getUUID());
        Site existing = SITES.get(player.getUUID());
        if (existing != null && level.dimension().equals(existing.dimension())) {
            // A building that is already standing is the answer to every page that names it, however
            // many times the page is taken. Surveying it again finds the map's own spot, and building it
            // again raises a copy - on ground its first copy has already changed, so a copy beside it
            // rather than over it. A player walking the campaign through a second time met a second crag
            // at every cave.
            if (existing.built() && existing.structure().equals(structure)) return true;
            // A second page to a structure the player has not reached yet is the same ground: a quest in
            // pieces sends them on to what they are already walking toward rather than pitching a rival
            // beside it. Once they have found one, a new page is a new place.
            // A landmark is the exception: its ground comes from the map rather than from where the page
            // was taken, so a site pitched before the map was asked where the landmark belonged is
            // corrected instead of kept.
            if (!existing.found() && existing.structure().equals(structure)
                    && !CANON.containsKey(structure)) {
                return true;
            }
        }
        // A landmark stands once in a world rather than once for each player sent to it: whoever takes a
        // page naming it is sent to the building that is already there, and nothing is raised twice.
        Site standing = standing(level, structure);
        if (standing != null) {
            SITES.put(player.getUUID(), new Site(player.getUUID(), standing.dimension(), standing.centre(),
                    structure, keeper, true, false, standing.country(), false));
            store.setDirty();
            return true;
        }
        // The drawing answers first. A structure's own biome list decides the country it may stand
        // in - the tent's wilds are already in the data - so nothing is written down here about where
        // anything belongs, beyond the handful of landmarks the books put somewhere specific.
        ArdaSurvey.Ground surveyed = survey(level, player, structure);
        if (surveyed != null) {
            SITES.put(player.getUUID(), new Site(player.getUUID(), level.dimension(),
                    surveyed.centre(), structure, keeper, false, false, surveyed.country().name(), false));
            store.setDirty();
            return true;
        }
        // A world the map does not draw keeps the older answer: a spot out in front of the player.
        RandomSource random = level.getRandom();
        Direction facing = player.getDirection();
        Direction across = facing.getClockWise();
        int reach = PITCH_MIN + random.nextInt(PITCH_SPREAD);
        int side = random.nextInt(PITCH_ACROSS * 2 + 1) - PITCH_ACROSS;
        BlockPos centre = player.blockPosition().offset(
                facing.getStepX() * reach + across.getStepX() * side,
                0,
                facing.getStepZ() * reach + across.getStepZ() * side);
        SITES.put(player.getUUID(), new Site(player.getUUID(), level.dimension(), centre, structure,
                keeper, false, false, "", false));
        store.setDirty();
        return true;
    }

    /** A site of this structure that is already standing in this world, or null when none is. */
    @Nullable
    private static Site standing(ServerLevel level, String structure) {
        for (Site site : SITES.values()) {
            if (!site.built() || !site.structure().equals(structure)) continue;
            if (level.dimension().equals(site.dimension())) return site;
        }
        return null;
    }

    /**
     * Whether a building the record says is standing is still in the world.
     * <p>
     * A sample of the template's own blocks is compared with what is at those blocks now, and a majority is enough:
     * this answers whether a camp is there, not whether anybody has been inside it.
     * <p>
     * The sample is read from the template's file rather than from the loaded template. Vanilla's way of asking a
     * template for its blocks takes a block to filter by and caches the answer under that block, so asking for all of
     * them means handing it a null and crashing in its cache - which it did, in a player's tick. The file is the same
     * source the template came from, the corner is the one the site's centre was recorded from, and the comparison is
     * between block names, so nothing here depends on properties or on which palette variant was saved.
     * <p>
     * Nothing is decided from chunks that are not loaded - an answer of "gone" has to be an answer and not a guess -
     * and a file that will not read leaves the record trusted rather than crashing the player it was reading for.
     */
    private static boolean standingBlocks(ServerLevel level, Site site) {
        Identifier id = Identifier.tryParse(site.structure());
        if (id == null) return true;
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) return true;
        Vec3i size = template.getSize();
        BlockPos corner = site.centre().offset(-size.getX() / 2, 0, -size.getZ() / 2);
        if (!level.hasChunkAt(corner) || !level.hasChunkAt(corner.offset(size.getX() - 1, 0, size.getZ() - 1))) {
            return true;
        }
        List<Sample> samples = samplesOf(level, id);
        if (samples.isEmpty()) return true;
        int found = 0;
        for (Sample sample : samples) {
            BlockState state = level.getBlockState(corner.offset(sample.x(), sample.y(), sample.z()));
            if (BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString().equals(sample.block())) found++;
        }
        return found * 2 >= samples.size();
    }

    /** One of a template's own blocks: where it sits from the corner, and the block the file says is there. */
    private record Sample(int x, int y, int z, String block) {}

    /** A few of the template's blocks, read out of the file the template itself was loaded from. */
    private static List<Sample> samplesOf(ServerLevel level, Identifier id) {
        String key = id.toString();
        List<Sample> samples = SAMPLES.get(key);
        if (samples != null) return samples;
        samples = readSamples(level, id);
        SAMPLES.put(key, samples);
        return samples;
    }

    private static List<Sample> readSamples(ServerLevel level, Identifier id) {
        Identifier file = Identifier.fromNamespaceAndPath(id.getNamespace(), "structure/" + id.getPath() + ".nbt");
        CompoundTag tag;
        try (InputStream stream = level.getServer().getResourceManager().getResourceOrThrow(file).open()) {
            tag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        } catch (IOException | RuntimeException unreadable) {
            LOGGER.warn("Could not read {} to check a building the world says is standing", file, unreadable);
            return List.of();
        }
        ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
        ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
        if (palette.isEmpty() || blocks.isEmpty()) return List.of();
        String[] names = new String[palette.size()];
        for (int index = 0; index < palette.size(); index++) {
            names[index] = palette.getCompoundOrEmpty(index).getStringOr("Name", "");
        }
        int stride = Math.max(1, blocks.size() / SAMPLE);
        List<Sample> samples = new ArrayList<>();
        for (int index = 0; index < blocks.size() && samples.size() < SAMPLE; index += stride) {
            CompoundTag block = blocks.getCompoundOrEmpty(index);
            int state = block.getIntOr("state", -1);
            if (state < 0 || state >= names.length) continue;
            String name = names[state];
            if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
            ListTag pos = block.getList("pos").orElseGet(ListTag::new);
            if (pos.size() < 3) continue;
            samples.add(new Sample(pos.getIntOr(0, 0), pos.getIntOr(1, 0), pos.getIntOr(2, 0), name));
        }
        return samples;
    }

    /** Where this player's site is, or null when they have not been sent to one. */
    @Nullable
    public static Site siteOf(ServerPlayer player) {
        instance(player.level());
        return SITES.get(player.getUUID());
    }

    /** The country of the map a player's site stands in, or an empty string when none was named. */
    public static String countryOf(ServerPlayer player) {
        Site site = siteOf(player);
        return site == null ? "" : site.country();
    }

    /** Whether the player is standing in the structure they were sent to find. */
    public static boolean reached(ServerPlayer player) {
        Site site = siteOf(player);
        if (site == null || !site.built()) return false;
        if (!player.level().dimension().equals(site.dimension())) return false;
        return away(player, site) <= foundReach(player.level(), site);
    }

    /**
     * Lays a build's road, once the build is standing.
     * <p>
     * A road is the map's own work rather than a page's: each build's two ends are a {@link Route} in {@link Roads},
     * and the pass that laid the valley's road is the one that lays these - the same search for a way round a hillside
     * rather than down it, and the same grade cut and filled into the ground, so no height is ever written down for a
     * road. The pass is planned the first time a player is near enough for the build's ground to be loaded, and stepped
     * a little on each approach after that, because a road four hundred blocks long is not one tick's work.
     */
    private static void road(ServerLevel level, Site site) {
        if (site.road()) return;
        UUID key = site.player();
        Vec3i size = footprint(level, site.structure());
        if (size == null) return;
        BlockPos corner = site.centre().offset(-size.getX() / 2, 0, -size.getZ() / 2);
        Route route = Roads.forGround(corner.getX(), corner.getZ(),
                corner.getX() + size.getX() - 1, corner.getZ() + size.getZ() - 1);
        // A build nobody has walked a road for gets none: a road laid to another build's numbers is worse than ground
        // with no way down it. Nothing is written down for that, so the day a route is walked the world can lay it.
        if (route == null) return;
        if (RoadCommands.down(level, route)) {
            // The road is down already, so the site's own record catches up without planning a second pass over it.
            SITES.put(key, SITES.getOrDefault(key, site).roaded());
            instance(level).setDirty();
            return;
        }
        if (!ROADS.containsKey(key)) ROADS.put(key, RoadCommands.plan(route));
    }

    /**
     * Steps every road this world is laying while its own head is near the player.
     * <p>
     * This is the pass a section is actually laid by. It is measured from the block the pass has reached rather than
     * from the build, so a player walking the road carries it along - which is what a road filling in as its section
     * is walked has to mean for a road four hundred blocks long. A player who leaves stops the work where it stands;
     * the record of it is written to the world every few steps, so coming back resumes the same grade rather than
     * laying a second one.
     */
    private static void roads(ServerPlayer player) {
        if (ROADS.isEmpty()) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return;
        for (UUID key : new ArrayList<>(ROADS.keySet())) {
            RoadCommands.RoadJob job = ROADS.get(key);
            Site site = SITES.get(key);
            if (job == null || site == null) {
                ROADS.remove(key);
                continue;
            }
            int[] head = job.head();
            double dx = player.getX() - head[0];
            double dz = player.getZ() - head[1];
            if (Math.sqrt(dx * dx + dz * dz) > ROAD_REACH) continue;
            boolean more;
            try {
                more = job.step(level);
            } catch (IOException unreadable) {
                // A pass that cannot run is not retried on every step; the record says the road is the ground's now.
                more = false;
            }
            if (more) continue;
            ROADS.remove(key);
            // The record as it stands now, not as this step found it: dressing may have moved the build's centre since.
            SITES.put(key, SITES.getOrDefault(key, site).roaded());
            instance(level).setDirty();
        }
    }

    /**
     * Plants the chapter's own buildings, and stands them as players come near.
     * <p>
     * A building the world raises is not one player's: it stands where the scout pinned it, and every player finds
     * the same camp there. So it is held under a key of its own rather than under anybody's id, and a page that later
     * names it is pointed at the ground already standing instead of pitching a second copy - {@link #plan} asks
     * {@link #standing} before it surveys, and this is what makes that answer findable.
     * <p>
     * The planting is a pin lookup and a template's own size; nothing is built until a player is within
     * {@link #BUILD_REACH} of it, because a chunk nobody is looking at is a chunk the game will not load, and a block
     * written into an unloaded chunk is quietly dropped. That is the rule a page's own site is held to as well, and it
     * is why a regenerated world fills in as its chapter is walked rather than all at once.
     */
    private static void scenery(ServerPlayer player) {
        if (!(player.level() instanceof ServerLevel level)) return;
        if (!TolkienDimensions.ARDA.equals(level.dimension())) return;
        for (Scenery raised : sceneryFor(level)) {
            String structure = raised.structure();
            Site standing = standing(level, structure);
            if (standing != null) {
                if (away(player, standing) > BUILD_REACH) continue;
                // The record is not the ground. A world whose chunks were regenerated or rolled back - a save copied
                // without its region files is how this was found - keeps a site saying the camp stands and loses the
                // camp. The template's own blocks are compared against the ground once a session, and a building that
                // is mostly gone is forgotten and raised again from the pin.
                if (!CHECKED.add(structure) || standingBlocks(level, standing)) {
                    // What a build carries besides its walls: the man at the fire, the animal at the ruin, the stone
                    // and the way down to it. Each of the three refuses to be placed twice, so asking on every
                    // approach cannot multiply them. Asking has to happen here, on the path a standing build takes -
                    // this used to continue past it, and a site is standing from the moment it is raised, so every
                    // camp in the world stood with no keeper, no stone and no road ever laid from its door.
                    dress(level, standing, player);
                    road(level, standing);
                    continue;
                }
                SITES.remove(standing.player());
                instance(level).setDirty();
            }
            UUID key = sceneryId(structure);
            Site site = SITES.get(key);
            // A pin outranks a spot already written down. A building planted before its pin was walked is holding the
            // drawing's guess, and a scout who re-pins it expects the world to raise it at the pin - the rule a page's
            // own landmark already follows. Only ground that is not built yet is re-seated: a camp already standing
            // stays where it stands rather than being doubled at the new pin.
            if (site != null && !site.built() && level.dimension().equals(site.dimension())) {
                BlockPos pinned = Pins.at(level, structure);
                if (pinned != null
                        && (site.centre().getX() != pinned.getX() || site.centre().getZ() != pinned.getZ())) {
                    SITES.remove(key);
                    site = null;
                    instance(level).setDirty();
                }
            }
            if (site == null || !level.dimension().equals(site.dimension())) {
                ArdaSurvey.Ground ground = survey(level, player, structure);
                if (ground == null) continue;
                site = new Site(key, level.dimension(), ground.centre(), structure, raised.keeper(), false,
                        false, ground.country().name(), false);
                SITES.put(key, site);
                instance(level).setDirty();
            }
            if (away(player, site) > BUILD_REACH) continue;
            // A building raised before the man, the animal or the stone existed keeps its walls and nothing else:
            // the site is recorded as built, so the laying never runs again. Asking again on every approach is
            // cheap and each of the three refuses to be placed twice, which is what lets an old world catch up.
            if (site.built()) {
                dress(level, site, player);
                road(level, site);
            } else build(level, site, player);
        }
        for (Stand stand : STANDING) {
            if (STOOD.contains(stand.pin())) continue;
            BlockPos pinned = Pins.at(level, stand.pin());
            if (pinned == null || away(player, pinned) > BUILD_REACH) continue;
            if (!level.hasChunkAt(pinned)) continue;
            STOOD.add(stand.pin());
            // The marker is where the stone goes, and the stone replaces it; the surface under the pin is the
            // answer for a world where the marker was never laid.
            BlockPos spot = level.getBlockState(pinned).is(TolkienBlocks.BLOCK_AMMOLITE.get())
                    ? pinned
                    : level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pinned);
            Stones.stone(level, spot, stand.name(), player);
        }
    }

    /** The key the world's own site for a building is held under, apart from every player's. */
    private static UUID sceneryId(String structure) {
        return UUID.nameUUIDFromBytes(("scenery:" + structure).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Every building this world raises: the chapter's own list, and every structure a pin names.
     * <p>
     * Scouting is what makes a world the world the notes describe, so a pin is the whole of adding a building - a
     * scout who walks a hillside and writes the numbers down expects the next world to have the thing standing on it,
     * whether or not anybody has taken a page to it. The chapter's own list keeps the only thing a pin cannot say:
     * what stands at a building when it is raised, the man at a camp and the animal at the ruin.
     * <p>
     * A pin that names a role rather than a building - a keeper's spot, a body's, a road's end - is not raised, and
     * neither is a place's name: a pin with no path in it names ground to announce, not a template to build.
     */
    private static List<Scenery> sceneryFor(Level level) {
        List<Scenery> all = new ArrayList<>(SCENERY);
        Set<String> known = new HashSet<>();
        for (Scenery raised : SCENERY) known.add(raised.structure());
        for (String structure : pinnedStructures(level)) {
            if (known.add(structure)) all.add(new Scenery(structure, Body.NONE));
        }
        return all;
    }

    /** The pins that name a building to raise, from the pack's own table and from this world's. */
    private static Set<String> pinnedStructures(Level level) {
        Set<String> structures = new LinkedHashSet<>();
        for (String key : Pins.sourceKeys()) {
            if (isStructure(key)) structures.add(key);
        }
        for (Pins.Pin pin : Pins.pinned(level)) {
            if (isStructure(pin.key())) structures.add(pin.key());
        }
        return structures;
    }

    /** Whether a pin's key is a template to build, as against a name to announce or a role at a build. */
    private static boolean isStructure(String key) {
        if (!key.contains("/")) return false;
        for (String role : List.of(KEEPER_PIN, BODY_PIN, ".end", ".road")) {
            if (key.endsWith(role)) return false;
        }
        return true;
    }

    /**
     * Looks at where the player is, builds their site once they are close enough for the chunk to be
     * loaded, and tells them when they have walked into it. Called from the same clock that names the
     * ground they are walking over.
     */
    public static void tick(ServerPlayer player) {
        // The chapter's own buildings are not a page's to raise: they are planted and stood before anybody is
        // sent to them, so a world is the world the notes describe rather than empty hills until a page names
        // something.
        scenery(player);
        roads(player);
        Site site = siteOf(player);
        if (site == null) return;
        if (!player.level().dimension().equals(site.dimension())) return;
        if (!(player.level() instanceof ServerLevel level)) return;
        double away = away(player, site);
        if (!site.built()) {
            if (away <= BUILD_REACH) build(level, site, player);
            return;
        }
        if (site.found() || away > foundReach(level, site)) return;
        SITES.put(player.getUUID(), site.markedFound());
        instance(level).setDirty();
        player.sendSystemMessage(keeperNearby(player, site)
                ? Component.translatable("quest.tolkienmobs.site.keeper", keeperName(player))
                        .withStyle(ChatFormatting.GOLD)
                : Component.translatable("quest.tolkienmobs.site.found").withStyle(ChatFormatting.GOLD));
    }

    /**
     * The ground the map can offer this structure near the player, or null when the level is not
     * Arda's or the template is not in the pack, which is what falls back to the older pick.
     */
    @Nullable
    private static ArdaSurvey.Ground survey(ServerLevel level, ServerPlayer player, String structure) {
        Vec3i size = footprint(level, structure);
        if (size == null) return null;
        // A building scouted on real ground stands where the scout stood. The spot is written down as
        // given and the survey is skipped, since its whole job was to guess the ground a scout has
        // since looked at; the laying still measures the footprint and seats it, so the building is
        // not dropped onto a slope the drawing never knew about.
        BlockPos scouted = Pins.at(level, structure);
        if (scouted != null) {
            return new ArdaSurvey.Ground(ArdaMap.sample(scouted.getX(), scouted.getZ()).country(), scouted);
        }
        // A landmark is looked for from the middle of the country the books give it, so the hilltop
        // stands in the heart of Eriador whether the page was taken in the Shire, in Bree or at the
        // herald's own bench. Everything else is looked for around the player - but never at their
        // feet, since the search begins in the cell they are standing in.
        ArdaMap.Country home = CANON.get(structure);
        if (home == null) {
            return ArdaSurvey.find(level, player.blockPosition(), countries(level, structure),
                    size.getX(), size.getZ(), structure.hashCode(), SITE_AWAY, 0);
        }
        int[] cell = CANON_CELL.get(structure);
        if (cell != null) {
            ArdaSurvey.Ground pinned = ArdaSurvey.find(level, ArdaMap.centreOf(cell[0], cell[1]),
                    home::equals, size.getX(), size.getZ(), structure.hashCode(), 0, ArdaMap.CELL / 2);
            if (pinned != null) return pinned;
        }
        BlockPos from = ArdaMap.anchor(home);
        // The landmark's own country first, so it stands in the country the books give it. A country
        // that will not take a building this size anywhere near its middle is asked again with no
        // opinion about which country it is - the drift stays inside the same two cells either way, so
        // the answer is still the right part of the map.
        ArdaSurvey.Ground ground = ArdaSurvey.find(level, from, home::equals,
                size.getX(), size.getZ(), structure.hashCode(), 0, CANON_REACH);
        return ground != null ? ground : ArdaSurvey.find(level, from, ArdaMap.Country::land,
                size.getX(), size.getZ(), structure.hashCode(), 0, CANON_REACH);
    }

    /** A template's own size, or null when there is no such template to build. */
    @Nullable
    private static Vec3i footprint(ServerLevel level, String structure) {
        Identifier id = Identifier.tryParse(structure);
        if (id == null) return null;
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) return null;
        Vec3i size = template.getSize();
        return size.getX() <= 0 || size.getZ() <= 0 ? null : size;
    }

    /**
     * The countries a structure may stand in, read from the structure's own biome list rather than
     * written down again here: a page naming a third structure gets its answer without anybody
     * editing this, and the tent and the castle cannot drift from the data they were registered with.
     */
    private static Predicate<ArdaMap.Country> countries(ServerLevel level, String structure) {
        Identifier id = Identifier.tryParse(structure);
        if (id == null) return ArdaMap.Country::land;
        Set<ResourceKey<Biome>> allowed = level.registryAccess()
                .lookupOrThrow(Registries.STRUCTURE)
                .get(ResourceKey.create(Registries.STRUCTURE, id))
                .map(holder -> holder.value().biomes().stream()
                        .map(biome -> biome.unwrapKey().orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()))
                .orElse(Set.of());
        if (allowed.isEmpty()) return ArdaMap.Country::land;
        return country -> allowed.contains(country.biome());
    }

    /**
     * Builds it: the template on tested ground, and the body at it if the site asked for one. A template
     * that is not in the pack, or a footprint that no ground nearby will take, leaves the site unbuilt
     * rather than half-built, so the next look can try again.
     */
    private static void build(ServerLevel level, Site site, ServerPlayer player) {
        Identifier id = Identifier.tryParse(site.structure());
        if (id == null) return;
        StructureTemplate template = level.getStructureManager().get(id).orElse(null);
        if (template == null) return;
        Vec3i size = template.getSize();
        if (size.getX() <= 0 || size.getZ() <= 0) return;
        // Everything the building stands over has to be loaded, not just the chunk at its middle:
        // a block written into an unloaded chunk is quietly dropped, and half a castle is worse than
        // none. The tent fit in one chunk and never needed to ask; a hilltop a hundred blocks across
        // does, and waits at the site until the player is close enough for all of it.
        // The corner it is laid from, before anything is measured: a pin names it, and a build with no pin is
        // centred on the spot the drawing guessed at. Checked at the footprint's own two opposite corners - the pair
        // furthest apart is the furthest anything can be - because a box around the centre is not the footprint of a
        // build laid from its corner, and the blocks written past its far edge would be dropped without a word.
        BlockPos pinned = Pins.at(level, site.structure());
        BlockPos anchor = pinned != null ? pinned : site.centre().offset(-size.getX() / 2, 0, -size.getZ() / 2);
        if (!chunksLoaded(level, anchor, size.getX(), size.getZ())) return;
        Seat seat = seatFor(site.structure());
        // A pinned building stands at its pin, and nowhere else. The corner the pin names is the corner the
        // template is laid from, so the door the road leaves by and the pin a scout walked are the same block; the
        // search used to move a camp up to eight rings away from it and put the camp and its road in two places.
        // A pin is measured, not trusted blindly - the seat still reads the ground under the footprint and sinks the
        // build into it - but a pin whose own ground will not take the footprint is a pin to walk again, not a spot
        // to drift away from: nothing is built, and the log says so. Only a building with no pin is searched for,
        // which is a building the drawing guessed at and no scout has stood on.
        Plane found;
        if (pinned != null) {
            found = plane(level, pinned, size.getX(), size.getZ(), seat);
            if (found == null) {
                if (UNBUILDABLE.add(site.player())) {
                    LOGGER.info("The pin at {} will not take {} - re-pin it rather than move the build ({})",
                            pinned, site.structure(),
                            refusal(level, pinned, size.getX(), size.getZ(), seat));
                }
                return;
            }
        } else {
            found = locate(level, site.centre(), size.getX(), size.getZ(), seat);
        }
        if (found == null) {
            // Ground the drawing would take and the world would not: worth saying, because the
            // player standing on the spot sees only that nothing happened. Once per site, since
            // this is retried on the clock the player is measured on.
            if (UNBUILDABLE.add(site.player())) {
                LOGGER.info("No ground within {} blocks of {} would take {}", SEARCH_REACH,
                        site.centre(), site.structure());
            }
            return;
        }
        UNBUILDABLE.remove(site.player());
        BlockPos corner = found.corner();
        // A building on a bank wants a bank under it, so the fill reaches as far down as the ground
        // it was lifted off actually fell away, up to a limit that keeps a butte from a plinth.
        underpin(level, corner, size.getX(), size.getZ(),
                Math.min(UNDERPIN_MAX, Math.max(UNDERPIN_DEPTH, found.relief() + 2)));
        template.placeInWorld(level, corner, corner, new StructurePlaceSettings(), level.getRandom(), 2);
        // The ground it was laid into is carried to its own roofline, the way the valley's is dressed to the cut's:
        // without this a build seated in a bank kept the bank standing over it, which is a camp nobody can find.
        sweep(level, corner, size, site.structure());
        // What the place carries once it stands: the man at a camp, the animal the rider came off at the ruin,
        // and the stone every building has so the travel list can bring a player back to it.
        Body body = site.keeper() ? Body.KEEPER : bodyFor(site.structure());
        if (body == Body.KEEPER) standKeeper(level, corner, size, seat, site.structure());
        if (body == Body.STEED) standSteed(level, corner, size, seat, site.structure());
        stoneAt(level, corner, size, seat, site.structure(), player);
        SITES.put(site.player(), site.builtAt(corner.offset(size.getX() / 2, 0, size.getZ() / 2)));
        instance(level).setDirty();
    }

    /**
     * Puts what belongs at a building that is already standing, for a world that raised it before any of it
     * existed: the man at a camp, the animal at the ruin and the stone beside the door. All three refuse to be
     * placed twice - one keeper a camp, one steed a ruin, a stone never stood over - so asking on every approach
     * cannot multiply them.
     */
    private static void dress(ServerLevel level, Site site, ServerPlayer player) {
        Vec3i size = footprint(level, site.structure());
        if (size == null) return;
        BlockPos corner = site.centre().offset(-size.getX() / 2, 0, -size.getZ() / 2);
        Seat seat = seatFor(site.structure());
        // Once a session, a build already standing has its ground swept the way a new one does: a world raised before
        // this pass existed is the world with a hill over its camps, and walking up to one is reason enough to fix it.
        if (SWEPT.add(site.structure())) sweep(level, corner, size, site.structure());
        Body body = site.keeper() ? Body.KEEPER : bodyFor(site.structure());
        if (body == Body.KEEPER) standKeeper(level, corner, size, seat, site.structure());
        if (body == Body.STEED) standSteed(level, corner, size, seat, site.structure());
        stoneAt(level, corner, size, seat, site.structure(), player);
    }

    /**
     * The nearest ground to the written spot that will take a building of this size, or null when none
     * of it will. The rings are tried nearest first, and the step between them grows with the building,
     * so a castle is not searched for at tent-sized intervals.
     */
    @Nullable
    private static Plane locate(ServerLevel level, BlockPos written, int wide, int deep, Seat seat) {
        int step = Math.max(8, Math.max(wide, deep) / 4);
        int reach = Math.max(SEARCH_REACH, Math.max(wide, deep));
        int drop = ArdaSurvey.dropAllowed(wide, deep);
        Plane best = null;
        for (int ring = 0; ring * step <= reach; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) continue;
                    BlockPos spot = written.offset(dx * step, 0, dz * step);
                    // Both ends of the footprint, not just the near one: a site may now stand a
                    // hundred blocks from the spot the page named, and a block written into an
                    // unloaded chunk is dropped without a word - half a hilltop is worse than none.
                    if (!level.hasChunkAt(spot) || !level.hasChunkAt(spot.offset(wide, 0, deep))) continue;
                    Plane plane = plane(level, spot, wide, deep, seat);
                    if (plane == null) continue;
                    if (plane.relief() <= drop) return plane;
                    // Ground that is only mostly level is still ground. A refuse here left a page
                    // waiting on a hilltop that was never going to stand, and the alternative is
                    // what the building does everywhere else: stand on the highest column and let
                    // the hill show through what it left empty. So the search keeps the gentlest
                    // spot it has seen and hands that back rather than nothing at all.
                    if (best == null || plane.relief() < best.relief()) best = plane;
                }
            }
        }
        return best;
    }

    /**
     * Ground a footprint would stand on: where it stands, and how far the ground under it falls away from
     * its highest column to its lowest. The falling away is reported rather than refused so that the
     * search can pick the gentlest spot it found instead of coming back empty handed.
     */
    private record Plane(BlockPos corner, int relief) {
    }

    /**
     * How deep a building is set into its ground, and which way its way in opens: never less than
     * {@code floor}, and never more than {@code cap}. A house asks for no floor and takes half the drop
     * under it; the cave asks to be buried by its grass line rather than seated on its base.
     * <p>
     * The door numbers are the cut's own: the column on the footprint's edge that is the doorway, the layer
     * its sill sits on, and the direction the gap opens onto the outside. Everything that has to know which
     * way a building faces is measured from them - the seat, the man at the fire, the stone beside the door -
     * so a build's facing is read from its entrance rather than written down a second time.
     */
    private record Seat(int floor, int cap, int doorX, int doorZ, int doorY, Direction front) {
        /** Whether this building knows where its door is, and is seated by that ground rather than by its own. */
        boolean doorway() {
            return doorY >= 0;
        }
    }

    /**
     * Which seat a structure wants, by the one thing that differs about it. The crag is the mod's only
     * build whose lower half is rock meant to be underground; every other piece - hall, tent, house,
     * arena - sits on its ground, and says so by asking for no floor at all.
     */
    /** The crag the nth asking of the stone is made in, and the last of them for any asking past it. */
    public static String caveAt(int asked) {
        return CAVE_VARIANTS.get(Math.max(0, Math.min(CAVE_VARIANTS.size() - 1, asked)));
    }

    /** Whether a template is one of the campaign's crags, which are buried in their ground, not seated on it. */
    public static boolean isCave(String structure) {
        return CAVE_VARIANTS.contains(structure);
    }

    private static Seat seatFor(String structure) {
        Seat door = DOORWAYS.get(structure);
        return door != null ? door : isCave(structure) ? SUNK_CRAG : ON_SURFACE;
    }

    /**
     * The ground a building of this size would stand on, or null when the footprint is not ground at all.
     * <p>
     * Three things are asked of it. Every column has to reach solid, dry ground - and the height it is
     * built at is the <em>highest</em> column under it rather than an average, because a building placed
     * at the lowest point has a hillside through the middle of it, and a hillside is harder to excuse in a
     * castle than a gap under one wall. It is then set a little below that column - half the drop under the
     * footprint within that building's own seat - so that what would have been left standing in the air is ground
     * the building has been sunk into instead.
     * <p>
     * Large buildings are sampled on a stride, since a castle is thousands of columns and the ground under
     * one is not going to change between two adjacent ones in any way that matters.
     */
    @Nullable
    private static Plane plane(ServerLevel level, BlockPos spot, int wide, int deep, Seat seat) {
        int stride = Math.max(1, Math.max(wide, deep) / 24);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int low = Integer.MAX_VALUE;
        int high = Integer.MIN_VALUE;
        for (int dx = 0; dx < wide; dx += stride) {
            for (int dz = 0; dz < deep; dz += stride) {
                int height = ground(level, spot.getX() + dx, spot.getZ() + dz, cursor);
                if (height == Integer.MIN_VALUE) return null;
                low = Math.min(low, height);
                high = Math.max(high, height);
            }
        }
        int sink;
        if (seat.doorway()) {
            // The doorway's own column is measured, and the template's sill laid level with what it opens
            // onto. ground() answers with the first air over a column, so that column's surface is one below
            // its answer, and the sill block sits on top of it.
            int sill = ground(level, spot.getX() + seat.doorX(), spot.getZ() + seat.doorZ(), cursor);
            if (sill == Integer.MIN_VALUE) return null;
            sink = Math.min(seat.cap(), Math.max(0, high - (sill - 1 - seat.doorY())));
        } else {
            // Half the drop under the footprint, so the plinth it would have stood on is halved and the
            // ground rises around its base. Bounded, because a castle on a hillside still has to keep its
            // door above the ground it opens onto, and flat ground has nothing to sink into at all.
            sink = Math.min(seat.cap(), Math.max(seat.floor(), (high - low) / 2));
        }
        return new Plane(new BlockPos(spot.getX(), high - sink, spot.getZ()), high - low);
    }

    /**
     * The top of the ground in one column, with whatever is growing on it discounted.
     * <p>
     * A heightmap answers with the top of the world, and the top of the world in a wood is a trunk: a
     * forest footprint therefore measured as a cliff from trunk to grass and no building of any size
     * could be seated in one, which is exactly what a hilltop in the trees kept hitting. Walking down
     * past the wood answers what the column is actually made of. Anything that is not ground - open air,
     * water, a column buried in stone - comes back as {@link Integer#MIN_VALUE}.
     */
    private static int ground(ServerLevel level, int x, int z, BlockPos.MutableBlockPos cursor) {
        int height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        for (int guard = 0; guard < 96; guard++) {
            cursor.set(x, height - 1, z);
            BlockState state = level.getBlockState(cursor);
            // Water is not ground and is not walked down past: a footprint in a river is a footprint in a river, and
            // a building meant for that bank has to be re-pinned rather than sunk to the bed of it.
            if (!level.getFluidState(cursor).isEmpty()) return Integer.MIN_VALUE;
            // What grows on the ground is not the ground. A heightmap answers with the top of a flower, so a footprint
            // that read one as a cliff refused a whole meadow - which is what stopped two of the three crags being
            // raised at their pins, a crag's footprint being wide enough that some column of any meadow has a tuft on
            // it. Wood and anything with no collision at all are stepped down through, so the answer is the earth the
            // column is actually made of.
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)
                    || state.getCollisionShape(level, cursor).isEmpty()) {
                height--;
                continue;
            }
            if (!state.isSolid()) return Integer.MIN_VALUE;
            cursor.setY(height);
            if (!level.getFluidState(cursor).isEmpty()) return Integer.MIN_VALUE;
            return height;
        }
        return Integer.MIN_VALUE;
    }

    /**
     * Why a footprint would not stand on a spot, for the log: the first column that is not ground, and what is there.
     * <p>
     * Asked only when a pin has refused a build, so the sweep costs nothing in the ordinary case and the note is a
     * note rather than a second walk. "Re-pin it" without saying what refused it is a report that takes the scout
     * back up the hill to look at a column they have already stood on.
     */
    private static String refusal(ServerLevel level, BlockPos spot, int wide, int deep, Seat seat) {
        int stride = Math.max(1, Math.max(wide, deep) / 24);
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < wide; dx += stride) {
            for (int dz = 0; dz < deep; dz += stride) {
                int x = spot.getX() + dx;
                int z = spot.getZ() + dz;
                if (ground(level, x, z, cursor) != Integer.MIN_VALUE) continue;
                int top = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                return x + ", " + z + " is " + level.getBlockState(new BlockPos(x, top, z)).getBlock().getName().getString()
                        + " at " + top;
            }
        }
        if (seat.doorway()) {
            int x = spot.getX() + seat.doorX();
            int z = spot.getZ() + seat.doorZ();
            if (ground(level, x, z, cursor) == Integer.MIN_VALUE) {
                return "the door's own column " + x + ", " + z + " is not ground";
            }
        }
        return "the ground under it could not be measured";
    }

    /**
     * Whether every chunk the footprint covers is loaded, asked at the two opposite corners: a footprint
     * is a rectangle, so the pair that is furthest apart is the furthest anything can be.
     */
    /** The same question asked of the footprint's own corners, which is what a build laid from its corner needs. */
    private static boolean chunksLoaded(ServerLevel level, BlockPos corner, int wide, int deep) {
        return level.hasChunkAt(corner) && level.hasChunkAt(corner.offset(wide, 0, deep));
    }

    private static boolean chunksLoaded(ServerLevel level, BlockPos centre, Vec3i size) {
        int halfW = size.getX() / 2 + 1;
        int halfD = size.getZ() / 2 + 1;
        return level.hasChunkAt(centre.offset(-halfW, 0, -halfD))
                && level.hasChunkAt(centre.offset(halfW, 0, halfD));
    }

    /**
     * Puts ground back under the building, the way a village does: a column the floor has been lifted
     * clear of is filled from the building's own base down to the ground it was lifted off, so a tent on
     * a bank stands on earth rather than on stilts with the water showing under one wall.
     * <p>
     * Air and water are both filled, because a building beside a shore is standing in the water it was
     * built beside, and a wall with a hole under it is worse than a wall with a bank under it. The
     * filling stops at the first solid block, so nothing is put where ground already is, and it gives up
     * after the layers it was given rather than hanging a plinth a long way down off a building that is
     * on a cliff.
     */
    public static void underpin(ServerLevel level, BlockPos corner, int wide, int deep, int layers) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = 0; dx < wide; dx++) {
            for (int dz = 0; dz < deep; dz++) {
                for (int dy = 1; dy <= layers; dy++) {
                    cursor.set(corner.getX() + dx, corner.getY() - dy, corner.getZ() + dz);
                    BlockState state = level.getBlockState(cursor);
                    if (!state.isAir() && state.getFluidState().isEmpty()) break;
                    level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    /** How far out from a build's walls the ground is carried to its own roofline, in blocks. */
    private static final int SWEEP_REACH = 12;
    /** A build with more footprint than this is swept only at its walls, so one placement stays one placement. */
    private static final int SWEEP_WIDE = 2048;
    /** Which builds this session has already had their ground swept, so a standing one is swept once a visit. */
    private static final Set<String> SWEPT = new HashSet<>();

    /**
     * Carries the ground around a build to the build's own roofline, and takes what grows over it.
     * <p>
     * The city has been dressed this way since it was first laid - column by column to the cut's own outline, easing
     * back to the country at the edge of the ring - and a building laid from a template had nothing of the kind. So a
     * camp the world seated in a hillside arrived with the hill leaning over its roof, a tower with a wood's crown
     * through its hall, and a footprint cut out of a bank with a wall of ground still standing against two of its
     * sides. The ground was never asked what it was covering. This is the same pass for one build: every column
     * inside the footprint is cleared above the build's own top, and the ring outside it is cut down to that top,
     * easing back to the ground at {@link #SWEEP_REACH} so the join out in the country is the country's own.
     * <p>
     * Water keeps its channel - a bank is not filled to a roofline - and a crag is left exactly as it is, a crag's
     * whole shape being the ground over it.
     */
    public static void sweep(ServerLevel level, BlockPos corner, Vec3i size, String structure) {
        if (isCave(structure)) return;
        int wide = size.getX();
        int deep = size.getZ();
        if (wide <= 0 || deep <= 0) return;
        int[] top = roofline(level, structure, size);
        if (top == null) return;
        open(top, wide, deep);
        int ring = wide * deep > SWEEP_WIDE ? 4 : SWEEP_REACH;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -ring; dx < wide + ring; dx++) {
            for (int dz = -ring; dz < deep + ring; dz++) {
                int out = Math.max(0, Math.max(Math.max(-dx, dx - wide + 1), Math.max(-dz, dz - deep + 1)));
                if (out > ring) continue;
                int nearX = Math.max(0, Math.min(wide - 1, dx));
                int nearZ = Math.max(0, Math.min(deep - 1, dz));
                int roof = top[nearX + nearZ * wide];
                if (roof == Integer.MIN_VALUE) continue;
                int x = corner.getX() + dx;
                int z = corner.getZ() + dz;
                int surface = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) - 1;
                if (surface <= level.getMinY()) continue;
                if (wet(level, x, surface + 1, z)) continue;
                double share = Math.min(1.0D, out / (double) ring);
                double ease = share * share * (3.0D - 2.0D * share);
                int cap = (int) Math.round(corner.getY() + roof + (surface - corner.getY() - roof) * ease);
                sweepColumn(level, cursor, x, z, surface, cap, out == 0);
            }
        }
    }

    /**
     * The build's own top in every column of its footprint, from its corner; {@code Integer.MIN_VALUE} where the
     * template stands nothing at all.
     * <p>
     * Read from the template's file rather than from the world, and read after no placement at all: the ground is
     * what the build has to be carried to, so the number wanted is the one the template drew, not what happens to be
     * standing now. A hill left over a roof inside the footprint would otherwise be its own answer.
     */
    private static int[] roofline(ServerLevel level, String structure, Vec3i size) {
        Identifier id = Identifier.tryParse(structure);
        if (id == null) return null;
        Identifier file = Identifier.fromNamespaceAndPath(id.getNamespace(), "structure/" + id.getPath() + ".nbt");
        CompoundTag tag;
        try (InputStream stream = level.getServer().getResourceManager().getResourceOrThrow(file).open()) {
            tag = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
        } catch (IOException | RuntimeException unreadable) {
            LOGGER.warn("Could not read {} to sweep the ground over it", file, unreadable);
            return null;
        }
        ListTag palette = tag.getList("palette").orElseGet(ListTag::new);
        if (palette.isEmpty()) return null;
        String[] names = new String[palette.size()];
        for (int index = 0; index < palette.size(); index++) {
            names[index] = palette.getCompoundOrEmpty(index).getStringOr("Name", "");
        }
        int wide = size.getX();
        int deep = size.getZ();
        int[] top = new int[wide * deep];
        Arrays.fill(top, Integer.MIN_VALUE);
        ListTag blocks = tag.getList("blocks").orElseGet(ListTag::new);
        for (int index = 0; index < blocks.size(); index++) {
            CompoundTag block = blocks.getCompoundOrEmpty(index);
            int state = block.getIntOr("state", -1);
            if (state < 0 || state >= names.length) continue;
            String name = names[state];
            if (name.endsWith(":air") || name.endsWith("cave_air") || name.endsWith("void_air")) continue;
            ListTag pos = block.getList("pos").orElseGet(ListTag::new);
            if (pos.size() < 3) continue;
            int x = pos.getIntOr(0, 0);
            int z = pos.getIntOr(2, 0);
            if (x < 0 || x >= wide || z < 0 || z >= deep) continue;
            top[x + z * wide] = Math.max(top[x + z * wide], pos.getIntOr(1, 0));
        }
        return top;
    }

    /** Fills the build's own holes from the columns beside them, so a doorway is still a wall for the ground's sake. */
    private static void open(int[] top, int wide, int deep) {
        for (int pass = 0; pass < 2; pass++) {
            int[] next = top.clone();
            for (int z = 0; z < deep; z++) {
                for (int x = 0; x < wide; x++) {
                    int at = x + z * wide;
                    if (top[at] != Integer.MIN_VALUE) continue;
                    int best = Integer.MIN_VALUE;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            int nx = x + dx;
                            int nz = z + dz;
                            if (nx < 0 || nx >= wide || nz < 0 || nz >= deep) continue;
                            best = Math.max(best, top[nx + nz * wide]);
                        }
                    }
                    next[at] = best;
                }
            }
            System.arraycopy(next, 0, top, 0, top.length);
        }
    }

    /**
     * One column of the sweep: everything standing above the cap goes, and the ring gets the ground back.
     * <p>
     * Inside the footprint nothing is put back, because what was cut there stood over the build and the build is
     * what belongs at the cap. Out on the ring the column is capped with the ground it had, or with grass where a
     * hill had grass - a crown of leaves laid flat at a roofline is not a shoulder, it is a lid.
     */
    private static void sweepColumn(ServerLevel level, BlockPos.MutableBlockPos cursor,
                                    int x, int z, int surface, int cap, boolean inside) {
        if (surface <= cap) return;
        BlockState skin = level.getBlockState(cursor.set(x, surface, z));
        boolean keep = skin.isSolid() && !skin.is(BlockTags.LOGS) && !skin.is(BlockTags.LEAVES)
                && skin.getFluidState().isEmpty();
        for (int y = surface; y > cap; y--) {
            cursor.set(x, y, z);
            if (level.getBlockState(cursor).isAir()) continue;
            level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        if (inside) return;
        if (keep) {
            level.setBlock(cursor.set(x, cap, z), skin, Block.UPDATE_CLIENTS);
            return;
        }
        cursor.set(x, cap - 1, z);
        BlockState below = level.getBlockState(cursor);
        if (!below.isSolid()) return;
        boolean grassy = below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.DIRT) || below.is(Blocks.PODZOL)
                || below.is(Blocks.MYCELIUM) || below.is(Blocks.COARSE_DIRT);
        level.setBlock(cursor.set(x, cap, z),
                grassy ? Blocks.GRASS_BLOCK.defaultBlockState() : below, Block.UPDATE_CLIENTS);
    }

    /**
     * How far the shoulder is walked out from the walls, in blocks, before the ground is left to
     * itself. A ramp meets the hillside on its own long before this; the number is the guard against a
     * build on a mountainside spending a whole session on its own shoulder.
     */
    private static final int APRON_REACH = 128;

    /**
     * How steeply a shoulder may climb or fall, in blocks of height per block walked out from the
     * wall. About twenty-one degrees: a hillside a player will walk, where six blocks in one ring was
     * a wall with a step cut into its foot.
     */
    private static final double APRON_SLOPE = 0.4D;

    /** What one column of the shoulder came to, so a blend can be counted rather than only claimed. */
    private static final int UNTOUCHED = 0;
    private static final int CUT = 1;
    private static final int FILLED = 2;

    /**
     * What a shoulder came to, in columns: the ones cut down to the ramp's line, the ones built up to
     * it, and the ones that already stood on it. A city whose shoulder moved nothing at all is a city
     * that will read as a block cut out of the ground, so this is the number that says whether the
     * ground was blended or merely touched.
     */
    public record Apron(int cut, int filled, int left) {
    }

    /**
     * Blends the ground around a footprint into the level the build's own rim stands at, which a
     * template cannot do for itself: `beard_thin` and `beard_box` are worldgen jigsaw settings, read
     * while chunks generate, and never run on a structure laid at runtime. So the shoulder is built
     * here instead, as a ramp out from the walls.
     * <p>
     * It starts from {@link #rim} - the highest dry ground on the ring just outside the footprint -
     * rather than from the build's lowest corner. That corner is a number about the cut, not about the
     * country: on a hillside it is the downhill side's, and blending from it digs the uphill side away
     * into a canyon with the build in the bottom of it. Measured from the rim, the shoulder meets the
     * ground where the wall meets it, and the drop is spent at {@link #APRON_SLOPE} per block walked
     * out. The trade is the other side of the same coin: on ground that falls away, the low side is
     * filled up to the highest place the wall meets ground, so an uneven site terraces rather than
     * shelves. The ramp stops where it has met the ground it was walking toward, or at
     * {@link #APRON_REACH}, and water is left exactly as it was.
     */
    public static Apron apron(ServerLevel level, BlockPos corner, int wide, int deep, int base) {
        int[] counts = new int[3];
        int datum = rim(level, corner, wide, deep, base);
        int rings = reach(level, corner, wide, deep, datum);
        for (int ring = 1; ring <= rings; ring++) {
            for (int dx = -ring; dx <= wide + ring; dx++) {
                counts[side(level, corner, dx, -ring, datum, ring)]++;
                counts[side(level, corner, dx, deep + ring, datum, ring)]++;
            }
            for (int dz = -ring + 1; dz < deep + ring; dz++) {
                counts[side(level, corner, -ring, dz, datum, ring)]++;
                counts[side(level, corner, wide + ring, dz, datum, ring)]++;
            }
        }
        return new Apron(counts[CUT], counts[FILLED], counts[UNTOUCHED]);
    }

    /**
     * The level the shoulder starts from: the highest column of the ring just outside the walls that
     * is dry ground, or the build's own base when the whole ring is water.
     * <p>
     * Water is skipped on purpose. A build at the water's edge - a city with a river leaving it - has a
     * rim that is half channel, and the surface of a river is not the level a hillside is blended to.
     * A ring with nothing dry on it answers with the number the caller passed, so a site in a lake
     * behaves exactly as it did before there was a rim to read at all.
     */
    public static int rim(ServerLevel level, BlockPos corner, int wide, int deep, int fallback) {
        int highest = Integer.MIN_VALUE;
        for (int dx = -1; dx <= wide; dx++) {
            highest = Math.max(highest, dry(level, corner.getX() + dx, corner.getZ() - 1));
            highest = Math.max(highest, dry(level, corner.getX() + dx, corner.getZ() + deep));
        }
        for (int dz = 0; dz < deep; dz++) {
            highest = Math.max(highest, dry(level, corner.getX() - 1, corner.getZ() + dz));
            highest = Math.max(highest, dry(level, corner.getX() + wide, corner.getZ() + dz));
        }
        return highest == Integer.MIN_VALUE ? fallback : highest;
    }

    /** The top of a column when it is dry ground, or nothing when it is water or has no ground. */
    private static int dry(ServerLevel level, int x, int z) {
        int ground = height(level, x, z);
        if (ground <= level.getMinY()) return Integer.MIN_VALUE;
        return wet(level, x, ground, z) ? Integer.MIN_VALUE : ground;
    }

    /** Whether the block a column's height stands over is water, which the shoulder leaves alone. */
    private static boolean wet(ServerLevel level, int x, int ground, int z) {
        return !level.getBlockState(new BlockPos(x, ground - 1, z)).getFluidState().isEmpty();
    }

    /**
     * How many rings the ramp needs: the worst difference between the rim's level and the ground right
     * against the walls, divided by the slope a ring may spend, and two rings more so the ramp has met
     * the hillside rather than stopping a block short of it.
     */
    private static int reach(ServerLevel level, BlockPos corner, int wide, int deep, int datum) {
        int worst = 0;
        for (int dx = 0; dx <= wide; dx++) {
            worst = Math.max(worst, Math.abs(height(level, corner.getX() + dx, corner.getZ() - 1) - datum));
            worst = Math.max(worst, Math.abs(height(level, corner.getX() + dx, corner.getZ() + deep) - datum));
        }
        for (int dz = 1; dz < deep; dz++) {
            worst = Math.max(worst, Math.abs(height(level, corner.getX() - 1, corner.getZ() + dz) - datum));
            worst = Math.max(worst, Math.abs(height(level, corner.getX() + wide, corner.getZ() + dz) - datum));
        }
        int needed = (int) Math.ceil(worst / APRON_SLOPE) + 2;
        return Math.min(APRON_REACH, Math.max(needed, 4));
    }

    /**
     * A column's height, with its chunk opened first: a heightmap is only filled for a chunk the world
     * has opened, and the shoulder reads rings up to {@link #APRON_REACH} past the walls while the water
     * line reads {@link #WATER_REACH}. From the middle of a city this size that is well outside the
     * chunks a player keeps loaded, and ground out there reads as nothing - a blend written against a
     * datum that is not there. Laying a piece already opens its chunks the same way.
     */
    private static int height(ServerLevel level, int x, int z) {
        level.getChunk(x >> 4, z >> 4);
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    /** One column of one ring, brought to where the ramp has got to by that ring. */
    private static int side(ServerLevel level, BlockPos corner, int dx, int dz, int datum, int distance) {
        int x = corner.getX() + dx;
        int z = corner.getZ() + dz;
        int ground = height(level, x, z);
        // Water keeps its channel: the shoulder is ground, and filling a river to the hillside would
        // put a shelf across water that was drawn to run out of the build.
        if (wet(level, x, ground, z)) return UNTOUCHED;
        int target = ramp(ground, datum, distance);
        if (target == ground) return UNTOUCHED;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        // What the column was paved with, so a shelf cut into a grass hill keeps its grass instead of
        // showing the rock the blade went through.
        cursor.set(x, ground - 1, z);
        BlockState surface = level.getBlockState(cursor);
        boolean grassy = surface.is(Blocks.GRASS_BLOCK) || surface.is(Blocks.DIRT)
                || surface.is(Blocks.PODZOL) || surface.is(Blocks.MYCELIUM)
                || surface.is(Blocks.COARSE_DIRT);
        if (target < ground) {
            for (int y = ground - 1; y >= target; y--) {
                cursor.set(x, y, z);
                if (level.getBlockState(cursor).isAir()) continue;
                level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            cap(level, cursor, x, target, z, grassy, false);
            return CUT;
        }
        for (int y = ground; y < target; y++) {
            cursor.set(x, y, z);
            if (!level.getBlockState(cursor).isAir()) continue;
            level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        cap(level, cursor, x, target, z, grassy, false);
        return FILLED;
    }

    /** Puts the grass back on the top of a column the shoulder moved, where the hill had grass. */
    private static void cap(ServerLevel level, BlockPos.MutableBlockPos cursor, int x, int target, int z,
                            boolean grassy, boolean wet) {
        if (!grassy || wet) return;
        cursor.set(x, target - 1, z);
        BlockState top = level.getBlockState(cursor);
        if (top.isAir() || !top.getFluidState().isEmpty()) return;
        level.setBlock(cursor, Blocks.GRASS_BLOCK.defaultBlockState(), Block.UPDATE_CLIENTS);
    }

    /**
     * How far a water line is followed out from the wall, in blocks. A bank has to reach the ground the
     * water is going to land on, and the spring at the city's back stands 124 blocks over the shelf below
     * it, so this is longer than {@link #APRON_REACH}.
     */
    private static final int WATER_REACH = 160;

    /**
     * How fast a bank's bed falls, in blocks per block walked out. One in one is steep enough to be a
     * spillway rather than a hillside, and short enough that a hundred and twenty-four blocks of drop fit
     * inside {@link #WATER_REACH}.
     */
    private static final double WATER_FALL = 1.0D;

    /**
     * How far the water has to stand over the ground before a bank is built under it. The city's mouth
     * hangs eight blocks over the vale, which the branch falls on its own; a wall of dirt under that would
     * be a dam across the drawn river, not a way out.
     */
    private static final int WATER_HANG = 12;

    /**
     * What a water line came to: the outlets found on the walls, and the columns built out into a bank
     * under the ones that stood high enough to need it.
     */
    public record Spillway(int open, int filled) {
    }

    /**
     * Gives the build's own water somewhere to land, which the shoulder cannot.
     * <p>
     * The apron reads dry columns only - {@link #rim} drops a wet one from the datum and {@link #side}
     * leaves it exactly as it was - so where water leaves a wall the shoulder stops at the water's edge
     * and the ground outside keeps whatever height it had. At the city's mouth that is enough: the branch
     * falls eight blocks into a vale that is already at 74. At the spring it is not: its water leaves the
     * east wall at 239 over a shelf at 115, and with no ground under it the water just hangs there.
     * <p>
     * So this reads the water instead of the ground. Every column of the footprint's own edge that is
     * water is an outlet, and where that water stands more than {@link #WATER_HANG} blocks over the first
     * dry ground outside it, the ground is built up into a bank under it - a bed starting one block below
     * the water and falling at {@link #WATER_FALL} until it meets the ground, or at {@link #WATER_REACH}.
     * Nothing is cut and no wet column is touched: the shoulder cuts, and a spillway is a bank for water
     * that is already there.
     */
    public static Spillway waterline(ServerLevel level, BlockPos corner, int wide, int deep) {
        int[] filled = new int[1];
        int open = 0;
        for (int dx = 0; dx < wide; dx++) {
            if (spill(level, corner, dx, 0, 0, -1, filled)) open++;
            if (spill(level, corner, dx, deep - 1, 0, 1, filled)) open++;
        }
        for (int dz = 0; dz < deep; dz++) {
            if (spill(level, corner, 0, dz, -1, 0, filled)) open++;
            if (spill(level, corner, wide - 1, dz, 1, 0, filled)) open++;
        }
        return new Spillway(open, filled[0]);
    }

    /**
     * Builds one outlet's bank, and answers whether that column of the wall carried water at all.
     * <p>
     * The bed is measured from the first dry column past the water, so a channel that already runs some
     * way out of the build is not counted as fall: the bank starts where the water meets ground, one block
     * under the water, and comes down one block per block out until it reaches the ground.
     */
    private static boolean spill(ServerLevel level, BlockPos corner, int dx, int dz,
                                 int stepX, int stepZ, int[] filled) {
        int x = corner.getX() + dx;
        int z = corner.getZ() + dz;
        int top = height(level, x, z);
        if (!wet(level, x, top, z)) return false;
        int first = -1;
        int ground = 0;
        for (int out = 1; out <= WATER_REACH; out++) {
            int at = height(level, x + stepX * out, z + stepZ * out);
            if (wet(level, x + stepX * out, at, z + stepZ * out)) continue;
            first = out;
            ground = at;
            break;
        }
        // The water already has somewhere to go: at grade it runs away by itself, and a fall of a few
        // blocks is the river's own business rather than a wall to build a bank against.
        if (first < 0 || top - 1 - ground <= WATER_HANG) return true;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int out = first; out <= WATER_REACH; out++) {
            int ox = x + stepX * out;
            int oz = z + stepZ * out;
            int at = height(level, ox, oz);
            if (wet(level, ox, at, oz)) continue;
            int line = top - 1 - (int) Math.floor((out - first) * WATER_FALL);
            if (line <= at) break;
            for (int y = at; y < line; y++) {
                cursor.set(ox, y, oz);
                if (!level.getBlockState(cursor).isAir()) continue;
                level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            filled[0]++;
        }
        return true;
    }

    /**
     * Where the ramp has got to at a given distance from the wall: the build's own level, lifted or
     * dropped toward the ground no faster than {@link #APRON_SLOPE} allows, and the ground itself once
     * the ramp has reached it.
     */
    private static int ramp(int ground, int base, int distance) {
        double allowed = APRON_SLOPE * distance;
        if (ground > base + allowed) return (int) Math.floor(base + allowed);
        if (ground < base - allowed) return (int) Math.ceil(base - allowed);
        return ground;
    }

    /**
     * How far the walls of a site's building stand from its middle.
     * <p>
     * A body kept at a building is stood once the player is inside it, and the inside of a hall a
     * hundred and twenty blocks across is a long way from the numbers in its middle: measured to the
     * middle with a reach meant for a camp, nobody keeping the hold was ever stood at all.
     */
    public static double span(Level level, Site site) {
        if (!(level instanceof ServerLevel server)) return 0.0D;
        Vec3i size = footprint(server, site.structure());
        if (size == null) return 0.0D;
        return Math.sqrt((double) size.getX() * size.getX() + (double) size.getZ() * size.getZ()) / 2.0D;
    }

    /**
     * How close to the middle of a building counts as standing in it, which grows with the building.
     * <p>
     * It did not grow: a flat twelve blocks was measured against the middle of the footprint, which for
     * any building wider than a tent is further from its own walls than twelve blocks. A player who had
     * walked into the ruin they were sent to stood under its stone with the page still open, because the
     * middle being measured to was half a footprint away over their head. The template has the size the
     * rest of this class already judges ground by, so the reach is half of it plus a little: standing
     * anywhere inside the walls is standing in the place.
     */
    private static double foundReach(Level level, Site site) {
        double half = 0.0D;
        if (level instanceof ServerLevel server) {
            Vec3i size = footprint(server, site.structure());
            if (size != null) half = Math.max(size.getX(), size.getZ()) / 2.0D;
        }
        return Math.max(12.0D, half + 4.0D);
    }

    /**
     * Puts somebody at the fire. He is an ordinary body of his folk with a name and a tag, not a giver:
     * a camp is a place a player is sent to, and the man minding it is what makes it one rather than
     * what makes it another bench to take work from.
     */
    private static void standKeeper(ServerLevel level, BlockPos corner, Vec3i size, Seat seat, String structure) {
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob keeper)) return;
        // The building's own door decides where the man stands: two blocks outside it, on the ground the
        // road arrives on, rather than on the far side of the walls - which was the answer for a fair-weather
        // tent and put the keeper of Elladan's camp behind it.
        BlockPos marked = marker(level, structure + KEEPER_PIN);
        BlockPos stand = marked != null ? room(level, marked)
                : level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, outside(corner, size, seat));
        // A third marker in a row is an NPC's spot, so the ammolite it was written with is not left at the man's
        // feet: the body is what belongs there.
        if (level.getBlockState(stand).is(TolkienBlocks.BLOCK_AMMOLITE.get())) level.removeBlock(stand, false);
        // One keeper per fire, whichever player raised the camp. A site belongs to the player who took
        // the page, but the ground it names is the same for everybody, so two players arriving at the
        // same camp both run the build - and without this each of them would put a second man at it.
        if (!level.getEntitiesOfClass(Mob.class, new AABB(stand).inflate(KEEPER_REACH),
                found -> found.entityTags().contains(KEEPER_TAG)).isEmpty()) {
            return;
        }
        keeper.snapTo(stand.getX() + 0.5D, stand.getY(), stand.getZ() + 0.5D,
                level.getRandom().nextFloat() * 360.0F, 0.0F);
        keeper.setPersistenceRequired();
        keeper.addTag(KEEPER_TAG);
        String named = KEEPER_NAMES.get(structure);
        keeper.setCustomName(named != null ? Component.literal(named) : QuestPool.giverName(level.getRandom()));
        keeper.setCustomNameVisible(true);
        level.addFreshEntity(keeper);
    }

    /**
     * The lowest place near a written spot where a body can actually be stood: air at its feet and at its head, and
     * ground under them both.
     * <p>
     * A pin's y is the surface the scout stood on rather than a measurement of the ground, and a raised camp is
     * brought to its own floor - Elladan's camp sits four blocks above the ground outside its door - so a keeper put
     * on the pin's own layer stands inside the fill and suffocates before anybody walks up to him. The column is
     * walked upward instead: inside the door that is the camp's floor, and on open ground it is where the marker
     * already was.
     */
    private static BlockPos room(ServerLevel level, BlockPos at) {
        for (int up = 0; up <= BODY_LIFT; up++) {
            BlockPos feet = at.above(up);
            if (!level.hasChunkAt(feet)) return at;
            BlockState floor = level.getBlockState(feet.below());
            if (floor.isAir() || !floor.getFluidState().isEmpty()) continue;
            if (!level.getBlockState(feet).canBeReplaced()) continue;
            if (!level.getBlockState(feet.above()).canBeReplaced()) continue;
            return feet;
        }
        return at;
    }

    /**
     * The ground just outside a building's way in: two blocks out from the doorway's own column along the
     * direction its gap opens. A cut with no door in the table keeps the older answer, which is past the far
     * edge of its footprint.
     */
    private static BlockPos outside(BlockPos corner, Vec3i size, Seat seat) {
        if (!seat.doorway()) return corner.offset(size.getX() / 2, 0, size.getZ() + 2);
        return corner.offset(seat.doorX() + seat.front().getStepX() * 2, 0,
                seat.doorZ() + seat.front().getStepZ() * 2);
    }

    /**
     * Where the stone that travels with a building stands: two blocks out from the door and one block to the
     * side, so every arrival at a place finds its stone in the same spot - beside the way in, off the walk, and
     * not in the man standing at the door.
     */
    private static BlockPos besideDoor(BlockPos corner, Vec3i size, Seat seat) {
        if (!seat.doorway()) return outside(corner, size, seat).offset(1, 0, 0);
        Direction side = seat.front().getClockWise();
        return corner.offset(seat.doorX() + seat.front().getStepX() * 2 + side.getStepX(), 0,
                seat.doorZ() + seat.front().getStepZ() * 2 + side.getStepZ());
    }

    /**
     * Which way a building's door opens, as its own cut has it, or null for a cut that names no door.
     * <p>
     * This is the facing a build was missing: the template already says where the way in is, and the way in is
     * the front, so the direction is derived from the entrance rather than supplied as a compass word beside it.
     */
    @Nullable
    public static Direction front(String structure) {
        Seat seat = DOORWAYS.get(structure);
        return seat == null || !seat.doorway() ? null : seat.front();
    }

    /** The pin a building's man or animal is written under once a scout has marked where it goes. */
    private static final String KEEPER_PIN = ".keeper";
    private static final String BODY_PIN = ".body";

    /**
     * The spot a role was pinned at, consuming a scout's marker if one is still standing there: a block of
     * ammolite is how a location is marked on the ground, and the thing that belongs there replaces it rather
     * than standing beside it. Null when nothing is pinned, which leaves the older answer - the ground outside
     * the door - to speak.
     */
    @Nullable
    private static BlockPos marker(ServerLevel level, String key) {
        BlockPos at = Pins.at(level, key);
        if (at == null || !level.hasChunkAt(at)) return null;
        if (level.getBlockState(at).is(TolkienBlocks.BLOCK_AMMOLITE.get())) level.removeBlock(at, false);
        return at;
    }

    /** What the world leaves at a building of its own, which a page's own site is free to ask for itself. */
    private static Body bodyFor(String structure) {
        for (Scenery raised : SCENERY) {
            if (raised.structure().equals(structure)) return raised.body();
        }
        return Body.NONE;
    }

    /**
     * Leaves the animal the rider came off at the ruin: a black steed with no rider, on the ground before the
     * way in, never minding anything. It is tagged so a second visit does not leave a second one, and it is
     * invulnerable and silent so a player finds it rather than fights it.
     */
    private static void standSteed(ServerLevel level, BlockPos corner, Vec3i size, Seat seat, String structure) {
        BlockPos spot = marker(level, structure + BODY_PIN);
        if (spot == null) {
            spot = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, outside(corner, size, seat));
        }
        if (!level.getEntitiesOfClass(Mob.class, new AABB(spot).inflate(KEEPER_REACH),
                found -> found.entityTags().contains(BODY_TAG)).isEmpty()) {
            return;
        }
        Entity entity = TolkienEntities.ENTITY_TTM_NAZGULSTEED.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof NazgulSteedEntity steed)) return;
        steed.snapTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, 0.0F, 0.0F);
        // The one part of it the world cannot see from where the blocks went: the animal is down, not standing.
        steed.setFallen(true);
        steed.setPersistenceRequired();
        steed.setNoAi(true);
        steed.setSilent(true);
        steed.setInvulnerable(true);
        steed.addTag(BODY_TAG);
        level.addFreshEntity(steed);
    }

    /**
     * Stands the stone that travels with a building beside its door.
     * <p>
     * The stone used to be planted by the page that sent a player to the place, which is what put a second one
     * at a camp whenever a second page named it, at whatever spot that page's ground happened to be written at.
     * Ground that carries its own stone cannot be stoned twice.
     */
    private static void stoneAt(ServerLevel level, BlockPos corner, Vec3i size, Seat seat, String structure,
                                ServerPlayer player) {
        String name = STONE_NAMES.get(structure);
        if (name == null) return;
        BlockPos spot = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                besideDoor(corner, size, seat));
        Stones.stone(level, spot, name, player);
    }

    /** Whether one of the mod's folk is standing within sight of the player, which a camp always has. */
    private static boolean keeperNearby(ServerPlayer player, Site site) {
        return !keeperName(player).getString().isEmpty();
    }

    /** The keeper's name, if one is near enough to be seen. */
    private static Component keeperName(ServerPlayer player) {
        var found = player.level().getEntities(player, player.getBoundingBox().inflate(BUILD_REACH),
                entity -> entity.entityTags().contains(KEEPER_TAG));
        return found.isEmpty() ? Component.empty() : found.getFirst().getName();
    }

    /** How far a player is from a block, across the ground. */
    private static double away(ServerPlayer player, BlockPos at) {
        double dx = player.getX() - (at.getX() + 0.5D);
        double dz = player.getZ() - (at.getZ() + 0.5D);
        return Math.sqrt(dx * dx + dz * dz);
    }

    /**
     * How far the player is from the middle of the building, across the ground. The middle is where the
     * fire is in a camp and where the floor of an arena is in an arena - a player who has walked up to
     * the open end of a tent is at the tent, and should not have to reach its far corner.
     */
    private static double away(ServerPlayer player, Site site) {
        double dx = player.getX() - (site.centre().getX() + 0.5D);
        double dz = player.getZ() - (site.centre().getZ() + 0.5D);
        return Math.sqrt(dx * dx + dz * dz);
    }

    // ---- plumbing ----

    private static Sites instance(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getServer() != null) {
            ServerLevel overworld = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if (overworld != null) return overworld.getDataStorage().computeIfAbsent(TYPE);
        }
        return NO_SERVER;
    }

    private static Sites from(List<Site> sites) {
        SITES.clear();
        for (Site site : sites) {
            SITES.put(site.player(), site);
        }
        // Read without clearing: the map was just filled from the file.
        return new Sites(false);
    }

    private List<Site> toList() {
        return new ArrayList<>(SITES.values());
    }
}
