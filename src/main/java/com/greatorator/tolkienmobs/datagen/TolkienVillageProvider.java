package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.greatorator.tolkienmobs.world.components.structure.VillageStructure;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.placement.VillagePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.*;
import java.util.function.Function;

/** Vanilla village assembly, with Tolkien houses selected through per-structure pool aliases. */
public final class TolkienVillageProvider {
    /**
     * How deep a village grows. Seven instead of vanilla's six because the buildings the villages put
     * on their squares take a street's place: a village with two landmarks has two street exits, so it
     * has to reach further to fill out.
     */
    public static final int SIZE = 7;
    public static final int MAX_DISTANCE = 112;
    public static final ResourceKey<StructureSet> VILLAGES = ResourceKey.create(Registries.STRUCTURE_SET,
            Identifier.fromNamespaceAndPath("tolkienmobs", "villages"));
    public static final List<Village> VILLAGES_BY_STYLE = List.of(
            new Village(TolkienStructures.HOUSE_DESERT, "desert", "desert", TolkienBiomes.HARADWAITH, "entityttmsouthron_npc", "east_up",
                    List.of(new House("house", 6, new BlockPos(14, 0, 7), new BlockPos(11, 4, 4)))),
            // Nurn is farmed by the same Southrons, in the same desert houses, rebuilt in dark stone.
            new Village(TolkienStructures.HOUSE_NURN, "nurn", "desert", TolkienBiomes.NURN, "entityttmsouthron_npc", "east_up",
                    List.of(new House("house", 6, new BlockPos(14, 0, 7), new BlockPos(11, 4, 4))), true, true),
            // The dwarven village builds from five plans of its own: the two halls that fill it out,
            // the windmill that stands behind them, and the smithy and the meadhall that front its
            // square. The halls and the windmill face the street at their doors - south for the
            // halls, west for the smithy and the meadhall - so each carries its own front; the two on
            // the square are turned by the game to face it, and stand once in every village.
            new Village(TolkienStructures.HOUSE_DWARF, "dwarf", "plains", TolkienBiomes.IRON_HILLS, "entityttmdwarf", "west_up",
                    List.of(new House("house", 6, new BlockPos(12, 0, 15), new BlockPos(10, 1, 8), "south_up"),
                            new House("house_2", 6, new BlockPos(0, 0, 10), new BlockPos(10, 1, 7)),
                            new House("windmill", 3, new BlockPos(7, 0, 13), new BlockPos(8, 1, 7), "south_up"),
                            new House("blacksmith", 1, new BlockPos(0, 0, 6), new BlockPos(10, 1, 10)),
                            new House("meadhall", 1, new BlockPos(0, 0, 9), new BlockPos(10, 1, 10))),
                    List.of(new Landmark("meadhall", Direction.SOUTH), new Landmark("blacksmith", Direction.NORTH))),
            new Village(TolkienStructures.HOUSE_ELVEN, "elven", "plains", TolkienBiomes.LORINAND, "entityttmelves", "west_up",
                    List.of(new House("house", 6, new BlockPos(0, 0, 5), new BlockPos(4, 8, 3)))),
            // The hobbit village builds from three plans, drawn equally often. The old house fronts the
            // street at its east door, level with its yard; the two burrows are cut into their own
            // hills and open south through their gardens, so their doorway columns sit on that edge and
            // their floors a step above the street. Each coordinate is the garden row the street walks
            // in on, and a plan is only carried four rows below that row: the game refuses a doorway
            // socket deeper than that, so the burrows were cut down to their own gardens' level.
            new Village(TolkienStructures.HOBBIT_HOUSE, "hobbit", "plains", TolkienBiomes.SHIRE, "entityttmhobbit", "east_up",
                    List.of(new House("house", 6, new BlockPos(27, 0, 14), new BlockPos(11, 0, 20)),
                            new House("house_2", 6, new BlockPos(14, 3, 31), new BlockPos(14, 2, 10), "south_up"),
                            new House("house_3", 6, new BlockPos(23, 2, 41), new BlockPos(20, 1, 18), "south_up"))),
            // The human village builds from five plans drawn out on its own plots: the triple house
            // a street grows up around, the single house and the market that fill it out, and the
            // inn and the church that front its square, once each. Each plan carries its own
            // doorway socket, on its own south wall, at the column its front path runs down.
            new Village(TolkienStructures.HOUSE_HUMAN, "human", "plains", TolkienBiomes.GLADDEN, "entityttmhuman", "south_up",
                    List.of(new House("triple_house", 6, new BlockPos(3, 0, 14), new BlockPos(3, 0, 11)),
                            new House("small_house", 3, new BlockPos(10, 0, 14), new BlockPos(10, 0, 9)),
                            new House("market", 3, new BlockPos(7, 0, 14), new BlockPos(4, 0, 9)),
                            new House("inn", 1, new BlockPos(10, 0, 14), new BlockPos(10, 0, 8)),
                            new House("church", 1, new BlockPos(6, 0, 14), new BlockPos(6, 0, 8))),
                    List.of(new Landmark("church", Direction.SOUTH), new Landmark("inn", Direction.NORTH))),
            new Village(TolkienStructures.HOUSE_ORC, "orc", "desert", TolkienBiomes.MORDOR, "entityttmorc", "south_up",
                    List.of(new House("house", 6, new BlockPos(7, 0, 14), new BlockPos(7, 0, 2)))),
            // The frozen house is six blocks deeper than the human one it borrows its other
            // numbers from, so its door socket sits on that deeper south wall, not on the human's.
            new Village(TolkienStructures.HOUSE_FROZEN, "frozen", "plains", TolkienBiomes.FOROCHEL, "entityttmhuman", "south_up",
                    List.of(new House("house", 6, new BlockPos(6, 0, 18), new BlockPos(5, 5, 8))), false, true)
    );
    /** The vanilla villager templates datagen copies for every village; weights match vanilla. */
    public static final List<String> VILLAGERS = List.of("nitwit", "baby", "unemployed");
    /**
     * The villages that lay this mod's own path block rather than a vanilla one. The desert style
     * has no path block of its own - its streets are sandstone - so for Nurn this reaches only the
     * path at each house's door and the driveway that meets it.
     */
    private static final Set<String> STONE_PATH_STYLES = Set.of("dwarf", "human", "nurn", "frozen");
    /**
     * The post each village stands where vanilla stands its own, by village style. A lamp is a decor
     * piece: a street carries sockets that draw from the vanilla decor pool, whose lamp is a fence
     * post with four torches on the plains and a sandstone post with one torch in the desert. A
     * village here points those sockets at a pool of its own, holding the post that belongs in its
     * country; the rest keep vanilla's. Each entry is one of the mod's free-standing posts with a
     * decor socket written into the foot of it.
     */
    private static final Map<String, Identifier> LAMPPOSTS = Map.of(
            // The stone post of the human and dwarven villages.
            "dwarf", lamppost("lamppost"),
            "human", lamppost("lamppost"),
            // Mordor's post is black stone and soul fire.
            "orc", lamppost("lamppost_2"),
            // The desert post lights Harad, and Nurn, where its sandstone is recoloured dark.
            "desert", lamppost("lamppost_3"),
            "nurn", lamppost("lamppost_3"));

