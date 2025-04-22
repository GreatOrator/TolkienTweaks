package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.components.layer.BiomeDensitySource;
import com.greatorator.tolkienmobs.world.components.layer.BiomeLayerFactory;
import com.greatorator.tolkienmobs.world.components.layer.BiomeLayerType;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.Locale;

import static com.greatorator.tolkienmobs.world.registration.BiomeHelper.*;
import static com.greatorator.tolkienmobs.world.registration.BiomeMaker.*;


public class TolkienBiomes {
    public static final Registry<BiomeLayerType> BIOME_LAYER_TYPE = new RegistryBuilder<>(Keys.BIOME_LAYER_TYPE).create();
    public static final ResourceKey<Biome> MIRKWOOD = makeKey("mirkwood");
    public static final ResourceKey<Biome> MIRKWOOD_SPOOKY = makeKey("mirkwood_spooky");
    public static final ResourceKey<Biome> FANGORN = makeKey("fangorn_forest");
    public static final ResourceKey<Biome> MORDOR = makeKey("mordor");
    public static final ResourceKey<Biome> ASH_MOUNTAINS = makeKey("ash_mountains");
    public static final ResourceKey<Biome> MARSHES = makeKey("marshes");
    public static final ResourceKey<Biome> LORINAND = makeKey("lorinand");
    public static final ResourceKey<Biome> BARROW_DOWNS = makeKey("barrow_downs");
    public static final ResourceKey<Biome> OLD_FOREST = makeKey("old_forest");
    public static final ResourceKey<Biome> SHIRE = makeKey("shire");
    public static final ResourceKey<Biome> FIRIEN = makeKey("firien");
    public static final ResourceKey<Biome> HITHAEGLIR = makeKey("hithaeglir");
    public static final ResourceKey<Biome> HARADWAITH = makeKey("haradwaith");
    public static final ResourceKey<Biome> DAGORLAD = makeKey("dagorlad");
    public static final ResourceKey<Biome> GLADDEN = makeKey("gladden");
    public static final ResourceKey<Biome> IRON_HILLS = makeKey("iron_hills");

    public static final ResourceKey<Biome> STREAM = makeKey("stream");
    public static final ResourceKey<Biome> UNDERGROUND = makeKey("underground");
    public static final ResourceKey<Biome> LAKE = makeKey("lake");

    private static ResourceKey<Biome> makeKey(String name) {
        return ResourceKey.create(Registries.BIOME, TolkienMobsMain.prefix(name));
    }

    public static final class Keys {
        public static final String REGISTRY_NAMESPACE = "tolkienmobs";

        public static final ResourceKey<Registry<BiomeLayerType>> BIOME_LAYER_TYPE = ResourceKey.createRegistryKey(namedRegistry("biome_layer_type"));

        public static final ResourceKey<Registry<BiomeLayerFactory>> BIOME_STACK = ResourceKey.createRegistryKey(namedRegistry("biome_layer_stack"));
        public static final ResourceKey<Registry<BiomeDensitySource>> BIOME_TERRAIN_DATA = ResourceKey.createRegistryKey(namedRegistry("biome_terrain_data"));

