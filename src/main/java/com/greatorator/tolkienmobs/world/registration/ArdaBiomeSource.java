package com.greatorator.tolkienmobs.world.registration;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.util.stream.Stream;

/**
 * Arda's biomes: the overworld's climate grid with this mod's biomes in it, banded north to south
 * by latitude.
 *
 * <p>Latitude is applied here because 26.1's overworld temperature is a noise field with no
 * latitude in it at all - the overworld has no climate zones, which is why a jungle can stand
 * beside a snowy plain - so there is nothing else for a band to hang on. Arda measures how far
 * north or south the column is and hands the grid a temperature of its own, while humidity,
 * erosion, continentalness and depth keep coming from the noise: latitude says how warm a place
 * is, the noise says how wet and how steep, and {@link ArdaClimate} says who lives there.
 *
 * <p>This is a plain {@link BiomeSource} rather than a {@link MultiNoiseBiomeSource} on purpose.
 * TerraBlender treats every multi-noise source in every dimension as an overworld it may seed with
 * its regions, and this mod's region carries the whole modified vanilla layout with it; a
 * dimension meant to hold nothing but this mod's biomes cannot be one. Arda's dimension type is
 * still tagged as a TerraBlender overworld region - see {@code data/terrablender/tags/
 * dimension_type/overworld_regions.json} - because that tag is what selects the mod's surface
 * rules for its biomes, and this source is what keeps the regions out.
 */
public class ArdaBiomeSource extends BiomeSource {
    /**
     * The biome list is serialized exactly as a multi-noise source's, so the generated dimension
     * reads the way every other one does.
     */
    public static final MapCodec<ArdaBiomeSource> CODEC =
            MultiNoiseBiomeSource.DIRECT_CODEC.xmap(ArdaBiomeSource::new, ArdaBiomeSource::parameters);

    /**
     * How far north or south of the world's middle the climate runs from temperate to icy or hot.
     * A vanilla temperature band is two fifths of the range, so at this span a band is 2,400 blocks
     * wide and all of Arda's climates lie within a short flight of its centre. The bands are
     * straight lines either side of that centre, so where the snow starts is one number to change.
     */
    public static final int BAND_HALF_SPAN = 6_000;

    private final Climate.ParameterList<Holder<Biome>> parameters;

    public ArdaBiomeSource(Climate.ParameterList<Holder<Biome>> parameters) {
        this.parameters = parameters;
    }

    public Climate.ParameterList<Holder<Biome>> parameters() {
        return this.parameters;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.parameters.values().stream().map(Pair::getSecond);
    }

    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        Climate.TargetPoint sampled = sampler.sample(quartX, quartY, quartZ);
        Climate.TargetPoint banded = new Climate.TargetPoint(
                Climate.quantizeCoord(temperatureAt(QuartPos.toBlock(quartZ))),
                sampled.humidity(),
                sampled.continentalness(),
                sampled.erosion(),
                sampled.depth(),
                sampled.weirdness());
        return this.parameters.findValue(banded);
    }

    /**
     * Arda's temperature at a block of latitude: icy at the far north of the world, hot at its
     * southern edge, and temperate at the middle, which is where the maps put Middle-earth.
     */
    public static float temperatureAt(int z) {
        return Mth.clamp(z / (float) BAND_HALF_SPAN, -1.0F, 1.0F);
    }
}
