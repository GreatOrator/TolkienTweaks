package com.greatorator.tolkienmobs.world.registration;

import com.mojang.serialization.MapCodec;
import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
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
    private static final List<ResourceKey<Biome>> OVERLAYS = List.of(Biomes.RIVER, Biomes.BEACH,
            TolkienBiomes.RIVER, TolkienBiomes.LAKE, TolkienBiomes.LAKE_EVENDIM, TolkienBiomes.SEA_OF_RHUN,
            TolkienBiomes.SEA_OF_NURNEN, TolkienBiomes.LONG_LAKE);

    /**
     * The biome list is serialized as a plain list of biome ids, one per entry of {@link #keys()},
     * so the generated dimension reads the way every other one does and every biome it may place is
     * named in it.
     */
    public static final MapCodec<ArdaBiomeSource> CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("biomes"));
        }

        @Override
        public <T> DataResult<ArdaBiomeSource> decode(DynamicOps<T> ops, MapLike<T> input) {
            if (input.get("biomes") == null) {
                return DataResult.error(() -> "Arda's dimension names no biomes at all");
            }
            // The biomes are read from the registry rather than from the world's own copy of this list. A
            // world saved before a country was added carries the older list, and reading it either shifts
            // every biome after the new one or loses the new one altogether - which is how a world made
            // before the valley came to have no valley biome in it and refused to open. The world's entry
            // now only has to say that this is a biome source; what it can place is the map's business.
            if (!(ops instanceof RegistryOps<?> registryOps)) {
                return DataResult.error(() -> "Arda's biomes can only be read from a registry");
            }
            Optional<HolderGetter<Biome>> getter = registryOps.getter(Registries.BIOME);
            if (getter.isEmpty()) {
                return DataResult.error(() -> "Arda's biomes need a biome registry to be read from");
            }
            List<Holder<Biome>> holders = new ArrayList<>();
            for (ResourceKey<Biome> key : ArdaBiomeSource.keys()) {
                Optional<Holder.Reference<Biome>> holder = getter.get().get(key);
                if (holder.isEmpty()) {
                    return DataResult.error(() -> "Arda's map names " + key.identifier()
                            + ", which this world has no biome for");
                }
                holders.add(holder.get());
            }
            return DataResult.success(new ArdaBiomeSource(holders));
        }

        @Override
        public <T> RecordBuilder<T> encode(ArdaBiomeSource source, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            // Written as ids, one per biome the map can place: a world saved again names the biomes the
            // code names, so the first save after a country is added carries that country's biome with it.
            return prefix.add("biomes", ops.createList(ArdaBiomeSource.keys().stream()
                    .map(key -> ops.createString(key.identifier().toString()))));
        }
    };

    private final List<Holder<Biome>> biomes;
    private final Map<ResourceKey<Biome>, Holder<Biome>> byKey;

    /** Every biome the map can place, in the order it names them; the registry has already answered. */
    private ArdaBiomeSource(List<Holder<Biome>> biomes) {
        List<ResourceKey<Biome>> keys = keys();
        if (biomes.size() != keys.size()) {
            throw new IllegalStateException("Arda's map names " + keys.size()
                    + " biomes and the source was given " + biomes.size() + "; the two are read together");
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
        return Stream.concat(Stream.concat(Stream.of(ArdaMap.Country.values()).map(ArdaMap.Country::biome),
                        OVERLAYS.stream()), Stream.of(TolkienBiomes.MORIA))
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
     * What a column of water reads as. The sample only knows that it is water - a river and a lake are laid
     * the same way - so the map is asked which drawn water this column belongs to, and that is the biome.
     */
    private static ResourceKey<Biome> water(ArdaMap.Sample sample, int blockX, int blockZ) {
        ResourceKey<Biome> key = sample.biome();
        return key == Biomes.RIVER ? ArdaMap.waterBiome(blockX, blockZ) : key;
    }

    /**
     * The biome at a quart position. The map is asked in block coordinates, so a border drawn in
     * cells lands within four blocks of where it is drawn rather than sixteen.
     */
    @Override
    public Holder<Biome> getNoiseBiome(int quartX, int quartY, int quartZ, Climate.Sampler sampler) {
        int blockX = QuartPos.toBlock(quartX);
        int blockZ = QuartPos.toBlock(quartZ);
        ArdaMap.Sample sample = ArdaMap.sample(blockX, blockZ, sampler.weirdness(), sampler.erosion());
        // Rock under the Misty crest is Moria's; the mountain over it stays the mountain. The rectangle
        // is the halls' own ground and answers whatever country the drawing puts above it: the range's
        // eastern flank is drawn as Lorinand in the cell the halls' middle stands in, and rock under the
        // range is the halls' whatever the map calls the surface. Only the rock below the roof is asked;
        // a column above it keeps its country's biome.
        boolean halls = QuartPos.toBlock(quartY) < ArdaMap.moriaRoof()
                && ArdaMap.underMoria(blockX, blockZ);
        Holder<Biome> biome = this.byKey.get(halls ? TolkienBiomes.MORIA : water(sample, blockX, blockZ));
        if (biome == null) {
            throw new IllegalStateException("Arda's map named " + sample.biome().identifier()
                    + ", which is not in its biome list; add it to ArdaMap and the source together");
        }
        return biome;
    }
}
