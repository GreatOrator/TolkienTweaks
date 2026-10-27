package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.world.registration.ArdaBiomeSource;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.BiomeSource;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.greatorator.tolkienmobs.TolkienMobsMain.MODID;

/** The biome sources the mod generates its own dimension with. */
public class TolkienBiomeSources {
    public static final DeferredRegister<MapCodec<? extends BiomeSource>> BIOME_SOURCES =
            DeferredRegister.create(Registries.BIOME_SOURCE, MODID);

    /** Arda's biomes: the overworld's climate grid, filled with the mod's biomes and banded by latitude. */
    public static final DeferredHolder<MapCodec<? extends BiomeSource>, MapCodec<ArdaBiomeSource>> ARDA =
            BIOME_SOURCES.register("arda", () -> ArdaBiomeSource.CODEC);
}
