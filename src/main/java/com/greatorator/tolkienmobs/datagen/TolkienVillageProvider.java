package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.greatorator.tolkienmobs.world.components.structure.VillageStructure;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.data.worldgen.placement.VillagePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Vanilla village assembly, with Tolkien houses selected through per-structure pool aliases. */
public final class TolkienVillageProvider {
    public static final int SIZE = 6;
    public static final int MAX_DISTANCE = 112;
    public static final ResourceKey<StructureSet> VILLAGES = ResourceKey.create(Registries.STRUCTURE_SET,
            ResourceLocation.fromNamespaceAndPath("tolkienmobs", "villages"));
    public static final List<Village> VILLAGES_BY_STYLE = List.of(
            new Village(TolkienStructures.HOUSE_DESERT, "desert", "desert", TolkienBiomes.HARADWAITH, "entityttmsouthron_npc", new BlockPos(14, 0, 7), "east_up", new BlockPos(11, 4, 4)),
            // Nurn is farmed by the same Southrons, in the same desert houses, rebuilt in dark stone.
            new Village(TolkienStructures.HOUSE_NURN, "nurn", "desert", TolkienBiomes.NURN, "entityttmsouthron_npc", new BlockPos(14, 0, 7), "east_up", new BlockPos(11, 4, 4), true, true),
            new Village(TolkienStructures.HOUSE_DWARF, "dwarf", "plains", TolkienBiomes.IRON_HILLS, "entityttmdwarf", new BlockPos(0, 0, 5), "west_up", new BlockPos(6, 0, 5)),
            new Village(TolkienStructures.HOUSE_ELVEN, "elven", "plains", TolkienBiomes.LORINAND, "entityttmelves", new BlockPos(0, 0, 5), "west_up", new BlockPos(4, 8, 3)),
            new Village(TolkienStructures.HOBBIT_HOUSE, "hobbit", "plains", TolkienBiomes.SHIRE, "entityttmhobbit", new BlockPos(27, 0, 14), "east_up", new BlockPos(11, 0, 20)),
            new Village(TolkienStructures.HOUSE_HUMAN, "human", "plains", TolkienBiomes.GLADDEN, "entityttmhuman", new BlockPos(6, 0, 12), "south_up", new BlockPos(5, 5, 8)),
            new Village(TolkienStructures.HOUSE_ORC, "orc", "desert", TolkienBiomes.MORDOR, "entityttmorc", new BlockPos(7, 0, 14), "south_up", new BlockPos(7, 0, 2))
    );
    /** The vanilla villager templates datagen copies for every village; weights match vanilla. */
    public static final List<String> VILLAGERS = List.of("nitwit", "baby", "unemployed");