        public static ResourceLocation namedRegistry(String name) {
            return ResourceLocation.fromNamespaceAndPath(REGISTRY_NAMESPACE, name.toLowerCase(Locale.ROOT));
        }}

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> featureGetter = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverGetter = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(MIRKWOOD, makeBiomeMirkwood(mirkwoodAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.6F)).waterColor(0).fogColor(5988193).foliageColorOverride(738353).grassColorOverride(738353), mirkwoodSpawning(), mirkwoodGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.25F).downfall(0.8F).build());
        context.register(MIRKWOOD_SPOOKY, makeBiomeMirkwood(mirkwoodAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.6F)).waterColor(0).fogColor(5988193).foliageColorOverride(738353).grassColorOverride(738353), mirkwoodSpawning(), mirkwoodSpookyGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.25F).downfall(0.8F).build());
        context.register(FANGORN, makeBiomeFangorn(leavesParticles(fangornAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(0x00D2FB).fogColor(0x5B5F61).foliageColorOverride(0x4EAD4E).grassColorOverride(0x4EAD4E), fangornForestSpawning(), fangornForestGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.7F).downfall(0.8F).build());
        context.register(MORDOR, makeBiomeMordor(desolateParticles(mordorAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(2.0F)).waterColor(0).fogColor(12638463).foliageColorOverride(2329).grassColorOverride(2329), mordorSpawning(), mordorGen(featureGetter, carverGetter)).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).build());
        context.register(ASH_MOUNTAINS, makeBiomeMordor(desolateParticles(mordorAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(2.0F)).waterColor(0).fogColor(12638463).foliageColorOverride(2329).grassColorOverride(2329), mordorSpawning(), mordorGen(featureGetter, carverGetter)).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).build());
        context.register(MARSHES, makeBiomeMarshes(marshesAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(14745540).fogColor(5988193).foliageColorOverride(6316071).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP), marshesSpawning(), marshesGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.9F).downfall(0.8F).build());
        context.register(LORINAND, makeBiomeLorinad(forestParticles(lorinadAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(0.6F)).waterColor(0).fogColor(5988193).foliageColorOverride(738353).grassColorOverride(738353), lorinadSpawning(), lorinadGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.25F).downfall(0.8F).build());
        context.register(BARROW_DOWNS, makeBiomeBarrowDowns(barrowAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(808080).fogColor(11119017).foliageColorOverride(14481884).grassColorOverride(14481884), barrowSpawning(), barrowGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.25F).downfall(0.8F).build());
        context.register(OLD_FOREST, makeBiomeOldForest(oldForestAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(54011).fogColor(5988193).foliageColorOverride(5163086).grassColorOverride(5163086), oldForestForestSpawning(), oldForestForestGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.7F).downfall(0.8F).build());
        context.register(SHIRE, makeBiomeShire(leavesParticles(shireAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(4159204).fogColor(10518688).foliageColorOverride(2292007).grassColorOverride(2292007), shireSpawning(), shireGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.9F).downfall(0.8F).build());
        context.register(FIRIEN, makeBiomeFirien(leavesParticles(firienAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(0.7F)).waterColor(4159204).waterFogColor(329011).fogColor(10518688).foliageColorOverride(2292007).grassColorOverride(2292007), firienSpawning(), firienGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.7F).downfall(0.8F).build());
        context.register(HITHAEGLIR, makeBiomeHithaelgir(hithaeglirAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.3F)).waterColor(4159204).waterFogColor(329011).fogColor(10518688).foliageColorOverride(2292007).grassColorOverride(2292007), hithaeglirSpawning(), hithaeglirGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.2F).downfall(0.3F).build());
        context.register(HARADWAITH, makeBiomeHaradwaith(haradwaithAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(2.0F)).waterColor(4159204).waterFogColor(329011).fogColor(12638463), haradwaithSpawning(), haradwaithGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(2.0F).downfall(0).build());
        context.register(DAGORLAD, makeBiomeDagorland(dagorladAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.4F)).waterColor(14596231).waterFogColor(2302743).fogColor(12638463).foliageColorOverride(14596231).grassColorOverride(14596231), dagorladSpawning(), dagorladGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.8F).downfall(0.4F).build());
        context.register(GLADDEN, makeBiomeGladden(gladdenAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(4159204).waterFogColor(329011).fogColor(10518688).grassColorOverride(14596231), gladdenSpawning(), gladdenGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.8F).downfall(0.4F).build());
        context.register(IRON_HILLS, makeBiomeIronHills(ironHillsAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.2F)).waterColor(4159204).waterFogColor(329011).fogColor(10518688).foliageColorOverride(2292007).grassColorOverride(2292007), ironHillsSpawning(), ironHillsGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.8F).downfall(0.4F).build());

        context.register(STREAM, biomeWithDefaults(defaultAmbientBuilder(), defaultMobSpawning(), streamsAndLakes(featureGetter, carverGetter, false)).temperature(0.5F).downfall(0.1F).build());
        context.register(LAKE, biomeWithDefaults(defaultAmbientBuilder(), defaultMobSpawning(), streamsAndLakes(featureGetter, carverGetter, true)).temperature(0.66F).downfall(1).build());
        context.register(UNDERGROUND, biomeWithDefaults(defaultAmbientBuilder(), undergroundMobSpawning(), undergroundGen(featureGetter, carverGetter)).temperature(0.7F).downfall(0.0F).build());
    }
}