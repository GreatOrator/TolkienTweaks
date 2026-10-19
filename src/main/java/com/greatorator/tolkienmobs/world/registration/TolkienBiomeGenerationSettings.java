package com.greatorator.tolkienmobs.world.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.*;

/** Datagen builder that keeps shared features in a consistent order across biome borders. */
public class TolkienBiomeGenerationSettings extends BiomeGenerationSettings.Builder {
    private static final List<Map<ResourceKey<PlacedFeature>, Integer>> VANILLA_ORDER = vanillaFeatureOrder();

    public TolkienBiomeGenerationSettings(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        super(features, carvers);
    }

    @Override
    public BiomeGenerationSettings build() {
        for (int step = 0; step < features.size(); step++) {
            Map<ResourceKey<PlacedFeature>, Integer> order = step < VANILLA_ORDER.size() ? VANILLA_ORDER.get(step) : Map.of();
            // Vanilla features must follow vanilla's dependency order. Our additions
            // follow them in a stable order shared by every Tolkien biome.
            Comparator<Holder<PlacedFeature>> comparator = Comparator
                    .comparingInt((Holder<PlacedFeature> feature) -> order.getOrDefault(feature.unwrapKey().orElseThrow(), Integer.MAX_VALUE))
                    .thenComparing(feature -> feature.unwrapKey().orElseThrow().location().toString());
            features.set(step, new ArrayList<>(features.get(step).stream().distinct().sorted(comparator).toList()));
        }
        return super.build();
    }

    private static List<Map<ResourceKey<PlacedFeature>, Integer>> vanillaFeatureOrder() {
        // Derive the order from this Minecraft version rather than maintaining a
        // hard-coded list. This builder is used only when generating biome JSON.
        var vanilla = VanillaRegistries.createLookup();
        var biomes = vanilla.lookupOrThrow(Registries.BIOME);
        var overworld = MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD.usedBiomes()
                .sorted(Comparator.comparing(key -> key.location().toString()))
                .map(biomes::getOrThrow).toList();
        var steps = FeatureSorter.buildFeaturesPerStep(overworld, biome -> biome.value().getGenerationSettings().features(), true);
        Map<PlacedFeature, ResourceKey<PlacedFeature>> keys = new IdentityHashMap<>();
        vanilla.lookupOrThrow(Registries.PLACED_FEATURE).listElements().forEach(holder -> keys.put(holder.value(), holder.key()));
        List<Map<ResourceKey<PlacedFeature>, Integer>> result = new ArrayList<>();
        for (var step : steps) {
            Map<ResourceKey<PlacedFeature>, Integer> order = new HashMap<>();
            for (int i = 0; i < step.features().size(); i++) {
                order.put(keys.get(step.features().get(i)), i);
            }
            result.add(Map.copyOf(order));
        }
        return List.copyOf(result);
    }
}
