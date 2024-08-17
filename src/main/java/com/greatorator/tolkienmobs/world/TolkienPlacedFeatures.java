package com.greatorator.tolkienmobs.world;

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

public class TolkienPlacedFeatures {
    public static final ResourceKey<PlacedFeature> MALLORN_PLACED_KEY = registerKey("mallorn_placed");
    public static final ResourceKey<PlacedFeature> MIRKWOOD_PLACED_KEY = registerKey("mirkwood_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, MALLORN_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.MALLORN_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MALLORN.get()));
        register(context, MIRKWOOD_PLACED_KEY, configuredFeatures.getOrThrow(TolkienConfiguredFeatures.MIRKWOOD_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(3, 0.1f, 2),
                        TolkienBlocks.SAPLING_MIRKWOOD.get()));
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(MODID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}