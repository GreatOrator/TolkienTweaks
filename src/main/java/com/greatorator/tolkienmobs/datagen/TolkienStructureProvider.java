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
import net.minecraft.resources.Identifier;
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
    /**
     * Where a traveller's tent may stand. These are the mod's wilds - the woods, the downs, the
     * marches and the passes a road crosses - and not the countries that have towns of their own,
     * so a tent is met on the way somewhere rather than in someone's fields.
     */
    public static final List<ResourceKey<Biome>> WILD_CAMP_BIOMES = List.of(
            TolkienBiomes.WILD_WOOD, TolkienBiomes.MIRKWOOD, TolkienBiomes.MIRKWOOD_SPOOKY,
            TolkienBiomes.FANGORN, TolkienBiomes.OLD_FOREST, TolkienBiomes.BARROW_DOWNS,
            TolkienBiomes.DAGORLAD, TolkienBiomes.MARSHES, TolkienBiomes.FIRIEN,
            TolkienBiomes.HITHAEGLIR, TolkienBiomes.DUNLAND, TolkienBiomes.ERIADOR);

    /**
     * Each alliance's countries, and the one table both the halls and the tags are written from: these
     * lists name the biomes a hall may be raised in, and the biome tag provider writes the very same
     * lists into {@code #tolkienmobs:factions/<alliance>}, which is what a guard patrol reads.
     */
    public static final List<ResourceKey<Biome>> WEST_COUNTRIES = List.of(
            TolkienBiomes.SHIRE, TolkienBiomes.ERIADOR, TolkienBiomes.LINDON, TolkienBiomes.GONDOR,
            TolkienBiomes.ROHAN, TolkienBiomes.GLADDEN, TolkienBiomes.LORINAND, TolkienBiomes.FIRIEN,
            TolkienBiomes.IRON_HILLS, TolkienBiomes.BLUE_MOUNTAINS);
    public static final List<ResourceKey<Biome>> EAST_COUNTRIES = List.of(
            TolkienBiomes.MORDOR, TolkienBiomes.ASH_MOUNTAINS, TolkienBiomes.DAGORLAD,
            TolkienBiomes.NURN, TolkienBiomes.MIRKWOOD_SPOOKY, TolkienBiomes.HITHAEGLIR,
            TolkienBiomes.MARSHES);
    public static final List<ResourceKey<Biome>> HARAD_COUNTRIES = List.of(TolkienBiomes.HARADWAITH);

    // Add one definition per structure. Optional branches contain only side NBTs. Distances are chunks; heightOffset is blocks.
    // The structure, pool, set and biome tag names are derived from the structure key.
    public static final List<Definition> STRUCTURES = List.of(
            // One hall for each alliance, standing in that alliance's own country. The three templates
            // are cut from the one build: the acacia trim becomes the alliance's own stair, and the
            // warden behind the counter is that alliance's folk.
            new Definition(TolkienStructures.FACTION_HALL_WEST, "tolkienmobs:faction_hall_west",
                    WEST_COUNTRIES, 48, 16, 193764850, 0, Optional.empty(), false, Optional.of(Layout.single())),
            new Definition(TolkienStructures.FACTION_HALL_EAST, "tolkienmobs:faction_hall_east",
                    EAST_COUNTRIES, 48, 16, 193764851, 0, Optional.empty(), false, Optional.of(Layout.single())),
            new Definition(TolkienStructures.FACTION_HALL_HARAD, "tolkienmobs:faction_hall_harad",
                    HARAD_COUNTRIES, 48, 16, 193764852, 0, Optional.empty(), false, Optional.of(Layout.single())),
            new Definition(TolkienStructures.HOBBIT_HOUSE, "tolkienmobs:ttmhouse_hobbit",
                    List.of(TolkienBiomes.SHIRE), 32, 8, 193764821, 0),
            // A tent in the wilds a traveller crosses rather than in a country anyone has settled:
            // the two Mirkwoods, the Wild Wood, Fangorn and the Old Forest, the Barrow Downs, the
            // Dagorlad and the Marshes, Firien, the Misty Mountains' pass, Dunland and Eriador. One
            // piece, dropped straight onto the ground it was found on.
            new Definition(TolkienStructures.WILD_TENT, "tolkienmobs:wild_tent", WILD_CAMP_BIOMES,
                    28, 8, 193764840, 0, Optional.empty(), false, Optional.of(Layout.single())),
            new Definition(TolkienStructures.SHIRE_RUIN, "tolkienmobs:ttmruin_small",
                    List.of(TolkienBiomes.DAGORLAD), 24, 8, 193764821, 0),
            new Definition(TolkienStructures.CAVE, "tolkienmobs:cave",
                    List.of(TolkienBiomes.MIRKWOOD), 32, 8, 193764822, 0,
                    Optional.of(new Branches("tolkienmobs:cave_side", 4, 3, 1)), true),
            new Definition(TolkienStructures.MAZE, "tolkienmobs:maze",
                    List.of(TolkienBiomes.HITHAEGLIR), 32, 8, 193764823, 0,
                    Optional.of(new Branches("tolkienmobs:maze_side", 4, 3, 1)), true),
            // Barrows come as one ring of up to five mounds a few blocks apart, close enough to read
            // as a graveyard field, and one cluster attempt every ten chunks.
            site(TolkienStructures.BARROW, TolkienBiomes.BARROW_DOWNS, 10, 4, 193764824, Layout.barrows()),
            site(TolkienStructures.DARK_TOWER, TolkienBiomes.MORDOR, 16, 6, 193764825,
                    new Layout(1, 1, Optional.empty(), Optional.of(TolkienStructures.WARGPIT))),
            site(TolkienStructures.GOLLUM_CAVE, TolkienBiomes.HITHAEGLIR, 16, 6, 193764826, Layout.top(-32)),
            new Definition(TolkienStructures.HOUSE_DESERT, "tolkienmobs:ttmhouse_desert",
                    List.of(TolkienBiomes.HARADWAITH), 32, 8, 193764827, 0),
            // Nurn's village is built from the desert house, in dark stone, by its own processors.
            new Definition(TolkienStructures.HOUSE_NURN, "tolkienmobs:ttmhouse_nurn",
                    List.of(TolkienBiomes.NURN), 32, 8, 193764838, 0),
            // Dwarves are as much the Blue Mountains' people as the Iron Hills'.
            new Definition(TolkienStructures.HOUSE_DWARF, "tolkienmobs:ttmhouse_dwarf",
                    List.of(TolkienBiomes.IRON_HILLS, TolkienBiomes.BLUE_MOUNTAINS), 32, 8, 193764828, 0),
            new Definition(TolkienStructures.HOUSE_ELVEN, "tolkienmobs:ttmhouse_elven",
                    List.of(TolkienBiomes.LORINAND), 32, 8, 193764829, 0),
            new Definition(TolkienStructures.HOUSE_HUMAN, "tolkienmobs:ttmhouse_human",
                    List.of(TolkienBiomes.GLADDEN), 32, 8, 193764830, 0),
            new Definition(TolkienStructures.HOUSE_ORC, "tolkienmobs:ttmhouse_orc",
                    List.of(TolkienBiomes.MORDOR), 32, 8, 193764837, 0),
            new Definition(TolkienStructures.HOUSE_FROZEN, "tolkienmobs:ttmhouse_frozen",
                    List.of(TolkienBiomes.FOROCHEL), 32, 8, 193764839, 0),
            site(TolkienStructures.INN_DESERT, TolkienBiomes.HARADWAITH, 12, 6, 193764831, Layout.single()),
            site(TolkienStructures.RUIN_LARGE, TolkienBiomes.DAGORLAD, 4, 3, 193764832, Layout.single()),
            site(TolkienStructures.RUIN_SMALL, TolkienBiomes.DAGORLAD, 4, 3, 193764832, Layout.single()),
            site(TolkienStructures.SPIDERTREE, TolkienBiomes.MIRKWOOD_SPOOKY, 12, 6, 193764833, Layout.single()),
            site(TolkienStructures.SWAMP_HAG_HUT, TolkienBiomes.MARSHES, 12, 6, 193764834, Layout.single()),
            site(TolkienStructures.WARGPIT, TolkienBiomes.MORDOR, 16, 6, 193764835, Layout.top(2)),
            // The campaign's ground. The spacing and separation are never used, because neither is in a
            // structure set: they are registered so that a world builder can put one down with
            // /place structure, and so that the campaign can build them where its story needs them.
            new Definition(TolkienStructures.BOSS_CASTLE, "tolkienmobs:boss_castle",
                    List.of(TolkienBiomes.MORDOR), 48, 12, 193764860, 0, Optional.empty(), false,
                    Optional.of(Layout.single())),
            new Definition(TolkienStructures.BOSS_ARENA, "tolkienmobs:boss_arena",
                    List.of(TolkienBiomes.MORDOR), 48, 12, 193764861, 0, Optional.empty(), false,
                    Optional.of(Layout.single()))
    );

    private TolkienStructureProvider() {}

    private static Definition site(ResourceKey<Structure> key, ResourceKey<Biome> biome,
                                   int spacing, int separation, int salt, Layout layout) {
        return new Definition(key, key.identifier().toString(), List.of(biome), spacing, separation, salt,
                0, Optional.empty(), false, Optional.of(layout));
    }

    public static void bootstrapStructures(BootstrapContext<Structure> context) {
        var biomes = context.lookup(Registries.BIOME);
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        for (Definition definition : STRUCTURES) {
            boolean below = definition.underground() || definition.layout().flatMap(Layout::topOffset).orElse(0) < 0;
            var village = TolkienVillageProvider.village(definition.key());
            if (village.isPresent()) {
                context.register(definition.key(), TolkienVillageProvider.create(village.get(), biomes, pools));
                continue;
            }
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
                        pools.getOrThrow(ResourceKey.create(Registries.TEMPLATE_POOL, key.identifier()))),
                        layout.gapMin(), layout.gapMax(), layout.circle()));
                continue;
            }
            context.register(definition.key(), definition.underground()
                    ? SurfaceStructures.underground(settings, pool, definition.heightOffset(), depth)
                    : new SurfaceStructures(settings, pool, definition.heightOffset(), depth));
        }
    }

    public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {
        TolkienVillageProvider.bootstrapRoadPools(context);
        var empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
        for (Definition definition : STRUCTURES) {
            var village = TolkienVillageProvider.village(definition.key());
            if (village.isPresent()) {
                TolkienVillageProvider.bootstrapPools(context, village.get());
                continue;
            }
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
            if (TolkienVillageProvider.village(definition.key()).isPresent()) continue;
            // Old Shire ID stays loadable for saved chunks; warg pits are tower companions.
            // Ruins share one grid so the two sizes cannot generate too close together.
            // The warg pits are tower companions, the ruins share one grid, and the two campaign
            // builds are placed by the campaign rather than found: no set means no natural generation,
            // which is what lets /place structure still name them.
            if (List.of(TolkienStructures.SHIRE_RUIN, TolkienStructures.WARGPIT,
                    TolkienStructures.RUIN_LARGE, TolkienStructures.RUIN_SMALL,
                    TolkienStructures.BOSS_CASTLE, TolkienStructures.BOSS_ARENA)
                    .contains(definition.key())) continue;
            context.register(definition.setKey(), new StructureSet(structures.getOrThrow(definition.key()),
                    new RandomSpreadStructurePlacement(definition.spacing(), definition.separation(),
                            RandomSpreadType.LINEAR, definition.salt())));
        }
        TolkienVillageProvider.bootstrapSet(context);
        context.register(ResourceKey.create(Registries.STRUCTURE_SET,
                        Identifier.fromNamespaceAndPath("tolkienmobs", "dagorlad_ruins")),
                new StructureSet(List.of(StructureSet.entry(structures.getOrThrow(TolkienStructures.RUIN_LARGE)),
                        StructureSet.entry(structures.getOrThrow(TolkienStructures.RUIN_SMALL))),
                        new RandomSpreadStructurePlacement(4, 3, RandomSpreadType.LINEAR, 193764832)));
    }

    public record Layout(int minCount, int maxCount, Optional<Integer> topOffset,
                         Optional<ResourceKey<Structure>> companion,
                         int gapMin, int gapMax, boolean circle) {
        public Layout(int minCount, int maxCount, Optional<Integer> topOffset,
                      Optional<ResourceKey<Structure>> companion) {
            this(minCount, maxCount, topOffset, companion,
                    TemplateLayoutStructure.DEFAULT_GAP_MIN, TemplateLayoutStructure.DEFAULT_GAP_MAX, false);
        }

        public static Layout single() { return new Layout(1, 1, Optional.empty(), Optional.empty()); }
        public static Layout houses() { return new Layout(3, 5, Optional.empty(), Optional.empty()); }
        public static Layout top(int offset) { return new Layout(1, 1, Optional.of(offset), Optional.empty()); }

        /** Up to five pieces on one circle, each one to three blocks from its neighbours. */
        public static Layout barrows() {
            return new Layout(1, 5, Optional.empty(), Optional.empty(), 1, 3, true);
        }
    }

    /** depth bounds expansion; weights choose between another side piece and stopping. */
    public record Branches(String template, int depth, int pieceWeight, int stopWeight) {
        public Branches {
            Identifier.parse(template);
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
            Identifier.parse(template);
            biomes = List.copyOf(biomes);
            if (biomes.isEmpty()) throw new IllegalArgumentException("A structure needs at least one biome");
            if (spacing <= separation || separation < 0 || spacing > 4096) {
                throw new IllegalArgumentException("Require 0 <= separation < spacing <= 4096");
            }
            if (salt < 0) throw new IllegalArgumentException("Structure salt must be nonnegative");
        }

        public ResourceKey<StructureTemplatePool> poolKey() {
            return ResourceKey.create(Registries.TEMPLATE_POOL, key.identifier());
        }

        public ResourceKey<StructureTemplatePool> sidePoolKey() {
            return ResourceKey.create(Registries.TEMPLATE_POOL, key.identifier().withSuffix("_pool/side_pool"));
        }

        public ResourceKey<StructureTemplatePool> branchPoolKey() {
            return ResourceKey.create(Registries.TEMPLATE_POOL, key.identifier().withSuffix("_pool/branches"));
        }

        public ResourceKey<StructureSet> setKey() {
            return ResourceKey.create(Registries.STRUCTURE_SET, key.identifier());
        }

        public TagKey<Biome> biomeTag() {
            return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(
                    key.identifier().getNamespace(), "has_structure/" + key.identifier().getPath()));
        }
    }
}
