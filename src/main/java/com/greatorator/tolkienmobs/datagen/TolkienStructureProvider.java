package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.greatorator.tolkienmobs.world.components.structure.SurfaceStructures;
import com.greatorator.tolkienmobs.world.components.structure.TemplateLayoutStructure;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Registry bootstrap callbacks used by TolkienDataRegistryProvider and the biome tag provider. */
public final class TolkienStructureProvider {
    // Add one definition per structure. Optional branches contain only side NBTs. Distances are chunks; heightOffset is blocks.
    // The structure, pool, set and biome tag names are derived from the structure key.
    public static final List<Definition> STRUCTURES = List.of(
            new Definition(TolkienStructures.HOBBIT_HOUSE, "tolkienmobs:ttmhouse_hobbit",
                    List.of(TolkienBiomes.SHIRE), 24, 8, 193764821, 0),
            new Definition(TolkienStructures.SHIRE_RUIN, "tolkienmobs:ttmruin_small",
                    List.of(TolkienBiomes.DAGORLAD), 24, 8, 193764821, 0),
            new Definition(TolkienStructures.CAVE, "tolkienmobs:cave",
                    List.of(TolkienBiomes.MIRKWOOD), 32, 8, 193764822, 0,
                    Optional.of(new Branches("tolkienmobs:cave_side", 4, 3, 1)), true),
            new Definition(TolkienStructures.MAZE, "tolkienmobs:maze",
                    List.of(TolkienBiomes.HITHAEGLIR), 32, 8, 193764823, 0,
                    Optional.of(new Branches("tolkienmobs:maze_side", 4, 3, 1)), true),
            site(TolkienStructures.BARROW, TolkienBiomes.BARROW_DOWNS, 16, 6, 193764824, Layout.single()),
            site(TolkienStructures.DARK_TOWER, TolkienBiomes.MORDOR, 16, 6, 193764825,
                    new Layout(1, 1, Optional.empty(), Optional.of(TolkienStructures.WARGPIT))),
            site(TolkienStructures.GOLLUM_CAVE, TolkienBiomes.HITHAEGLIR, 16, 6, 193764826, Layout.top(-32)),
            site(TolkienStructures.HOUSE_DESERT, TolkienBiomes.HARADWAITH, 24, 16, 193764827, Layout.houses()),
            site(TolkienStructures.HOUSE_DWARF, TolkienBiomes.IRON_HILLS, 24, 16, 193764828, Layout.houses()),
            site(TolkienStructures.HOUSE_ELVEN, TolkienBiomes.LORINAND, 24, 16, 193764829, Layout.houses()),
            site(TolkienStructures.HOUSE_HUMAN, TolkienBiomes.GLADDEN, 24, 16, 193764830, Layout.houses()),
            site(TolkienStructures.INN_DESERT, TolkienBiomes.HARADWAITH, 12, 6, 193764831, Layout.single()),
            site(TolkienStructures.RUIN_LARGE, TolkienBiomes.DAGORLAD, 4, 3, 193764832, Layout.single()),
            site(TolkienStructures.RUIN_SMALL, TolkienBiomes.DAGORLAD, 4, 3, 193764832, Layout.single()),
            site(TolkienStructures.SPIDERTREE, TolkienBiomes.MIRKWOOD_SPOOKY, 12, 6, 193764833, Layout.single()),
            site(TolkienStructures.SWAMP_HAG_HUT, TolkienBiomes.MARSHES, 12, 6, 193764834, Layout.single()),
            site(TolkienStructures.WARGPIT, TolkienBiomes.MORDOR, 16, 6, 193764835, Layout.top(2))
    );

    private TolkienStructureProvider() {}

    private static Definition site(ResourceKey<Structure> key, ResourceKey<Biome> biome,
                                   int spacing, int separation, int salt, Layout layout) {
        return new Definition(key, key.location().toString(), List.of(biome), spacing, separation, salt,
                0, Optional.empty(), false, Optional.of(layout));
    }

