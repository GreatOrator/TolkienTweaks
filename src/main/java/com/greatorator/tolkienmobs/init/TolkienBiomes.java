package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

//import static com.greatorator.tolkienmobs.world.registration.BiomeHelper.*;


public class TolkienBiomes {
//    public static final ResourceKey<Biome> FOREST = makeKey("forest");

    private static ResourceKey<Biome> makeKey(String name) {
        return ResourceKey.create(Registries.BIOME, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<Biome> context) {
        HolderGetter<PlacedFeature> featureGetter = context.lookup(Registries.PLACED_FEATURE);
        HolderGetter<ConfiguredWorldCarver<?>> carverGetter = context.lookup(Registries.CONFIGURED_CARVER);

//        context.register(FOREST, biomeWithDefaults(fireflyParticles(defaultAmbientBuilder()), defaultMobSpawning(), twilightForestGen(featureGetter, carverGetter)).build());
    }
}