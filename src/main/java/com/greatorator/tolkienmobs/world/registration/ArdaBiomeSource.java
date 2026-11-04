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
import java.util.LinkedHashSet;
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

    /**
     * Reads a world's biome list, which may have been saved before the map last gained a country.
     * <p>
     * The list is matched by biome id rather than by position, so a country added since the world was
     * made does not shift every biome after it. A list that named thirty-five biomes where the map now
     * names thirty-six used to abort the dimension outright, and a world that will not open is a worse
     * answer than one whose newest ground reads as the hills it was cut out of: a biome the world does
     * not name is filled with the country the new ground was carved from - the Trollshaws, for the
     * valley - so the floor still stands where it should, and only that ground's own colour, foliage
     * and people wait for the world to be remade.
     */
    private ArdaBiomeSource(List<Holder<Biome>> biomes) {
        Map<ResourceKey<Biome>, Holder<Biome>> saved = new LinkedHashMap<>();
        for (Holder<Biome> holder : biomes) {
            holder.unwrapKey().ifPresent(key -> saved.put(key, holder));
        }
        Holder<Biome> stand = saved.get(ArdaMap.Country.TROLLSHAWS.biome());
        if (stand == null && !biomes.isEmpty()) {
            stand = biomes.get(0);
        }
        if (stand == null) {
            throw new IllegalStateException("Arda's world names no biomes at all, so nothing can be placed");
        }

        Map<ResourceKey<Biome>, Holder<Biome>> lookup = new LinkedHashMap<>();
        for (ResourceKey<Biome> key : keys()) {
            Holder<Biome> holder = saved.get(key);
            lookup.put(key, holder == null ? stand : holder);
        }
        this.byKey = Map.copyOf(lookup);
        // A substituted country is one holder named twice, so the list handed back and re-encoded is
        // deduplicated: a world carries each biome the map has once, and never the same one under two
        // names.
        this.biomes = List.copyOf(new LinkedHashSet<>(lookup.values()));
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
