package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Which of the mod's biomes stands in each cell of Arda's climate grid, read off the Middle-earth
 * maps.
 *
 * <p>The grid itself is the overworld's: 26.1's own biome preset, the same temperatures,
 * humidities, erosions and depths that shape vanilla terrain. Arda keeps the grid because a
 * climate cell means ground of a particular shape - low erosion is the steep country a range
 * stands on, high erosion the flats a marsh or a field lies on - so filling a cell with a biome of
 * the same character is what puts Mordor on broken ground and the Shire on grass. What changes is
 * who lives in each cell.
 *
 * <p>The seas, their shores and the rivers keep their vanilla biomes: water is water, and a
 * vanilla ocean floor is the one that reads as a sea bed. Everything else the maps show is one of
 * the mod's twenty biomes.
 *
 * <p>{@link #parameters} refuses to build a grid with a cell left unwritten, so a Minecraft
 * version that adds a biome cannot quietly put vanilla country in Arda; it fails data generation
 * instead, where the missing cell can be seen and filled in.
 */
final class ArdaClimate {
    private ArdaClimate() {
    }

    private static final Map<ResourceKey<Biome>, ResourceKey<Biome>> CELLS = cells();

    /** Arda's biome list: every cell of the overworld's grid, with a mod biome in all but the water. */
    static Climate.ParameterList<Holder<Biome>> parameters(HolderGetter<Biome> biomes) {
        Climate.ParameterList<ResourceKey<Biome>> grid = MultiNoiseBiomeSourceParameterList.knownPresets()
                .get(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD);
        if (grid == null) {
            throw new IllegalStateException("Arda lays its biomes over the overworld's climate grid, and 26.1 has no overworld preset");
        }

        List<Pair<Climate.ParameterPoint, Holder<Biome>>> arda = new ArrayList<>(grid.values().size());
        Set<ResourceKey<Biome>> written = new LinkedHashSet<>();
        for (Pair<Climate.ParameterPoint, ResourceKey<Biome>> cell : grid.values()) {
            ResourceKey<Biome> biome = CELLS.get(cell.getSecond());
            if (biome == null) {
                throw new IllegalStateException("Arda has no biome for the climate cell vanilla's " + cell.getSecond().identifier()
                        + " holds; add it to ArdaClimate before this ships.");
            }
            written.add(cell.getSecond());
            arda.add(Pair.of(cell.getFirst(), biomes.getOrThrow(biome)));
        }

        for (ResourceKey<Biome> cell : CELLS.keySet()) {
            if (!written.contains(cell)) {
                throw new IllegalStateException("ArdaClimate names the climate cell " + cell.identifier()
                        + ", which the overworld's grid no longer has; take it out of the table.");
            }
        }
        return new Climate.ParameterList<>(arda);
    }

    /**
     * The grid, cell by cell. Every entry is a place on one of the maps: the table is the mapping,
     * so a biome's neighbours here are the biomes its country borders, and the comments say which
     * country that is.
     */
    private static Map<ResourceKey<Biome>, ResourceKey<Biome>> cells() {
        Map<ResourceKey<Biome>, ResourceKey<Biome>> cells = new LinkedHashMap<>();

        // Water: the seas of both hemispheres, their shores and the rivers, which stay vanilla.
        keep(cells, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN,
                Biomes.DEEP_LUKEWARM_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN,
                Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.RIVER, Biomes.FROZEN_RIVER,
                Biomes.BEACH, Biomes.SNOWY_BEACH, Biomes.STONY_SHORE);

        // The great wood: Greenwood the Great, the Wild Wood the world map draws across Rhovanion,
        // and the broadleaf and jungle country of the south. The pale northern corner of it is the
        // haunted part of Mirkwood about Dol Guldur.
        put(cells, TolkienBiomes.MIRKWOOD, Biomes.FOREST, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE,
                Biomes.BAMBOO_JUNGLE);
        put(cells, TolkienBiomes.MIRKWOOD_SPOOKY, Biomes.DARK_FOREST, Biomes.PALE_GARDEN);

        // The old woods: the oak wood south of the Misty Mountains, and the hollies of Eregion.
        put(cells, TolkienBiomes.FANGORN, Biomes.OLD_GROWTH_PINE_TAIGA);
        put(cells, TolkienBiomes.OLD_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.MUSHROOM_FIELDS);

        // The flower country: Ithilien, the wooded slopes below the White Mountains and the
        // orchards of the west, which the maps draw as birch and flowering wood.
        put(cells, TolkienBiomes.FIRIEN, Biomes.BIRCH_FOREST, Biomes.FLOWER_FOREST, Biomes.CHERRY_GROVE);

        // The pine belt: the taiga that runs across the north of the world map, and Lothlorien.
        put(cells, TolkienBiomes.LORINAND, Biomes.TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA,
                Biomes.SNOWY_TAIGA, Biomes.GROVE);

        // Grass: the Shire's green fields, and the wider vale of the Anduin.
        put(cells, TolkienBiomes.SHIRE, Biomes.PLAINS);
        put(cells, TolkienBiomes.GLADDEN, Biomes.SUNFLOWER_PLAINS, Biomes.MEADOW);

        // The brown lands: Rohan, Rhun, Rhovanion, Khand and the nearer marches of Gondor, drawn on
        // both maps as steppe and savannah.
        put(cells, TolkienBiomes.DAGORLAD, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA);

        // The watered half of Mordor, and Mordor itself: the eroded country the maps draw brown,
        // which is the same ground vanilla gives its badlands.
        put(cells, TolkienBiomes.NURN, Biomes.BADLANDS, Biomes.WOODED_BADLANDS);
        put(cells, TolkienBiomes.MORDOR, Biomes.ERODED_BADLANDS);

        // Harad, from Umbar down to the desert of the far south.
        put(cells, TolkienBiomes.HARADWAITH, Biomes.DESERT);

        // The marshes: the Wetwang and the Dead Marshes of the north, and the mangrove country of
        // the southern coast.
        put(cells, TolkienBiomes.MARSHES, Biomes.SWAMP, Biomes.MANGROVE_SWAMP);

        // Cold, worn-down ground: Angmar and the Barrow Downs, which the maps leave bare.
        put(cells, TolkienBiomes.BARROW_DOWNS, Biomes.WINDSWEPT_HILLS);

        // The ranges. Ered Luin is temperate country, Ered Mithrin and the Iron Hills the cold
        // rocky hills, and the arctic wall the ice. The ash of the White Mountains, Emyn Muil and
        // the Red Mountains is the mod's own dark stone, and the three cave cells come out as that
        // same rock, since Arda's biomes carry no carver for a cave to be cut into.
        put(cells, TolkienBiomes.BLUE_MOUNTAINS, Biomes.WINDSWEPT_FOREST);
        put(cells, TolkienBiomes.IRON_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS);
        put(cells, TolkienBiomes.ASH_MOUNTAINS, Biomes.STONY_PEAKS, Biomes.DEEP_DARK,
                Biomes.LUSH_CAVES, Biomes.DRIPSTONE_CAVES);
        put(cells, TolkienBiomes.HITHAEGLIR, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.ICE_SPIKES);

        // The polar north and the slopes that lead down from it.
        put(cells, TolkienBiomes.FOROCHEL, Biomes.SNOWY_PLAINS);
        put(cells, TolkienBiomes.FOROCHEL_SLOPES, Biomes.SNOWY_SLOPES);

        return cells;
    }

    @SafeVarargs
    private static void put(Map<ResourceKey<Biome>, ResourceKey<Biome>> cells, ResourceKey<Biome> biome,
                            ResourceKey<Biome>... from) {
        for (ResourceKey<Biome> cell : from) {
            cells.put(cell, biome);
        }
    }

    @SafeVarargs
    private static void keep(Map<ResourceKey<Biome>, ResourceKey<Biome>> cells, ResourceKey<Biome>... vanilla) {
        for (ResourceKey<Biome> cell : vanilla) {
            cells.put(cell, cell);
        }
    }
}
