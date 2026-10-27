package com.greatorator.tolkienmobs.world.registration;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Arda's biomes: whatever {@link ArdaMap} draws at a column.
 *
 * <p>This is a plain {@link BiomeSource} and not a {@link MultiNoiseBiomeSource} on purpose.
 * TerraBlender treats every multi-noise source in every dimension as an overworld it may seed with
 * its regions, and this mod's region carries the whole modified vanilla layout with it; a dimension
 * meant to hold nothing but this mod's biomes and the map's own seas cannot be one. Arda's dimension
 * type is still tagged {@code terrablender:overworld_regions} - see {@code data/terrablender/tags/
 * dimension_type/overworld_regions.json} - because that tag is what selects the mod's surface rules
 * for its biomes, and this source is what keeps the regions out.
 *
 * <p>The climate sampler is still used, but only for the warp: two of the sampler's own noises move
 * the lookup a couple of hundred blocks about, so the drawn borders come out organic rather than
 * ruled. The generator warps with the same two noises, which is what keeps a drawn sea's water on
 * the drawn sea's ground.
 */
public class ArdaBiomeSource extends BiomeSource {
    /** The biomes the map can name that are not countries: a river's bed and a shore. */
    private static final List<ResourceKey<Biome>> OVERLAYS = List.of(Biomes.RIVER, Biomes.BEACH);

    /**
     * The biome list is serialized as a plain list of biome ids, one per entry of {@link #keys()},
     * so the generated dimension reads the way every other one does and every biome it may place is
     * named in it.
     */
    public static final MapCodec<ArdaBiomeSource> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Biome.CODEC.listOf().fieldOf("biomes").forGetter(ArdaBiomeSource::biomes))
            .apply(instance, ArdaBiomeSource::new));

    private final List<Holder<Biome>> biomes;
    private final Map<ResourceKey<Biome>, Holder<Biome>> byKey;

    private ArdaBiomeSource(List<Holder<Biome>> biomes) {
        List<ResourceKey<Biome>> keys = keys();
        if (biomes.size() != keys.size()) {
            throw new IllegalStateException("Arda's map names " + keys.size() + " biomes and the world names "
                    + biomes.size() + "; the two lists are written together and cannot be edited apart");
        }

        this.biomes = List.copyOf(biomes);
        Map<ResourceKey<Biome>, Holder<Biome>> lookup = new LinkedHashMap<>();
        for (int index = 0; index < keys.size(); index++) {
            lookup.put(keys.get(index), biomes.get(index));
        }
        this.byKey = Map.copyOf(lookup);
    }

    /** Arda's biomes, resolved from the registry the dimension is being built against. */
    public static ArdaBiomeSource of(HolderGetter<Biome> biomes) {
        List<Holder<Biome>> holders = new ArrayList<>();
        keys().forEach(key -> holders.add(biomes.getOrThrow(key)));
        return new ArdaBiomeSource(holders);
    }

    /** Every biome the map can place, once each, in the order the codec writes them. */
    static List<ResourceKey<Biome>> keys() {
        return Stream.concat(Stream.of(ArdaMap.Country.values()).map(ArdaMap.Country::biome), OVERLAYS.stream())
                .distinct()
                .toList();
    }

    private List<Holder<Biome>> biomes() {
        return this.biomes;
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return this.biomes.stream();
    }

    /**
     * The biome at a quart position. The map is asked in block coordinates, so a border drawn in
     * cells lands within four blocks of where it is drawn rather than sixteen.
     */
    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        ArdaMap.Sample sample = ArdaMap.sample(QuartPos.toBlock(quartX), QuartPos.toBlock(quartZ),
                sampler.weirdness(), sampler.erosion());
        Holder<Biome> biome = this.byKey.get(sample.biome());
        if (biome == null) {
            throw new IllegalStateException("Arda's map named " + sample.biome().identifier()
                    + ", which is not in its biome list; add it to ArdaMap and the source together");
        }
        return biome;
    }
}
