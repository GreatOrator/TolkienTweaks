package com.greatorator.tolkienmobs.world;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.components.placements.AvoidLandmarkModifier;
import com.greatorator.tolkienmobs.world.components.placements.TolkienOrePlacement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.VegetationFeatures;
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
import static com.greatorator.tolkienmobs.world.TolkienConfiguredFeatures.*;

public class TolkienPlacedFeatures {
    public static final ResourceKey<PlacedFeature> MALLORN_PLACED_KEY = registerPlacedKey("mallorn_placed");
    public static final ResourceKey<PlacedFeature> MIRKWOOD_PLACED_KEY = registerPlacedKey("mirkwood_placed");
    public static final ResourceKey<PlacedFeature> CULUMALDA_PLACED_KEY = registerPlacedKey("culumalda_placed");
    public static final ResourceKey<PlacedFeature> LEBETHRON_PLACED_KEY = registerPlacedKey("lebethron_placed");
    public static final ResourceKey<PlacedFeature> FANGORNOAK_PLACED_KEY = registerPlacedKey("fangornoak_placed");
    public static final ResourceKey<PlacedFeature> DEADWOOD_PLACED_KEY = registerPlacedKey("deadwood_placed");
    public static final ResourceKey<PlacedFeature> DWARVEN_PLACED_KEY = registerPlacedKey("dwarven_placed");
    public static final ResourceKey<PlacedFeature> OLDFORESTOAK_PLACED_KEY = registerPlacedKey("oldforestoak_placed");

