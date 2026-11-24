package com.greatorator.tolkienmobs.world.registration;

import com.greatorator.tolkienmobs.TolkienMobsMain;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.biome.OverworldBiomes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TimelineTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.attribute.AmbientSounds;
import net.minecraft.world.attribute.BackgroundMusic;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.attribute.EnvironmentAttributeMap;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.clock.WorldClocks;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.timeline.Timeline;

import java.util.Optional;

/**
 * Arda, the world the mod's biomes belong to.
 *
 * <p>It is an overworld: same height, same sky, same clock, and coordinates that line up one to one
 * with the overworld's, so a place on one map is the same place on the other. What makes it Arda is
 * that it is the Middle-earth of the maps - {@link ArdaMap} draws the countries, the ranges, the
 * seas and the rivers, {@link ArdaBiomeSource} reads that drawing for its biomes and
 * {@link ArdaChunkGenerator} cuts the terrain to it - so Mordor stands in its ring of ash, the Mark
 * is grass between the Misty Mountains and the white wall, and the Anduin runs from the Grey
 * Mountains to the Bay of Belfalas.
 *
 * <p>Terrain and surface come from {@code minecraft:overworld} itself: this dimension reuses its
 * noise settings, which keeps the water, the aquifers and the ground under every biome identical to
 * the overworld's - with one number changed. The overworld's sea level is sixty-three, and the
 * drawing's waterline is {@link ArdaMap#SEA_LEVEL} twenty-five, so the copy Arda generates
 * ({@link #ARDA_SETTINGS}) has that sea level and nothing else altered: the aquifer stops filling
 * above the drawing's water and the carvers stop cutting into water the drawing has as land. That is
 * also how the mod's own rock,
 * grass and sand reach Arda. TerraBlender wraps a dimension's surface rules when its dimension
 * type is tagged {@code terrablender:overworld_regions} - that tag is what tells it the dimension
 * is an overworld it should paint - and the wrapped rules pick themselves by the namespace of the
 * biome at each position, so a Tolkien biome is painted by this mod's rules in Arda exactly as it
 * is in the overworld. The dimension type is therefore added to that tag in this mod's data.
 */
public final class TolkienDimensions {
    private TolkienDimensions() {
    }

    /** The dimension itself, as {@code Level} keys it. */
    public static final ResourceKey<Level> ARDA = TolkienMobsMain.key(Registries.DIMENSION, "arda");
    /** The same dimension as the datapack registry keys it. */
    public static final ResourceKey<LevelStem> ARDA_STEM = TolkienMobsMain.key(Registries.LEVEL_STEM, "arda");
    public static final ResourceKey<DimensionType> ARDA_TYPE = TolkienMobsMain.key(Registries.DIMENSION_TYPE, "arda_type");
    /**
     * Arda's own noise settings: the overworld's, with the sea level read as the drawing's waterline.
     * <p>
     * The overworld's settings are read from a sea level of sixty-three, which is where its own sea stands; the
     * drawing's water stands at {@link ArdaMap#SEA_LEVEL} twenty-five, and everything the settings' sea level feeds
     * reads that number: the aquifer fills the noise's voids up to it, and the carvers fill what they cut up to it.
     * Held at sixty-three, the Shire's ground - forty to sixty-two under the drawing - came out of the noise with the
     * aquifer's water for its top block wherever the noise had a void there, and the surface rules cannot dress water:
     * the drawing's fields arrived as a sheet terraced at their own contours. Cut to twenty-five, nothing the aquifer
     * fills is above the drawing's waterline, and a cave in the drawing's land is air.
     * <p>
     * The copy is generated rather than patched at run time: {@link #bootstrapNoise} reads the overworld's record at
     * data generation, where the vanilla registries are bound, and writes it out under this mod's own key, so the
     * shipped entry is a complete noise setting - its router, its surface rule, its fluid - and no code reads
     * {@code minecraft:overworld} while the registries are still being built. Everything but the sea level is the
     * overworld's own, so the noise, the caves, the ores and the water are the same terrain they always were.
     */
    public static final ResourceKey<NoiseGeneratorSettings> ARDA_SETTINGS =
            TolkienMobsMain.key(Registries.NOISE_SETTINGS, "arda");

