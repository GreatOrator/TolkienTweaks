package com.greatorator.tolkienmobs.gametest;

import com.greatorator.tolkienmobs.datagen.TolkienStructureProvider;
import com.greatorator.tolkienmobs.datagen.TolkienVillageProvider;
import com.greatorator.tolkienmobs.datagen.TolkienVillageTemplateProvider;
import com.greatorator.tolkienmobs.datagen.tags.TolkienStructureTagProvider;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@GameTestHolder("tolkienmobs_villages")
@PrefixGameTestTemplate(false)
public class TolkienVillageGameTests {
    @GameTest(template = "village_arena", timeoutTicks = 1200)
    public static void providerPoolsAndTerrainGeneration(GameTestHelper helper) throws Exception {
        var fixtures = fixtures(helper);
        var level = helper.getLevel();
        var registries = fixtures.registries();
        var lookup = fixtures.lookup();
        var ops = lookup.createSerializationContext(JsonOps.INSTANCE);
        var normal = (NoiseBasedChunkGenerator) WorldPresets.getNormalOverworld(level.registryAccess()).generator();
        var settings = normal.generatorSettings();
        var villageSet = lookup.lookupOrThrow(Registries.STRUCTURE_SET).getOrThrow(TolkienVillageProvider.VILLAGES).value();
        helper.assertTrue(villageSet.structures().size() == 6, "All village styles must share one placement grid");
        helper.assertTrue(lookup.lookupOrThrow(Registries.STRUCTURE_SET).listElements().noneMatch(holder ->
                TolkienVillageProvider.VILLAGES_BY_STYLE.stream().anyMatch(village -> village.structure().location().equals(holder.key().location()))),
                "Old independent house placement sets must no longer be generated");
        for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
            var definition = TolkienStructureProvider.STRUCTURES.stream().filter(d -> d.key().equals(village.structure())).findFirst().orElseThrow();
            var structure = lookup.lookupOrThrow(Registries.STRUCTURE).getOrThrow(village.structure()).value();
            helper.assertTrue(structure instanceof JigsawStructure, "House must now be a vanilla jigsaw village");
            var encoded = Structure.DIRECT_CODEC.encodeStart(ops, structure).getOrThrow();
            helper.assertTrue(Structure.DIRECT_CODEC.parse(ops, encoded).getOrThrow() instanceof JigsawStructure, "Village codec roundtrip");
            var biome = registries.registryOrThrow(Registries.BIOME).getHolderOrThrow(definition.biomes().getFirst());
            var source = new FixedBiomeSource(biome);
            var generator = new NoiseBasedChunkGenerator(source, settings);
            Set<Integer> houseCounts = new HashSet<>();
            for (long seed = 0; seed < 8; seed++) {
                var state = RandomState.create(settings.value(), registries.lookupOrThrow(Registries.NOISE), seed);
                var start = structure.generate(registries, generator, source, state, level.getStructureManager(), seed,
                        new ChunkPos(0, 0), 0, level, structure.biomes()::contains);
                helper.assertTrue(start.isValid(), "Village must start in " + village.style() + " biome");
                int houses = 0, roads = 0;
                Set<String> templates = new HashSet<>();
                for (var piece : start.getPieces()) {
                    var poolPiece = (PoolElementStructurePiece) piece;
                    var element = StructurePoolElement.CODEC.encodeStart(ops, poolPiece.getElement()).getOrThrow().getAsJsonObject();
                    if (!element.has("location")) continue;
                    String location = element.get("location").getAsString();
                    templates.add(location);
                    if (location.equals(village.houseTemplate().toString())) houses++;
                    if (location.contains("/roads/")) roads++;
                    helper.assertTrue(!location.contains("plains_small_house") && !location.contains("desert_small_house"), "Vanilla houses must be replaced locally");
                }
                helper.assertTrue(houses >= 1 && roads >= 2, "Missing houses/roads for " + village.style() + " seed " + seed + ": " + houses + "/" + roads);
                houseCounts.add(houses);
                var box = start.getBoundingBox();
                helper.assertTrue(box.getXSpan() <= 2 * TolkienVillageProvider.MAX_DISTANCE + 64
                        && box.getZSpan() <= 2 * TolkienVillageProvider.MAX_DISTANCE + 64, "Village bounds");
                var wrongBiome = new FixedBiomeSource(registries.registryOrThrow(Registries.BIOME)
                        .getHolderOrThrow(net.minecraft.world.level.biome.Biomes.OCEAN));
                helper.assertTrue(!structure.generate(registries, new NoiseBasedChunkGenerator(wrongBiome, settings), wrongBiome, state, level.getStructureManager(), seed,
                        new ChunkPos(0, 0), 0, level, structure.biomes()::contains).isValid(), "Wrong biome must reject the village");
            }
            com.greatorator.tolkienmobs.TolkienMobsMain.LOGGER.info("Village {} verified house counts {}", village.style(), houseCounts);
        }
        // Bootstrap changes must preserve all non-house configured structures exactly.
        var oldOps = level.registryAccess().createSerializationContext(JsonOps.INSTANCE);
        for (var definition : TolkienStructureProvider.STRUCTURES) {
            if (TolkienVillageProvider.village(definition.key()).isPresent()) continue;
            var old = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getHolderOrThrow(definition.key()).value();
            var updated = lookup.lookupOrThrow(Registries.STRUCTURE).getOrThrow(definition.key()).value();
            helper.assertTrue(Structure.DIRECT_CODEC.encodeStart(oldOps, old).getOrThrow().equals(
                    Structure.DIRECT_CODEC.encodeStart(ops, updated).getOrThrow()), "Unrelated structure changed: " + definition.key());
        }
        helper.succeed();
    }

    @GameTest(template = "village_arena", timeoutTicks = 1200)
    public static void villageTemplatesAndTagsDatagen(GameTestHelper helper) throws Exception {
        var fixtures = fixtures(helper);
        var directory = Files.createTempDirectory("tolkien-village-provider-");
        var output = new PackOutput(directory);
        new TolkienVillageTemplateProvider(output, new ExistingFileHelper(java.util.List.of(), Set.of("tolkienmobs"), true, null, null)).run(CachedOutput.NO_CACHE).join();
        new TolkienStructureTagProvider(output, CompletableFuture.completedFuture(fixtures.lookup())).run(CachedOutput.NO_CACHE).join();
        var villageTag = com.google.gson.JsonParser.parseString(Files.readString(directory.resolve("data/minecraft/tags/worldgen/structure/village.json"))).getAsJsonObject();
        helper.assertTrue(villageTag.getAsJsonArray("values").size() == 6, "Village recognition tag must contain six structures");
        for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
            var path = directory.resolve("data/tolkienmobs/structure/" + village.houseTemplate().getPath() + ".nbt");
            var tag = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
            helper.assertTrue(tag.equals(TolkienVillageTemplateProvider.prepareHouse(village, templates(helper))), "NBT provider output must match preparation");
            var template = helper.getLevel().getStructureManager().getOrCreate(village.houseTemplate());
            var connectors = template.filterBlocks(BlockPos.ZERO, new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings(), Blocks.JIGSAW);
            helper.assertTrue(connectors.size() == 2, "Every house needs an entrance and resident connector");
            var entrance = connectors.stream().filter(info -> info.nbt().getString("name").equals("minecraft:building_entrance")).findFirst().orElseThrow();
            helper.assertTrue(entrance.pos().equals(village.preparedEntrance()) && entrance.nbt().getString("pool").equals("minecraft:empty"), "House must terminate at the expected front entrance");
            var resident = connectors.stream().filter(info -> info.nbt().getString("name").equals("minecraft:bottom")).findFirst().orElseThrow();
            helper.assertTrue(resident.nbt().getString("pool").endsWith("/villagers"), "Village residents must be enabled");
            helper.assertTrue(!tag.getList("entities", Tag.TAG_COMPOUND).isEmpty() || village.style().equals("desert"), "Keep existing Tolkien inhabitants");
        }
        int templatesWritten;
        try (var files = Files.walk(directory)) {
            templatesWritten = (int) files.filter(path -> path.toString().endsWith(".nbt")).count();
        }
        helper.assertTrue(templatesWritten == 33, "Datagen must write six houses and all 27 roads");
        for (String style : java.util.List.of("plains", "desert")) for (var street : TolkienVillageProvider.streets(style)) {
            var path = directory.resolve("data/tolkienmobs/structure/" + street.template().getPath() + ".nbt");
            var road = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap());
            helper.assertTrue(road.equals(TolkienVillageTemplateProvider.prepareRoad(street, templates(helper))), "Road resource preparation mismatch");
            var size = road.getList("size", Tag.TAG_INT);
            var palette = road.getList("palette", Tag.TAG_COMPOUND);
            var positions = new HashSet<BlockPos>();
            for (var tag : road.getList("blocks", Tag.TAG_COMPOUND)) {
                var block = (net.minecraft.nbt.CompoundTag) tag;
                var pos = block.getList("pos", Tag.TAG_INT);
                var position = new BlockPos(pos.getInt(0), pos.getInt(1), pos.getInt(2));
                helper.assertTrue(positions.add(position), "Duplicate road block: " + street.source() + " at " + position);
                if (!block.getCompound("nbt").getString("name").equals("minecraft:building_entrance")) continue;
                var direction = Direction.byName(palette.getCompound(block.getInt("state"))
                        .getCompound("Properties").getString("orientation").split("_")[0]);
                boolean atEdge = switch (direction) {
                    case NORTH -> position.getZ() == 0;
                    case SOUTH -> position.getZ() == size.getInt(2) - 1;
                    case WEST -> position.getX() == 0;
                    case EAST -> position.getX() == size.getInt(0) - 1;
                    default -> false;
                };
                helper.assertTrue(atEdge, "Road house connector must reach outward edge: " + street.source());
            }
            var original = helper.getLevel().getStructureManager().getOrCreate(street.source());
            var prepared = helper.getLevel().getStructureManager().getOrCreate(street.template());
            var settings = new net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings();
            helper.assertTrue(original.filterBlocks(BlockPos.ZERO, settings, Blocks.JIGSAW).size()
                    == prepared.filterBlocks(BlockPos.ZERO, settings, Blocks.JIGSAW).size(), "All original road connections must survive");
        }
        helper.succeed();
    }

    @GameTest(template = "village_arena", timeoutTicks = 1200)
    public static void villagePlacesRoadsWellBedsAndResidents(GameTestHelper helper) throws Exception {
        var fixtures = fixtures(helper);
        var level = helper.getLevel();
        var generator = level.getChunkSource().getGenerator();
        var state = level.getChunkSource().randomState();
        int index = 0;
        for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
            var structure = fixtures.lookup().lookupOrThrow(Registries.STRUCTURE).getOrThrow(village.structure()).value();
            var biome = structure.biomes().get(0);
            var chunk = new ChunkPos(helper.absolutePos(new BlockPos(0, 0, 0)).offset(1024 * ++index, 0, 1024));
            var start = structure.generate(fixtures.registries(), generator, new FixedBiomeSource(biome), state,
                    level.getStructureManager(), 0, chunk, 0, level, ignored -> true);
            helper.assertTrue(start.isValid(), "Flat placement village must start");
            var box = start.getBoundingBox();
            for (int x = box.minX() >> 4; x <= box.maxX() >> 4; x++) for (int z = box.minZ() >> 4; z <= box.maxZ() >> 4; z++) {
                var chunkPos = new ChunkPos(x, z);
                start.placeInChunk(level, level.structureManager(), generator, RandomSource.create(0),
                        new BoundingBox(chunkPos.getMinBlockX(), level.getMinBuildHeight(), chunkPos.getMinBlockZ(),
                                chunkPos.getMaxBlockX(), level.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ()), chunkPos);
            }
            int paths = 0, water = 0, bells = 0, beds = 0, jigsaws = 0;
            for (BlockPos pos : BlockPos.betweenClosed(box.minX(), -62, box.minZ(), box.maxX(), box.maxY() + 1, box.maxZ())) {
                var block = level.getBlockState(pos);
                if (block.is(Blocks.DIRT_PATH) || (village.style().equals("desert") && block.is(Blocks.SANDSTONE))) paths++;
                if (block.is(Blocks.WATER)) water++;
                if (block.is(Blocks.BELL)) bells++;
                if (block.is(net.minecraft.tags.BlockTags.BEDS)) beds++;
                if (block.is(Blocks.JIGSAW)) jigsaws++;
            }
            helper.assertTrue(paths > 20 && water > 0 && bells > 0 && beds > 0 && jigsaws == 0,
                    village.style() + " missing infrastructure: " + paths + "/" + water + "/" + bells + "/" + beds + "/" + jigsaws);
            helper.assertTrue(!level.getEntitiesOfClass(Villager.class, AABB.of(box).inflate(5)).isEmpty(), "Vanilla residents must spawn");
        }
        helper.succeed();
    }

    private static TolkienVillageTemplateProvider.TemplateSource templates(GameTestHelper helper) {
        return location -> helper.getLevel().getServer().getResourceManager().getResourceOrThrow(location).open();
    }

    private static Fixtures fixtures(GameTestHelper helper) throws Exception {
        var current = helper.getLevel().registryAccess();
        var pools = new MappedRegistry<>(Registries.TEMPLATE_POOL, Lifecycle.stable());
        var structures = new MappedRegistry<>(Registries.STRUCTURE, Lifecycle.stable());
        var sets = new MappedRegistry<>(Registries.STRUCTURE_SET, Lifecycle.stable());
        copyVanilla(current.registryOrThrow(Registries.TEMPLATE_POOL), pools);
        copyVanilla(current.registryOrThrow(Registries.STRUCTURE), structures);
        copyVanilla(current.registryOrThrow(Registries.STRUCTURE_SET), sets);
        var registries = new ArrayList<Registry<?>>();
        current.registries().filter(entry -> !Set.of(Registries.TEMPLATE_POOL, Registries.STRUCTURE, Registries.STRUCTURE_SET)
                .contains(entry.key())).forEach(entry -> registries.add(entry.value()));
        registries.add(pools); registries.add(structures); registries.add(sets);
        var mutable = new RegistryAccess.ImmutableRegistryAccess(registries);
        TolkienStructureProvider.bootstrapPools(context(pools, mutable));
        TolkienStructureProvider.bootstrapStructures(context(structures, mutable));
        TolkienStructureProvider.bootstrapSets(context(sets, mutable));
        var access = mutable.freeze();
        HolderLookup.Provider lookup = access;
        for (var village : TolkienVillageProvider.VILLAGES_BY_STYLE) {
            helper.getLevel().getStructureManager().getOrCreate(village.houseTemplate()).load(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), TolkienVillageTemplateProvider.prepareHouse(village, templates(helper)));
        }
        for (String style : java.util.List.of("plains", "desert")) for (var street : TolkienVillageProvider.streets(style)) {
            helper.getLevel().getStructureManager().getOrCreate(street.template()).load(
                    net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), TolkienVillageTemplateProvider.prepareRoad(street, templates(helper)));
        }
        return new Fixtures(lookup, access);
    }

    private static <T> void copyVanilla(Registry<T> original, MappedRegistry<T> target) {
        original.entrySet().stream().filter(entry -> !entry.getKey().location().getNamespace().equals("tolkienmobs"))
                .forEach(entry -> target.register(entry.getKey(), entry.getValue(), RegistrationInfo.BUILT_IN));
    }

    private static <T> net.minecraft.data.worldgen.BootstrapContext<T> context(MappedRegistry<T> registry, RegistryAccess access) {
        return new net.minecraft.data.worldgen.BootstrapContext<>() {
            @Override
            public net.minecraft.core.Holder.Reference<T> register(ResourceKey<T> key, T value, Lifecycle lifecycle) {
                return registry.register(key, value, new RegistrationInfo(java.util.Optional.empty(), lifecycle));
            }

            @Override
            public <S> net.minecraft.core.HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return access.lookupOrThrow(key);
            }
        };
    }

    private record Fixtures(HolderLookup.Provider lookup, RegistryAccess.Frozen registries) {}
}
