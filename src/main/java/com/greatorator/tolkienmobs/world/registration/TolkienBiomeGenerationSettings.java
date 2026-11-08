package com.greatorator.tolkienmobs.world.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.FeatureSorter;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.*;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** Datagen builder that keeps shared features in a consistent order across biome borders. */
public class TolkienBiomeGenerationSettings extends BiomeGenerationSettings.Builder {
    private static final List<Map<ResourceKey<PlacedFeature>, Integer>> VANILLA_ORDER = vanillaFeatureOrder();

    public TolkienBiomeGenerationSettings(HolderGetter<PlacedFeature> features, HolderGetter<ConfiguredWorldCarver<?>> carvers) {
        super(features, carvers);
    }

    /**
     * Nothing but the mod's own places is scattered in Arda.
     * <p>
     * The map draws the ground, the water and the country, and everything standing on it is placed - a camp, a road,
     * a crag - so a feature from another world's lists is that world answering a question the drawing has already
     * answered: a wood grown through a build, grass over a road, an ore vein under a mountain that was drawn as rock.
     * Vanilla's water was the first of these to go, because a placed lake digs a bowl of its own and fills it to a
     * height it picks; this is the rest of them, and the same rule for another mod's features as for vanilla's.
     * <p>
     * The mod's own places are the country's furnish and stay: rubble, stumps, the mallorn, the flowers. What is left
     * out is everything not asked for by name - including vanilla's ores and its snow layer, since a feature is a
     * feature.
     */
    @Override
    public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
        if (!own(feature.unwrapKey().map(ResourceKey::identifier))) return this;
        super.addFeature(step, feature);
        return this;
    }

    /**
     * The same rule for a feature asked for by name, which is how vanilla's own lists ask.
     * <p>
     * {@code BiomeDefaultFeatures} does not hand the builder a holder: it hands it a {@link ResourceKey} and lets the
     * builder look the feature up itself. That lookup adds to the step's list without passing through the override
     * above, so every lake, monster room and ore vein vanilla's lists carry walked straight past the filter and into
     * the biome data - which is why regenerating the data put them back after they had been taken out. The rule was
     * right and simply never asked.
     */
    @Override
    public BiomeGenerationSettings.Builder addFeature(GenerationStep.Decoration step, ResourceKey<PlacedFeature> feature) {
        if (!own(Optional.of(feature.identifier()))) return this;
        return super.addFeature(step, feature);
    }

    /**
     * And nothing carves it either.
     * <p>
     * A carver is the same answer from the same other world, and the one that has cost the map most: it cuts where the
     * <em>noise's</em> terrain was, so a cave is dug through the drawn ground in places the drawing never put a cave -
     * a mouth opening in the middle of a field, a shaft under a road, a hole the road then has to be carried over. The
     * crags are templates sunk into their own ground and keep their chambers; the drawn rock stays drawn.
     */
    @Override
    public BiomeGenerationSettings.Builder addCarver(Holder<ConfiguredWorldCarver<?>> carver) {
        if (!own(carver.unwrapKey().map(ResourceKey::identifier))) return this;
        super.addCarver(carver);
        return this;
    }

    /** Whether a registry entry is the mod's own, which is the only kind Arda grows. */
    private static boolean own(java.util.Optional<net.minecraft.resources.Identifier> id) {
        return id.map(identifier -> identifier.getNamespace().equals(MODID)).orElse(false);
    }

    @Override
    public BiomeGenerationSettings build() {
        for (int step = 0; step < features.size(); step++) {
            Map<ResourceKey<PlacedFeature>, Integer> order = step < VANILLA_ORDER.size() ? VANILLA_ORDER.get(step) : Map.of();
            // Vanilla features must follow vanilla's dependency order. Our additions
            // follow them in a stable order shared by every Tolkien biome.
            Comparator<Holder<PlacedFeature>> comparator = Comparator
                    .comparingInt((Holder<PlacedFeature> feature) -> order.getOrDefault(feature.unwrapKey().orElseThrow(), Integer.MAX_VALUE))
                    .thenComparing(feature -> feature.unwrapKey().orElseThrow().identifier().toString());
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
                .sorted(Comparator.comparing(key -> key.identifier().toString()))
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
