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

/** Registry bootstrap callbacks used by TolkienDataRegistryProvider and the biome tag provider. */
public final class TolkienStructureProvider {
    // Add one definition per standalone NBT. Distances are chunks; heightOffset is blocks.
    // The structure, pool, set and biome tag names are derived from the structure key.
    public static final List<Definition> STRUCTURES = List.of(
            new Definition(TolkienStructures.HOBBIT_HOUSE, "tolkienmobs:ttmhouse_hobbit",
                    List.of(TolkienBiomes.SHIRE), 24, 8, 193764821, 0)
    );

    private TolkienStructureProvider() {}

    public static void bootstrapStructures(BootstrapContext<Structure> context) {
        var biomes = context.lookup(Registries.BIOME);
        var pools = context.lookup(Registries.TEMPLATE_POOL);
        for (Definition definition : STRUCTURES) {
            context.register(definition.key(), new SurfaceStructures(
                    new Structure.StructureSettings(biomes.getOrThrow(definition.biomeTag()), Map.of(),
                            GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN),
                    pools.getOrThrow(definition.poolKey()), definition.heightOffset()));
        }
    }

    public static void bootstrapPools(BootstrapContext<StructureTemplatePool> context) {
        var empty = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(Pools.EMPTY);
        for (Definition definition : STRUCTURES) {
            context.register(definition.poolKey(), new StructureTemplatePool(empty,
                    List.of(Pair.of(StructurePoolElement.single(definition.template()), 1)),
                    StructureTemplatePool.Projection.RIGID));
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

    public record Definition(ResourceKey<Structure> key, String template, List<ResourceKey<Biome>> biomes,
                             int spacing, int separation, int salt, int heightOffset) {
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

        public ResourceKey<StructureSet> setKey() {
            return ResourceKey.create(Registries.STRUCTURE_SET, key.location());
        }

        public TagKey<Biome> biomeTag() {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(
                    key.location().getNamespace(), "has_structure/" + key.location().getPath()));
        }
    }
}