    private TolkienVillageProvider() {}

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
                        ResourceLocation.withDefaultNamespace("village/" + village.vanillaStyle() + "/houses")), village.housePool()),
                PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                        ResourceLocation.withDefaultNamespace("village/" + village.vanillaStyle() + "/streets")), roadPool(village.roadStyle())),
                // Houses, town centers, farms and decorations ask for vanilla villagers through this
                // pool; send every socket to this village's own NPC instead.
                PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                        ResourceLocation.withDefaultNamespace("village/" + village.vanillaStyle() + "/villagers")), village.villagerPool())));
        if (village.darkStone()) {
            // The vanilla decorations are sandstone lamps and cacti. Ours are the same lamps in dark
            // stone, because a cactus garden is not what a farmed lowland looks like.
            aliases.add(PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.withDefaultNamespace("village/" + village.vanillaStyle() + "/decor")), village.decorPool()));
        }
        // A village that must stand on grass is placed by our own structure, which reads the height
        // maps and the surface noise before it agrees to a site.
        return village.dryLand()
                ? new VillageStructure(settings, startPool, Optional.empty(), SIZE,
                        ConstantHeight.of(VerticalAnchor.absolute(0)), true, Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        MAX_DISTANCE, aliases, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS)
                : new JigsawStructure(settings, startPool, Optional.empty(), SIZE,
                        ConstantHeight.of(VerticalAnchor.absolute(0)), true, Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                        MAX_DISTANCE, aliases, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
    }

    public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context, Village village) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);
        var placed = context.lookup(Registries.PLACED_FEATURE);
        var empty = pools.getOrThrow(Pools.EMPTY);
        String center = village.vanillaStyle().equals("desert")
                ? "village/desert/town_centers/desert_meeting_point_1"
                : "village/plains/town_centers/plains_fountain_01";
        var stone = village.darkStone() ? processors.getOrThrow(TolkienProcessorLists.NURN_DARK_STONE) : null;
        // A water feature and bell in every start; streets and decorations are vanilla.
        context.register(village.startPool(), new StructureTemplatePool(empty,
                List.of(Pair.of(legacy(center, stone), 1)), StructureTemplatePool.Projection.RIGID));
        String farm = "village/" + village.vanillaStyle() + "/houses/" + village.vanillaStyle()
                + (village.vanillaStyle().equals("desert") ? "_farm_" : "_small_farm_");
        var farmProcessors = processors.getOrThrow(village.darkStone() ? TolkienProcessorLists.NURN_FARMS
                : village.vanillaStyle().equals("desert") ? ProcessorLists.FARM_DESERT : ProcessorLists.FARM_PLAINS);
        context.register(village.housePool(), new StructureTemplatePool(empty,
                List.of(Pair.of(single(village.houseTemplate().toString(), stone), 6),
                        Pair.of(StructurePoolElement.legacy(farm + "1", farmProcessors), 1),
                        Pair.of(StructurePoolElement.legacy(farm + "2", farmProcessors), 1)),
                StructureTemplatePool.Projection.RIGID));
        if (village.darkStone()) {
            context.register(village.decorPool(), new StructureTemplatePool(empty,
                    List.of(Pair.of(legacy("village/" + village.vanillaStyle() + "/desert_lamp_1", stone), 10),
                            Pair.of(StructurePoolElement.feature(placed.getOrThrow(VillagePlacements.PILE_HAY_VILLAGE)), 4),
                            Pair.of(StructurePoolElement.empty(), 10)),
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
    public static ResourceLocation villagerTemplate(Village village, String name) {
        return ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + village.style() + "/villagers/" + name);
    }

    public static ResourceKey<StructureTemplatePool> roadPool(String style) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/roads/" + style));
    }

    /**
     * The road pools datagen builds, keyed by their own style: each one is filled with the vanilla
     * style's street shapes. A village that recolours its blocks needs roads of its own, because a
     * road pool is shared by every village that uses the style.
     */
    public static final Map<String, String> ROAD_STYLES = Map.of(
            "desert", "desert",
            "plains", "plains",
            "nurn", "desert");

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
        return new Street(style, style, ResourceLocation.withDefaultNamespace("village/" + style + "/streets/" + name), weight);
    }

    public static void bootstrapRoadPools(BootstrapContext<StructureTemplatePool> context) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);
        for (String style : ROAD_STYLES.keySet()) {
            String vanillaStyle = ROAD_STYLES.get(style);
            // Plains streets are levelled, the vanilla desert streets need no processing, and Nurn's
            // roads are the desert ones with their sandstone traded for dark stone.
            var recolour = style.equals("nurn") ? processors.getOrThrow(TolkienProcessorLists.NURN_DARK_STONE) : null;
            var processor = style.equals("plains") ? processors.getOrThrow(ProcessorLists.STREET_PLAINS) : recolour;
            ResourceKey<StructureTemplatePool> terminators = ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.withDefaultNamespace("village/" + vanillaStyle + "/terminators"));
            if (recolour != null) {
                // A street that runs out into open ground is closed off by a terminator piece, and the
                // vanilla pool holds those as raw smooth sandstone. They do not travel through the road
                // templates, so a recoloured village needs a terminator pool of its own as well.
                ResourceKey<StructureTemplatePool> own = terminatorPool(style);
                context.register(own, new StructureTemplatePool(pools.getOrThrow(Pools.EMPTY),
                        List.of(Pair.of(legacy(terminatorTemplate(vanillaStyle, "terminator_01"), recolour), 1),
                                Pair.of(legacy(terminatorTemplate(vanillaStyle, "terminator_02"), recolour), 1)),
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
                ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/roads/" + style + "/terminators"));
    }

    private static String terminatorTemplate(String vanillaStyle, String name) {
        return "village/" + vanillaStyle + "/terminators/" + name;
    }

    /** {@code style} is the vanilla style the shape comes from, {@code output} the pool it fills. */
    public record Street(String style, String output, ResourceLocation source, int weight) {
        public ResourceLocation template() {
            return ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/roads/" + output + "/"
                    + source.getPath().substring(source.getPath().lastIndexOf('/') + 1));
        }
    }

    public static void bootstrapSet(BootstrapContext<StructureSet> context) {
        var structures = context.lookup(Registries.STRUCTURE);
        context.register(VILLAGES, new StructureSet(VILLAGES_BY_STYLE.stream()
                .map(village -> StructureSet.entry(structures.getOrThrow(village.structure()))).toList(),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 193764836)));
    }

    /**
     * Entrance and resident coordinates are local to the original house NBT. {@code darkStone} says
     * the village is rebuilt from its vanilla style's blocks in dark stone, and {@code dryLand} that
     * it may only start on grass.
     */
    public record Village(ResourceKey<Structure> structure, String style, String vanillaStyle, ResourceKey<Biome> biome,
                          String residentEntity, BlockPos entrance, String orientation, BlockPos residentFloor,
                          boolean darkStone, boolean dryLand) {
        public Village(ResourceKey<Structure> structure, String style, String vanillaStyle, ResourceKey<Biome> biome,
                       String residentEntity, BlockPos entrance, String orientation, BlockPos residentFloor) {
            this(structure, style, vanillaStyle, biome, residentEntity, entrance, orientation, residentFloor, false, false);
        }

        public Direction front() { return Direction.byName(orientation.split("_")[0]); }

        public BlockPos templateShift() {
            return new BlockPos(front() == Direction.WEST ? 1 : 0, 0, front() == Direction.NORTH ? 1 : 0);
        }

        public BlockPos preparedEntrance() { return entrance.relative(front()).above().offset(templateShift()); }

        public ResourceLocation houseTemplate() {
            return ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/house");
        }

        /** A recoloured village builds from its vanilla style's house; only the blocks differ. */
        public ResourceLocation houseSource() {
            return ResourceLocation.fromNamespaceAndPath("tolkienmobs", "ttmhouse_" + (darkStone ? vanillaStyle : style));
        }

        /** A village that recolours its blocks needs roads of its own, or it would redraw its neighbours'. */
        public String roadStyle() {
            return darkStone ? style : vanillaStyle;
        }

        public ResourceKey<StructureTemplatePool> housePool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/houses"));
        }

        /** The villager-style NPC that replaces vanilla residents in this village. */
        public ResourceLocation resident() {
            return ResourceLocation.fromNamespaceAndPath("tolkienmobs", residentEntity);
        }

        /** Decorations drawn by this village's streets and farms. */
        public ResourceKey<StructureTemplatePool> decorPool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/decor"));
        }

        public ResourceKey<StructureTemplatePool> villagerPool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/villagers"));
        }

        public ResourceKey<StructureTemplatePool> startPool() {
            // Keep the original structure/pool IDs so saved starts remain loadable.
            return ResourceKey.create(Registries.TEMPLATE_POOL, structure.location());
        }

        public TagKey<Biome> biomeTag() {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(
                    structure.location().getNamespace(), "has_structure/" + structure.location().getPath()));
        }
    }
}
