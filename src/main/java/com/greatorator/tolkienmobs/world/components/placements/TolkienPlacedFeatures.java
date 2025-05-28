package com.greatorator.tolkienmobs.world.components.placements;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.world.components.config.TolkienConfiguredFeatures.*;

public class TolkienPlacedFeatures {
    public static final ResourceKey<PlacedFeature> MALLORN_PLACED_KEY = registerKey("mallorn_placed");
    public static final ResourceKey<PlacedFeature> MIRKWOOD_PLACED_KEY = registerKey("mirkwood_placed");
    public static final ResourceKey<PlacedFeature> HARDENED_MIRKWOOD_PLACED_KEY = registerKey("hardened_mirkwood_placed");
    public static final ResourceKey<PlacedFeature> CULUMALDA_PLACED_KEY = registerKey("culumalda_placed");
    public static final ResourceKey<PlacedFeature> LEBETHRON_PLACED_KEY = registerKey("lebethron_placed");
    public static final ResourceKey<PlacedFeature> FANGORNOAK_PLACED_KEY = registerKey("fangornoak_placed");
    public static final ResourceKey<PlacedFeature> DEADWOOD_PLACED_KEY = registerKey("deadwood_placed");
    public static final ResourceKey<PlacedFeature> DWARVEN_PLACED_KEY = registerKey("dwarven_placed");
    public static final ResourceKey<PlacedFeature> HARDENED_MALLORN_PLACED_KEY = registerKey("hardened_mallorn_placed");
    public static final ResourceKey<PlacedFeature> HARDENED_CULUMALDA_PLACED_KEY = registerKey("hardened_culumalda_placed");
    public static final ResourceKey<PlacedFeature> HARDENED_LEBETHRON_PLACED_KEY = registerKey("hardened_lebethron_placed");
    public static final ResourceKey<PlacedFeature> HARDENED_FANGORNOAK_PLACED_KEY = registerKey("hardened_fangornoak_placed");
    public static final ResourceKey<PlacedFeature> HARDENED_DWARVEN_PLACED_KEY = registerKey("hardened_dwarven_placed");
    public static final ResourceKey<PlacedFeature> OLDFORESTOAK_PLACED_KEY = registerKey("oldforestoak_placed");

