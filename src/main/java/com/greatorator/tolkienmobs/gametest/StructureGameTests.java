package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.datagen.TolkienStructureProvider;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import java.util.stream.Stream;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import java.util.HashSet;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.greatorator.tolkienmobs.world.components.structure.SurfaceStructures;
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
    private static final ResourceLocation EXAMPLE = TolkienStructures.RUIN_SMALL.location();

    @GameTest(template = "trinket_table_empty")
    public static void exampleReferencesLoadAndLegacyNbtHasExpectedSize(GameTestHelper helper) {
        var level = helper.getLevel();
        var registries = level.registryAccess();
        var structure = registries.registryOrThrow(Registries.STRUCTURE).getOptional(EXAMPLE).orElseThrow();
        helper.assertTrue(structure.type() == TolkienStructures.TEMPLATE_LAYOUT.get(),
                "Generated ruin must use the registered template layout type");
        var set = registries.registryOrThrow(Registries.STRUCTURE_SET).getOptional(ResourceLocation.fromNamespaceAndPath(TolkienMobsMain.MODID, "dagorlad_ruins")).orElseThrow();
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
        for (var biomeKey : List.of(TolkienBiomes.DAGORLAD, TolkienBiomes.MORDOR)) {
            // Keep normal terrain settings while controlling the biome, so the test
            // exercises Minecraft's real biome filter rather than a random search.
            var biome = biomes.getHolderOrThrow(biomeKey);
            var source = new FixedBiomeSource(biome);
            var generator = new NoiseBasedChunkGenerator(source, settings);
            var start = structure.generate(registries, generator, source, randomState, level.getStructureManager(),
                    seed, new ChunkPos(0, 0), 0, level, structure.biomes()::contains);
            helper.assertTrue(start.isValid() == biome.is(TolkienBiomes.DAGORLAD),
                    "Example must generate in Dagorlad and be rejected in Mordor");
            if (start.isValid()) {
                helper.assertTrue(start.getPieces().size() == 1, "Standalone NBT must produce one structure piece");
            }
        }
        helper.succeed();
    }
    @GameTest(template = "trinket_table_empty", timeoutTicks = 1200)
    public static void branchingStructuresHaveOneRootAndRandomSidePieces(GameTestHelper helper) {
        var level = helper.getLevel();
        var registries = level.registryAccess();
        var settings = ((NoiseBasedChunkGenerator) WorldPresets.getNormalOverworld(registries).generator()).generatorSettings();
        var ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        for (var key : List.of(TolkienStructures.CAVE, TolkienStructures.MAZE)) {
            var definition = TolkienStructureProvider.STRUCTURES.stream().filter(d -> d.key().equals(key)).findFirst().orElseThrow();
            var structure = registries.registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(key).value();
            var biome = registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(definition.biomes().getFirst());
            var below = registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.LUSH_CAVES);
            var source = layeredBiomes(biome, below);
            var generator = new NoiseBasedChunkGenerator(source, settings);
            var counts = new HashSet<Integer>();
            for (long seed = 0; seed < 8; seed++) {
                var randomState = RandomState.create(settings.value(), registries.lookupOrThrow(Registries.NOISE), seed);
                var start = structure.generate(registries, generator, source, randomState, level.getStructureManager(),
                        seed, new ChunkPos(0, 0), 0, level, structure.biomes()::contains);
                helper.assertTrue(start.isValid(), "Branch structure failed to start: " + key.location());
                var pieces = start.getPieces();
                helper.assertTrue(start.getBoundingBox().maxY() < 16,
                        "Underground structures must remain below Y=16 with default height settings");
                int roots = 0;
                for (int i = 0; i < pieces.size(); i++) {
                    var piece = (PoolElementStructurePiece) pieces.get(i);
                    String template = StructurePoolElement.CODEC.encodeStart(ops, piece.getElement()).getOrThrow()
                            .getAsJsonObject().get("location").getAsString();
                    if (template.equals(definition.template())) roots++;
                    else helper.assertTrue(template.equals(definition.branches().orElseThrow().template()),
                            "Unexpected descendant template: " + template);
                    for (int j = 0; j < i; j++) {
                        helper.assertTrue(!piece.getBoundingBox().intersects(pieces.get(j).getBoundingBox()),
                                "Branch pieces must not overlap");
                    }
                }
                helper.assertTrue(roots == 1, "Main NBT must appear exactly once: " + key.location());
                helper.assertTrue(pieces.size() > 1, "No sides attached: " + key.location());
                counts.add(pieces.size());
            }
            var wrongSource = layeredBiomes(registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(TolkienBiomes.SHIRE), below);
            var rejected = structure.generate(registries, new NoiseBasedChunkGenerator(wrongSource, settings), wrongSource,
                    RandomState.create(settings.value(), registries.lookupOrThrow(Registries.NOISE), 0),
                    level.getStructureManager(), 0, new ChunkPos(0, 0), 0, level, structure.biomes()::contains);
            helper.assertTrue(!rejected.isValid(), "Underground structures must reject land in the wrong surface biome");
            helper.assertTrue(counts.size() > 1, "Side counts must vary across seeds: " + key.location());
            TolkienMobsMain.LOGGER.info("Branch test {} generated piece counts {} across eight seeds", key.location(), counts);
        }
        helper.succeed();
    }

    private static BiomeSource layeredBiomes(Holder<Biome> surface, Holder<Biome> below) {
        return new BiomeSource() {
            @Override
            protected MapCodec<? extends BiomeSource> codec() {
                return MapCodec.unit(this);
            }

            @Override
            protected Stream<Holder<Biome>> collectPossibleBiomes() {
                return Stream.of(surface, below);
            }

            @Override
            public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
                // Quart coordinates: underground pieces see lush caves, while
                // the surface biome filter sees Mirkwood or Hithaeglir.
                return y < 8 ? below : surface;
            }
        };
    }

}
