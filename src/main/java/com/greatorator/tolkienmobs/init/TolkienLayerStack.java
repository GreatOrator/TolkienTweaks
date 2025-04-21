package com.greatorator.tolkienmobs.init;

import com.google.common.collect.ImmutableList;
import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.components.layer.*;
import com.greatorator.tolkienmobs.world.registration.BiomeMaker;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;

public class TolkienLayerStack {
    public static final Codec<BiomeLayerFactory> DISPATCH_CODEC = TolkienLayerTypes.CODEC.dispatch("layer_type", BiomeLayerFactory::getType, BiomeLayerType::getCodec);
    public static final Codec<Holder<BiomeLayerFactory>> HOLDER_CODEC = RegistryFileCodec.create(TolkienBiomes.Keys.BIOME_STACK, TolkienLayerStack.DISPATCH_CODEC, true);

    public static final ResourceKey<BiomeDensitySource> BIOME_GRID = ResourceKey.create(TolkienBiomes.Keys.BIOME_TERRAIN_DATA, TolkienMobsMain.prefix("biome_grid"));
    public static final ResourceKey<BiomeLayerFactory> BIOMES_ALONG_STREAMS = registerKey("biomes_along_streams");
    public static final ResourceKey<BiomeLayerFactory> RANDOM_FOREST_BIOMES = registerKey("random_forest_biomes");

    public static ResourceKey<BiomeLayerFactory> registerKey(String name) {
        return ResourceKey.create(TolkienBiomes.Keys.BIOME_STACK, TolkienMobsMain.prefix(name));
    }

    public static void bootstrap(BootstrapContext<BiomeLayerFactory> context) {
        BiomeLayerFactory biomes = new RandomBiomeLayer.Factory(1L, 15, ImmutableList.of(
                TolkienBiomes.MIRKWOOD,
                TolkienBiomes.FANGORN
        ), ImmutableList.of(
                TolkienBiomes.MIRKWOOD
        ));

//        biomes = new ZoomLayer.Factory(1000L, false, Holder.direct(biomes));
//        biomes = new ZoomLayer.Factory(1001L, false, Holder.direct(biomes));
//        biomes = new StabilizeLayer.Factory(700L, Holder.direct(biomes));
//        biomes = new ZoomLayer.Factory(1002L, false, Holder.direct(biomes));
//        biomes = new ZoomLayer.Factory(1003L, false, Holder.direct(biomes));
//        biomes = new ZoomLayer.Factory(1004L, false, Holder.direct(biomes));
//        biomes = new ZoomLayer.Factory(1005L, false, Holder.direct(biomes));

        Holder.Reference<BiomeLayerFactory> randomBiomes = context.register(RANDOM_FOREST_BIOMES, biomes);

        BiomeLayerFactory riverLayer = new SeamLayer.Factory(1L, TolkienBiomes.STREAM, List.of(TolkienBiomes.FANGORN, TolkienBiomes.MIRKWOOD), List.of(
                Pair.of(TolkienBiomes.FANGORN, TolkienBiomes.MIRKWOOD)
        ), randomBiomes);
        riverLayer = new SmoothLayer.Factory(7000L, Holder.direct(riverLayer));

        context.register(BIOMES_ALONG_STREAMS, new FilteredBiomeLayer.Factory(100L, TolkienBiomes.STREAM, Holder.direct(riverLayer), randomBiomes));
    }

        public static void bootstrapData(BootstrapContext<BiomeDensitySource> context) {
        HolderGetter<Biome> biomeRegistry = context.lookup(Registries.BIOME);

        context.register(BIOME_GRID, new BiomeDensitySource(
                BiomeMaker.makeBiomeList(biomeRegistry, biomeRegistry.getOrThrow(TolkienBiomes.UNDERGROUND)),
                context.lookup(TolkienBiomes.Keys.BIOME_STACK).getOrThrow(BIOMES_ALONG_STREAMS)));
    }
}