    /** What datagen writes beside a village's own pools: a post, made fit for a decor socket. */
    private static Identifier lamppost(String name) {
        return Identifier.fromNamespaceAndPath("tolkienmobs", "village/decor/" + name);
    }

    /**
     * A village's own farmland, by style: a vanilla plan it rebuilds, the block vanilla rings that plot
     * with, and the mod block that ring becomes. A plan with no {@code bin} keeps vanilla's composter,
     * which is where a farmer works; the Shire's is the one that keeps its crop in a barrel instead.
     *
     * <p>The Shire is also the one village that farms a crop of its own - its rows are replanted with
     * leaf - while every other country keeps the crop vanilla plants there.
     */
    public record FarmPlan(Identifier source, String wall, String wood, String bin, String store) {
        public FarmPlan(Identifier source, String wall, String wood) {
            this(source, wall, wood, null, null);
        }
    }

    /** One of vanilla's village plans, named as the template it is read from. */
    private static Identifier vanillaFarm(String style, String name) {
        return Identifier.withDefaultNamespace("village/" + style + "/houses/" + name);
    }

    /**
     * The villages that build their fields in their own wood rather than vanilla's oak, by style. Each
     * plot is walled in the wood its country already builds in: the shire's in lebethron, the wood of
     * its burrows; the dwarves' in dwarven maple; the men of the Vales' in culumalda; the elves' in
     * mallorn, the wood their halls are fenced and stair-stepped in; and the two desert villages' in
     * the wood their own houses use - mirkwood in Mordor, deadwood in Nurn.
     *
     * <p>A plains farm is ringed with a log, so only the wood changes; a desert farm's rim is smooth
     * sandstone stairs, the closest thing a sandstone plot has to a fence, and it is rebuilt as the
     * post it stands in for. Every plot keeps the composter a farmer works at, except the Shire's,
     * which keeps its leaf in a barrel.
     */
    private static final Map<String, List<FarmPlan>> OWN_FARMS = Map.of(
            "hobbit", List.of(new FarmPlan(vanillaFarm("plains", "plains_small_farm_1"),
                    "minecraft:oak_log", "tolkienmobs:log_lebethron",
                    "minecraft:composter", "tolkienmobs:barrel_lebethron")),
            "dwarf", List.of(new FarmPlan(vanillaFarm("plains", "plains_small_farm_1"),
                    "minecraft:oak_log", "tolkienmobs:log_dwarven_maple")),
            "human", List.of(new FarmPlan(vanillaFarm("plains", "plains_small_farm_1"),
                    "minecraft:oak_log", "tolkienmobs:log_culumalda")),
            "elven", List.of(new FarmPlan(vanillaFarm("plains", "plains_small_farm_1"),
                    "minecraft:oak_log", "tolkienmobs:log_mallorn")),
            "orc", List.of(
                    new FarmPlan(vanillaFarm("desert", "desert_farm_1"),
                            "minecraft:smooth_sandstone_stairs", "tolkienmobs:log_mirkwood"),
                    new FarmPlan(vanillaFarm("desert", "desert_farm_2"),
                            "minecraft:smooth_sandstone_stairs", "tolkienmobs:log_mirkwood")),
            "nurn", List.of(
                    new FarmPlan(vanillaFarm("desert", "desert_farm_1"),
                            "minecraft:smooth_sandstone_stairs", "tolkienmobs:log_deadwood"),
                    new FarmPlan(vanillaFarm("desert", "desert_farm_2"),
                            "minecraft:smooth_sandstone_stairs", "tolkienmobs:log_deadwood")));

