package com.greatorator.tolkienmobs.faction;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

    /** The tent the camp errand pitches, as a template the mod already ships. */
    public static final String WILD_TENT = "tolkienmobs:key_structures/wild_tent";
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
    public static final String WOOD_RUIN = "tolkienmobs:ttmruin_large_2";
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
    private static final Map<String, ArdaMap.Country> CANON = Map.of(
            WEATHERTOP, ArdaMap.Country.ERIADOR,
            BOSS_CASTLE, ArdaMap.Country.ANGMAR,
            BOSS_ARENA, ArdaMap.Country.ANGMAR,
            WOOD_RUIN, ArdaMap.Country.TROLLSHAWS,
            // The camp the errands pitch and the three crags the stone asks in are the campaign's own
            // buildings, and a building that is pitched where the page was taken is a building two
            // players on one server get two of. They stand in the hills with the rest of the chapter,
            // on ground written down once.
            WILD_TENT, ArdaMap.Country.TROLLSHAWS,
            TROLL_CAVE, ArdaMap.Country.TROLLSHAWS,
            TROLL_CAVE_2, ArdaMap.Country.TROLLSHAWS,
            TROLL_CAVE_3, ArdaMap.Country.TROLLSHAWS,
            // The camp is at the east end of the High Pass: the ground the pass drops into on the Anduin
            // side, which the drawing names Gladden, rather than the mountains it cuts through. That is where
            // the old world built it and where the notes send the player, north of the valley.
            GLOIN_CAMP, ArdaMap.Country.GLADDEN,
            // Gundabad is in the Hithaeglir - the Misty Mountains themselves - which is where the
            // notes put it and where the dwarves would have cut it. No cell is pinned for it, because
            // a building 122 by 183 can only stand where the ground is level enough to take it, and the
            // search has to be free to walk out from the country's middle to find that ground.
            GUNDABAD, ArdaMap.Country.HITHAEGLIR);
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
     * on. Seated by its grass line instead: nine blocks in puts the grass at the ground it is placed
     * on, and ten is as deep as it is ever set, which is the one-block error the shape of the ground
     * under a footprint this size is allowed to leave.
     */
    private static final Seat SUNK_CRAG = new Seat(9, 10);
    /** A building that stands on its ground rather than in it: hall, tent, house, arena. */
    private static final Seat ON_SURFACE = new Seat(0, SEAT_SINK);

    /** The tag the body minding a camp wears, so it can be told from any other of its folk. */
    public static final String KEEPER_TAG = "tolkienmobs:camp_keeper";
    /** How close to a camp's fire another keeper counts as already minding it, in blocks. */
    private static final int KEEPER_REACH = 32;

    private static final Map<UUID, Site> SITES = new HashMap<>();
    /** Players whose site has already been complained about, so a refusal is said once, not every tick. */
    private static final Set<UUID> UNBUILDABLE = new HashSet<>();
    private static final Sites NO_SERVER = new Sites(false);

    /**
     * One player's site: what is being built, where it was chosen, and how far along it is. The centre
     * is the middle of the building once it is standing, and the spot that was written down before
     * that, so a distance to it always means the same thing.
     */
    public record Site(UUID player, ResourceKey<Level> dimension, BlockPos centre, String structure,
                       boolean keeper, boolean built, boolean found, String country) {
        static final Codec<Site> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                UUIDUtil.CODEC.fieldOf("player").forGetter(Site::player),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("dimension").forGetter(Site::dimension),
                BlockPos.CODEC.fieldOf("centre").forGetter(Site::centre),
                Codec.STRING.fieldOf("structure").forGetter(Site::structure),
                Codec.BOOL.optionalFieldOf("keeper", false).forGetter(Site::keeper),
                Codec.BOOL.optionalFieldOf("built", false).forGetter(Site::built),
                Codec.BOOL.optionalFieldOf("found", false).forGetter(Site::found),
                Codec.STRING.optionalFieldOf("country", "").forGetter(Site::country)
        ).apply(instance, Site::new));

        Site builtAt(BlockPos middle) {
            return new Site(player, dimension, middle, structure, keeper, true, found, country);
        }

        /** The same site, noted as walked into. Named apart from the accessor it shadows. */
        Site markedFound() {
            return new Site(player, dimension, centre, structure, keeper, built, true, country);
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
        if (fresh) SITES.clear();
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
                    structure, keeper, true, false, standing.country()));
            store.setDirty();
            return true;
        }
        // The drawing answers first. A structure's own biome list decides the country it may stand
        // in - the tent's wilds are already in the data - so nothing is written down here about where
        // anything belongs, beyond the handful of landmarks the books put somewhere specific.
        ArdaSurvey.Ground surveyed = survey(level, player, structure);
        if (surveyed != null) {
            SITES.put(player.getUUID(), new Site(player.getUUID(), level.dimension(),
                    surveyed.centre(), structure, keeper, false, false, surveyed.country().name()));
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
                keeper, false, false, ""));
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
     * Looks at where the player is, builds their site once they are close enough for the chunk to be
     * loaded, and tells them when they have walked into it. Called from the same clock that names the
     * ground they are walking over.
     */
    public static void tick(ServerPlayer player) {
        Site site = siteOf(player);
        if (site == null) return;
        if (!player.level().dimension().equals(site.dimension())) return;
        double away = away(player, site);
        if (!site.built()) {
            if (away <= BUILD_REACH) build(player, site);
            return;
        }
        if (site.found() || away > foundReach(player.level(), site)) return;
        if (!(player.level() instanceof ServerLevel level)) return;
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
    private static void build(ServerPlayer player, Site site) {
        if (!(player.level() instanceof ServerLevel level)) return;
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
        if (!chunksLoaded(level, site.centre(), size)) return;
        Plane found = locate(level, site.centre(), size.getX(), size.getZ(), seatFor(site.structure()));
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
        if (site.keeper()) standKeeper(level, corner, size);
        SITES.put(player.getUUID(), site.builtAt(corner.offset(size.getX() / 2, 0, size.getZ() / 2)));
        instance(level).setDirty();
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
     * How deep a building is set into its ground: never less than {@code floor}, and never more than
     * {@code cap}. A house asks for no floor and takes half the drop under it; the cave asks to be
     * buried by its grass line rather than seated on its base.
     */
    private record Seat(int floor, int cap) {
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
        return isCave(structure) ? SUNK_CRAG : ON_SURFACE;
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
        // Half the drop under the footprint, so the plinth it would have stood on is halved and the
        // ground rises around its base. Bounded, because a castle on a hillside still has to keep its
        // door above the ground it opens onto, and flat ground has nothing to sink into at all.
        int sink = Math.min(seat.cap(), Math.max(seat.floor(), (high - low) / 2));
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
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
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
     * Whether every chunk the footprint covers is loaded, asked at the two opposite corners: a footprint
     * is a rectangle, so the pair that is furthest apart is the furthest anything can be.
     */
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
     * Blends the ground around a footprint into the level a build stands at, which a template cannot do
     * for itself: `beard_thin` and `beard_box` are worldgen jigsaw settings, read while chunks generate,
     * and never run on a structure laid at runtime. So the shoulder is built here instead, as a ramp:
     * the ground beside the walls is brought to the build's own base, and the difference is spent at
     * {@link #APRON_SLOPE} per block walked out, so a hill is walked down - or a hollow built up - to
     * meet the walls instead of being cut off at them. The ramp stops where it has met the ground it was
     * walking toward, or at {@link #APRON_REACH}, and nothing beyond that is touched.
     */
    public static Apron apron(ServerLevel level, BlockPos corner, int wide, int deep, int base) {
        int[] counts = new int[3];
        int rings = reach(level, corner, wide, deep, base);
        for (int ring = 1; ring <= rings; ring++) {
            for (int dx = -ring; dx <= wide + ring; dx++) {
                counts[side(level, corner, dx, -ring, base, ring)]++;
                counts[side(level, corner, dx, deep + ring, base, ring)]++;
            }
            for (int dz = -ring + 1; dz < deep + ring; dz++) {
                counts[side(level, corner, -ring, dz, base, ring)]++;
                counts[side(level, corner, wide + ring, dz, base, ring)]++;
            }
        }
        return new Apron(counts[CUT], counts[FILLED], counts[UNTOUCHED]);
    }

    /**
     * How many rings the ramp needs: the worst difference between the build's base and the ground right
     * against its walls, divided by the slope a ring may spend, and two rings more so the ramp has met
     * the hillside rather than stopping a block short of it.
     */
    private static int reach(ServerLevel level, BlockPos corner, int wide, int deep, int base) {
        int worst = 0;
        for (int dx = 0; dx <= wide; dx++) {
            worst = Math.max(worst, Math.abs(height(level, corner.getX() + dx, corner.getZ() - 1) - base));
            worst = Math.max(worst, Math.abs(height(level, corner.getX() + dx, corner.getZ() + deep) - base));
        }
        for (int dz = 1; dz < deep; dz++) {
            worst = Math.max(worst, Math.abs(height(level, corner.getX() - 1, corner.getZ() + dz) - base));
            worst = Math.max(worst, Math.abs(height(level, corner.getX() + wide, corner.getZ() + dz) - base));
        }
        int needed = (int) Math.ceil(worst / APRON_SLOPE) + 2;
        return Math.min(APRON_REACH, Math.max(needed, 4));
    }

    private static int height(ServerLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
    }

    /** One column of one ring, brought to where the ramp has got to by that ring. */
    private static int side(ServerLevel level, BlockPos corner, int dx, int dz, int base, int distance) {
        int x = corner.getX() + dx;
        int z = corner.getZ() + dz;
        int ground = height(level, x, z);
        int target = ramp(ground, base, distance);
        if (target == ground) return UNTOUCHED;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        // What the column was paved with, so a shelf cut into a grass hill keeps its grass instead of
        // showing the rock the blade went through.
        cursor.set(x, ground - 1, z);
        BlockState surface = level.getBlockState(cursor);
        boolean grassy = surface.is(Blocks.GRASS_BLOCK) || surface.is(Blocks.DIRT)
                || surface.is(Blocks.PODZOL) || surface.is(Blocks.MYCELIUM)
                || surface.is(Blocks.COARSE_DIRT);
        boolean wet = !surface.getFluidState().isEmpty();
        if (target < ground) {
            for (int y = ground - 1; y >= target; y--) {
                cursor.set(x, y, z);
                if (level.getBlockState(cursor).isAir()) continue;
                level.setBlock(cursor, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            cap(level, cursor, x, target, z, grassy, wet);
            return CUT;
        }
        for (int y = ground; y < target; y++) {
            cursor.set(x, y, z);
            if (!level.getBlockState(cursor).isAir()) continue;
            level.setBlock(cursor, Blocks.DIRT.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
        cap(level, cursor, x, target, z, grassy, wet);
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
    private static void standKeeper(ServerLevel level, BlockPos corner, Vec3i size) {
        Entity entity = TolkienEntities.ENTITY_TTM_HUMAN.get().create(level, EntitySpawnReason.EVENT);
        if (!(entity instanceof Mob keeper)) return;
        BlockPos stand = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                corner.offset(size.getX() / 2, 0, size.getZ() + 2));
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
        keeper.setCustomName(QuestPool.giverName(level.getRandom()));
        keeper.setCustomNameVisible(true);
        level.addFreshEntity(keeper);
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
