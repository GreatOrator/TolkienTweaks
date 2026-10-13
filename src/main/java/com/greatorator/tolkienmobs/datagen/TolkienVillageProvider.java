package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;

import java.util.List;
import java.util.Optional;

/** Vanilla village assembly, with Tolkien houses selected through per-structure pool aliases. */
public final class TolkienVillageProvider {
    public static final int SIZE = 6;
    public static final int MAX_DISTANCE = 112;
    public static final ResourceKey<StructureSet> VILLAGES = ResourceKey.create(Registries.STRUCTURE_SET,
            ResourceLocation.fromNamespaceAndPath("tolkienmobs", "villages"));
    public static final List<Village> VILLAGES_BY_STYLE = List.of(
            new Village(TolkienStructures.HOUSE_DESERT, "desert", "desert", new BlockPos(14, 0, 7), "east_up", new BlockPos(11, 4, 4)),
            new Village(TolkienStructures.HOUSE_DWARF, "dwarf", "plains", new BlockPos(0, 0, 5), "west_up", new BlockPos(6, 0, 5)),
            new Village(TolkienStructures.HOUSE_ELVEN, "elven", "plains", new BlockPos(0, 0, 5), "west_up", new BlockPos(4, 8, 3)),
            new Village(TolkienStructures.HOBBIT_HOUSE, "hobbit", "plains", new BlockPos(27, 0, 14), "east_up", new BlockPos(11, 0, 20)),
            new Village(TolkienStructures.HOUSE_HUMAN, "human", "plains", new BlockPos(6, 0, 12), "south_up", new BlockPos(5, 5, 8))
    );

    private TolkienVillageProvider() {}

    public static Optional<Village> village(ResourceKey<Structure> key) {
        return VILLAGES_BY_STYLE.stream().filter(village -> village.structure().equals(key)).findFirst();
    }

    public static JigsawStructure create(Structure.StructureSettings settings, Village village,
                                        HolderGetter<StructureTemplatePool> pools) {
        // Alias resolution is local to this village; vanilla village pools stay intact.
        return new JigsawStructure(settings, pools.getOrThrow(village.startPool()), Optional.empty(), SIZE,
                ConstantHeight.of(VerticalAnchor.absolute(0)), true, Optional.of(Heightmap.Types.WORLD_SURFACE_WG),
                MAX_DISTANCE, List.of(PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                        ResourceLocation.withDefaultNamespace("village/" + village.vanillaStyle() + "/houses")), village.housePool()),
                        PoolAliasBinding.direct(ResourceKey.create(Registries.TEMPLATE_POOL,
                                ResourceLocation.withDefaultNamespace("village/" + village.vanillaStyle() + "/streets")), roadPool(village.vanillaStyle()))),
                JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
    }

    public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context, Village village) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);
        var empty = pools.getOrThrow(Pools.EMPTY);
        String center = village.vanillaStyle().equals("desert")
                ? "village/desert/town_centers/desert_meeting_point_1"
                : "village/plains/town_centers/plains_fountain_01";
        // A water feature and bell in every start; streets, decorations and villagers are vanilla.
        context.register(village.startPool(), new StructureTemplatePool(empty,
                List.of(Pair.of(StructurePoolElement.legacy(center), 1)), StructureTemplatePool.Projection.RIGID));
        String farm = "village/" + village.vanillaStyle() + "/houses/" + village.vanillaStyle()
                + (village.vanillaStyle().equals("desert") ? "_farm_" : "_small_farm_");
        var farmProcessors = processors.getOrThrow(village.vanillaStyle().equals("desert")
                ? ProcessorLists.FARM_DESERT : ProcessorLists.FARM_PLAINS);
        context.register(village.housePool(), new StructureTemplatePool(empty,
                List.of(Pair.of(StructurePoolElement.single(village.houseTemplate().toString()), 6),
                        Pair.of(StructurePoolElement.legacy(farm + "1", farmProcessors), 1),
                        Pair.of(StructurePoolElement.legacy(farm + "2", farmProcessors), 1)),
                StructureTemplatePool.Projection.RIGID));
    }

    public static ResourceKey<StructureTemplatePool> roadPool(String style) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/roads/" + style));
    }

    /** Keep vanilla street shapes and weights; move their house sockets outside reserved road footprints. */
    public static List<Street> streets(String style) {
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
        return new Street(style, ResourceLocation.withDefaultNamespace("village/" + style + "/streets/" + name), weight);
    }

    public static void bootstrapRoadPools(BootstrapContext<StructureTemplatePool> context) {
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        var processors = context.lookup(Registries.PROCESSOR_LIST);
        for (String style : List.of("plains", "desert")) {
            context.register(roadPool(style), new StructureTemplatePool(pools.getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.withDefaultNamespace("village/" + style + "/terminators"))), streets(style).stream()
                    .map(street -> Pair.<java.util.function.Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>of(style.equals("plains")
                            ? StructurePoolElement.legacy(street.template().toString(), processors.getOrThrow(ProcessorLists.STREET_PLAINS))
                            : StructurePoolElement.legacy(street.template().toString()), street.weight())).toList(),
                    StructureTemplatePool.Projection.TERRAIN_MATCHING));
        }
    }

    public record Street(String style, ResourceLocation source, int weight) {
        public ResourceLocation template() {
            return ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/roads/" + style + "/"
                    + source.getPath().substring(source.getPath().lastIndexOf('/') + 1));
        }
    }

    public static void bootstrapSet(BootstrapContext<StructureSet> context) {
        var structures = context.lookup(Registries.STRUCTURE);
        context.register(VILLAGES, new StructureSet(VILLAGES_BY_STYLE.stream()
                .map(village -> StructureSet.entry(structures.getOrThrow(village.structure()))).toList(),
                new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 193764836)));
    }

    /** Entrance and resident coordinates are local to the original house NBT. */
    public record Village(ResourceKey<Structure> structure, String style, String vanillaStyle,
                          BlockPos entrance, String orientation, BlockPos residentFloor) {
        public Direction front() { return Direction.byName(orientation.split("_")[0]); }

        public BlockPos templateShift() {
            return new BlockPos(front() == Direction.WEST ? 1 : 0, 0, front() == Direction.NORTH ? 1 : 0);
        }

        public BlockPos preparedEntrance() { return entrance.relative(front()).above().offset(templateShift()); }

        public ResourceLocation houseTemplate() {
            return ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/house");
        }

        public ResourceKey<StructureTemplatePool> housePool() {
            return ResourceKey.create(Registries.TEMPLATE_POOL,
                    ResourceLocation.fromNamespaceAndPath("tolkienmobs", "village/" + style + "/houses"));
        }

        public ResourceKey<StructureTemplatePool> startPool() {
            // Keep the original structure/pool IDs so saved starts remain loadable.
            return ResourceKey.create(Registries.TEMPLATE_POOL, structure.location());
        }
    }
}
