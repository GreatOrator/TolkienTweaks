package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import static com.greatorator.tolkienmobs.world.registration.BiomeHelper.*;


public class TolkienBiomes {
    public static final ResourceKey<Biome> FANGORN = makeKey("fangorn_forest");
    public static final ResourceKey<Biome> MORDOR = makeKey("mordor");
    public static final ResourceKey<Biome> MARSHES = makeKey("marshes");

    private static ResourceKey<Biome> makeKey(String name) {
        return ResourceKey.create(Registries.BIOME, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> featureGetter = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverGetter = context.lookup(Registries.CONFIGURED_CARVER);

        context.register(FANGORN, makeBiomeFangorn(forestParticles(fangornAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(0x00D2FB).fogColor(0x5B5F61).foliageColorOverride(0x4EAD4E).grassColorOverride(0x4EAD4E), fangornForestSpawning(), fangornForestGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.07F).downfall(0.8F).build());
        context.register(MORDOR, makeBiomeMordor(desolateParticles(mordorAmbientBuilder()).skyColor(getSkyColorWithTemperatureModifier(2.0F)).waterColor(0).fogColor(12638463).foliageColorOverride(2329).grassColorOverride(2329), mordorSpawning(), mordorGen(featureGetter, carverGetter)).hasPrecipitation(false).temperature(2.0F).downfall(0.0F).build());
        context.register(MARSHES, makeBiomeMarshes(marshesAmbientBuilder().skyColor(getSkyColorWithTemperatureModifier(0.8F)).waterColor(14745540).fogColor(5988193).foliageColorOverride(6316071).grassColorModifier(BiomeSpecialEffects.GrassColorModifier.SWAMP), marshesSpawning(), marshesGen(featureGetter, carverGetter)).hasPrecipitation(true).temperature(0.9F).downfall(0.8F).build());
    }
}