    public static final ResourceKey<PlacedFeature> FLOWER_SIMBELMYNE_PLACED_KEY = registerKey("flower_simbelmyne_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_MIRKWOOD_PLACED_KEY = registerKey("flower_mirkwood_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_ALFIRIN_PLACED_KEY = registerKey("flower_alfirin_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_ATHELAS_PLACED_KEY = registerKey("flower_athelas_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_NIPHREDIL_PLACED_KEY = registerKey("flower_niphredil_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_SWAMPMILKWEED_PLACED_KEY = registerKey("flower_swamp_milkweed_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_LILLYOFTHEVALLEY_PLACED_KEY = registerKey("flower_valley_lilly_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_ELANOR_PLACED_KEY = registerKey("flower_elanor_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_AEGLOS_PLACED_KEY = registerKey("flower_aeglos_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_LISSUIN_PLACED_KEY = registerKey("flower_lissuin_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_MALLOS_PLACED_KEY = registerKey("flower_mallos_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_BRAMBLES_PLACED_KEY = registerKey("flower_brambles_placed");

    public static final ResourceKey<PlacedFeature> PLACED_DECAY_BLOOM_MUSHROOMS = registerKey("decay_bloom_mushrooms");
    public static final ResourceKey<PlacedFeature> PLACED_BLOOM_DECAY_MUSHROOMS = registerKey("bloom_decay_mushrooms");

    public static final ResourceKey<PlacedFeature> PLACED_FANGORN_FALLEN_LOG = registerKey("fangorn_fallen_log");
//    public static final ResourceKey<PlacedFeature> PLACED_DEADWOOD_FALLEN_LOG = registerKey("deadwood_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_DEADWOOD_FALLEN_LOG = registerKey("deadwood_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_MIRKWOOD_FALLEN_LOG = registerKey("mirkwood_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_MALLORN_FALLEN_LOG = registerKey("mallorn_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_LEBETHRON_FALLEN_LOG = registerKey("lebethron_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_CULUMALDA_FALLEN_LOG = registerKey("culumalda_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_DWARVEN_MAPLE_FALLEN_LOG = registerKey("dwarven_maple_fallen_log");

    public static final ResourceKey<PlacedFeature> PLACED_FANGORN_FALLEN_LEAVES = registerKey("fangorn_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_MIRKWOOD_FALLEN_LEAVES = registerKey("mirkwood_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_MALLORN_FALLEN_LEAVES = registerKey("mallorn_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_CULUMALDA_FALLEN_LEAVES = registerKey("culumalda_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_LEBETHRON_FALLEN_LEAVES = registerKey("lebethron_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_DWARVEN_MAPLE_FALLEN_LEAVES = registerKey("dwarven_maple_fallen_leaves");

    public static final ResourceKey<PlacedFeature> PLACED_ROCKPILE = registerKey("rockpile");
    public static final ResourceKey<PlacedFeature> PLACED_STONE_SPIKE = registerKey("stone_spike");
    public static final ResourceKey<PlacedFeature> PLACED_RANDOM_RUBBLE = registerKey("random_rubble");
    public static final ResourceKey<PlacedFeature> PLACED_WEBS = registerKey("webs");
    public static final ResourceKey<PlacedFeature> PLACED_STUMPS = registerKey("stumps");

    public static final ResourceKey<PlacedFeature> MITHRIL_ORE_PLACED_KEY = registerKey("mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_MITHRIL_ORE_PLACED_KEY = registerKey("nether_mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MITHRIL_ORE_PLACED_KEY = registerKey("end_mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> MORGULIRON_ORE_PLACED_KEY = registerKey("morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_MORGULIRON_ORE_PLACED_KEY = registerKey("nether_morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MORGULIRON_ORE_PLACED_KEY = registerKey("end_morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> AMMOLITE_ORE_PLACED_KEY = registerKey("ammolite_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_AMMOLITE_ORE_PLACED_KEY = registerKey("nether_ammolite_ore_placed");
    public static final ResourceKey<PlacedFeature> END_AMMOLITE_ORE_PLACED_KEY = registerKey("end_ammolite_ore_placed");

    public static final ResourceKey<PlacedFeature> PLACED_COAL_ORE = registerKey("legacy_coal_ore");
    public static final ResourceKey<PlacedFeature> PLACED_IRON_ORE = registerKey("legacy_iron_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GOLD_ORE = registerKey("legacy_gold_ore");
    public static final ResourceKey<PlacedFeature> PLACED_REDSTONE_ORE = registerKey("legacy_redstone_ore");
    public static final ResourceKey<PlacedFeature> PLACED_DIAMOND_ORE = registerKey("legacy_diamond_ore");
    public static final ResourceKey<PlacedFeature> PLACED_LAPIS_ORE = registerKey("legacy_lapis_ore");
    public static final ResourceKey<PlacedFeature> PLACED_COPPER_ORE = registerKey("legacy_copper_ore");

    public static final ResourceKey<PlacedFeature> PLACED_SMALL_GRANITE = registerKey("small_granite");
    public static final ResourceKey<PlacedFeature> PLACED_SMALL_DIORITE = registerKey("small_diorite");
    public static final ResourceKey<PlacedFeature> PLACED_SMALL_ANDESITE = registerKey("small_andesite");
    public static final ResourceKey<PlacedFeature> PLACED_WOOD_ROOTS_SPREAD = registerKey("wood_roots");
    public static final ResourceKey<PlacedFeature> PLACED_PLANT_ROOTS = registerKey("plant_roots");
    public static final ResourceKey<PlacedFeature> PLACED_MUD_SPLATTER = registerKey("mud_splatter");

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static ImmutableList.Builder<PlacementModifier> tolkienFeatureCheckArea(AvoidLandmarkModifier filter, int rarity, PlacementModifier... extra) {
        return ImmutableList.<PlacementModifier>builder().add(extra).add(filter, RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> features = context.lookup(Registries.CONFIGURED_FEATURE);

        context.register(MALLORN_PLACED_KEY, new PlacedFeature(features.getOrThrow(MALLORN_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MALLORN.get())));
        context.register(HARDENED_MALLORN_PLACED_KEY, new PlacedFeature(features.getOrThrow(HARDENED_MALLORN_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MALLORN.get())));
        context.register(MIRKWOOD_PLACED_KEY, new PlacedFeature(features.getOrThrow(MIRKWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MIRKWOOD.get())));
        context.register(HARDENED_MIRKWOOD_PLACED_KEY, new PlacedFeature(features.getOrThrow(HARDENED_MIRKWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(80, 0.1f, 2),
                        TolkienBlocks.SAPLING_MIRKWOOD.get())));
        context.register(CULUMALDA_PLACED_KEY, new PlacedFeature(features.getOrThrow(CULUMALDA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_CULUMALDA.get())));
        context.register(HARDENED_CULUMALDA_PLACED_KEY, new PlacedFeature(features.getOrThrow(HARDENED_CULUMALDA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_CULUMALDA.get())));
        context.register(LEBETHRON_PLACED_KEY, new PlacedFeature(features.getOrThrow(LEBETHRON_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_LEBETHRON.get())));
        context.register(HARDENED_LEBETHRON_PLACED_KEY, new PlacedFeature(features.getOrThrow(HARDENED_LEBETHRON_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_LEBETHRON.get())));
        context.register(FANGORNOAK_PLACED_KEY, new PlacedFeature(features.getOrThrow(FANGORNOAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(25, 0.1f, 2),
                        TolkienBlocks.SAPLING_FANGORNOAK.get())));
        context.register(HARDENED_FANGORNOAK_PLACED_KEY, new PlacedFeature(features.getOrThrow(HARDENED_FANGORNOAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(25, 0.1f, 2),
                        TolkienBlocks.SAPLING_FANGORNOAK.get())));
        context.register(DEADWOOD_PLACED_KEY, new PlacedFeature(features.getOrThrow(DEADWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_DEADWOOD.get())));
        context.register(DWARVEN_PLACED_KEY, new PlacedFeature(features.getOrThrow(DWARVEN_MAPLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 2),
                        TolkienBlocks.SAPLING_DWARVEN_MAPLE.get())));
        context.register(HARDENED_DWARVEN_PLACED_KEY, new PlacedFeature(features.getOrThrow(HARDENED_DWARVEN_MAPLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 2),
                        TolkienBlocks.SAPLING_DWARVEN_MAPLE.get())));
        context.register(OLDFORESTOAK_PLACED_KEY, new PlacedFeature(features.getOrThrow(OLDFORESTOAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(35, 0.1f, 2),
                        Blocks.DARK_OAK_SAPLING)));

        context.register(MITHRIL_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(OVERWORLD_ORE_MITHRIL_KEY),
                TolkienOrePlacement.commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16)))));
        context.register(NETHER_MITHRIL_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(NETHER_ORE_MITHRIL_KEY),
                TolkienOrePlacement.commonOrePlacement(25, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16)))));
        context.register(END_MITHRIL_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(END_ORE_MITHRIL_KEY),
                TolkienOrePlacement.commonOrePlacement(25, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16)))));
        context.register(MORGULIRON_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(OVERWORLD_ORE_MORGULIRON_KEY),
                TolkienOrePlacement.commonOrePlacement(50, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80)))));
        context.register(NETHER_MORGULIRON_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(NETHER_ORE_MORGULIRON_KEY),
                TolkienOrePlacement.commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80)))));
        context.register(END_MORGULIRON_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(END_ORE_MORGULIRON_KEY),
                TolkienOrePlacement.commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80)))));
        context.register(AMMOLITE_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(OVERWORLD_ORE_AMMOLITE_KEY),
                TolkienOrePlacement.commonOrePlacement(100, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480)))));
        context.register(NETHER_AMMOLITE_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(NETHER_ORE_AMMOLITE_KEY),
                TolkienOrePlacement.commonOrePlacement(100, HeightRangePlacement.uniform(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480)))));
        context.register(END_AMMOLITE_ORE_PLACED_KEY, new PlacedFeature(features.getOrThrow(END_ORE_AMMOLITE_KEY),
                TolkienOrePlacement.commonOrePlacement(100, HeightRangePlacement.uniform(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480)))));

        context.register(PLACED_COAL_ORE, new PlacedFeature(features.getOrThrow(COAL_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(127)), InSquarePlacement.spread(), CountPlacement.of(20), BiomeFilter.biome()).build()));
        context.register(PLACED_IRON_ORE, new PlacedFeature(features.getOrThrow(IRON_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(63)), InSquarePlacement.spread(), CountPlacement.of(20), BiomeFilter.biome()).build()));
        context.register(PLACED_GOLD_ORE, new PlacedFeature(features.getOrThrow(GOLD_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(31)), InSquarePlacement.spread(), CountPlacement.of(2), BiomeFilter.biome()).build()));
        context.register(PLACED_REDSTONE_ORE, new PlacedFeature(features.getOrThrow(REDSTONE_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), InSquarePlacement.spread(), CountPlacement.of(8), BiomeFilter.biome()).build()));
        context.register(PLACED_DIAMOND_ORE, new PlacedFeature(features.getOrThrow(DIAMOND_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), InSquarePlacement.spread(), BiomeFilter.biome()).build()));
        context.register(PLACED_LAPIS_ORE, new PlacedFeature(features.getOrThrow(LAPIS_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(30)), InSquarePlacement.spread(), CountPlacement.of(2), BiomeFilter.biome()).build()));
        context.register(PLACED_COPPER_ORE, new PlacedFeature(features.getOrThrow(COPPER_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(96)), InSquarePlacement.spread(), CountPlacement.of(6), BiomeFilter.biome()).build()));

        context.register(FLOWER_SIMBELMYNE_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_SIMBELMYNE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_MIRKWOOD_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_MIRKWOOD_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_ALFIRIN_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_ALFIRIN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_ATHELAS_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_ATHELAS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_NIPHREDIL_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_NIPHREDIL_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_SWAMPMILKWEED_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_SWAMPMILKWEED_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_LILLYOFTHEVALLEY_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_LILLYOFTHEVALLEY_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_ELANOR_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_ELANOR_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_AEGLOS_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_AEGLOS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_LISSUIN_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_LISSUIN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_MALLOS_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_MALLOS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));
        context.register(FLOWER_BRAMBLES_PLACED_KEY, new PlacedFeature(features.getOrThrow(FLOWER_BRAMBLES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome())));

        context.register(PLACED_DECAY_BLOOM_MUSHROOMS, new PlacedFeature(features.getOrThrow(DECAY_BLOOM_MUSHROOMS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), CountPlacement.of(3), AvoidLandmarkModifier.checkUnderground(), BiomeFilter.biome()).build()));
        context.register(PLACED_BLOOM_DECAY_MUSHROOMS, new PlacedFeature(features.getOrThrow(BLOOM_DECAY_MUSHROOMS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), CountPlacement.of(3), AvoidLandmarkModifier.checkUnderground(), BiomeFilter.biome()).build()));

        context.register(PLACED_MALLORN_FALLEN_LOG, new PlacedFeature(features.getOrThrow(MALLORN_LOG), List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_MIRKWOOD_FALLEN_LOG, new PlacedFeature(features.getOrThrow(MIRKWOOD_LOG), List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_CULUMALDA_FALLEN_LOG, new PlacedFeature(features.getOrThrow(CULUMALDA_LOG), List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_LEBETHRON_FALLEN_LOG, new PlacedFeature(features.getOrThrow(LEBETHRON_LOG), List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_FANGORN_FALLEN_LOG, new PlacedFeature(features.getOrThrow(FANGORNOAK_LOG), List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_DEADWOOD_FALLEN_LOG, new PlacedFeature(features.getOrThrow(DEADWOOD_LOG), List.of(RarityFilter.onAverageOnceEvery(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));
        context.register(PLACED_DWARVEN_MAPLE_FALLEN_LOG, new PlacedFeature(features.getOrThrow(DWARVEN_LOG), List.of(RarityFilter.onAverageOnceEvery(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome())));

        context.register(PLACED_MALLORN_FALLEN_LEAVES, new PlacedFeature(features.getOrThrow(MALLORN_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build()));
        context.register(PLACED_MIRKWOOD_FALLEN_LEAVES, new PlacedFeature(features.getOrThrow(MIRKWOOD_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build()));
        context.register(PLACED_CULUMALDA_FALLEN_LEAVES, new PlacedFeature(features.getOrThrow(CULUMALDA_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build()));
        context.register(PLACED_LEBETHRON_FALLEN_LEAVES, new PlacedFeature(features.getOrThrow(LEBETHRON_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build()));
        context.register(PLACED_FANGORN_FALLEN_LEAVES, new PlacedFeature(features.getOrThrow(FANGORNOAK_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build()));
        context.register(PLACED_DWARVEN_MAPLE_FALLEN_LEAVES, new PlacedFeature(features.getOrThrow(DWARVEN_MAPLE_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build()));

        context.register(PLACED_ROCKPILE, new PlacedFeature(features.getOrThrow(ROCK_PILE), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(10), InSquarePlacement.spread(), BiomeFilter.biome()).build()));
        context.register(PLACED_RANDOM_RUBBLE, new PlacedFeature(features.getOrThrow(RANDOM_RUBBLE), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(10), InSquarePlacement.spread(), BiomeFilter.biome()).build()));
        context.register(PLACED_STONE_SPIKE, new PlacedFeature(features.getOrThrow(STONE_SPIKE), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(10), InSquarePlacement.spread(), BiomeFilter.biome()).build()));
        context.register(PLACED_WEBS, new PlacedFeature(features.getOrThrow(WEBS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.RANGE_BOTTOM_TO_MAX_TERRAIN_HEIGHT, CountPlacement.of(20), InSquarePlacement.spread(), BiomeFilter.biome()).build()));
        context.register(PLACED_STUMPS, new PlacedFeature(features.getOrThrow(STUMPS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(10), InSquarePlacement.spread(), BiomeFilter.biome()).build()));

        context.register(PLACED_SMALL_GRANITE, new PlacedFeature(features.getOrThrow(SMALL_GRANITE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), CountPlacement.of(5), BiomeFilter.biome()).build()));
        context.register(PLACED_SMALL_DIORITE, new PlacedFeature(features.getOrThrow(SMALL_DIORITE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), CountPlacement.of(5), BiomeFilter.biome()).build()));
        context.register(PLACED_SMALL_ANDESITE, new PlacedFeature(features.getOrThrow(SMALL_ANDESITE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), CountPlacement.of(5), BiomeFilter.biome()).build()));
        context.register(PLACED_WOOD_ROOTS_SPREAD, new PlacedFeature(features.getOrThrow(WOOD_ROOTS_SPREAD), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkUnderground(), 40, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))).build()));
        context.register(PLACED_PLANT_ROOTS, new PlacedFeature(features.getOrThrow(PLANT_ROOTS), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkUnderground(), 1, CountPlacement.of(4), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(10))).build()));
        context.register(PLACED_MUD_SPLATTER, new PlacedFeature(features.getOrThrow(MUD_SPLATTER), List.of(CountPlacement.of(1), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BiomeFilter.biome())));
    }
}