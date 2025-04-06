package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.greatorator.tolkienmobs.world.components.chunkgenerators.TerrainColumn;
import it.unimi.dsi.fastutil.doubles.Double2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.doubles.Double2ObjectSortedMap;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

import java.util.List;
import java.util.function.Consumer;

public class BiomeMaker extends BiomeHelper{
    public static List<TerrainColumn> makeBiomeList(HolderGetter<Biome> biomeRegistry, Holder<Biome> undergroundBiome) {
        return List.of(
//                biomeColumnWithUnderground(3, 0.25F, biomeRegistry, TolkienBiomes.FANGORN, biomeRegistry.getOrThrow(TFBiomes.HIGHLANDS_UNDERGROUND)),
                biomeColumnToBedrock(5.5, 0.1F, biomeRegistry, TolkienBiomes.FANGORN),
                biomeColumnToBedrock(0.1F, 0.1F, biomeRegistry, TolkienBiomes.MORDOR)
        );
    }

    private static TerrainColumn biomeColumnWithUnderground(double noiseDepth, double noiseScale, HolderGetter<Biome> biomeRegistry, ResourceKey<Biome> key, Holder<Biome> undergroundBiome) {
        Holder.Reference<Biome> biomeHolder = biomeRegistry.getOrThrow(key);

        biomeHolder.bindKey(key);

        return makeColumn(DensityFunctions.constant(noiseDepth), DensityFunctions.constant(noiseScale), biomeHolder, treeMap -> {
            // This will put the transition boundary around Y-8
            treeMap.put(Math.min(noiseDepth - 1, -1), biomeHolder);
            treeMap.put(Math.min(noiseDepth - 3, -3), undergroundBiome);
        });
    }

    private static TerrainColumn biomeColumnToBedrock(double noiseDepth, double noiseScale, HolderGetter<Biome> biomeRegistry, ResourceKey<Biome> key) {
        Holder.Reference<Biome> biomeHolder = biomeRegistry.getOrThrow(key);

        biomeHolder.bindKey(key);

        return makeColumn(DensityFunctions.constant(noiseDepth), DensityFunctions.constant(noiseScale), biomeHolder, treeMap -> treeMap.put(0, biomeHolder));
    }

    private static TerrainColumn makeColumn(DensityFunction noiseDepth, DensityFunction noiseScale, Holder<Biome> biomeHolder, Consumer<Double2ObjectSortedMap<Holder<Biome>>> layerBuilder) {
        return new TerrainColumn(biomeHolder, Util.make(new Double2ObjectAVLTreeMap<>(), layerBuilder), noiseDepth, noiseScale);
    }
}