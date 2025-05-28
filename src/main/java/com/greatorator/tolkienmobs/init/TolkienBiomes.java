package com.greatorator.tolkienmobs.init;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.components.layer.BiomeLayerType;
import com.greatorator.tolkienmobs.world.registration.BiomeMaker;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;


public class TolkienBiomes {
    public static final Registry<BiomeLayerType> BIOME_LAYER_TYPE = new RegistryBuilder<>(Keys.BIOME_LAYER_TYPE).create();
    public static final Map<ResourceKey<Biome>, BiomeFactory> BIOME_FACTORIES = new Reference2ObjectOpenHashMap<>();
    public static final Multimap<TagKey<Biome>, ResourceKey<Biome>> BIOMES_BY_TAG = Multimaps.newSetMultimap(new HashMap<>(), HashSet::new);

    public static final ResourceKey<Biome> MIRKWOOD = createBiome("mirkwood", BiomeMaker::makeBiomeMirkwood, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> MIRKWOOD_SPOOKY = createBiome("mirkwood_spooky", BiomeMaker::makeBiomeMirkwoodSpooky, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> IRON_HILLS = createBiome("iron_hills", BiomeMaker::makeBiomeIronHills, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.COLD);
    public static final ResourceKey<Biome> HITHAEGLIR = createBiome("hithaeglir", BiomeMaker::makeBiomeHithaelgir, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.ICY);
    public static final ResourceKey<Biome> FANGORN = createBiome("fangorn_forest", BiomeMaker::makeBiomeFangorn, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> MORDOR = createBiome("mordor", BiomeMaker::makeBiomeMordor, TolkienTags.Biomes.WASTELAND, TolkienTags.Biomes.HOT);
    public static final ResourceKey<Biome> ASH_MOUNTAINS = createBiome("ash_mountains", BiomeMaker::makeBiomeAshMountain, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.DRY);
    public static final ResourceKey<Biome> MARSHES = createBiome("marshes", BiomeMaker::makeBiomeMarshes, TolkienTags.Biomes.SWAMP, TolkienTags.Biomes.WET);
    public static final ResourceKey<Biome> LORINAND = createBiome("lorinand", BiomeMaker::makeBiomeLorinad, TolkienTags.Biomes.CONIFEROUS, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> BARROW_DOWNS = createBiome("barrow_downs", BiomeMaker::makeBiomeBarrowDowns, TolkienTags.Biomes.DEAD, TolkienTags.Biomes.COLD);
    public static final ResourceKey<Biome> OLD_FOREST = createBiome("old_forest", BiomeMaker::makeBiomeOldForest, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> SHIRE = createBiome("shire", BiomeMaker::makeBiomeShire, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> FIRIEN = createBiome("firien", BiomeMaker::makeBiomeFirien, TolkienTags.Biomes.FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> HARADWAITH = createBiome("haradwaith", BiomeMaker::makeBiomeHaradwaith, TolkienTags.Biomes.DESERT, TolkienTags.Biomes.DRY);
    public static final ResourceKey<Biome> DAGORLAD = createBiome("dagorlad", BiomeMaker::makeBiomeDagorland, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> GLADDEN = createBiome("gladden", BiomeMaker::makeBiomeGladden, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE);

    public static void bootstrap(BootstrapContext<Biome> context) {
    }

    public static final class Keys {
        public static final String REGISTRY_NAMESPACE = "tolkienmobs";

        public static final ResourceKey<Registry<BiomeLayerType>> BIOME_LAYER_TYPE = ResourceKey.createRegistryKey(namedRegistry("biome_layer_type"));

        public static ResourceLocation namedRegistry(String name) {
            return ResourceLocation.fromNamespaceAndPath(REGISTRY_NAMESPACE, name.toLowerCase(Locale.ROOT));
        }
    }

    @SafeVarargs
    private static ResourceKey<Biome> createBiome(String id, BiomeFactory biomeFactory, TagKey<Biome>... tags) {
        ResourceKey<Biome> biomeResourceKey = TolkienMobsMain.key(Registries.BIOME, id);
        BIOME_FACTORIES.put(biomeResourceKey, biomeFactory);

        for (TagKey<Biome> tag : tags)
            BIOMES_BY_TAG.put(tag, biomeResourceKey);

        return biomeResourceKey;
    }

    @FunctionalInterface
    public interface BiomeFactory {
        Biome generate(HolderGetter<PlacedFeature> placedFeatureHolderGetter, HolderGetter<ConfiguredWorldCarver<?>> worldCarverHolderGetter);
    }
}