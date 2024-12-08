package com.greatorator.tolkienmobs.world;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import com.greatorator.tolkienmobs.world.components.placements.TolkienOrePlacement;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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

    public static final ResourceKey<PlacedFeature> MITHRIL_ORE_PLACED_KEY = registerPlacedKey("mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_MITHRIL_ORE_PLACED_KEY = registerPlacedKey("nether_mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MITHRIL_ORE_PLACED_KEY = registerPlacedKey("end_mithril_ore_placed");
    public static final ResourceKey<PlacedFeature> MORGULIRON_ORE_PLACED_KEY = registerPlacedKey("morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_MORGULIRON_ORE_PLACED_KEY = registerPlacedKey("nether_morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> END_MORGULIRON_ORE_PLACED_KEY = registerPlacedKey("end_morguliron_ore_placed");
    public static final ResourceKey<PlacedFeature> AMMOLITE_ORE_PLACED_KEY = registerPlacedKey("ammolite_ore_placed");
    public static final ResourceKey<PlacedFeature> NETHER_AMMOLITE_ORE_PLACED_KEY = registerPlacedKey("nether_ammolite_ore_placed");
    public static final ResourceKey<PlacedFeature> END_AMMOLITE_ORE_PLACED_KEY = registerPlacedKey("end_ammolite_ore_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        registerPlaced(context, MALLORN_PLACED_KEY, configuredFeatures.getOrThrow(MALLORN_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MALLORN.get()));
        registerPlaced(context, MIRKWOOD_PLACED_KEY, configuredFeatures.getOrThrow(MIRKWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MIRKWOOD.get()));
        registerPlaced(context, CULUMALDA_PLACED_KEY, configuredFeatures.getOrThrow(CULUMALDA_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_CULUMALDA.get()));
        registerPlaced(context, LEBETHRON_PLACED_KEY, configuredFeatures.getOrThrow(LEBETHRON_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_LEBETHRON.get()));
        registerPlaced(context, FANGORNOAK_PLACED_KEY, configuredFeatures.getOrThrow(FANGORNOAK_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_FANGORNOAK.get()));
        registerPlaced(context, DEADWOOD_PLACED_KEY, configuredFeatures.getOrThrow(DEADWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_DEADWOOD.get()));
        registerPlaced(context, DWARVEN_PLACED_KEY, configuredFeatures.getOrThrow(DWARVEN_MAPLE_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_DWARVEN_MAPLE.get()));

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
    }

    private static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static void registerPlaced(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                       List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}