    public static void bootstrapStructures(BootstrapContext<Structure> context) {
        var biomes = context.lookup(Registries.BIOME);
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        for (Definition definition : STRUCTURES) {
            boolean below = definition.underground() || definition.layout().flatMap(Layout::topOffset).orElse(0) < 0;
            var settings = new Structure.StructureSettings(biomes.getOrThrow(definition.biomeTag()), Map.of(),
                    below ? GenerationStep.Decoration.UNDERGROUND_STRUCTURES : GenerationStep.Decoration.SURFACE_STRUCTURES,
                    (below || definition.layout().flatMap(Layout::topOffset).isPresent()
                            || definition.layout().flatMap(Layout::companion).isPresent()) ? TerrainAdjustment.NONE : TerrainAdjustment.BEARD_THIN);
            var pool = pools.getOrThrow(definition.poolKey());
            int depth = definition.branches().map(Branches::depth).orElse(1);
            if (definition.layout().isPresent()) {
                var layout = definition.layout().get();
                context.register(definition.key(), new TemplateLayoutStructure(settings, pool, layout.minCount(),
                        layout.maxCount(), layout.topOffset(), layout.companion().map(key ->
                        pools.getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, key.location())))));
                continue;
            }
            context.register(definition.key(), definition.underground()
                    ? SurfaceStructures.underground(settings, pool, definition.heightOffset(), depth)
                    : new SurfaceStructures(settings, pool, definition.heightOffset(), depth));
        }
    }

    public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {
        var empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
        for (Definition definition : STRUCTURES) {
            context.register(definition.poolKey(), new StructureTemplatePool(empty,
                    List.of(Pair.of(StructurePoolElement.single(definition.template()), 1)),
                    StructureTemplatePool.Projection.RIGID));
            definition.branches().ifPresent(branches -> {
                // Root connectors always try a side piece. Only side pieces are
                // eligible here, so the main template can never be repeated.
                context.register(definition.sidePoolKey(), new StructureTemplatePool(empty,
                        List.of(Pair.of(StructurePoolElement.single(branches.template()), 1)),
                        StructureTemplatePool.Projection.RIGID));
                context.register(definition.branchPoolKey(), new StructureTemplatePool(empty,
                        List.of(Pair.of(StructurePoolElement.single(branches.template()), branches.pieceWeight()),
                                Pair.of(StructurePoolElement.empty(), branches.stopWeight())),
                        StructureTemplatePool.Projection.RIGID));
            });
        }
    }

    public static void bootstrapSets(BootstrapContext<StructureSet> context) {
        var structures = context.lookup(Registries.STRUCTURE);
        for (Definition definition : STRUCTURES) {
            // Old Shire ID stays loadable for saved chunks; warg pits are tower companions.
            // Ruins share one grid so the two sizes cannot generate too close together.
            if (List.of(TolkienStructures.SHIRE_RUIN, TolkienStructures.WARGPIT,
                    TolkienStructures.RUIN_LARGE, TolkienStructures.RUIN_SMALL).contains(definition.key())) continue;
            context.register(definition.setKey(), new StructureSet(structures.getOrThrow(definition.key()),
                    new RandomSpreadStructurePlacement(definition.spacing(), definition.separation(),
                            RandomSpreadType.LINEAR, definition.salt())));
        }
        context.register(ResourceKey.create(Registries.STRUCTURE_SET,
                        ResourceLocation.fromNamespaceAndPath("tolkienmobs", "dagorlad_ruins")),
                new StructureSet(List.of(StructureSet.entry(structures.getOrThrow(TolkienStructures.RUIN_LARGE)),
                        StructureSet.entry(structures.getOrThrow(TolkienStructures.RUIN_SMALL))),
                        new RandomSpreadStructurePlacement(4, 3, RandomSpreadType.LINEAR, 193764832)));
    }

    public record Layout(int minCount, int maxCount, Optional<Integer> topOffset,
                         Optional<ResourceKey<Structure>> companion) {
        public static Layout single() { return new Layout(1, 1, Optional.empty(), Optional.empty()); }
        public static Layout houses() { return new Layout(3, 5, Optional.empty(), Optional.empty()); }
        public static Layout top(int offset) { return new Layout(1, 1, Optional.of(offset), Optional.empty()); }
    }

    /** depth bounds expansion; weights choose between another side piece and stopping. */
    public record Branches(String template, int depth, int pieceWeight, int stopWeight) {
        public Branches {
            ResourceLocation.parse(template);
            if (depth < 1 || depth > 30) throw new IllegalArgumentException("Branch depth must be 1..30");
            if (pieceWeight < 1 || pieceWeight > 150 || stopWeight < 1 || stopWeight > 150) {
                throw new IllegalArgumentException("Pool weights must be 1..150");
            }
        }
    }

    public record Definition(ResourceKey<Structure> key, String template, List<ResourceKey<Biome>> biomes,
                             int spacing, int separation, int salt, int heightOffset, Optional<Branches> branches, boolean underground, Optional<Layout> layout) {
        public Definition(ResourceKey<Structure> key, String template, List<ResourceKey<Biome>> biomes,
                          int spacing, int separation, int salt, int heightOffset, Optional<Branches> branches, boolean underground) {
            this(key, template, biomes, spacing, separation, salt, heightOffset, branches, underground, Optional.empty());
        }

        public Definition(ResourceKey<Structure> key, String template, List<ResourceKey<Biome>> biomes,
                          int spacing, int separation, int salt, int heightOffset) {
            this(key, template, biomes, spacing, separation, salt, heightOffset, Optional.empty(), false, Optional.empty());
        }

        public Definition {
            ResourceLocation.parse(template);
            biomes = List.copyOf(biomes);
            if (biomes.isEmpty()) throw new IllegalArgumentException("A structure needs at least one biome");
            if (spacing <= separation || separation < 0 || spacing > 4096) {
                throw new IllegalArgumentException("Require 0 <= separation < spacing <= 4096");
            }
            if (salt < 0) throw new IllegalArgumentException("Structure salt must be nonnegative");
        }

        public ResourceKey<StructureTemplatePool> poolKey() {
            return ResourceKey.create(Registries.TEMPLATE_POOL, key.location());
        }

        public ResourceKey<StructureTemplatePool> sidePoolKey() {
            return ResourceKey.create(Registries.TEMPLATE_POOL, key.location().withSuffix("_pool/side_pool"));
        }

        public ResourceKey<StructureTemplatePool> branchPoolKey() {
            return ResourceKey.create(Registries.TEMPLATE_POOL, key.location().withSuffix("_pool/branches"));
        }

        public ResourceKey<StructureSet> setKey() {
            return ResourceKey.create(Registries.STRUCTURE_SET, key.location());
        }

        public TagKey<Biome> biomeTag() {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(
                    key.location().getNamespace(), "has_structure/" + key.location().getPath()));
        }
    }
}
