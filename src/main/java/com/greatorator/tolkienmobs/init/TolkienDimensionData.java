package com.greatorator.tolkienmobs.init;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import com.greatorator.tolkienmobs.world.registration.TolkienSurfaceRules;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.*;

import java.util.List;

public class TolkienDimensionData {
    public static final int SEALEVEL = 0;

    public static final ResourceKey<NoiseGeneratorSettings> TOLKIENMOBS_NOISE_GEN = ResourceKey.create(Registries.NOISE_SETTINGS, TolkienMobsMain.prefix("tolkienmobs_noise_gen"));
    public static final ResourceKey<NoiseGeneratorSettings> SKYLIGHT_NOISE_GEN = ResourceKey.create(Registries.NOISE_SETTINGS, TolkienMobsMain.prefix("skylight_noise_gen"));

    public static NoiseGeneratorSettings makeNoiseSettings(BootstrapContext<NoiseGeneratorSettings> context, boolean skylight) {
        HolderGetter<DensityFunction> densityFunctions = context.lookup(Registries.DENSITY_FUNCTION);
        DensityFunction finalDensity = new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(skylight ? TolkienDensityFunctions.SKYLIGHT_TERRAIN : TolkienDensityFunctions.FORESTED_TERRAIN));

        NoiseSettings tfNoise = NoiseSettings.create(
                -32,
                320,
                2,
                2
        );

        return new NoiseGeneratorSettings(
                tfNoise,
                Blocks.STONE.defaultBlockState(),
                skylight ? Blocks.AIR.defaultBlockState() : Blocks.WATER.defaultBlockState(),
                new NoiseRouter(
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        finalDensity,
                        finalDensity,
                        DensityFunctions.zero(),
                        DensityFunctions.zero(),
                        DensityFunctions.zero()
                ),
                TolkienSurfaceRules.tolkienSurface(),
                List.of(),
                TolkienDimensionData.SEALEVEL,
                false,
                false,
                false,
                false
        );
    }

    public static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
        context.register(TOLKIENMOBS_NOISE_GEN, makeNoiseSettings(context, false));
        context.register(SKYLIGHT_NOISE_GEN, makeNoiseSettings(context, true));
    }
}
