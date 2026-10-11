package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import terrablender.util.LevelUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@GameTestHolder(TolkienMobsMain.MODID)
@PrefixGameTestTemplate(false)
public class OverworldBiomeGameTests {
    @GameTest(template = "trinket_table_empty", timeoutTicks = 1200)
    public static void normalOverworldContainsAndSelectsAllTolkienBiomes(GameTestHelper helper) {
        var registries = helper.getLevel().registryAccess();
        LevelStem overworld = WorldPresets.getNormalOverworld(registries);
        var generator = (NoiseBasedChunkGenerator) overworld.generator();
        // GameTests run in a flat world. Initialize the NORMAL preset exactly as
        // TerraBlender's server-start hook does for a real Overworld.
        long seed = 12345L;
        LevelUtils.initializeBiomes(registries, overworld.type(), LevelStem.OVERWORLD, generator, seed);
        var source = generator.getBiomeSource();
        Set<ResourceKey<Biome>> available = new HashSet<>();
        source.possibleBiomes().forEach(biome -> available.add(biome.unwrapKey().orElseThrow()));
        helper.assertTrue(available.containsAll(TolkienBiomes.BIOME_FACTORIES.keySet()), "Normal source is missing Tolkien biomes");
        helper.assertTrue(available.stream().anyMatch(key -> key.location().getNamespace().equals("minecraft")),
                "Vanilla biomes must remain in the Overworld");
        TolkienBiomes.BIOME_FACTORIES.keySet().forEach(key -> helper.assertTrue(
                registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(key).is(BiomeTags.IS_OVERWORLD),
                "Missing Overworld biome tag: " + key.location()));

        RandomState randomState = RandomState.create(generator.generatorSettings().value(), registries.lookupOrThrow(Registries.NOISE), seed);
        Random positions = new Random(0);
        Set<ResourceKey<Biome>> found = new HashSet<>();
        for (int i = 0; i < 300_000 && !found.containsAll(TolkienBiomes.BIOME_FACTORIES.keySet()); i++) {
            int x = positions.nextInt(32_000) - 16_000;
            int z = positions.nextInt(32_000) - 16_000;
            ResourceKey<Biome> key = source.getNoiseBiome(x, 20, z, randomState.sampler()).unwrapKey().orElseThrow();
            if (found.add(key) && TolkienBiomes.BIOME_FACTORIES.containsKey(key)) {
                TolkienMobsMain.LOGGER.info("Sampled Overworld biome {} at block [{}, 80, {}] for seed {}",
                        key.location(), x * 4, z * 4, seed);
            }
        }
        Set<ResourceKey<Biome>> missing = new HashSet<>(TolkienBiomes.BIOME_FACTORIES.keySet());
        missing.removeAll(found);
        helper.assertTrue(missing.isEmpty(), "Biomes registered but not sampled in normal terrain: " + missing);
        helper.succeed();
    }

    @GameTest(template = "trinket_table_empty")
    public static void overworldFeaturesHaveNoOrderingCycles(GameTestHelper helper) {
        var biomes = helper.getLevel().registryAccess().registryOrThrow(Registries.BIOME).holders()
                .filter(biome -> biome.is(BiomeTags.IS_OVERWORLD)).toList();
        FeatureSorter.buildFeaturesPerStep(biomes, biome -> biome.value().getGenerationSettings().features(), true);
        helper.succeed();
    }
}