    /** True when the village with this style builds its fields instead of drawing vanilla's. */
    public static boolean hasOwnFarm(String style) {
        return OWN_FARMS.containsKey(style);
    }

    /** The fields a village with this style builds, in the order datagen writes them. */
    public static List<FarmPlan> farmPlans(String style) {
        return OWN_FARMS.getOrDefault(style, List.of());
    }

    private TolkienVillageProvider() {}

    /** True when the village with this style lays this mod's path block. */
    public static boolean laysStonePath(String style) {
        return STONE_PATH_STYLES.contains(style);
    }

    /** True when the village with this style stands this mod's lamppost along its streets. */
    public static boolean usesLamppost(String style) {
        return LAMPPOSTS.containsKey(style);
    }

    /** The post a village of this style stands, if it stands one of ours. */
    public static Optional<Identifier> lamppostFor(String style) {
        return Optional.ofNullable(LAMPPOSTS.get(style));
    }

    /** That block, read from the registry so a rename can never leave a stale id in a template. */
    public static String stonePathBlock() {
        return BuiltInRegistries.BLOCK.getKey(TolkienBlocks.STONE_PATH.get()).toString();
    }

    /**
     * True when the village with this road style has to rewire the vanilla street pieces it builds
     * from. The plains streets, their terminator stubs and the fountain town centre all lay a dirt
     * path; the desert style the other villages use lays sandstone and never a dirt path.
     */
    private static boolean rewiresStreets(String style) {
        return VILLAGES_BY_STYLE.stream().anyMatch(village -> village.style().equals(style) && village.rewiresStreets());
    }

    public static Optional<Village> village(ResourceKey<Structure> key) {
        return VILLAGES_BY_STYLE.stream().filter(village -> village.structure().equals(key)).findFirst();
    }

    public static Structure.StructureSettings createSettings(Village village, HolderGetter<Biome> biomes) {
        // Like vanilla villages, no spawn overrides: residents arrive through the villager pool.
        return new Structure.StructureSettings(biomes.getOrThrow(village.biomeTag()), Map.of(),
                GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN);
    }