    public static final ResourceKey<PlacedFeature> FLOWER_SIMBELMYNE_PLACED_KEY = registerPlacedKey("flower_simbelmyne_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_MIRKWOOD_PLACED_KEY = registerPlacedKey("flower_mirkwood_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_ALFIRIN_PLACED_KEY = registerPlacedKey("flower_alfirin_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_ATHELAS_PLACED_KEY = registerPlacedKey("flower_athelas_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_NIPHREDIL_PLACED_KEY = registerPlacedKey("flower_niphredil_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_SWAMPMILKWEED_PLACED_KEY = registerPlacedKey("flower_swamp_milkweed_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_LILLYOFTHEVALLEY_PLACED_KEY = registerPlacedKey("flower_valley_lilly_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_ELANOR_PLACED_KEY = registerPlacedKey("flower_elanor_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_AEGLOS_PLACED_KEY = registerPlacedKey("flower_aeglos_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_LISSUIN_PLACED_KEY = registerPlacedKey("flower_lissuin_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_MALLOS_PLACED_KEY = registerPlacedKey("flower_mallos_placed");
    public static final ResourceKey<PlacedFeature> FLOWER_BRAMBLES_PLACED_KEY = registerPlacedKey("flower_brambles_placed");

    public static final ResourceKey<PlacedFeature> PLACED_DECAY_BLOOM_MUSHROOMS = registerPlacedKey("decay_bloom_mushrooms");
    public static final ResourceKey<PlacedFeature> PLACED_BLOOM_DECAY_MUSHROOMS = registerPlacedKey("bloom_decay_mushrooms");

    public static final ResourceKey<PlacedFeature> PLACED_FANGORN_FALLEN_LOG = registerPlacedKey("fangorn_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_DEADWOOD_FALLEN_LOG = registerPlacedKey("deadwood_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_MIRKWOOD_FALLEN_LOG = registerPlacedKey("mirkwood_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_MALLORN_FALLEN_LOG = registerPlacedKey("mallorn_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_LEBETHRON_FALLEN_LOG = registerPlacedKey("lebethron_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_CULUMALDA_FALLEN_LOG = registerPlacedKey("culumalda_fallen_log");
    public static final ResourceKey<PlacedFeature> PLACED_DWARVEN_MAPLE_FALLEN_LOG = registerPlacedKey("dwarven_maple_fallen_log");

    public static final ResourceKey<PlacedFeature> PLACED_FANGORN_FALLEN_LEAVES = registerPlacedKey("fangorn_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_MIRKWOOD_FALLEN_LEAVES = registerPlacedKey("mirkwood_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_MALLORN_FALLEN_LEAVES = registerPlacedKey("mallorn_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_CULUMALDA_FALLEN_LEAVES = registerPlacedKey("culumalda_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_LEBETHRON_FALLEN_LEAVES = registerPlacedKey("lebethron_fallen_leaves");
    public static final ResourceKey<PlacedFeature> PLACED_DWARVEN_MAPLE_FALLEN_LEAVES = registerPlacedKey("dwarven_maple_fallen_leaves");

    public static final ResourceKey<PlacedFeature> PLACED_ROCKPILE = registerPlacedKey("rockpile");
    public static final ResourceKey<PlacedFeature> PLACED_STONE_SPIKE = registerPlacedKey("stone_spike");
    public static final ResourceKey<PlacedFeature> PLACED_RANDOM_RUBBLE = registerPlacedKey("random_rubble");
    public static final ResourceKey<PlacedFeature> PLACED_WEBS = registerPlacedKey("webs");
    public static final ResourceKey<PlacedFeature> PLACED_STUMPS = registerPlacedKey("stumps");

    public static final ResourceKey<PlacedFeature> MITHRIL_ORE_PLACED_KEY = registerPlacedKey("mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_MITHRIL_ORE_PLACED_KEY = registerPlacedKey("nether_mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MITHRIL_ORE_PLACED_KEY = registerPlacedKey("end_mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> MORGULIRON_ORE_PLACED_KEY = registerPlacedKey("morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_MORGULIRON_ORE_PLACED_KEY = registerPlacedKey("nether_morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MORGULIRON_ORE_PLACED_KEY = registerPlacedKey("end_morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> AMMOLITE_ORE_PLACED_KEY = registerPlacedKey("ammolite_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_AMMOLITE_ORE_PLACED_KEY = registerPlacedKey("nether_ammolite_ore_placed");
    public static final ResourceKey<PlacedFeature> END_AMMOLITE_ORE_PLACED_KEY = registerPlacedKey("end_ammolite_ore_placed");

    public static final ResourceKey<PlacedFeature> PLACED_COAL_ORE = registerPlacedKey("legacy_coal_ore");
    public static final ResourceKey<PlacedFeature> PLACED_IRON_ORE = registerPlacedKey("legacy_iron_ore");
    public static final ResourceKey<PlacedFeature> PLACED_GOLD_ORE = registerPlacedKey("legacy_gold_ore");
    public static final ResourceKey<PlacedFeature> PLACED_REDSTONE_ORE = registerPlacedKey("legacy_redstone_ore");
    public static final ResourceKey<PlacedFeature> PLACED_DIAMOND_ORE = registerPlacedKey("legacy_diamond_ore");
    public static final ResourceKey<PlacedFeature> PLACED_LAPIS_ORE = registerPlacedKey("legacy_lapis_ore");
    public static final ResourceKey<PlacedFeature> PLACED_COPPER_ORE = registerPlacedKey("legacy_copper_ore");

    public static final ResourceKey<PlacedFeature> PLACED_SMALL_GRANITE = registerPlacedKey("small_granite");
    public static final ResourceKey<PlacedFeature> PLACED_SMALL_DIORITE = registerPlacedKey("small_diorite");
    public static final ResourceKey<PlacedFeature> PLACED_SMALL_ANDESITE = registerPlacedKey("small_andesite");
    public static final ResourceKey<PlacedFeature> PLACED_WOOD_ROOTS_SPREAD = registerPlacedKey("wood_roots");
    public static final ResourceKey<PlacedFeature> PLACED_PLANT_ROOTS = registerPlacedKey("plant_roots");

    private static ImmutableList.Builder<PlacementModifier> tolkienFeatureCheckArea(AvoidLandmarkModifier filter, int rarity, PlacementModifier... extra) {
        return ImmutableList.<PlacementModifier>builder().add(extra).add(filter, RarityFilter.onAverageOnceEvery(rarity), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP, BiomeFilter.biome());
    }

    private static ImmutableList.Builder<PlacementModifier> hollowLog(AvoidLandmarkModifier filter) {
        return ImmutableList.<PlacementModifier>builder().add(RarityFilter.onAverageOnceEvery(40), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, filter, BiomeFilter.biome());
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        Holder<ConfiguredFeature<?, ?>> grassConfig = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_GRASS);
        Holder<ConfiguredFeature<?, ?>> tallGrassConfig = configuredFeatures.getOrThrow(VegetationFeatures.PATCH_TALL_GRASS);

        registerPlaced(context, MALLORN_PLACED_KEY, configuredFeatures.getOrThrow(MALLORN_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MALLORN.get()));
        registerPlaced(context, MIRKWOOD_PLACED_KEY, configuredFeatures.getOrThrow(MIRKWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(50, 0.1f, 2),
                        TolkienBlocks.SAPLING_MIRKWOOD.get()));
        registerPlaced(context, CULUMALDA_PLACED_KEY, configuredFeatures.getOrThrow(CULUMALDA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_CULUMALDA.get()));
        registerPlaced(context, LEBETHRON_PLACED_KEY, configuredFeatures.getOrThrow(LEBETHRON_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_LEBETHRON.get()));
        registerPlaced(context, FANGORNOAK_PLACED_KEY, configuredFeatures.getOrThrow(FANGORNOAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(25, 0.1f, 2),
                        TolkienBlocks.SAPLING_FANGORNOAK.get()));
        registerPlaced(context, DEADWOOD_PLACED_KEY, configuredFeatures.getOrThrow(DEADWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_DEADWOOD.get()));
        registerPlaced(context, DWARVEN_PLACED_KEY, configuredFeatures.getOrThrow(DWARVEN_MAPLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(1, 0.1f, 2),
                        TolkienBlocks.SAPLING_DWARVEN_MAPLE.get()));
        registerPlaced(context, OLDFORESTOAK_PLACED_KEY, configuredFeatures.getOrThrow(OLDFORESTOAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(35, 0.1f, 2),
                        Blocks.DARK_OAK_SAPLING));

        registerPlaced(context, MITHRIL_ORE_PLACED_KEY, configuredFeatures.getOrThrow(OVERWORLD_ORE_MITHRIL_KEY),
                TolkienOrePlacement.commonOrePlacement(25, HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16))));
        registerPlaced(context, NETHER_MITHRIL_ORE_PLACED_KEY, configuredFeatures.getOrThrow(NETHER_ORE_MITHRIL_KEY),
                TolkienOrePlacement.commonOrePlacement(25, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16))));
        registerPlaced(context, END_MITHRIL_ORE_PLACED_KEY, configuredFeatures.getOrThrow(END_ORE_MITHRIL_KEY),
                TolkienOrePlacement.commonOrePlacement(25, HeightRangePlacement.uniform(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(16))));
        registerPlaced(context, MORGULIRON_ORE_PLACED_KEY, configuredFeatures.getOrThrow(OVERWORLD_ORE_MORGULIRON_KEY),
                TolkienOrePlacement.commonOrePlacement(50, HeightRangePlacement.triangle(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80))));
        registerPlaced(context, NETHER_MORGULIRON_ORE_PLACED_KEY, configuredFeatures.getOrThrow(NETHER_ORE_MORGULIRON_KEY),
                TolkienOrePlacement.commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80))));
        registerPlaced(context, END_MORGULIRON_ORE_PLACED_KEY, configuredFeatures.getOrThrow(END_ORE_MORGULIRON_KEY),
                TolkienOrePlacement.commonOrePlacement(50, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(80))));
        registerPlaced(context, AMMOLITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(OVERWORLD_ORE_AMMOLITE_KEY),
                TolkienOrePlacement.commonOrePlacement(100, HeightRangePlacement.triangle(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480))));
        registerPlaced(context, NETHER_AMMOLITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(NETHER_ORE_AMMOLITE_KEY),
                TolkienOrePlacement.commonOrePlacement(100, HeightRangePlacement.uniform(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480))));
        registerPlaced(context, END_AMMOLITE_ORE_PLACED_KEY, configuredFeatures.getOrThrow(END_ORE_AMMOLITE_KEY),
                TolkienOrePlacement.commonOrePlacement(100, HeightRangePlacement.uniform(VerticalAnchor.absolute(-16), VerticalAnchor.absolute(480))));

        registerPlaced(context, PLACED_COAL_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.COAL_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(127)), InSquarePlacement.spread(), CountPlacement.of(20), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_IRON_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.IRON_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(63)), InSquarePlacement.spread(), CountPlacement.of(20), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_GOLD_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.GOLD_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(31)), InSquarePlacement.spread(), CountPlacement.of(2), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_REDSTONE_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.REDSTONE_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), InSquarePlacement.spread(), CountPlacement.of(8), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_DIAMOND_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.DIAMOND_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), InSquarePlacement.spread(), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_LAPIS_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.LAPIS_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(30)), InSquarePlacement.spread(), CountPlacement.of(2), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_COPPER_ORE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.COPPER_ORE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(96)), InSquarePlacement.spread(), CountPlacement.of(6), BiomeFilter.biome()).build());

        registerPlaced(context, FLOWER_SIMBELMYNE_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_SIMBELMYNE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_MIRKWOOD_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_MIRKWOOD_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_ALFIRIN_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_ALFIRIN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_ATHELAS_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_ATHELAS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_NIPHREDIL_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_NIPHREDIL_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_SWAMPMILKWEED_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_SWAMPMILKWEED_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_LILLYOFTHEVALLEY_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_LILLYOFTHEVALLEY_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_ELANOR_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_ELANOR_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_AEGLOS_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_AEGLOS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_LISSUIN_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_LISSUIN_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_MALLOS_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_MALLOS_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));
        registerPlaced(context, FLOWER_BRAMBLES_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.FLOWER_BRAMBLES_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        registerPlaced(context, PLACED_DECAY_BLOOM_MUSHROOMS, configuredFeatures.getOrThrow(DECAY_BLOOM_MUSHROOMS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), CountPlacement.of(3), AvoidLandmarkModifier.checkUnderground(), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_BLOOM_DECAY_MUSHROOMS, configuredFeatures.getOrThrow(BLOOM_DECAY_MUSHROOMS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(15)), CountPlacement.of(3), AvoidLandmarkModifier.checkUnderground(), BiomeFilter.biome()).build());

        registerPlaced(context, PLACED_MALLORN_FALLEN_LOG, configuredFeatures.getOrThrow(MALLORN_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());
        registerPlaced(context, PLACED_MIRKWOOD_FALLEN_LOG, configuredFeatures.getOrThrow(MIRKWOOD_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());
        registerPlaced(context, PLACED_CULUMALDA_FALLEN_LOG, configuredFeatures.getOrThrow(CULUMALDA_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());
        registerPlaced(context, PLACED_LEBETHRON_FALLEN_LOG, configuredFeatures.getOrThrow(LEBETHRON_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());
        registerPlaced(context, PLACED_FANGORN_FALLEN_LOG, configuredFeatures.getOrThrow(FANGORNOAK_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());
        registerPlaced(context, PLACED_DEADWOOD_FALLEN_LOG, configuredFeatures.getOrThrow(DEADWOOD_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());
        registerPlaced(context, PLACED_DWARVEN_MAPLE_FALLEN_LOG, configuredFeatures.getOrThrow(DWARVEN_SMALL_LOG), hollowLog(AvoidLandmarkModifier.checkSurface()).build());

        registerPlaced(context, PLACED_MALLORN_FALLEN_LEAVES, configuredFeatures.getOrThrow(MALLORN_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build());
        registerPlaced(context, PLACED_MIRKWOOD_FALLEN_LEAVES, configuredFeatures.getOrThrow(MIRKWOOD_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build());
        registerPlaced(context, PLACED_CULUMALDA_FALLEN_LEAVES, configuredFeatures.getOrThrow(CULUMALDA_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build());
        registerPlaced(context, PLACED_LEBETHRON_FALLEN_LEAVES, configuredFeatures.getOrThrow(LEBETHRON_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build());
        registerPlaced(context, PLACED_FANGORN_FALLEN_LEAVES, configuredFeatures.getOrThrow(FANGORNOAK_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build());
        registerPlaced(context, PLACED_DWARVEN_MAPLE_FALLEN_LEAVES, configuredFeatures.getOrThrow(DWARVEN_MAPLE_FALLEN_LEAVES), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkSurface(), 1).build());

        registerPlaced(context, PLACED_ROCKPILE, configuredFeatures.getOrThrow(ROCK_PILE), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(30), InSquarePlacement.spread(), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_RANDOM_RUBBLE, configuredFeatures.getOrThrow(RANDOM_RUBBLE), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(40), InSquarePlacement.spread(), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_STONE_SPIKE, configuredFeatures.getOrThrow(STONE_SPIKE), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(30), InSquarePlacement.spread(), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_WEBS, configuredFeatures.getOrThrow(WEBS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(300), InSquarePlacement.spread(), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_STUMPS, configuredFeatures.getOrThrow(STUMPS), ImmutableList.<PlacementModifier>builder().add(PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(50), InSquarePlacement.spread(), BiomeFilter.biome()).build());

        registerPlaced(context, PLACED_SMALL_GRANITE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.SMALL_GRANITE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), CountPlacement.of(5), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_SMALL_DIORITE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.SMALL_DIORITE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), CountPlacement.of(5), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_SMALL_ANDESITE, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.SMALL_ANDESITE), ImmutableList.<PlacementModifier>builder().add(HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(64)), RarityFilter.onAverageOnceEvery(10), InSquarePlacement.spread(), CountPlacement.of(5), BiomeFilter.biome()).build());
        registerPlaced(context, PLACED_WOOD_ROOTS_SPREAD, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.WOOD_ROOTS_SPREAD), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkUnderground(), 40, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(0))).build());
        registerPlaced(context, PLACED_PLANT_ROOTS, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.PLANT_ROOTS), tolkienFeatureCheckArea(AvoidLandmarkModifier.checkUnderground(), 1, CountPlacement.of(4), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(10))).build());
    }

    private static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static void registerPlaced(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                       List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}