package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.init.TolkienBiomes;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import terrablender.api.ModifiedVanillaOverworldBuilder;
import terrablender.api.ParameterUtils;
import terrablender.worldgen.RegionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The climate the Tolkien region keeps for itself, next to the vanilla slots it renames.
 *
 * <p>Renaming a vanilla slot means inheriting that slot's size: the Shire sits in plains and is
 * met constantly, while Firien sits in flower forest and is met almost never. {@link #CLAIMS}
 * fixes that by cutting whole parameter cells out of the slots that hold most of the region -
 * plains, forest and the desert - and giving them to the biomes that were starved, choosing the
 * cells whose temperature, humidity and erosion suit the biome. Erosion is what shapes the ground,
 * so a mountain range is given the steep cells of a large slot rather than a whole small peak slot.
 *
 * <p>A cell is removed from its slot rather than shared with it: two points covering the same
 * climate tie, and a tie leaves the vanilla side of it standing.
 */
final class TolkienClimate {
    private TolkienClimate() {
    }

    /** One vanilla slot a Tolkien biome takes over, in the order the region applies them. */
    static final Map<ResourceKey<Biome>, ResourceKey<Biome>> SLOT_REPLACEMENTS = slotReplacements();

    /**
     * A block of parameter cells taken out of {@code from} and handed to {@code to}, narrowed by the
     * bands the biome belongs in. Any array left empty matches every band of that dimension.
     */
    record Claim(ResourceKey<Biome> from, ResourceKey<Biome> to,
                 Climate.Parameter[] temperatures, Climate.Parameter[] humidities,
                 Climate.Parameter[] erosions) {
        boolean matches(Climate.ParameterPoint point) {
            return holds(temperatures, point.temperature()) && holds(humidities, point.humidity())
                    && holds(erosions, point.erosion());
        }

        /**
         * The bands are compared by their middle, not their edges: TerraBlender's erosion bands end
         * at -0.375 and -0.2225 where vanilla's end at -0.38 and -0.22, so an equal-edged test
         * quietly matches nothing.
         */
        private static boolean holds(Climate.Parameter[] bands, Climate.Parameter value) {
            if (bands.length == 0) {
                return true;
            }
            long middle = (value.min() + value.max()) / 2;
            for (Climate.Parameter band : bands) {
                if (band.min() <= middle && middle <= band.max()) {
                    return true;
                }
            }
            return false;
        }
    }

    static final List<Claim> CLAIMS = claims();

    /** Removes the claimed cells from the vanilla slots and returns the points that replace them. */
    static List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> carve(ModifiedVanillaOverworldBuilder builder) {
        List<Pair<Climate.ParameterPoint, ResourceKey<Biome>>> own = new ArrayList<>();
        Set<Climate.ParameterPoint> taken = new LinkedHashSet<>();
        for (Claim claim : CLAIMS) {
            for (Climate.ParameterPoint point : RegionUtils.getVanillaParameterPoints(claim.from())) {
                if (claim.matches(point) && taken.add(point)) {
                    builder.removeParameter(point);
                    own.add(Pair.of(point, claim.to()));
                }
            }
        }
        return own;
    }

    private static Map<ResourceKey<Biome>, ResourceKey<Biome>> slotReplacements() {
        Map<ResourceKey<Biome>, ResourceKey<Biome>> slots = new LinkedHashMap<>();
        slots.put(Biomes.DARK_FOREST, TolkienBiomes.MIRKWOOD);
        slots.put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, TolkienBiomes.MIRKWOOD_SPOOKY);
        slots.put(Biomes.WINDSWEPT_GRAVELLY_HILLS, TolkienBiomes.IRON_HILLS);
        // The windswept forest point is the last free windswept climate, and its temperate
        // hills give the dwarven range of the Blue Mountains somewhere to stand.
        slots.put(Biomes.WINDSWEPT_FOREST, TolkienBiomes.BLUE_MOUNTAINS);
        slots.put(Biomes.FROZEN_PEAKS, TolkienBiomes.HITHAEGLIR);
        slots.put(Biomes.OLD_GROWTH_PINE_TAIGA, TolkienBiomes.FANGORN);
        slots.put(Biomes.ERODED_BADLANDS, TolkienBiomes.MORDOR);
        slots.put(Biomes.BADLANDS, TolkienBiomes.NURN);
        slots.put(Biomes.WOODED_BADLANDS, TolkienBiomes.NURN);
        slots.put(Biomes.JAGGED_PEAKS, TolkienBiomes.ASH_MOUNTAINS);
        slots.put(Biomes.SWAMP, TolkienBiomes.MARSHES);
        slots.put(Biomes.BIRCH_FOREST, TolkienBiomes.LORINAND);
        slots.put(Biomes.WINDSWEPT_HILLS, TolkienBiomes.BARROW_DOWNS);
        slots.put(Biomes.FOREST, TolkienBiomes.OLD_FOREST);
        slots.put(Biomes.PLAINS, TolkienBiomes.SHIRE);
        slots.put(Biomes.FLOWER_FOREST, TolkienBiomes.FIRIEN);
        slots.put(Biomes.DESERT, TolkienBiomes.HARADWAITH);
        slots.put(Biomes.SAVANNA_PLATEAU, TolkienBiomes.DAGORLAD);
        slots.put(Biomes.SUNFLOWER_PLAINS, TolkienBiomes.GLADDEN);
        slots.put(Biomes.SNOWY_PLAINS, TolkienBiomes.FOROCHEL);
        slots.put(Biomes.SNOWY_SLOPES, TolkienBiomes.FOROCHEL_SLOPES);

        // The countries the drawn map names that none of the older biomes could stand for take the
        // vanilla slot whose own country they are: the Mark's grass is the savanna, Ered Nimrais is
        // the stone peaks, Dunland is the last windswept hills, Lindon is the cherry grove's green
        // land, and Gondor is the meadow - open flowered grass - rather than the Shire's plains.
        slots.put(Biomes.SAVANNA, TolkienBiomes.ROHAN);
        slots.put(Biomes.STONY_PEAKS, TolkienBiomes.WHITE_MOUNTAINS);
        slots.put(Biomes.WINDSWEPT_SAVANNA, TolkienBiomes.DUNLAND);
        slots.put(Biomes.CHERRY_GROVE, TolkienBiomes.LINDON);
        slots.put(Biomes.MEADOW, TolkienBiomes.GONDOR);
        return slots;
    }

    private static List<Claim> claims() {
        List<Claim> claims = new ArrayList<>();

        // Plains hold the Shire, and they are the largest single holding in the region. Their warm
        // half is the flower country, the flattest of their dry ground is the Gladden fields, their
        // cool dry slopes are the Barrow Downs and its steepest are the Iron Hills, and the steep
        // ground of their mild dry half lifts into the Misty Mountains.
        claims.add(claim(Biomes.PLAINS, TolkienBiomes.FIRIEN,
                temps(ParameterUtils.Temperature.WARM), hums(ParameterUtils.Humidity.NEUTRAL)));
        claims.add(claim(Biomes.PLAINS, TolkienBiomes.BARROW_DOWNS,
                temps(ParameterUtils.Temperature.COOL), hums(ParameterUtils.Humidity.DRY),
                eros(ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_3)));
        claims.add(claim(Biomes.PLAINS, TolkienBiomes.HITHAEGLIR,
                temps(ParameterUtils.Temperature.NEUTRAL), hums(ParameterUtils.Humidity.DRY),
                eros(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)));
        claims.add(claim(Biomes.PLAINS, TolkienBiomes.IRON_HILLS,
                temps(ParameterUtils.Temperature.COOL), hums(ParameterUtils.Humidity.ARID),
                steep()));
        // Eriador is the Shire's neighbour on the map and shares its slot: the flat cool dry ground
        // that the Iron Hills left, which is the fields of the North.
        claims.add(claim(Biomes.PLAINS, TolkienBiomes.ERIADOR,
                temps(ParameterUtils.Temperature.COOL), hums(ParameterUtils.Humidity.ARID),
                flat()));
        claims.add(claim(Biomes.PLAINS, TolkienBiomes.GLADDEN,
                temps(ParameterUtils.Temperature.NEUTRAL), hums(ParameterUtils.Humidity.DRY),
                flat()));

        // Forest holds the Old Forest. Its cool half is split by erosion between Fangorn and the
        // Blue Mountains, its wet flats are the marshes, and its warm half is Lothlórien.
        claims.add(claim(Biomes.FOREST, TolkienBiomes.FANGORN,
                temps(ParameterUtils.Temperature.COOL), hums(ParameterUtils.Humidity.NEUTRAL),
                flat()));
        claims.add(claim(Biomes.FOREST, TolkienBiomes.BLUE_MOUNTAINS,
                temps(ParameterUtils.Temperature.COOL), hums(ParameterUtils.Humidity.NEUTRAL),
                steep()));
        claims.add(claim(Biomes.FOREST, TolkienBiomes.MARSHES,
                temps(ParameterUtils.Temperature.NEUTRAL), hums(ParameterUtils.Humidity.NEUTRAL),
                flat()));
        claims.add(claim(Biomes.FOREST, TolkienBiomes.LORINAND,
                temps(ParameterUtils.Temperature.WARM), hums(ParameterUtils.Humidity.NEUTRAL),
                eros(ParameterUtils.Erosion.EROSION_5, ParameterUtils.Erosion.EROSION_6)));
        // The Wild Wood is Rhovanion's beech country, and the warm half of the forest slot is split
        // the way its cool half is: its flat ground is Lothlorien's, its rolling ground is the wood
        // that stayed wild.
        claims.add(claim(Biomes.FOREST, TolkienBiomes.WILD_WOOD,
                temps(ParameterUtils.Temperature.WARM), hums(ParameterUtils.Humidity.NEUTRAL),
                eros(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1,
                        ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_3,
                        ParameterUtils.Erosion.EROSION_4)));

        // The desert is the second largest holding and Haradwaith the whole of it. Its two driest
        // halves become the brown plain of Dagorlad and the ash of Mordor, and its steep hot ground
        // carries the Ash Mountains.
        claims.add(claim(Biomes.DESERT, TolkienBiomes.DAGORLAD,
                temps(ParameterUtils.Temperature.HOT), hums(ParameterUtils.Humidity.DRY)));
        claims.add(claim(Biomes.DESERT, TolkienBiomes.MORDOR,
                temps(ParameterUtils.Temperature.HOT), hums(ParameterUtils.Humidity.ARID)));
        claims.add(claim(Biomes.DESERT, TolkienBiomes.ASH_MOUNTAINS,
                temps(ParameterUtils.Temperature.HOT), hums(ParameterUtils.Humidity.NEUTRAL),
                steep()));

        // The dark forest is Mirkwood and nothing else; its steepest ground is the haunted part.
        claims.add(claim(Biomes.DARK_FOREST, TolkienBiomes.MIRKWOOD_SPOOKY,
                temps(ParameterUtils.Temperature.NEUTRAL), hums(ParameterUtils.Humidity.HUMID),
                eros(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)));

        // The snowy plains are Forochel, and they cannot carry three snowy biomes, so only their
        // coldest high ground is taken - for the Misty Mountains and the slope country - and
        // Forochel Slopes also takes steep forest ground, where its own temperature still snows it.
        claims.add(claim(Biomes.SNOWY_PLAINS, TolkienBiomes.HITHAEGLIR,
                temps(ParameterUtils.Temperature.ICY), hums(ParameterUtils.Humidity.NEUTRAL),
                eros(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1,
                        ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_3,
                        ParameterUtils.Erosion.EROSION_4, ParameterUtils.Erosion.EROSION_5)));
        claims.add(claim(Biomes.SNOWY_PLAINS, TolkienBiomes.HITHAEGLIR,
                temps(ParameterUtils.Temperature.ICY), hums(ParameterUtils.Humidity.ARID),
                steep()));
        claims.add(claim(Biomes.SNOWY_PLAINS, TolkienBiomes.FOROCHEL_SLOPES,
                temps(ParameterUtils.Temperature.ICY), hums(ParameterUtils.Humidity.ARID),
                flat()));
        claims.add(claim(Biomes.FOREST, TolkienBiomes.FOROCHEL_SLOPES,
                temps(ParameterUtils.Temperature.NEUTRAL), hums(ParameterUtils.Humidity.NEUTRAL),
                eros(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1)));
        return claims;
    }

    /** The low erosion bands: ground steep enough to carry a range of mountains or hills. */
    private static Climate.Parameter[] steep() {
        return eros(ParameterUtils.Erosion.EROSION_0, ParameterUtils.Erosion.EROSION_1,
                ParameterUtils.Erosion.EROSION_2, ParameterUtils.Erosion.EROSION_3);
    }

    /** The high erosion bands: the flat ground a marsh, a field or an old forest stands on. */
    private static Climate.Parameter[] flat() {
        return eros(ParameterUtils.Erosion.EROSION_4, ParameterUtils.Erosion.EROSION_5,
                ParameterUtils.Erosion.EROSION_6);
    }

    private static Claim claim(ResourceKey<Biome> from, ResourceKey<Biome> to,
                               Climate.Parameter[] temperatures, Climate.Parameter[] humidities) {
        return new Claim(from, to, temperatures, humidities, new Climate.Parameter[0]);
    }

    private static Claim claim(ResourceKey<Biome> from, ResourceKey<Biome> to, Climate.Parameter[] temperatures,
                               Climate.Parameter[] humidities, Climate.Parameter[] erosions) {
        return new Claim(from, to, temperatures, humidities, erosions);
    }

    private static Climate.Parameter[] temps(ParameterUtils.Temperature... bands) {
        Climate.Parameter[] parameters = new Climate.Parameter[bands.length];
        for (int i = 0; i < bands.length; i++) {
            parameters[i] = bands[i].parameter();
        }
        return parameters;
    }

    private static Climate.Parameter[] hums(ParameterUtils.Humidity... bands) {
        Climate.Parameter[] parameters = new Climate.Parameter[bands.length];
        for (int i = 0; i < bands.length; i++) {
            parameters[i] = bands[i].parameter();
        }
        return parameters;
    }

    private static Climate.Parameter[] eros(ParameterUtils.Erosion... bands) {
        Climate.Parameter[] parameters = new Climate.Parameter[bands.length];
        for (int i = 0; i < bands.length; i++) {
            parameters[i] = bands[i].parameter();
        }
        return parameters;
    }
}
