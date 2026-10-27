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
import net.minecraft.resources.Identifier;
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
    public static final ResourceKey<Biome> MIRKWOOD_SPOOKY = createBiome("mirkwood_spooky", BiomeMaker::makeBiomeMirkwoodSpooky, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> IRON_HILLS = createBiome("iron_hills", BiomeMaker::makeBiomeIronHills, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.COLD, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> BLUE_MOUNTAINS = createBiome("blue_mountains", BiomeMaker::makeBiomeBlueMountains, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> HITHAEGLIR = createBiome("hithaeglir", BiomeMaker::makeBiomeHithaelgir, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.ICY, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> FANGORN = createBiome("fangorn_forest", BiomeMaker::makeBiomeFangorn, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> MORDOR = createBiome("mordor", BiomeMaker::makeBiomeMordor, TolkienTags.Biomes.WASTELAND, TolkienTags.Biomes.HOT, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> NURN = createBiome("nurn", BiomeMaker::makeBiomeNurn, TolkienTags.Biomes.PLAINS);
    public static final ResourceKey<Biome> ASH_MOUNTAINS = createBiome("ash_mountains", BiomeMaker::makeBiomeAshMountain, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.DRY, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> MARSHES = createBiome("marshes", BiomeMaker::makeBiomeMarshes, TolkienTags.Biomes.SWAMP, TolkienTags.Biomes.WET, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> LORINAND = createBiome("lorinand", BiomeMaker::makeBiomeLorinad, TolkienTags.Biomes.CONIFEROUS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> BARROW_DOWNS = createBiome("barrow_downs", BiomeMaker::makeBiomeBarrowDowns, TolkienTags.Biomes.DEAD, TolkienTags.Biomes.COLD, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> OLD_FOREST = createBiome("old_forest", BiomeMaker::makeBiomeOldForest, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> SHIRE = createBiome("shire", BiomeMaker::makeBiomeShire, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> FIRIEN = createBiome("firien", BiomeMaker::makeBiomeFirien, TolkienTags.Biomes.FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> HARADWAITH = createBiome("haradwaith", BiomeMaker::makeBiomeHaradwaith, TolkienTags.Biomes.DESERT, TolkienTags.Biomes.DRY, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> DAGORLAD = createBiome("dagorlad", BiomeMaker::makeBiomeDagorland, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> GLADDEN = createBiome("gladden", BiomeMaker::makeBiomeGladden, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> FOROCHEL = createBiome("forochel", BiomeMaker::makeBiomeForochel, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.COLD, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> FOROCHEL_SLOPES = createBiome("forochel_slopes", BiomeMaker::makeBiomeForochelSlopes, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.COLD);

    /*
     * The seven countries the world map draws that none of the older biomes could stand for: the
     * wild wood of Rhovanion, the Mark's grass, the white wall of Ered Nimrais, the worn hills of
     * Dunland, and the three settled lands whose grass belongs to no other people. The Shire keeps
     * its own biome and only its own country, as the maps draw it, so Eriador's fields, the Grey
     * Havens' coast and the fields of Gondor are their own ground rather than borrowed grass.
     */
    public static final ResourceKey<Biome> WILD_WOOD = createBiome("wild_wood", BiomeMaker::makeBiomeWildWood, TolkienTags.Biomes.FOREST, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> ROHAN = createBiome("rohan", BiomeMaker::makeBiomeRohan, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> WHITE_MOUNTAINS = createBiome("white_mountains", BiomeMaker::makeBiomeWhiteMountains, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.COLD, TolkienTags.Biomes.SNOWY);
    public static final ResourceKey<Biome> DUNLAND = createBiome("dunland", BiomeMaker::makeBiomeDunland, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.WINDSWEPT);
    public static final ResourceKey<Biome> LINDON = createBiome("lindon", BiomeMaker::makeBiomeLindon, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> GONDOR = createBiome("gondor", BiomeMaker::makeBiomeGondor, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> ERIADOR = createBiome("eriador", BiomeMaker::makeBiomeEriador, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);

    public static void bootstrap(BootstrapContext<Biome> context) {
        BIOME_FACTORIES.forEach((key, factory) -> context.register(key,
                factory.generate(context.lookup(Registries.PLACED_FEATURE), context.lookup(Registries.CONFIGURED_CARVER))));
    }

    public static final class Keys {
        public static final String REGISTRY_NAMESPACE = "tolkienmobs";

        public static final ResourceKey<Registry<BiomeLayerType>> BIOME_LAYER_TYPE = ResourceKey.createRegistryKey(namedRegistry("biome_layer_type"));

        public static Identifier namedRegistry(String name) {
            return Identifier.fromNamespaceAndPath(REGISTRY_NAMESPACE, name.toLowerCase(Locale.ROOT));
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