    public static Structure create(Village village, HolderGetter<Biome> biomes,
                                   HolderGetter<StructureTemplatePool> pools) {
        // Alias resolution is local to this village; vanilla village pools stay intact.
        Structure.StructureSettings settings = createSettings(village, biomes);
        Holder<StructureTemplatePool> startPool = pools.getOrThrow(village.startPool());
        List<PoolAliasBinding> aliases = new ArrayList<>(List.of(PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                        Identifier.withDefaultNamespace("village/" + village.vanillaStyle() + "/houses")), village.housePool()),
                PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                        Identifier.withDefaultNamespace("village/" + village.vanillaStyle() + "/streets")), roadPool(village.roadStyle())),
                // Houses, town centers, farms and decorations ask for vanilla villagers through this
                // pool; send every socket to this village's own NPC instead.
                PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                        Identifier.withDefaultNamespace("village/" + village.vanillaStyle() + "/villagers")), village.villagerPool())));
        if (village.darkStone() || usesLamppost(village.style())) {
            // The vanilla decorations hold a lamp, and the desert ones a cactus; a village that stands
            // its own post, or is rebuilt in dark stone, points those sockets at a pool of its own.
            aliases.add(PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                    Identifier.withDefaultNamespace("village/" + village.vanillaStyle() + "/decor")), village.decorPool()));
        }
        // A village that must stand on grass is placed by our own structure, which reads the height
        // maps and the surface noise before it agrees to a site.
        return village.dryLand()
                ? new VillageStructure(settings, startPool, Optional.empty(), SIZE,
                        ConstantHeight.of(VerticalAnchor.absolute(0)), true, Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        MAX_DISTANCE, aliases, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS)
                : new JigsawStructure(settings, startPool, Optional.empty(), SIZE,
                        ConstantHeight.of(VerticalAnchor.absolute(0)), true, Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        new JigsawStructure.MaxDistance(MAX_DISTANCE), aliases,
                        JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
    }

    public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context, Village village) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);
        var placed = context.lookup(Registries.PLACED_FEATURE);
        var empty = pools.getOrThrow(Pools.EMPTY);
        // A village that swaps the paths of its vanilla pieces starts from its own copy of the town
        // centre, so the paths in the plaza are swapped along with the ones under the streets.
        var center = (village.rewiresStreets() ? townCentre(village) : vanillaTownCentre(village)).toString();
        var stone = village.darkStone() ? processors.getOrThrow(TolkienProcessorLists.NURN_DARK_STONE) : null;
        // Nurn's plaza walks on the same path block as its streets, so the centre takes the road
        // rules; its houses, farms and lamps keep the plain recolour.
        var plaza = village.darkStone() ? processors.getOrThrow(TolkienProcessorLists.NURN_ROADS) : stone;
        // A water feature and bell in every start; streets and decorations are vanilla.
        context.register(village.startPool(), new StructureTemplatePool(empty,
                List.of(Pair.of(legacy(center, plaza), 1)), StructureTemplatePool.Projection.RIGID));
        String farm = "village/" + village.vanillaStyle() + "/houses/" + village.vanillaStyle()
                + (village.vanillaStyle().equals("desert") ? "_farm_" : "_small_farm_");
        var farmProcessors = processors.getOrThrow(village.darkStone() ? TolkienProcessorLists.NURN_FARMS
                : village.vanillaStyle().equals("desert") ? ProcessorLists.FARM_DESERT : ProcessorLists.FARM_PLAINS);
        // 26.1 dropped plains_small_farm_2, so a plains-style house pool can only offer the farm
        // that still ships; the desert styles keep both of theirs.
        var houseElements = new ArrayList<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>();
        for (var house : village.houses()) {
            // A plan that stands on the square is drawn by its own pool, once, at the square's socket.
            if (village.isLandmark(house)) continue;
            houseElements.add(Pair.of(single(village.houseTemplate(house).toString(), stone), house.weight()));
        }
        if (hasOwnFarm(village.style())) {
            // A village that builds its own fields draws the prepared copies: each has its wall in the
            // village's own wood and keeps its own street socket, so it still joins a road at its edge.
            List<FarmPlan> plans = farmPlans(village.style());
            for (int i = 0; i < plans.size(); i++) {
                houseElements.add(Pair.of(legacy(village.farmTemplate(i).toString(), farmProcessors), 1));
            }
        } else {
            houseElements.add(Pair.of(StructurePoolElement.legacy(farm + "1", farmProcessors), 1));
            if (village.vanillaStyle().equals("desert")) {
                houseElements.add(Pair.of(StructurePoolElement.legacy(farm + "2", farmProcessors), 1));
            }
        }
        context.register(village.housePool(), new StructureTemplatePool(empty, houseElements,
                StructureTemplatePool.Projection.RIGID));
        for (var landmark : village.landmarks()) {
            // The square's socket names this pool, and the centre is built once, so the plan it holds
            // stands exactly once in a village - however wide it is, and with nothing to collide with.
            context.register(village.landmarkPool(landmark), new StructureTemplatePool(empty,
                    List.of(Pair.of(single(village.houseTemplate(village.plan(landmark)).toString(), stone), 1)),
                    StructureTemplatePool.Projection.RIGID));
        }
        if (village.darkStone() || usesLamppost(village.style())) {
            // Vanilla's own decorations for this style, at vanilla's own weights, with our post drawn
            // where its lamp drew, so only the light post changes. Nurn keeps no cacti: a cactus
            // garden is not what a farmed lowland looks like, and its post is recoloured with the
            // rest of the village.
            var desert = village.vanillaStyle().equals("desert");
            var decor = new ArrayList<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>>();
            decor.add(Pair.of(legacy(lamppostFor(village.style()).orElseThrow().toString(), stone), desert ? 10 : 2));
            if (desert) {
                if (!village.darkStone()) {
                    decor.add(Pair.of(StructurePoolElement.feature(placed.getOrThrow(VillagePlacements.PATCH_CACTUS_VILLAGE)), 4));
                }
                decor.add(Pair.of(StructurePoolElement.feature(placed.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE)), 4));
                decor.add(Pair.of(StructurePoolElement.empty(), 10));
            } else {
                decor.add(Pair.of(StructurePoolElement.feature(placed.getOrThrow(VillagePlacements.OAK_VILLAGE)), 1));
                decor.add(Pair.of(StructurePoolElement.feature(placed.getOrThrow(VillagePlacements.FLOWER_PLAIN_VILLAGE)), 1));
                decor.add(Pair.of(StructurePoolElement.feature(placed.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE)), 1));
                decor.add(Pair.of(StructurePoolElement.empty(), 2));
            }
            context.register(village.decorPool(), new StructureTemplatePool(empty, decor,
                    StructureTemplatePool.Projection.RIGID));
        }
        // The vanilla villager pool contents, same templates and weights (1/1/10), holding this NPC.
        context.register(village.villagerPool(), new StructureTemplatePool(empty,
                List.of(Pair.of(StructurePoolElement.legacy(villagerTemplate(village, "nitwit").toString()), 1),
                        Pair.of(StructurePoolElement.legacy(villagerTemplate(village, "baby").toString()), 1),
                        Pair.of(StructurePoolElement.legacy(villagerTemplate(village, "unemployed").toString()), 10)),
                StructureTemplatePool.Projection.RIGID));
    }

    /** Where datagen writes the village's copy of a vanilla villager template. */
    public static Identifier villagerTemplate(Village village, String name) {
        return Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + village.style() + "/villagers/" + name);
    }

    /** Where datagen writes the village's copy of the town centre it starts from. */
    public static Identifier townCentre(Village village) {
        return Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + village.style() + "/town_center");
    }

    /** The vanilla start piece that copy is derived from: plains styles get a fountain, desert a well. */
    public static Identifier vanillaTownCentre(Village village) {
        return Identifier.withDefaultNamespace(village.vanillaStyle().equals("desert")
                ? "village/desert/town_centers/desert_meeting_point_1"
                : "village/plains/town_centers/plains_fountain_01");
    }

    public static ResourceKey<StructureTemplatePool> roadPool(String style) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, Identifier.fromNamespaceAndPath("tolkienmobs", "village/roads/" + style));
    }

    /**
     * The road pools datagen builds, keyed by their own style: each one is filled with the vanilla
     * style's street shapes. A village that recolours its blocks, or lays its own path block, needs
     * roads of its own, because a road pool is shared by every village that uses the style. That is
     * why the dwarven and human villages do not simply borrow the plains roads the elven and hobbit
     * villages drive on.
     */
    public static final Map<String, String> ROAD_STYLES = Map.of(
            "desert", "desert",
            "plains", "plains",
            "nurn", "desert",
            "dwarf", "plains",
            "human", "plains",
            "frozen", "plains");

    /** Pool elements take processor holders; a village with no recolouring passes none. */
    private static java.util.function.Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> legacy(
            String template, Holder<StructureProcessorList> processors) {
        return processors == null ? StructurePoolElement.legacy(template) : StructurePoolElement.legacy(template, processors);
    }

    private static java.util.function.Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> single(
            String template, Holder<StructureProcessorList> processors) {
        return processors == null ? StructurePoolElement.single(template) : StructurePoolElement.single(template, processors);
    }

    /** The streets of a road pool, by output style: the vanilla shapes that fill it, ready to name. */
    public static List<Street> streets(String output) {
        String style = ROAD_STYLES.getOrDefault(output, output);
        return sourceStreets(style).stream()
                .map(street -> new Street(street.style(), output, street.source(), street.weight()))
                .toList();
    }

    /** Keep vanilla street shapes and weights; move their house sockets outside reserved road footprints. */
    private static List<Street> sourceStreets(String style) {
        // Minecraft 1.21.1 street weights. Registry bootstrap has no resource manager.
        return switch (style) {
            case "plains" -> List.of(
                    street(style, "corner_01", 2),
                    street(style, "corner_02", 2),
                    street(style, "corner_03", 2),
                    street(style, "straight_01", 4),
                    street(style, "straight_02", 4),
                    street(style, "straight_03", 7),
                    street(style, "straight_04", 7),
                    street(style, "straight_05", 3),
                    street(style, "straight_06", 4),
                    street(style, "crossroad_01", 2),
                    street(style, "crossroad_02", 1),
                    street(style, "crossroad_03", 2),
                    street(style, "crossroad_04", 2),
                    street(style, "crossroad_05", 2),
                    street(style, "crossroad_06", 2),
                    street(style, "turn_01", 3));
            case "desert" -> List.of(
                    street(style, "corner_01", 3),
                    street(style, "corner_02", 3),
                    street(style, "straight_01", 4),
                    street(style, "straight_02", 4),
                    street(style, "straight_03", 3),
                    street(style, "crossroad_01", 3),
                    street(style, "crossroad_02", 3),
                    street(style, "crossroad_03", 3),
                    street(style, "square_01", 3),
                    street(style, "square_02", 3),
                    street(style, "turn_01", 3));
            default -> throw new IllegalArgumentException("Unsupported village street style: " + style);
        };
    }

    private static Street street(String style, String name, int weight) {
        return new Street(style, style, Identifier.withDefaultNamespace("village/" + style + "/streets/" + name), weight);
    }

    public static void bootstrapRoadPools(BootstrapContext<StructureTemplatePool> context) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);
        for (String style : ROAD_STYLES.keySet()) {
            String vanillaStyle = ROAD_STYLES.get(style);
            // Vanilla's plains streets lay a dirt path, so a village that lays this mod's path block
            // needs a copy of those street rules. The desert streets lay sandstone and need no
            // processing, and Nurn's roads are the desert ones with their sandstone traded for stone.
            var pathRules = rewiresStreets(style) ? processors.getOrThrow(TolkienProcessorLists.STONE_PATH_STREETS) : null;
            // Nurn's roads are the desert ones in dark stone, walking on this mod's path block where
            // the desert streets walk on smooth sandstone.
            var recolour = style.equals("nurn") ? processors.getOrThrow(TolkienProcessorLists.NURN_ROADS) : null;
            var processor = pathRules != null ? pathRules
                    : vanillaStyle.equals("plains") ? processors.getOrThrow(ProcessorLists.STREET_PLAINS)
                    : recolour;
            ResourceKey<StructureTemplatePool> terminators = ResourceKey.create(Registries.TEMPLATE_POOL,
                    Identifier.withDefaultNamespace("village/" + vanillaStyle + "/terminators"));
            if (pathRules != null || recolour != null) {
                // A street that runs out into open ground is closed off by a terminator piece. Those
                // do not travel through the road templates - the vanilla pool holds them as raw
                // street pieces under vanilla's own street rules - so a recoloured village, or one
                // that lays its own path, needs a terminator pool of its own as well.
                ResourceKey<StructureTemplatePool> own = terminatorPool(style);
                context.register(own, new StructureTemplatePool(pools.getOrThrow(Pools.EMPTY),
                        terminatorNames(vanillaStyle).stream()
                                .map(name -> Pair.<java.util.function.Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>of(
                                        legacy(terminatorTemplate(vanillaStyle, name), processor), 1))
                                .toList(),
                        StructureTemplatePool.Projection.TERRAIN_MATCHING));
                terminators = own;
            }
            context.register(roadPool(style), new StructureTemplatePool(pools.getOrThrow(terminators), streets(style).stream()
                    .map(street -> Pair.<java.util.function.Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>of(
                            legacy(street.template().toString(), processor), street.weight())).toList(),
                    StructureTemplatePool.Projection.TERRAIN_MATCHING));
        }
    }

    /** Where datagen writes the terminator pool of a recoloured village's roads. */
    public static ResourceKey<StructureTemplatePool> terminatorPool(String style) {
        return ResourceKey.create(Registries.TEMPLATE_POOL,
                Identifier.fromNamespaceAndPath("tolkienmobs", "village/roads/" + style + "/terminators"));
    }

    private static String terminatorTemplate(String vanillaStyle, String name) {
        return "village/" + vanillaStyle + "/terminators/" + name;
    }

    /** The terminator pieces the vanilla style's own pool holds: four for the plains, two for the desert. */
    private static List<String> terminatorNames(String vanillaStyle) {
        return vanillaStyle.equals("plains")
                ? List.of("terminator_01", "terminator_02", "terminator_03", "terminator_04")
                : List.of("terminator_01", "terminator_02");
    }

    /** {@code style} is the vanilla style the shape comes from, {@code output} the pool it fills. */
    public record Street(String style, String output, Identifier source, int weight) {
        public Identifier template() {
            return Identifier.fromNamespaceAndPath("tolkienmobs", "village/roads/" + output + "/"
                    + source.getPath().substring(source.getPath().lastIndexOf('/') + 1));
        }

        /** True when the driveway this shape carries is laid in this mod's path block. */
        public boolean laysStonePath() {
            return TolkienVillageProvider.laysStonePath(output);
        }
    }

    /**
     * One house plan a village can build: its template, how often the house pool picks it, and the
     * two coordinates written against that template - where its doorway socket goes, and where its
     * resident stands. {@code name} is also the template's own name, so a plan's coordinates can
     * never be read against another plan's NBT by mistake. A plan that fronts the village's square
     * is not in the house pool at all, and its weight is written down for the record only.
     *
     * <p>The entrance is the column of the plan's outermost door, on the side of the build that
     * faces the street: the triple house's south door, one row in from its south wall, not the
     * doors of its two wings. A plan with no door at all - the market is an open square - takes any
     * point on that front edge, so its own approach runs from the middle of its clearing.</p>
     *
     * <p>{@code orientation} is that front, as a jigsaw orientation string, for a plan whose door
     * does not face the way the village's streets run; null means the village's own front. A build
     * is drawn facing whichever way its builder put the door, and the street has to meet the door,
     * so the dwarf village's round halls face south and its working buildings west.</p>
     */
    public record House(String name, int weight, BlockPos entrance, BlockPos residentFloor, String orientation) {
        public House(String name, int weight, BlockPos entrance, BlockPos residentFloor) {
            this(name, weight, entrance, residentFloor, null);
        }
    }

    public static void bootstrapSet(BootstrapContext<StructureSet> context) {
        var structures = context.lookup(Registries.STRUCTURE);
        context.register(VILLAGES, new StructureSet(VILLAGES_BY_STYLE.stream()
                .map(village -> StructureSet.entry(structures.getOrThrow(village.structure()))).toList(),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 193764836)));
    }

    /**
     * One of the buildings a village puts on its square instead of along a street. They are the plans
     * too wide for a street to front - a church is thirty-one blocks across where every house is
     * fifteen - so the game's own street fitting drops them nearly every time they are drawn. A
     * landmark stands on one {@code side} of the square: it is turned to face the square and its door
     * opens onto it.
     *
     * <p>The town centre is placed once, before anything else, and is paved out into a square so a
     * landmark has room beside it; that gives exactly one of each landmark in a village, where a street
     * could neither place one reliably nor keep it to one.</p>
     */
    public record Landmark(String name, Direction side) {}

    /**
     * A house plan's doorway and resident coordinates are local to that plan's own NBT. {@code darkStone}
     * says the village is rebuilt from its vanilla style's blocks in dark stone, and {@code dryLand}
     * that it may only start on grass.
     */
    public record Village(ResourceKey<Structure> structure, String style, String vanillaStyle, ResourceKey<Biome> biome,
                          String residentEntity, String orientation, List<House> houses,
                          boolean darkStone, boolean dryLand, List<Landmark> landmarks) {
        public Village(ResourceKey<Structure> structure, String style, String vanillaStyle, ResourceKey<Biome> biome,
                       String residentEntity, String orientation, List<House> houses,
                       boolean darkStone, boolean dryLand) {
            this(structure, style, vanillaStyle, biome, residentEntity, orientation, houses, darkStone, dryLand, List.of());
        }

        public Village(ResourceKey<Structure> structure, String style, String vanillaStyle, ResourceKey<Biome> biome,
                       String residentEntity, String orientation, List<House> houses) {
            this(structure, style, vanillaStyle, biome, residentEntity, orientation, houses, false, false, List.of());
        }

        public Village(ResourceKey<Structure> structure, String style, String vanillaStyle, ResourceKey<Biome> biome,
                       String residentEntity, String orientation, List<House> houses, List<Landmark> landmarks) {
            this(structure, style, vanillaStyle, biome, residentEntity, orientation, houses, false, false, landmarks);
        }

        /** The plan a landmark is built from, read out of this village's own plans. */
        public House plan(Landmark landmark) {
            return houses.stream().filter(house -> house.name().equals(landmark.name())).findFirst()
                    .orElseThrow(() -> new IllegalStateException("The " + style + " village's " + landmark.name()
                            + " landmark has no plan of that name"));
        }

        /** True when a plan stands on the square rather than in the house pool. */
        public boolean isLandmark(House house) {
            return landmarks.stream().anyMatch(landmark -> landmark.name().equals(house.name()));
        }

        /** The pool a landmark's square socket draws from: one plan, so it stands exactly once. */
        public ResourceKey<StructureTemplatePool> landmarkPool(Landmark landmark) {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/landmarks/" + landmark.name()));
        }

        public Direction front() { return Direction.byName(orientation.split("_")[0]); }

        /** The way this plan faces: its own front when it has one, the village's streets otherwise. */
        public String orientation(House house) { return house.orientation() == null ? orientation : house.orientation(); }

        public Direction front(House house) { return Direction.byName(orientation(house).split("_")[0]); }

        /**
         * The porch column this plan grows by: west and north plans grow at their far side, so every
         * block moves one along the front's axis and the box gains a column there; a south-facing
         * plan keeps its origin and the new column lands beyond its own last one.
         */
        public BlockPos templateShift(House house) {
            Direction front = front(house);
            return new BlockPos(front == Direction.WEST ? 1 : 0, 0, front == Direction.NORTH ? 1 : 0);
        }

        public BlockPos preparedEntrance(House house) {
            return house.entrance().relative(front(house)).above().offset(templateShift(house));
        }

        /** Where datagen writes this plan's village-ready copy. */
        public Identifier houseTemplate(House house) {
            return Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/" + house.name());
        }

        /**
         * Where datagen writes a field this village builds; only a village that builds them has this.
         * The first keeps the plain name, so a village that builds one field names it as it always has.
         */
        public Identifier farmTemplate(int index) {
            return Identifier.fromNamespaceAndPath("tolkienmobs",
                    "village/" + style + "/" + (index == 0 ? "farm" : "farm_" + (index + 1)));
        }

        /**
         * The plan as it was drawn: a village with one plan is named for its style alone, so its
         * template keeps the name it always had; a village with several suffixes each plan's name.
         * A recoloured village builds from its vanilla style's house, so only the blocks differ.
         */
        public Identifier houseSource(House house) {
            String base = "ttmhouse_" + (darkStone ? vanillaStyle : style);
            return Identifier.fromNamespaceAndPath("tolkienmobs",
                    house.name().equals("house") ? base : base + "_" + house.name());
        }

        /** True when this village builds one of its plans under a name of its own. */
        public boolean namesItsHouses() {
            return houses.stream().anyMatch(house -> !house.name().equals("house"));
        }

        /** True when this village lays this mod's path block where a vanilla one would lay a path. */
        public boolean laysStonePath() {
            return TolkienVillageProvider.laysStonePath(style);
        }

        /**
         * True when the vanilla pieces this village builds from lay a dirt path that has to be
         * swapped for ours. The plains streets, terminator stubs and fountain town centre all
         * carry one; the desert style hands the other villages sandstone and no path at all.
         */
        public boolean rewiresStreets() {
            return laysStonePath() && vanillaStyle.equals("plains");
        }

        /** A village that recolours its blocks, or lays its own path, needs roads of its own. */
        public String roadStyle() {
            return darkStone || laysStonePath() ? style : vanillaStyle;
        }

        public ResourceKey<StructureTemplatePool> housePool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/houses"));
        }

        /** The villager-style NPC that replaces vanilla residents in this village. */
        public Identifier resident() {
            return Identifier.fromNamespaceAndPath("tolkienmobs", residentEntity);
        }

        /** Decorations drawn by this village's streets and farms. */
        public ResourceKey<StructureTemplatePool> decorPool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/decor"));
        }

        public ResourceKey<StructureTemplatePool> villagerPool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    Identifier.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/villagers"));
        }

        public ResourceKey<StructureTemplatePool> startPool() {
            // Keep the original structure/pool IDs so saved starts remain loadable.
            return ResourceKey.create(Registries.TEMPLATE_POOL, structure.identifier());
        }

        public TagKey<Biome> biomeTag() {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(
                    structure.identifier().getNamespace(), "has_structure/" + structure.identifier().getPath()));
        }
    }
}
