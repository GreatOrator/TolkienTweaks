package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(TolkienMobsMain.MODID)
@PrefixGameTestTemplate(false)
public class StructureGameTests {
    private static final ResourceLocation EXAMPLE = ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "shire_ruin");

    @GameTest(template = "trinket_table_empty")
    public static void exampleReferencesLoadAndLegacyNbtHasExpectedSize(GameTestHelper helper) {
        var level = helper.getLevel();
        var registries = level.registryAccess();
        var structure = registries.registryOrThrow(Registries.STRUCTURE).getOptional(EXAMPLE).orElseThrow();
        var set = registries.registryOrThrow(Registries.STRUCTURE_SET).getOptional(EXAMPLE).orElseThrow();
        helper.assertTrue(set.structures().stream().anyMatch(entry -> entry.structure().value() == structure),
                "Example structure set must schedule the example structure");
        helper.assertTrue(registries.registryOrThrow(Registries.TEMPLATE_POOL).getOptional(EXAMPLE).isPresent(),
                "Example template pool must load");
        var template = level.getStructureManager().get(
                ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "ttmruin_small")).orElseThrow();
        helper.assertTrue(template.getSize().equals(new Vec3i(11, 5, 11)), "Legacy ruin NBT must load at its original size");
        helper.succeed();
    }

    @GameTest(template = "trinket_table_empty")
    public static void exampleGeneratesOnlyInAllowedBiome(GameTestHelper helper) {
        var level = helper.getLevel();
        var registries = level.registryAccess();
        Structure structure = registries.registryOrThrow(Registries.STRUCTURE).getOptional(EXAMPLE).orElseThrow();
        var biomes = registries.registryOrThrow(Registries.BIOME);
        var settings = ((NoiseBasedChunkGenerator) WorldPresets.getNormalOverworld(registries).generator()).generatorSettings();
        long seed = 12345L;
        var randomState = RandomState.create(settings.value(), registries.lookupOrThrow(Registries.NOISE), seed);
        for (var biomeKey : List.of(TolkienBiomes.SHIRE, TolkienBiomes.MORDOR)) {
            // Keep normal terrain settings while controlling the biome, so the test
            // exercises Minecraft's real biome filter rather than a random search.
            var biome = biomes.getHolderOrThrow(biomeKey);
            var source = new FixedBiomeSource(biome);
            var generator = new NoiseBasedChunkGenerator(source, settings);
            var start = structure.generate(registries, generator, source, randomState, level.getStructureManager(),
                    seed, new ChunkPos(0, 0), 0, level, structure.biomes()::contains);
            helper.assertTrue(start.isValid() == biome.is(TolkienBiomes.SHIRE),
                    "Example must generate in Shire and be rejected in Mordor");
            if (start.isValid()) {
                helper.assertTrue(start.getPieces().size() == 1, "Standalone NBT must produce one structure piece");
            }
        }
        helper.succeed();
    }
}
