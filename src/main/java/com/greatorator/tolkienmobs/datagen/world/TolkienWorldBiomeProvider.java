package com.greatorator.tolkienmobs.datagen.world;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.datagen.world.TolkienWorldPlacementProvider.*;

public class TolkienWorldBiomeProvider {
    // Single Trees
    public static final ResourceKey<BiomeModifier> ADD_TREE_MALLORN = registerBiomeKey("tree/add_tree_mallorn");
    public static final ResourceKey<BiomeModifier> ADD_TREE_MIRKWOOD = registerBiomeKey("tree/add_tree_mirkwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_CULUMALDA = registerBiomeKey("tree/add_tree_culumalda");
    public static final ResourceKey<BiomeModifier> ADD_TREE_LEBETHRON = registerBiomeKey("tree/add_tree_lebethron");
    public static final ResourceKey<BiomeModifier> ADD_TREE_FANGORNOAK = registerBiomeKey("tree/add_tree_fangornoak");
    public static final ResourceKey<BiomeModifier> ADD_TREE_DEADWOOD = registerBiomeKey("tree/add_tree_deadwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_DWARVEN = registerBiomeKey("tree/add_tree_dwarven");

    // Random Bunches
    public static final ResourceKey<BiomeModifier> FIRIEN_WOODS = registerBiomeKey("tree/selector/firien_woods");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_MALLORN, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(MALLORN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_MIRKWOOD, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(MIRKWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_CULUMALDA, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(CULUMALDA_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_LEBETHRON, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(LEBETHRON_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_FANGORNOAK, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(FANGORNOAK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_DEADWOOD, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(DEADWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_DWARVEN, new net.neoforged.neoforge.common.world.BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(DWARVEN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
    }

    private static ResourceKey<BiomeModifier> registerBiomeKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}