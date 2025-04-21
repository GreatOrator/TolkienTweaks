package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.components.layer.*;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

public class TolkienLayerTypes {
    public static final DeferredRegister<BiomeLayerType> BIOME_LAYER_TYPES = DeferredRegister.create(TolkienBiomes.Keys.BIOME_LAYER_TYPE, MODID);
    public static final Codec<BiomeLayerType> CODEC = Codec.lazyInitialized(TolkienBiomes.BIOME_LAYER_TYPE::byNameCodec);

    public static final DeferredHolder<BiomeLayerType, BiomeLayerType> RANDOM_BIOMES = registerType("random_biomes", () -> () -> RandomBiomeLayer.Factory.CODEC);
    public static final DeferredHolder<BiomeLayerType, BiomeLayerType> FILTERED = registerType("filtered", () -> () -> FilteredBiomeLayer.Factory.CODEC);
    public static final DeferredHolder<BiomeLayerType, BiomeLayerType> SEAM = registerType("seam", () -> () -> SeamLayer.Factory.CODEC);
    public static final DeferredHolder<BiomeLayerType, BiomeLayerType> SMOOTH = registerType("smooth", () -> () -> SmoothLayer.Factory.CODEC);

    private static DeferredHolder<BiomeLayerType, BiomeLayerType> registerType(String name, Supplier<BiomeLayerType> factory) {
        return BIOME_LAYER_TYPES.register(name, factory);
    }}
