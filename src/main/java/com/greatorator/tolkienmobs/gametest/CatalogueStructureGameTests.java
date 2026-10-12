package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.datagen.TolkienStructureProvider;
import com.greatorator.tolkienmobs.init.TolkienStructures;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.HashSet;

@GameTestHolder(TolkienMobsMain.MODID)
@PrefixGameTestTemplate(false)
public class CatalogueStructureGameTests {
    @GameTest(template = "trinket_table_empty", timeoutTicks = 1200)
    public static void catalogueLayoutsFollowCountsClearanceAndTerrain(GameTestHelper helper) {
        var level = helper.getLevel();
        var registries = level.registryAccess();
        var settings = ((NoiseBasedChunkGenerator) WorldPresets.getNormalOverworld(registries).generator()).generatorSettings();
        var ops = RegistryOps.create(JsonOps.INSTANCE, registries);
        for (var definition : TolkienStructureProvider.STRUCTURES) {
            if (definition.layout().isEmpty()) continue;
            var layout = definition.layout().get();
            var structure = registries.registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(definition.key()).value();
            var biome = registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(definition.biomes().getFirst());
            var source = new FixedBiomeSource(biome);
            var generator = new NoiseBasedChunkGenerator(source, settings);
            var counts = new HashSet<Integer>();
            for (long seed = 0; seed < 8; seed++) {
                var state = RandomState.create(settings.value(), registries.lookupOrThrow(Registries.NOISE), seed);
                var start = structure.generate(registries, generator, source, state, level.getStructureManager(), seed,
                        new ChunkPos(0, 0), 0, level, structure.biomes()::contains);
                helper.assertTrue(start.isValid(), "No start for " + definition.key().location());
                var pieces = start.getPieces();
                int companions = layout.companion().isPresent() ? 1 : 0;
                helper.assertTrue(pieces.size() >= layout.minCount() + companions && pieces.size() <= layout.maxCount() + companions,
                        "Wrong piece count for " + definition.key().location());
                counts.add(pieces.size());
                for (int i = 0; i < pieces.size(); i++) {
                    var piece = (PoolElementStructurePiece) pieces.get(i);
                    var box = piece.getBoundingBox();
                    String template = StructurePoolElement.CODEC.encodeStart(ops, piece.getElement()).getOrThrow()
                            .getAsJsonObject().get("location").getAsString();
                    boolean companion = i >= layout.maxCount();
                    String expected = companion ? layout.companion().orElseThrow().location().toString() : definition.template();
                    helper.assertTrue(template.equals(expected), "Unexpected template " + template);
                    int surface = generator.getFirstOccupiedHeight((box.minX() + box.maxX()) / 2,
                            (box.minZ() + box.maxZ()) / 2, Heightmap.Types.WORLD_SURFACE_WG, level, state);
                    if (companion || layout.topOffset().isPresent()) {
                        helper.assertTrue(box.maxY() == surface + (companion ? 2 : layout.topOffset().orElseThrow()),
                                "Incorrect roof depth for " + template);
                    } else helper.assertTrue(box.minY() == surface, "House/building foundation must follow local ground");
                    boolean neighbor = pieces.size() == 1;
                    for (int j = 0; j < pieces.size(); j++) {
                        if (i == j) continue;
                        int gap = edgeGap(box, pieces.get(j).getBoundingBox());
                        helper.assertTrue(gap >= 5, "Buildings overlap or leave less than five blocks clearance");
                        neighbor |= gap <= 10;
                    }
                    helper.assertTrue(neighbor, "Each clustered building needs a neighbor within ten blocks");
                }
            }
            if (layout.minCount() != layout.maxCount()) helper.assertTrue(counts.size() > 1, "Cluster count must vary across seeds");
            TolkienMobsMain.LOGGER.info("Catalogue {} verified counts {} and terrain alignment", definition.key().location(), counts);
        }
        helper.succeed();
    }

    private static int edgeGap(BoundingBox a, BoundingBox b) {
        int x = Math.max(0, Math.max(a.minX() - b.maxX() - 1, b.minX() - a.maxX() - 1));
        int z = Math.max(0, Math.max(a.minZ() - b.maxZ() - 1, b.minZ() - a.maxZ() - 1));
        return Math.max(x, z);
    }

    @GameTest(template = "trinket_table_empty")
    public static void catalogueNbtHasAvailableModBlocksAndEntities(GameTestHelper helper) throws Exception {
        for (var definition : TolkienStructureProvider.STRUCTURES) {
            if (definition.layout().isEmpty()) continue;
            var id = ResourceLocation.parse(definition.template());
            var resource = id.withPath("structure/" + id.getPath() + ".nbt");
            try (var stream = helper.getLevel().getServer().getResourceManager().getResourceOrThrow(resource).open()) {
                var nbt = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());
                for (var tag : nbt.getList("palette", Tag.TAG_COMPOUND)) {
                    var name = ResourceLocation.parse(((net.minecraft.nbt.CompoundTag) tag).getString("Name"));
                    if (!name.getNamespace().equals("minecraft")) helper.assertTrue(BuiltInRegistries.BLOCK.containsKey(name), "Missing NBT block: " + name);
                }
                for (var tag : nbt.getList("entities", Tag.TAG_COMPOUND)) {
                    var name = ResourceLocation.parse(((net.minecraft.nbt.CompoundTag) tag).getCompound("nbt").getString("id"));
                    helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.containsKey(name), "Missing NBT entity: " + name);
                }
            }
        }
        helper.succeed();
    }
}
