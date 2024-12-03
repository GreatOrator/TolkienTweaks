package com.greatorator.tolkienmobs.world;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.world.TolkienPlacedFeatures.*;

public class TolkienBiomeModifiers {
    // Single Trees
    public static final ResourceKey<BiomeModifier> ADD_TREE_MALLORN = registerBiomeKey("tree/add_tree_mallorn");
    public static final ResourceKey<BiomeModifier> ADD_TREE_MIRKWOOD = registerBiomeKey("tree/add_tree_mirkwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_CULUMALDA = registerBiomeKey("tree/add_tree_culumalda");
    public static final ResourceKey<BiomeModifier> ADD_TREE_LEBETHRON = registerBiomeKey("tree/add_tree_lebethron");
    public static final ResourceKey<BiomeModifier> ADD_TREE_FANGORNOAK = registerBiomeKey("tree/add_tree_fangornoak");
    public static final ResourceKey<BiomeModifier> ADD_TREE_DEADWOOD = registerBiomeKey("tree/add_tree_deadwood");
    public static final ResourceKey<BiomeModifier> ADD_TREE_DWARVEN = registerBiomeKey("tree/add_tree_dwarven");

    public static final ResourceKey<BiomeModifier> ADD_MITHRIL_ORE = registerBiomeKey("ore/add_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_MITHRIL_ORE = registerBiomeKey("ore/add_nether_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_MITHRIL_ORE = registerBiomeKey("ore/add_end_mithril_ore");
    public static final ResourceKey<BiomeModifier> ADD_MORGULIRON_ORE = registerBiomeKey("ore/add_morguliron_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_MORGULIRON_ORE = registerBiomeKey("ore/add_nether_morguliron_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_MORGULIRON_ORE = registerBiomeKey("ore/add_end_morguliron_ore");
    public static final ResourceKey<BiomeModifier> ADD_AMMOLITE_ORE = registerBiomeKey("ore/add_ammolite_ore");
    public static final ResourceKey<BiomeModifier> ADD_NETHER_AMMOLITE_ORE = registerBiomeKey("ore/add_nether_ammolite_ore");
    public static final ResourceKey<BiomeModifier> ADD_END_AMMOLITE_ORE = registerBiomeKey("ore/add_end_ammolite_ore");

    // Random Bunches
    public static final ResourceKey<BiomeModifier> FIRIEN_WOODS = registerBiomeKey("tree/selector/firien_woods");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_MALLORN, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(MALLORN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_MIRKWOOD, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(MIRKWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_CULUMALDA, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(CULUMALDA_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_LEBETHRON, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(LEBETHRON_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_FANGORNOAK, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(FANGORNOAK_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_DEADWOOD, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(DEADWOOD_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));
        context.register(ADD_TREE_DWARVEN, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.BIRCH_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(DWARVEN_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.STONY_PEAKS)),
                HolderSet.direct(placedFeatures.getOrThrow(MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_NETHER_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.NETHER_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_END_MITHRIL_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.END_MITHRIL_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_MORGULIRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.BADLANDS)),
                HolderSet.direct(placedFeatures.getOrThrow(MORGULIRON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_NETHER_MORGULIRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.NETHER_MORGULIRON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_END_MORGULIRON_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.END_MORGULIRON_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_AMMOLITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST)),
                HolderSet.direct(placedFeatures.getOrThrow(AMMOLITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_NETHER_AMMOLITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_NETHER),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.NETHER_AMMOLITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
        context.register(ADD_END_AMMOLITE_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_END),
                HolderSet.direct(placedFeatures.getOrThrow(TolkienPlacedFeatures.END_AMMOLITE_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<BiomeModifier> registerBiomeKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }
}