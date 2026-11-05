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

    public static final ResourceKey<Biome> RHUN = createBiome("rhun", BiomeMaker::makeBiomeRhun, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.DRY, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> EASTERN_WASTE = createBiome("eastern_waste", BiomeMaker::makeBiomeEasternWaste, TolkienTags.Biomes.WASTELAND, TolkienTags.Biomes.DRY, TolkienTags.Biomes.HOSTILE, TolkienTags.Biomes.SPARSE);
    public static final ResourceKey<Biome> EREGION = createBiome("eregion", BiomeMaker::makeBiomeEregion, TolkienTags.Biomes.FOREST, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> DALE = createBiome("dale", BiomeMaker::makeBiomeDale, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> ITHILIEN = createBiome("ithilien", BiomeMaker::makeBiomeIthilien, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> ERED_MITHRIN = createBiome("ered_mithrin", BiomeMaker::makeBiomeEredMithrin, TolkienTags.Biomes.MOUNTAIN, TolkienTags.Biomes.COLD, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> RHUDAUR = createBiome("rhudaur", BiomeMaker::makeBiomeRhudaur, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.COLD, TolkienTags.Biomes.WINDSWEPT);
    public static final ResourceKey<Biome> ETTENMOORS = createBiome("ettenmoors", BiomeMaker::makeBiomeEttenmoors, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.COLD, TolkienTags.Biomes.WINDSWEPT);
    public static final ResourceKey<Biome> ERYN_VORN = createBiome("eryn_vorn", BiomeMaker::makeBiomeErynVorn, TolkienTags.Biomes.DARK_FOREST, TolkienTags.Biomes.TEMPERATE);
    public static final ResourceKey<Biome> BROWN_LANDS = createBiome("brown_lands", BiomeMaker::makeBiomeBrownLands, TolkienTags.Biomes.WASTELAND, TolkienTags.Biomes.DRY, TolkienTags.Biomes.SPARSE);
    public static final ResourceKey<Biome> EMYN_MUIL = createBiome("emyn_muil", BiomeMaker::makeBiomeEmynMuil, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.WINDSWEPT, TolkienTags.Biomes.SPARSE);
    public static final ResourceKey<Biome> BELFALAS = createBiome("belfalas", BiomeMaker::makeBiomeBelfalas, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
    public static final ResourceKey<Biome> KHAND = createBiome("khand", BiomeMaker::makeBiomeKhand, TolkienTags.Biomes.DESERT, TolkienTags.Biomes.DRY, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> UMBAR = createBiome("umbar", BiomeMaker::makeBiomeUmbar, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.DRY, TolkienTags.Biomes.HOSTILE);
    public static final ResourceKey<Biome> CARDOLAN = createBiome("cardolan", BiomeMaker::makeBiomeCardolan, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.WINDSWEPT);
    public static final ResourceKey<Biome> MINHIRIATH = createBiome("minhiriath", BiomeMaker::makeBiomeMinhiriath, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.SPARSE);
    public static final ResourceKey<Biome> ENEDWAITH = createBiome("enedwaith", BiomeMaker::makeBiomeEnedwaith, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.WET);
    public static final ResourceKey<Biome> DORWINION = createBiome("dorwinion", BiomeMaker::makeBiomeDorwinion, TolkienTags.Biomes.PLAINS, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL);
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
    /**
     * The Trollshaws: the upland wood between Rhudaur and the Misty Mountains, west of Rivendell. Beech
     * and oak on the slopes with pine above them, cut through by gorges and by the Hoarwell and the
     * Bruinen, and named for the hill-trolls that came down out of the Ettenmoors to live in it - which
     * is why it is tagged hostile rather than peaceful, temperate wood or not.
     */
    public static final ResourceKey<Biome> TROLLSHAWS = createBiome("trollshaws", BiomeMaker::makeBiomeTrollshaws, TolkienTags.Biomes.FOREST, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.WINDSWEPT, TolkienTags.Biomes.HOSTILE);
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
    /**
     * Angmar: the Witch-king's own country, and no longer the barrow field it used to borrow. A cold
     * rocky upland with thin soil, low scrub and wind-beaten spruce - subarctic, hostile, and the
     * enemy's ground, as the books describe the land under Carn Dum.
     */
    public static final ResourceKey<Biome> ANGMAR = createBiome("angmar", BiomeMaker::makeBiomeAngmar, TolkienTags.Biomes.WASTELAND, TolkienTags.Biomes.HOSTILE, TolkienTags.Biomes.WINDSWEPT, TolkienTags.Biomes.SPARSE);

    /**
     * Imladris, the hidden valley: a cool, semi-continental broadleaf hollow held inside an alpine gorge
     * under the western feet of the Misty Mountains. The walls above it are mountain, and the pine on
     * them is the mountain's; the floor is sheltered enough to be oak and beech over deep grass, with
     * gardens, terraces and loud water rather than a wild wood. It is tagged peaceful because the
     * valley is the one part of that country the hill-trolls never held.
     */
    public static final ResourceKey<Biome> IMLADRIS = createBiome("imladris", BiomeMaker::makeBiomeImladris, TolkienTags.Biomes.FOREST, TolkienTags.Biomes.TEMPERATE, TolkienTags.Biomes.PEACEFUL, TolkienTags.Biomes.MAGICAL);

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