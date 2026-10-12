package com.greatorator.tolkienmobs.datagen;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.greatorator.tolkienmobs.world.components.structure.SurfaceStructures;
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
            new Definition(TolkienStructures.SHIRE_RUIN, "tolkienmobs:ttmruin_small",
                    List.of(TolkienBiomes.SHIRE), 24, 8, 193764821, 0),
            new Definition(TolkienStructures.CAVE, "tolkienmobs:cave",
                    List.of(TolkienBiomes.MIRKWOOD), 32, 8, 193764822, 0,
                    Optional.of(new Branches("tolkienmobs:cave_side", 4, 3, 1)), true),
            new Definition(TolkienStructures.MAZE, "tolkienmobs:maze",
                    List.of(TolkienBiomes.HITHAEGLIR), 32, 8, 193764823, 0,
                    Optional.of(new Branches("tolkienmobs:maze_side", 4, 3, 1)), true)
    );

    private TolkienStructureProvider() {}

    public static void bootstrapStructures(BootstrapContext<Structure> context) {
        var biomes = context.lookup(Registries.BIOME);
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        for (Definition definition : STRUCTURES) {
            var settings = new Structure.StructureSettings(biomes.getOrThrow(definition.biomeTag()), Map.of(),
                    definition.underground() ? GenerationStep.Decoration.UNDERGROUND_STRUCTURES : GenerationStep.Decoration.SURFACE_STRUCTURES,
                    definition.underground() ? TerrainAdjustment.NONE : TerrainAdjustment.BEARD_THIN);
            var pool = pools.getOrThrow(definition.poolKey());
            int depth = definition.branches().map(Branches::depth).orElse(1);
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
            context.register(definition.setKey(), new StructureSet(structures.getOrThrow(definition.key()),
                    new RandomSpreadStructurePlacement(definition.spacing(), definition.separation(),
                            RandomSpreadType.LINEAR, definition.salt())));
        }
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
                             int spacing, int separation, int salt, int heightOffset, Optional<Branches> branches, boolean underground) {
        public Definition(ResourceKey<Structure> key, String template, List<ResourceKey<Biome>> biomes,
                          int spacing, int separation, int salt, int heightOffset) {
            this(key, template, biomes, spacing, separation, salt, heightOffset, Optional.empty(), false);
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
