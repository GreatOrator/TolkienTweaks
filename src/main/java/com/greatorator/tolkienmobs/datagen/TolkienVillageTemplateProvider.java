package com.greatorator.tolkienmobs.datagen;

import com.google.common.hash.Hashing;
import com.greatorator.tolkienmobs.block.custom.PipeweedCropBlock;
import com.greatorator.tolkienmobs.datagen.helpers.TolkienLootHelper;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.*;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.storage.loot.LootTable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Derives the village-ready copies - houses with their porch and residents, town centres, roads and
 * villagers - when datagen runs; users keep editing the original house NBTs.
 */
public final class TolkienVillageTemplateProvider implements DataProvider {
    private final PackOutput output;

    private final TemplateSource templates;

    /**
     * NeoForge 21.4 removed ExistingFileHelper; the templates are read straight out of the resource manager the
     * gather event exposes for the data pack.
     */
    public TolkienVillageTemplateProvider(PackOutput output, ResourceManager resources) {
        this(output, location -> resources.getResource(location)
                .orElseThrow(() -> new IOException("missing village template " + location))
                .open());
    }

    public TolkienVillageTemplateProvider(PackOutput output, TemplateSource templates) {
        this.output = output;
        this.templates = templates;
    }

    @FunctionalInterface
    public interface TemplateSource {
        InputStream open(Identifier location) throws IOException;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return CompletableFuture.runAsync(() -> {
            try {
                for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
                    // A village draws on one plan or several; each is prepared against its own NBT.
                    for (var house : village.houses()) {
                        write(cache, village.houseTemplate(house), prepareHouse(village, house, templates));
                    }
                    // A village that farms a crop of its own builds from a replanted copy of the
                    // vanilla farm, the same way it builds from prepared copies of its houses.
                    if (TolkienVillageProvider.hasOwnFarm(village.style())) {
                        write(cache, village.farmTemplate(), prepareFarm(village, templates));
                    }
                    // A village that lays this mod's path block starts from a copy of the town centre
                    // with the paths in its plaza swapped too, the sockets included.
                    if (village.rewiresStreets()) {
                        write(cache, TolkienVillageProvider.townCentre(village), prepareTownCentre(village, templates));
                    }
                    for (String name : TolkienVillageProvider.VILLAGERS) {
                        write(cache, TolkienVillageProvider.villagerTemplate(village, name), prepareVillager(village, name, templates));
                    }
                }
                for (String style : TolkienVillageProvider.ROAD_STYLES.keySet()) {
                    for (var street : TolkienVillageProvider.streets(style)) write(cache, street.template(), prepareRoad(street, templates));
                }
            } catch (IOException error) {
                throw new IllegalStateException("Cannot prepare village templates", error);
            }
        }, Util.backgroundExecutor());
    }

    public static CompoundTag prepareHouse(TolkienVillageProvider.Village village, TolkienVillageProvider.House plan,
                                           TemplateSource templates) throws IOException {
        CompoundTag house = read(village.houseSource(plan), templates);
        // Add a one-block porch outside the original footprint, keeping the entire house intact.
        BlockPos shift = village.templateShift(plan);
        for (Tag tag : list(house, "blocks")) shiftList(list((CompoundTag) tag, "pos"), shift, false);
        for (Tag tag : list(house, "entities")) {
            shiftList(list((CompoundTag) tag, "blockPos"), shift, false);
            shiftList(list((CompoundTag) tag, "pos"), shift, true);
        }
        ListTag size = list(house, "size");
        int dimension = village.front(plan).getAxis() == Direction.Axis.X ? 0 : 2;
        size.set(dimension, IntTag.valueOf(size.getIntOr(dimension, 0) + 1));
        // Clear occupied flags captured while building the templates, so generated beds can be used.
        for (Tag tag : list(house, "palette")) {
            CompoundTag state = (CompoundTag) tag;
            if (string(state, "Name").endsWith("_bed")) {
                compound(state, "Properties").putString("occupied", "false");
            }
        }
        // The house's own path blocks, and the porch in front of its door, are this mod's path
        // block in a village that lays one; the source templates still carry vanilla's grass path.
        if (village.laysStonePath()) layStonePath(house);
        BlockPos entrance = village.preparedEntrance(plan);
        putState(house, entrance.below(),
                village.laysStonePath() ? TolkienVillageProvider.stonePathBlock() : "minecraft:dirt_path");
        addConnector(house, entrance, village.orientation(plan), "minecraft:building_entrance",
                "minecraft:empty", "minecraft:air", 0);
        requireFrontEdge(village, plan, house, entrance);
        requireShallowFooting(village, plan, entrance);
        addConnector(house, plan.residentFloor().offset(shift), "up_north", "minecraft:bottom",
                "minecraft:village/" + village.vanillaStyle() + "/villagers", null, 0);
        stockChests(house, houseLoot(village, plan));
        if (village.style().equals("hobbit")) plantPipeweedGarden(house, entrance);
        return house;
    }

    /** How many plants a burrow's bed holds, how far from the door it may be laid, and how many rows
     * above or below the door it may climb. */
    private static final int GARDEN_PLANTS = 9;
    private static final int GARDEN_REACH = 6;
    private static final int GARDEN_ROWS = 4;

    /**
     * What a garden may clear off its ground: bare air, and the weeds and wildflowers the burrow plans
     * grow around their doors. Anything else over a grass block - a fence, a slab, or the plan's own
     * structure void - is part of the build and is left where it stands.
     */
    private static final Set<String> GARDEN_WEEDS = Set.of(
            "minecraft:air",
            "minecraft:short_grass", "minecraft:tall_grass", "minecraft:fern", "minecraft:large_fern",
            "minecraft:dandelion", "minecraft:poppy", "minecraft:oxeye_daisy", "minecraft:cornflower",
            "minecraft:azure_bluet", "minecraft:allium", "minecraft:blue_orchid",
            "minecraft:lily_of_the_valley", "minecraft:red_tulip", "minecraft:orange_tulip",
            "minecraft:white_tulip", "minecraft:pink_tulip");

    /**
     * Lays a hobbit burrow's pipe-weed bed.
     *
     * <p>A burrow arrives with grass terraces and nothing growing on them, so the Shire's own weed is
     * put where a hobbit would keep it: the turf beside the door, tilled and planted. The bed is taken
     * from ground the plan already has - grass a garden may clear, whether bare or under weeds - so it
     * can never cut into the house, and it keeps clear of the doorstep. Its row is the nearest turf's,
     * because a burrow's doorway does not always stand at its garden's level. Which leaf each plant is
     * comes from its place in the bed, so the same plan always yields the same garden.
     */
    private static void plantPipeweedGarden(CompoundTag house, BlockPos entrance) {
        ListTag palette = list(house, "palette");
        ListTag blocks = list(house, "blocks");
        Map<BlockPos, String> standing = new HashMap<>();
        List<BlockPos> turf = new ArrayList<>();
        for (Tag tag : blocks) {
            CompoundTag block = (CompoundTag) tag;
            BlockPos pos = position(block);
            String name = string(palette.getCompoundOrEmpty(block.getIntOr("state", 0)), "Name");
            standing.put(pos, name);
            if (name.equals("minecraft:grass_block")) turf.add(pos);
        }

        // A saved plan lists the air inside and above it as blocks of its own, so open ground is read
        // from what stands in the cell rather than from whether the plan mentions it at all. The top row
        // is left alone: a plant there would hang outside the template.
        int height = list(house, "size").getIntOr(1, 0);
        turf.removeIf(pos -> pos.getY() + 1 >= height
                || !GARDEN_WEEDS.contains(standing.getOrDefault(pos.above(), "minecraft:air"))
                || Math.abs(pos.getY() - entrance.getY()) > GARDEN_ROWS
                || Math.abs(pos.getX() - entrance.getX()) <= 1 && Math.abs(pos.getZ() - entrance.getZ()) <= 1
                || pos.distSqr(entrance) > GARDEN_REACH * GARDEN_REACH);
        if (turf.size() < 4) return;
        turf.sort(Comparator.comparingDouble((BlockPos pos) -> pos.distSqr(entrance))
                .thenComparingInt(BlockPos::getX).thenComparingInt(BlockPos::getZ));
        int bed = turf.get(0).getY();
        turf.removeIf(pos -> pos.getY() != bed);
        if (turf.size() < 4) return;

        for (int i = 0; i < Math.min(GARDEN_PLANTS, turf.size()); i++) {
            BlockPos soil = turf.get(i);
            putState(house, soil, "minecraft:farmland");
            putState(house, soil.above(), ripePipeweed(pipeweedStrain(i)));
        }
    }

    /**
     * Which leaf stands in a given cell of a Shire bed: mostly the common one, with a plant of each
     * finer strain in every bed worth the name. The position in the bed decides it, so the same plan
     * always yields the same rows.
     */
    private static String pipeweedStrain(int i) {
        return i % 4 == 3 ? "tolkienmobs:pipeweed_oldtoby"
                : i % 7 == 6 ? "tolkienmobs:pipeweed_southlinch"
                : "tolkienmobs:pipeweed";
    }

    /** A strain ready to cut, as a template state. */
    private static CompoundTag ripePipeweed(String strain) {
        CompoundTag ripe = new CompoundTag();
        ripe.putString("Name", strain);
        CompoundTag properties = new CompoundTag();
        properties.putString("age", String.valueOf(PipeweedCropBlock.MAX_AGE));
        ripe.put("Properties", properties);
        return ripe;
    }

    /**
     * The crops a vanilla farm plants, which a village with a crop of its own replants. Every row of
     * the plan is taken, whatever it grew: the mod's farms are leaf fields, not a rotation of greens.
     */
    private static final Set<String> FARM_CROPS = Set.of(
            "minecraft:wheat", "minecraft:carrots", "minecraft:potatoes", "minecraft:beetroots",
            "minecraft:melon_stem", "minecraft:pumpkin_stem");

    /**
     * Replants a vanilla farm with the village's own crop.
     *
     * <p>The farm is copied rather than written from scratch, so it keeps the shape a plains village's
     * fields have - the tilled rows, the water channel between them, the fence and the gate - and, most
     * of all, its own street socket: a farm joins a village from its front edge exactly as a house does.
     * Only what stands on the soil changes, and it changes to this mod's leaf, ripe, in the same mix the
     * burrows' gardens grow. The copy is written whether or not the plan turns out to hold a crop, and
     * this fails the data run if it holds none, so a game update that moves the crops cannot leave the
     * Shire quietly growing wheat again.
     */
    public static CompoundTag prepareFarm(TolkienVillageProvider.Village village, TemplateSource templates) throws IOException {
        CompoundTag farm = read(TolkienVillageProvider.ownFarmSource(village.style())
                .orElseThrow(() -> new IllegalStateException("The " + village.style() + " village has no farm source")), templates);
        ListTag palette = list(farm, "palette");
        int planted = 0;
        for (Tag tag : list(farm, "blocks")) {
            CompoundTag block = (CompoundTag) tag;
            String name = string(palette.getCompoundOrEmpty(block.getIntOr("state", 0)), "Name");
            if (!FARM_CROPS.contains(name)) continue;
            block.putInt("state", mergeState(palette, ripePipeweed(pipeweedStrain(planted++))));
        }
        if (planted == 0) {
            throw new IllegalStateException("The " + village.style() + " village's farm copy holds no crop to"
                    + " replant - the vanilla farm it is made from ('" + TolkienVillageProvider.ownFarmSource(village.style())
                    .orElseThrow() + "') has moved or changed, so the village would grow nothing.");
        }
        return farm;
    }

    /**
     * The table every chest in a plan's copy is stocked from.
     *
     * <p>A village builds from the copies written here rather than from the plans themselves, so this
     * is the one place its chest loot is decided. That matters for two reasons: a plan can be reused by
     * a second village (Nurn builds the desert house, and should still eat from a Nurn farmhouse's
     * table), and the hobbit village's second plan is the baker's house, which keeps a baker's pantry
     * where the other two keep a household's.
     */
    private static ResourceKey<LootTable> houseLoot(TolkienVillageProvider.Village village,
                                                    TolkienVillageProvider.House plan) {
        return switch (village.style()) {
            case "hobbit" -> plan.name().equals("house_2")
                    ? TolkienLootHelper.HOBBIT_BAKER
                    : TolkienLootHelper.HOBBIT_HOUSE;
            case "dwarf" -> TolkienLootHelper.DWARF_HOUSE;
            case "elven" -> TolkienLootHelper.ELVEN_HOUSE;
            case "human" -> TolkienLootHelper.HUMAN_HOUSE;
            case "frozen" -> TolkienLootHelper.FROZEN_HOUSE;
            case "orc" -> TolkienLootHelper.ORC_HOUSE;
            case "desert" -> TolkienLootHelper.DESERT_HOUSE;
            case "nurn" -> TolkienLootHelper.NURN_HOUSE;
            // A new village has to say what its houses are stocked from; guessing one would put a
            // human household's bread and letters in whatever was built next.
            default -> throw new IllegalStateException("The " + village.style() + " village has no house loot"
                    + " table; add one to houseLoot and to TolkienChestLootTables before it can build.");
        };
    }

    /**
     * Points every chest in a prepared house at a loot table.
     *
     * <p>Ender chests are left alone - their palette entries name a different block, so they never
     * match - and so is every other container: a barrel or a furnace in a plan keeps whatever it was
     * saved with.
     */
    private static void stockChests(CompoundTag house, ResourceKey<LootTable> table) {
        ListTag palette = list(house, "palette");
        Set<Integer> chestStates = new HashSet<>();
        for (int i = 0; i < palette.size(); i++) {
            if (string(palette.getCompoundOrEmpty(i), "Name").equals("minecraft:chest")) chestStates.add(i);
        }
        if (chestStates.isEmpty()) return;
        for (Tag tag : list(house, "blocks")) {
            CompoundTag block = (CompoundTag) tag;
            if (!chestStates.contains(block.getIntOr("state", 0))) continue;
            CompoundTag nbt = block.getCompound("nbt").orElseGet(() -> {
                CompoundTag created = new CompoundTag();
                block.put("nbt", created);
                return created;
            });
            nbt.putString("id", "minecraft:chest");
            nbt.putString("LootTable", table.identifier().toString());
        }
    }

    /**
     * How far below its doorway a plan may reach. The game bridges a street socket to a doorway socket
     * and drops the piece when the piece's box dips further below the street than this, without saying
     * so; a village then simply never shows that house. Five rows was measured to be refused and four to
     * join, on both of the mod's burrows, so this is the limit datagen enforces rather than a guess.
     */
    private static final int MAX_DOORWAY_FOOTING = 4;

    /**
     * A plan may only carry so much ground under its own doorway. The burrows are cut into hillsides, so
     * their gardens sat seven rows above the plan's lowest row: the village drew them, refused them, and
     * showed no house at all. The plan is now cut down to its garden first, and this fails the data run
     * if another one arrives deeper than the game can fit.
     */
    private static void requireShallowFooting(TolkienVillageProvider.Village village,
                                              TolkienVillageProvider.House plan, BlockPos entrance) {
        if (entrance.getY() > MAX_DOORWAY_FOOTING) {
            throw new IllegalStateException("The " + village.style() + " village's " + plan.name()
                    + " puts its doorway at row " + entrance.getY() + " of its own template, but a plan may"
                    + " only reach " + MAX_DOORWAY_FOOTING + " rows below its doorway: the game refuses a"
                    + " piece that dips deeper, and the village would silently never build it. Cut the ground"
                    + " below the doorway down to " + MAX_DOORWAY_FOOTING + " rows and write the entrance"
                    + " coordinate against the trimmed template.");
        }
    }

    /**
     * A doorway marker only connects a house from the house's own front edge. One set inside the
     * footprint asks the street behind it to grow into the walls, which the game refuses, and it
     * drops the whole house rather than placing it: the village keeps its town centre and nothing
     * else. The size read here already carries the porch column, so an entrance coordinate copied
     * from a house of another depth or width fails the data run instead of a world.
     */
    private static void requireFrontEdge(TolkienVillageProvider.Village village, TolkienVillageProvider.House plan,
                                         CompoundTag house, BlockPos marker) {
        Direction front = village.front(plan);
        int extent = list(house, "size").getIntOr(front.getAxis() == Direction.Axis.X ? 0 : 2, 0);
        int along = front.getAxis() == Direction.Axis.X ? marker.getX() : marker.getZ();
        int edge = front.getAxisDirection() == Direction.AxisDirection.POSITIVE ? extent - 1 : 0;
        if (along != edge) {
            throw new IllegalStateException("The " + village.style() + " village's " + plan.name() + " puts its doorway marker at "
                    + marker + ", column " + along + " of " + extent + " along " + front
                    + ", but the marker has to sit on the " + front + " edge, column " + edge
                    + ": an entrance coordinate is written against a house's own template, not against"
                    + " the template whose other numbers it borrows.");
        }
    }

    /** Vanilla's villager template with the entity swapped for this village's own NPC. */
    public static CompoundTag prepareVillager(TolkienVillageProvider.Village village, String name,
                                              TemplateSource templates) throws IOException {
        CompoundTag villager = read(Identifier.withDefaultNamespace(
                "village/" + village.vanillaStyle() + "/villagers/" + name), templates);
        for (Tag tag : list(villager, "entities")) {
            compound((CompoundTag) tag, "nbt").putString("id", village.resident().toString());
        }
        return villager;
    }

    /**
     * 26.1's {@code CompoundTag} reads hand back {@link java.util.Optional}s instead of a fallback value.
     * These keep the template editing below readable, and the one that must not be detached throws rather
     * than handing back a list whose edits would go nowhere: structure NBT always carries these.
     */
    private static ListTag list(CompoundTag tag, String name) {
        return tag.getList(name).orElseThrow(() -> new IllegalArgumentException("Template has no " + name + " list"));
    }

    private static String string(CompoundTag tag, String name) {
        return tag.getStringOr(name, "");
    }

    private static CompoundTag compound(CompoundTag tag, String name) {
        return tag.getCompoundOrEmpty(name);
    }

    private void write(CachedOutput cache, Identifier name, CompoundTag tag) throws IOException {
        var bytes = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, bytes);
        byte[] content = bytes.toByteArray();
        cache.writeIfNeeded(output.getOutputFolder(PackOutput.Target.DATA_PACK)
                .resolve(name.getNamespace() + "/structure/" + name.getPath() + ".nbt"), content, Hashing.sha1().hashBytes(content));
    }

    private static CompoundTag read(Identifier source, TemplateSource templates) throws IOException {
        CompoundTag tag;
        try (var input = templates.open(source.withPrefix("structure/").withSuffix(".nbt"))) {
            if (input == null) throw new IOException("Missing template " + source);
            tag = NbtIo.readCompressed(input, NbtAccounter.unlimitedHeap());
        }
        tag = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), tag, tag.getIntOr("DataVersion", 0));
        tag.putInt("DataVersion", SharedConstants.getCurrentVersion().dataVersion().version());
        return tag;
    }

    private static void shiftList(ListTag coordinates, BlockPos shift, boolean floating) {
        for (int i = 0; i < 3; i++) {
            int offset = i == 0 ? shift.getX() : i == 2 ? shift.getZ() : 0;
            if (floating) coordinates.set(i, DoubleTag.valueOf(coordinates.getDoubleOr(i, 0) + offset));
            else coordinates.set(i, IntTag.valueOf(coordinates.getIntOr(i, 0) + offset));
        }
    }

    /** How far the square is paved out beyond the vanilla meeting point: nine blocks across becomes twenty-one. */
    private static final int PLAZA_MARGIN = 6;

    /** The town centre as a village that lays its own path block builds it: its paths swapped. */
    public static CompoundTag prepareTownCentre(TolkienVillageProvider.Village village,
                                                TemplateSource templates) throws IOException {
        CompoundTag centre = read(TolkienVillageProvider.vanillaTownCentre(village), templates);
        layStonePath(centre);
        if (village.landmarks().isEmpty()) return centre;
        if (!village.rewiresStreets()) {
            throw new IllegalStateException("The " + village.style() + " village puts " + village.landmarks().size()
                    + " landmark(s) on its square, but only a village that rebuilds the town centre has a copy of"
                    + " it to pave a square around.");
        }
        return plaza(village, centre);
    }

    /**
     * The meeting point paved out into a village square: the centre in the middle, the village's own path
     * under the whole square, a doorway socket on each side a landmark fronts, and the street ends moved
     * out to the square's own edge.
     *
     * <p>A landmark is a plan no street can front: a church is thirty-one blocks across where a house is
     * fifteen, so the game drops it nearly every time a street draws it. The square is the one place with
     * the room, and it has to be paved out first - beside a nine-block meeting point a plan that wide
     * covers the sides the streets would have left by, and the streets are refused instead. The square is
     * written here because it is geometry, not content: nothing in it depends on a world.</p>
     */
    private static CompoundTag plaza(TolkienVillageProvider.Village village, CompoundTag centre) {
        ListTag centreSize = list(centre, "size");
        int width = centreSize.getIntOr(0, 0) + PLAZA_MARGIN * 2;
        int height = centreSize.getIntOr(1, 0);
        int depth = centreSize.getIntOr(2, 0) + PLAZA_MARGIN * 2;
        String floor = village.laysStonePath() ? TolkienVillageProvider.stonePathBlock() : "minecraft:dirt_path";
        CompoundTag plaza = new CompoundTag();
        plaza.putInt("DataVersion", centre.getIntOr("DataVersion", 0));
        ListTag size = new ListTag();
        size.add(IntTag.valueOf(width));
        size.add(IntTag.valueOf(height));
        size.add(IntTag.valueOf(depth));
        plaza.put("size", size);
        plaza.put("palette", new ListTag());
        plaza.put("blocks", new ListTag());
        plaza.put("entities", new ListTag());
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) putState(plaza, new BlockPos(x, 0, z), floor);
        }
        // The meeting point stands in the middle of the square. Its own street ends are left out: a street
        // leaving from inside the square would grow across paving that is part of the same piece, so it is
        // refused and the village loses that street. The square's own edges carry them instead.
        ListTag centrePalette = list(centre, "palette");
        String streetPool = null;
        for (Tag tag : list(centre, "blocks")) {
            CompoundTag block = (CompoundTag) tag;
            CompoundTag nbt = compound(block, "nbt");
            if (string(nbt, "name").equals("minecraft:street")) {
                streetPool = string(nbt, "pool");
                continue;
            }
            addBlock(plaza, position(block).offset(PLAZA_MARGIN, 0, PLAZA_MARGIN),
                    centrePalette.getCompoundOrEmpty(block.getIntOr("state", 0)), nbt.isEmpty() ? null : nbt.copy());
        }
        if (streetPool == null) {
            throw new IllegalStateException("The " + village.style() + " village's meeting point has no street ends to"
                    + " move out to its square; a village that paves a square is drawn against a meeting point that"
                    + " has some.");
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (village.landmarks().stream().anyMatch(landmark -> landmark.side() == side)) continue;
            addConnector(plaza, edge(width, depth, side), side.getName() + "_up", "minecraft:street", streetPool, floor, 0);
        }
        for (var landmark : village.landmarks()) {
            // A landmark is drawn before the square's streets, not after them. The game works through a
            // piece's sockets by placement priority, and a street that grew across the square first would
            // take the room the landmark needs - which is what left villages without a church at all.
            addConnector(plaza, edge(width, depth, landmark.side()), landmark.side().getName() + "_up",
                    "minecraft:building_entrance", village.landmarkPool(landmark).identifier().toString(), floor, 1);
        }
        return plaza;
    }

    /** The middle of one side of the square, at the height a street's own sockets are written at. */
    private static BlockPos edge(int width, int depth, Direction side) {
        return switch (side) {
            case NORTH -> new BlockPos(width / 2, 1, 0);
            case SOUTH -> new BlockPos(width / 2, 1, depth - 1);
            case WEST -> new BlockPos(0, 1, depth / 2);
            case EAST -> new BlockPos(width - 1, 1, depth / 2);
            default -> throw new IllegalStateException("A landmark stands on one of the square's four sides, not " + side);
        };
    }

    private static BlockPos position(CompoundTag block) {
        ListTag pos = list(block, "pos");
        return new BlockPos(pos.getIntOr(0, 0), pos.getIntOr(1, 0), pos.getIntOr(2, 0));
    }

    /** Adds a block written by another template, merging its palette entry into this one's. */
    private static void addBlock(CompoundTag template, BlockPos position, CompoundTag state, CompoundTag nbt) {
        ListTag palette = list(template, "palette");
        int index = -1;
        for (int i = 0; i < palette.size(); i++) {
            if (palette.getCompoundOrEmpty(i).equals(state)) { index = i; break; }
        }
        if (index == -1) {
            index = palette.size();
            palette.add(state.copy());
        }
        CompoundTag block = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(position.getX()));
        pos.add(IntTag.valueOf(position.getY()));
        pos.add(IntTag.valueOf(position.getZ()));
        block.put("pos", pos);
        block.putInt("state", index);
        if (nbt != null) block.put("nbt", nbt);
        list(template, "blocks").add(block);
    }

    /**
     * Swaps every path block in a template for this mod's own: the palette entries that the blocks
     * list points at, and the jigsaw sockets that write a path block once their neighbour connects.
     * A socket's final state is set straight into the world after the processors have run, so
     * renaming the palette alone would leave a vanilla path behind at each of them.
     */
    private static void layStonePath(CompoundTag template) {
        String stone = TolkienVillageProvider.stonePathBlock();
        for (Tag tag : list(template, "palette")) {
            CompoundTag state = (CompoundTag) tag;
            if (string(state, "Name").equals("minecraft:dirt_path")) state.putString("Name", stone);
        }
        for (Tag tag : list(template, "blocks")) {
            CompoundTag nbt = compound((CompoundTag) tag, "nbt");
            if (string(nbt, "final_state").equals("minecraft:dirt_path")) nbt.putString("final_state", stone);
        }
    }

    public static CompoundTag prepareRoad(TolkienVillageProvider.Street street, TemplateSource templates) throws IOException {
        // The street surface is left as the vanilla template wrote it, because the road pool's own
        // processor is what lays the village's path block there - keeping the grass patches a tenth
        // of a plains street grows over and the plank bridge where one crosses water.
        CompoundTag road = read(street.source(), templates);
        ListTag palette = list(road, "palette");
        ListTag blocks = list(road, "blocks");
        ListTag size = list(road, "size");
        var entrances = new java.util.ArrayList<CompoundTag>();
        for (Tag tag : blocks) {
            CompoundTag block = (CompoundTag) tag;
            if (string(compound(block, "nbt"), "name").equals("minecraft:building_entrance")) entrances.add(block.copy());
        }
        for (CompoundTag entrance : entrances) {
            ListTag coordinates = list(entrance, "pos");
            BlockPos old = new BlockPos(coordinates.getIntOr(0, 0), coordinates.getIntOr(1, 0), coordinates.getIntOr(2, 0));
            Direction facing = Direction.byName(palette.getCompoundOrEmpty(entrance.getIntOr("state", 0))
                    .getCompoundOrEmpty("Properties").getStringOr("orientation", "").split("_")[0]);
            BlockPos edge = switch (facing) {
                case NORTH -> new BlockPos(old.getX(), old.getY(), 0);
                case SOUTH -> new BlockPos(old.getX(), old.getY(), size.getIntOr(2, 0) - 1);
                case WEST -> new BlockPos(0, old.getY(), old.getZ());
                case EAST -> new BlockPos(size.getIntOr(0, 0) - 1, old.getY(), old.getZ());
                default -> throw new IllegalArgumentException("Street entrance must face horizontally");
            };
            removeBlock(road, old);
            for (BlockPos step = old; ; step = step.relative(facing)) {
                // A short driveway connects the reserved road edge to the original path. The
                // driveway is this mod's own block in a village that lays one, and the vanilla
                // material otherwise: sandstone down from the desert streets, the dirt path the
                // plains ones use. It is written in rather than put through the street rules so
                // the strip at each house's door is solid, not mottled with grass.
                putState(road, step.below(), street.laysStonePath() ? TolkienVillageProvider.stonePathBlock()
                        : street.style().equals("desert") ? "minecraft:sandstone" : "minecraft:dirt_path");
                if (step.equals(edge)) break;
            }
            coordinates.set(0, IntTag.valueOf(edge.getX())); coordinates.set(2, IntTag.valueOf(edge.getZ()));
            removeBlock(road, edge);
            blocks.add(entrance);
        }
        return road;
    }

    private static void removeBlock(CompoundTag template, BlockPos position) {
        ListTag blocks = list(template, "blocks");
        blocks.removeIf(tag -> {
            ListTag pos = list((CompoundTag) tag, "pos");
            return pos.getIntOr(0, 0) == position.getX() && pos.getIntOr(1, 0) == position.getY() && pos.getIntOr(2, 0) == position.getZ();
        });
    }

    private static void putState(CompoundTag template, BlockPos position, String name) {
        CompoundTag state = new CompoundTag();
        state.putString("Name", name);
        putState(template, position, state);
    }

    /** Writes a block, with its properties, over whatever the plan had at that position. */
    private static void putState(CompoundTag template, BlockPos position, CompoundTag state) {
        int index = mergeState(list(template, "palette"), state);
        removeBlock(template, position);
        CompoundTag block = new CompoundTag();
        ListTag pos = new ListTag(); pos.add(IntTag.valueOf(position.getX())); pos.add(IntTag.valueOf(position.getY())); pos.add(IntTag.valueOf(position.getZ()));
        block.put("pos", pos); block.putInt("state", index);
        list(template, "blocks").add(block);
    }

    /** A state's index in a template palette, added to the palette when the template lacks it. */
    private static int mergeState(ListTag palette, CompoundTag state) {
        for (int i = 0; i < palette.size(); i++) {
            if (palette.getCompoundOrEmpty(i).equals(state)) return i;
        }
        palette.add(state.copy());
        return palette.size() - 1;
    }

    private static void addConnector(CompoundTag house, BlockPos position, String orientation,
                                     String name, String pool, String finalState, int placementPriority) {
        ListTag size = list(house, "size");
        if (position.getX() < 0 || position.getX() >= size.getIntOr(0, 0) || position.getY() < 0
                || position.getY() >= size.getIntOr(1, 0) || position.getZ() < 0 || position.getZ() >= size.getIntOr(2, 0)) {
            throw new IllegalArgumentException("Connector outside house: " + position);
        }
        ListTag palette = list(house, "palette");
        ListTag blocks = list(house, "blocks");
        CompoundTag replaced = null;
        for (Tag tag : blocks) {
            CompoundTag block = (CompoundTag) tag;
            ListTag pos = list(block, "pos");
            if (pos.getIntOr(0, 0) == position.getX() && pos.getIntOr(1, 0) == position.getY() && pos.getIntOr(2, 0) == position.getZ()) {
                replaced = block;
                break;
            }
        }
        if (finalState == null) {
            if (replaced == null) throw new IllegalArgumentException("Resident connector needs an existing floor");
            CompoundTag state = palette.getCompoundOrEmpty(replaced.getIntOr("state", 0));
            CompoundTag properties = compound(state, "Properties");
            finalState = string(state, "Name") + (properties.isEmpty() ? "" : properties.keySet().stream().sorted()
                    .map(key -> key + "=" + string(properties, key)).collect(Collectors.joining(",", "[", "]")));
        }
        CompoundTag state = new CompoundTag();
        state.putString("Name", "minecraft:jigsaw");
        CompoundTag properties = new CompoundTag();
        properties.putString("orientation", orientation);
        state.put("Properties", properties);
        int stateIndex = palette.size();
        palette.add(state);
        if (replaced != null) blocks.remove(replaced);
        CompoundTag block = new CompoundTag();
        ListTag pos = new ListTag();
        pos.add(IntTag.valueOf(position.getX())); pos.add(IntTag.valueOf(position.getY())); pos.add(IntTag.valueOf(position.getZ()));
        block.put("pos", pos);
        block.putInt("state", stateIndex);
        CompoundTag nbt = new CompoundTag();
        nbt.putString("id", "minecraft:jigsaw");
        nbt.putString("name", name);
        nbt.putString("target", name);
        nbt.putString("pool", pool);
        nbt.putString("joint", "aligned");
        nbt.putString("final_state", finalState);
        // Only the square's sockets carry one; a written zero would rewrite every village template.
        if (placementPriority != 0) nbt.putInt("placement_priority", placementPriority);
        block.put("nbt", nbt);
        blocks.add(block);
    }

    @Override
    public String getName() { return "Tolkienmobs - Village templates and connectors"; }
}
