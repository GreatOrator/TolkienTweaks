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
 * noise settings rather than copying them, which keeps the sea level, the water, the aquifers and
 * the ground under every biome identical to the overworld's. That is also how the mod's own rock,
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
     * Arda's properties. Every value is the overworld's own, read from 26.1's
     * {@code net.minecraft.data.worldgen.DimensionTypes}, so the two worlds look and behave alike:
     * sky light and weather, a day and a night on the overworld's clock, sleeping that works and
     * respawn anchors that do not.
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

    /** The world itself: the overworld's terrain, generated with Arda's biomes. */
    public static void bootstrapStem(BootstrapContext<LevelStem> context) {
        HolderGetter<DimensionType> types = context.lookup(Registries.DIMENSION_TYPE);
        HolderGetter<NoiseGeneratorSettings> settings = context.lookup(Registries.NOISE_SETTINGS);
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(ARDA_STEM, new LevelStem(
                types.getOrThrow(ARDA_TYPE),
                new ArdaChunkGenerator(
                        ArdaBiomeSource.of(biomes),
                        settings.getOrThrow(NoiseGeneratorSettings.OVERWORLD))));
    }
}
