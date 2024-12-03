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
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

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
    }

    private static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static void registerPlaced(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                       List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}