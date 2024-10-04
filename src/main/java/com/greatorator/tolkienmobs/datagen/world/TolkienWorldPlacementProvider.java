package com.greatorator.tolkienmobs.datagen.world;

import com.greatorator.tolkienmobs.init.TolkienBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import java.util.List;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;
import static com.greatorator.tolkienmobs.datagen.world.TolkienWorldConfigurationProvider.*;

public class TolkienWorldPlacementProvider {
    public static final ResourceKey<PlacedFeature> MALLORN_PLACED_KEY = registerPlacedKey("mallorn_placed");
    public static final ResourceKey<PlacedFeature> MIRKWOOD_PLACED_KEY = registerPlacedKey("mirkwood_placed");
    public static final ResourceKey<PlacedFeature> CULUMALDA_PLACED_KEY = registerPlacedKey("culumalda_placed");
    public static final ResourceKey<PlacedFeature> LEBETHRON_PLACED_KEY = registerPlacedKey("lebethron_placed");
    public static final ResourceKey<PlacedFeature> FANGORNOAK_PLACED_KEY = registerPlacedKey("fangornoak_placed");
    public static final ResourceKey<PlacedFeature> DEADWOOD_PLACED_KEY = registerPlacedKey("deadwood_placed");
    public static final ResourceKey<PlacedFeature> DWARVEN_PLACED_KEY = registerPlacedKey("dwarven_placed");

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
    }

    private static ResourceKey<PlacedFeature> registerPlacedKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static void registerPlaced(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                       List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}