    /**
     * Arda's properties. Every value is the overworld's own, read from 26.1's
     * {@code net.minecraft.data.worldgen.DimensionTypes}, so the two worlds look and behave alike:
     * sky light and weather, a day and a night on the overworld's clock, sleeping that works and
     * respawn anchors that do not.
     * <p>
     * The height is the overworld's too - 384 blocks, whose highest block is 319 - because that is the
     * height everything else in the game is built and measured against, and a ceiling higher than the
     * overworld's is a ceiling nobody can climb to from here without a command. The city's own crown at
     * 318 is therefore one block under the sky, and the room its massif needs comes from the ground
     * instead: the country round the valley is brought down to the vale the city stands in, rather than
     * the city being raised out of it (see ArdaMap's vale).
     */
    public static void bootstrapType(BootstrapContext<DimensionType> context) {
        HolderGetter<Timeline> timelines = context.lookup(Registries.TIMELINE);
        HolderGetter<WorldClock> clocks = context.lookup(Registries.WORLD_CLOCK);

        context.register(ARDA_TYPE, new DimensionType(
                false,
                true,
                false,
                false,
                1.0,
                -64,
                384,
                384,
                BlockTags.INFINIBURN_OVERWORLD,
                0.0F,
                new DimensionType.MonsterSettings(UniformInt.of(0, 7), 0),
                DimensionType.Skybox.OVERWORLD,
                CardinalLighting.Type.DEFAULT,
                EnvironmentAttributeMap.builder()
                        .set(EnvironmentAttributes.FOG_COLOR, -4138753)
                        .set(EnvironmentAttributes.SKY_COLOR, OverworldBiomes.calculateSkyColor(0.8F))
                        .set(EnvironmentAttributes.AMBIENT_LIGHT_COLOR, -16119286)
                        .set(EnvironmentAttributes.CLOUD_COLOR, ARGB.white(0.8F))
                        .set(EnvironmentAttributes.CLOUD_HEIGHT, 192.33F)
                        .set(EnvironmentAttributes.BACKGROUND_MUSIC, BackgroundMusic.OVERWORLD)
                        .set(EnvironmentAttributes.BED_RULE, BedRule.CAN_SLEEP_WHEN_DARK)
                        .set(EnvironmentAttributes.RESPAWN_ANCHOR_WORKS, false)
                        .set(EnvironmentAttributes.NETHER_PORTAL_SPAWNS_PIGLINS, true)
                        .set(EnvironmentAttributes.AMBIENT_SOUNDS, AmbientSounds.LEGACY_CAVE_SETTINGS)
                        .build(),
                timelines.getOrThrow(TimelineTags.IN_OVERWORLD),
                Optional.of(clocks.getOrThrow(WorldClocks.OVERWORLD))));
    }

    /**
     * Arda's noise settings: the overworld's own record, with the sea level read as the drawing's waterline.
     * <p>
     * Nothing but one number is Arda's. The router, the noise, the default block and fluid, the surface rule the
     * dimension never uses (it lays {@link TolkienSurfaceRules#ardaSurface()}), the spawn target and the switches for
     * aquifers, ore veins and the legacy random source are all copied from {@code minecraft:overworld}, so the rock,
     * the caves and the veins are the terrain they were; the sea level is {@link ArdaMap#SEA_LEVEL} because that is
     * where the drawing's water stands. Read here rather than at run time on purpose: this runs at data generation,
     * where the vanilla registry is bound and its records can be read, and the result is written out as this mod's own
     * {@code worldgen/noise_settings/arda.json} for the game to load.
     */
    public static void bootstrapNoise(BootstrapContext<NoiseGeneratorSettings> context) {
        NoiseGeneratorSettings overworld = NoiseGeneratorSettings.overworld(context, false, false);
        context.register(ARDA_SETTINGS, new NoiseGeneratorSettings(
                overworld.noiseSettings(),
                overworld.defaultBlock(),
                overworld.defaultFluid(),
                overworld.noiseRouter(),
                overworld.surfaceRule(),
                overworld.spawnTarget(),
                ArdaMap.SEA_LEVEL,
                overworld.disableMobGeneration(),
                overworld.aquifersEnabled(),
                overworld.oreVeinsEnabled(),
                overworld.useLegacyRandomSource()));
    }

    /** The world itself: the overworld's terrain, generated with Arda's biomes. */
    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(ARDA_STEM, new LevelStem(
                types.getOrThrow(ARDA_TYPE),
                new ArdaChunkGenerator(
                        ArdaBiomeSource.of(biomes),
                        settings.getOrThrow(ARDA_SETTINGS))));
    }
}
